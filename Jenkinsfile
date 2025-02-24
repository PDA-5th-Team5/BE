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

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    parallel services.collectEntries { service ->
                        ["Build & Push ${service}": {
                            sh """
                            cd src/${service} && \
                            docker build -t ${DOCKER_USER}/${service}:latest . && \
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
                        def portMapping = PORT_MAPPING[service]
                        parallelDeploy["Deploy ${service}"] = {
                            withCredentials([sshUserPrivateKey(credentialsId: 'bastion-ssh-key', keyFileVariable: 'SSH_KEY_FILE')]) {
                                sh """
                                ssh -i ${env.SSH_KEY_FILE} -o StrictHostKeyChecking=no ubuntu@${BASTION_HOST} <<EOF
                                ssh -i ~/.ssh/id_rsa ubuntu@${privateIP} '
                                    IMAGE_ID=\$(docker images -q ${DOCKER_USER}/${service}:latest)
                                    if [ -z "\$IMAGE_ID" ]; then
                                        echo "새로운 이미지 다운로드 중: ${DOCKER_USER}/${service}:latest"
                                        docker pull ${DOCKER_USER}/${service}:latest
                                    else
                                        echo "이미 최신 이미지가 존재합니다. 다운로드하지 않습니다."
                                    fi
                                    docker stop ${service} || true
                                    docker rm ${service} || true
                                    docker run -d --name ${service} -p ${portMapping} ${DOCKER_USER}/${service}:latest
                                '
                                EOF
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
