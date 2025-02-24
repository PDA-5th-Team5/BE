pipeline {
    agent any

    environment {
        DOCKER_USER = "grrrrr1123"
        BASTION_HOST = "ubuntu@43.200.225.24"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().split("\n")
                    def servicesToBuild = ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'].findAll { service ->
                        changedFiles.any { it.contains(service) }
                    }
                    if (servicesToBuild.isEmpty()) {
                        servicesToBuild = ['util-service']
                    }
                    env.SERVICES_TO_BUILD = servicesToBuild.join(',')
                }
            }
        }

        stage('Build Jar') {
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    parallel services.collectEntries { service ->
                        ["Build Jar ${service}": {
                            sh """
                            cd src/${service} && \
                            ./gradlew clean build --build-cache --parallel --daemon
                            """
                        }]
                    }
                }
            }
        }

        stage('Verify JAR File') {
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    services.each { service ->
                        def jarPath = "src/${service}/build/libs/"
                        def jarExists = sh(script: "ls ${jarPath}*.jar | grep -E '(${service}-.*.jar|app.jar)'", returnStatus: true) == 0
                        if (!jarExists) {
                            error "JAR 파일이 생성되지 않았습니다: ${service}"
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    parallel services.collectEntries { service ->
                        ["Build & Push ${service}": {
                            sh """
                            docker build --cache-from=${DOCKER_USER}/${service}:latest -t ${DOCKER_USER}/${service}:latest -f ./src/${service}/Dockerfile ./src/${service}
                            docker push ${DOCKER_USER}/${service}:latest
                            """
                        }]
                    }
                }
            }
        }

        stage('Deploy to Private Subnet Servers') {
            steps {
                script {
                    def SERVER_MAPPING = [
                        "api-gateway": "10.0.2.61",
                        "eureka-server": "10.0.2.61",
                        "util-service": "10.0.2.61",
                        "user-service": "10.0.2.44",
                        "stock-service": "10.0.2.96",
                        "snowflake-service": "10.0.2.25"
                    ]

                    def PORT_MAPPING = [
                        "api-gateway": "8081:8081",
                        "eureka-server": "8761:8761",
                        "util-service": "8082:8082",
                        "user-service": "8083:8083",
                        "stock-service": "8084:8084",
                        "snowflake-service": "8085:8085"
                    ]

                    def parallelDeploy = [:]
                    env.SERVICES_TO_BUILD.split(',').each { service ->
                        def privateIP = SERVER_MAPPING[service]
                        parallelDeploy["Deploy ${service}"] = {
                            withCredentials([sshUserPrivateKey(credentialsId: 'bastion-ssh-key', keyFileVariable: 'SSH_KEY_FILE')]) {
                                sh """
                                ssh -i $SSH_KEY_FILE -o StrictHostKeyChecking=no ${BASTION_HOST} '
                                    ssh -i ~/.ssh/id_rsa ubuntu@${privateIP} \\"tmux new-session -d -s deploy-${service} \\"docker pull ${DOCKER_USER}/${service}:latest && docker stop ${service} && docker rm ${service} && docker run -d --name ${service} -p ${PORT_MAPPING[service]} ${DOCKER_USER}/${service}:latest \\"\\""
                                '
                                """
                            }
                        }
                    }
                    parallel parallelDeploy
                }
            }
        }
    }
}
