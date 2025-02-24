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

        stage('Build Jar') {
            steps {
                script {
                    def services = ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service']
                    parallel services.collectEntries { service ->
                        ["Build Jar ${service}": {
                            sh """
                            cd src/${service} && \
                            ./gradlew clean build
                            """
                        }]
                    }
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def services = ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service']
                    parallel services.collectEntries { service ->
                        ["Build & Push ${service}": {
                            sh """
                            docker build -t ${DOCKER_USER}/${service}:latest -f ./src/${service}/Dockerfile ./src/${service} && \
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
                    SERVER_MAPPING.each { service, privateIP ->
                        parallelDeploy["Deploy ${service}"] = {
                            withCredentials([sshUserPrivateKey(credentialsId: 'bastion-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                                sh """
                                ssh -i $SSH_KEY -o StrictHostKeyChecking=no ${BASTION_HOST} "
                                    ssh -i ~/.ssh/id_rsa ubuntu@${privateIP} '
                                        docker pull ${DOCKER_USER}/${service}:latest && \
                                        if docker ps -q --filter name=${service}; then docker stop ${service} && docker rm ${service}; fi && \
                                        docker run -d --name ${service} -p ${PORT_MAPPING[service]} ${DOCKER_USER}/${service}:latest
                                    '
                                "
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
