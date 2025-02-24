pipeline {
    agent any

    parameters {
        choice(name: 'SERVICE', choices: ['all', 'api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'], description: '배포할 서비스를 선택하세요.')
    }

    environment {
        GIT_REPO = "https://github.com/PDA-5th-Team5/BE.git"
        DOCKER_USER = "grrrrr1123"
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
                    if (params.SERVICE == 'all') {
                        def parallelJarBuild = [:]
                        ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'].each { service ->
                            parallelJarBuild["Build Jar ${service}"] = {
                                sh """
                                cd /var/lib/jenkins/workspace/snowper-pipeline/src/${service} && \
                                ./gradlew clean build
                                """
                            }
                        }
                        parallel parallelJarBuild
                    } else {
                        sh """
                        cd /var/lib/jenkins/workspace/snowper-pipeline/src/${params.SERVICE} && \
                        ./gradlew clean build
                        """
                    }
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    if (params.SERVICE == 'all') {
                        def parallelStages = [:]
                        ['api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'].each { service ->
                            parallelStages["Build & Push ${service}"] = {
                                sh """
                                cd /var/lib/jenkins/workspace/snowper-pipeline/ && \
                                docker build -t ${DOCKER_USER}/${service}:latest -f ./src/${service}/Dockerfile ./src/${service} && \
                                docker push ${DOCKER_USER}/${service}:latest
                                """
                            }
                        }
                        parallel parallelStages
                    } else {
                        sh """
                        cd /var/lib/jenkins/workspace/snowper-pipeline/ && \
                        docker build -t ${DOCKER_USER}/${params.SERVICE}:latest -f ./src/${params.SERVICE}/Dockerfile ./src/${params.SERVICE} && \
                        docker push ${DOCKER_USER}/${params.SERVICE}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to Application Servers') {
            steps {
                script {
                    def SERVER_MAPPING = [
                        "api-gateway" : "apigateway",
                        "eureka-server" : "apigateway",
                        "util-service" : "apigateway",
                        "stock-service" : "stock",
                        "snowflake-service" : "snowflake",
                        "user-service" : "user"
                    ]

                    def PORT_MAPPING = [
                        "api-gateway": "8081:8081",
                        "eureka-server": "8761:8761",
                        "util-service": "8082:8082",
                        "user-service": "8083:8083",
                        "stock-service": "8084:8084",
                        "snowflake-service": "8085:8085"
                    ]

                    if (params.SERVICE == 'all') {
                        def parallelDeploy = [:]
                        SERVER_MAPPING.each { service, server ->
                            parallelDeploy["Deploy ${service}"] = {
                                sh """
                                ssh ${server} "
                                    docker pull ${DOCKER_USER}/${service}:latest && \
                                    if docker ps -q --filter name=${service}; then docker stop ${service} && docker rm ${service}; fi && \
                                    docker run -d --name ${service} -p ${PORT_MAPPING[service]} ${DOCKER_USER}/${service}:latest
                                "
                                """
                            }
                        }
                        parallel parallelDeploy
                    } else {
                        def targetServer = SERVER_MAPPING[params.SERVICE]
                        sh """
                        ssh ${targetServer} "
                            docker pull ${DOCKER_USER}/${params.SERVICE}:latest && \
                            if docker ps -q --filter name=${params.SERVICE}; then docker stop ${params.SERVICE} && docker rm ${params.SERVICE}; fi && \
                            docker run -d --name ${params.SERVICE} -p ${PORT_MAPPING[params.SERVICE]} ${DOCKER_USER}/${params.SERVICE}:latest
                        "
                        """
                    }
                }
            }
        }
    }
}
