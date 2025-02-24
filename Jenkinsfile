pipeline {
    agent any

    parameters {
        choice(name: 'SERVICE', choices: ['all', 'api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'], description: '배포할 서비스를 선택하세요.')
    }

    environment {
        GIT_REPO = "https://github.com/PDA-5th-Team5/BE.git"
        REPO_DIR = "BE"
        DOCKER_USER = "grrrrr1123"
        PRODUCTION_SERVER = "ubuntu@43.200.225.24"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('DockerHub Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh "echo ${PASSWORD} | docker login -u ${USERNAME} --password-stdin"
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def services = ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service']

                    if (params.SERVICE == 'all') {
                        parallel services.collectEntries { service ->
                            ["Build & Push ${service}" : {
                                sh """
                                docker build -t ${DOCKER_USER}/${service}:latest ${REPO_DIR}/${service}
                                docker push ${DOCKER_USER}/${service}:latest
                                """
                            }]
                        }
                    } else {
                        sh """
                        docker build -t ${DOCKER_USER}/${params.SERVICE}:latest ${REPO_DIR}/${params.SERVICE}
                        docker push ${DOCKER_USER}/${params.SERVICE}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to Server') {
            steps {
                script {
                    def services = ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service']
                    def portMapping = [
                        "api-gateway": "8081:8081",
                        "eureka-server": "8761:8761",
                        "util-service": "8082:8082",
                        "user-service": "8083:8083",
                        "stock-service": "8084:8084",
                        "snowflake-service": "8085:8085"
                    ]

                    if (params.SERVICE == 'all') {
                        parallel services.collectEntries { service ->
                            ["Deploy ${service}" : {
                                sh """
                                ssh ${PRODUCTION_SERVER} "
                                    docker pull ${DOCKER_USER}/${service}:latest && \
                                    if docker ps -q --filter name=${service}; then docker stop ${service} && docker rm ${service}; fi && \
                                    docker run -d --name ${service} -p ${portMapping[service]} ${DOCKER_USER}/${service}:latest
                                "
                                """
                            }]
                        }
                    } else {
                        sh """
                        ssh ${PRODUCTION_SERVER} "
                            docker pull ${DOCKER_USER}/${params.SERVICE}:latest && \
                            if docker ps -q --filter name=${params.SERVICE}; then docker stop ${params.SERVICE} && docker rm ${params.SERVICE}; fi && \
                            docker run -d --name ${params.SERVICE} -p ${portMapping[params.SERVICE]} ${DOCKER_USER}/${params.SERVICE}:latest
                        "
                        """
                    }
                }
            }
        }
    }
}
