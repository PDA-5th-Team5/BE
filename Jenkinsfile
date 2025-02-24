pipeline {
    agent any

    parameters {
        choice(name: 'SERVICE', choices: ['all', 'api-gateway', 'eureka-server', 'util-service', 'user-service', 'stock-service', 'snowflake-service'], description: '배포할 서비스를 선택하세요.')
    }

    environment {
        GIT_REPO = "https://github.com/PDA-5th-Team5/BE.git"
        DOCKER_USER = "grrrrr1123"
        PRODUCTION_SERVER = "production"  // SSH Config에서 설정한 alias 사용
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Set Variables') {
            steps {
                script {
                    // 서버별 서비스 매핑 (환경 변수 X, script 블록에서 선언해야 함)
                    def SERVER_MAPPING = [
                        "api-gateway" : "apigateway",
                        "eureka-server" : "apigateway",
                        "util-service" : "apigateway",
                        "stock-service" : "stock",
                        "snowflake-service" : "snowflake",
                        "user-service" : "user"
                    ]

                    // 포트 매핑 (환경 변수 X, script 블록에서 선언해야 함)
                    def PORT_MAPPING = [
                        "api-gateway": "8081:8081",
                        "eureka-server": "8761:8761",
                        "util-service": "8082:8082",
                        "user-service": "8083:8083",
                        "stock-service": "8084:8084",
                        "snowflake-service": "8085:8085"
                    ]

                    // 공유 변수로 사용 가능하도록 this.pipeline에 저장
                    this.SERVER_MAPPING = SERVER_MAPPING
                    this.PORT_MAPPING = PORT_MAPPING
                }
            }
        }

        stage('DockerHub Login on Production Server') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh """
                    ssh ${PRODUCTION_SERVER} "
                        echo ${PASSWORD} | docker login -u ${USERNAME} --password-stdin
                    "
                    """
                }
            }
        }

        stage('Build & Push Docker Image on Production Server') {
            steps {
                script {
                    if (params.SERVICE == 'all') {
                        parallel SERVER_MAPPING.collectEntries { service, server ->
                            ["Build & Push ${service}" : {
                                sh """
                                ssh ${PRODUCTION_SERVER} "
                                    cd /var/lib/jenkins/workspace/snowper-pipeline/ && \
                                    docker build -t ${DOCKER_USER}/${service}:latest -f ./src/${service}/Dockerfile ./src/${service} && \
                                    docker push ${DOCKER_USER}/${service}:latest
                                "
                                """
                            }]
                        }
                    } else {
                        sh """
                        ssh ${PRODUCTION_SERVER} "
                            cd /var/lib/jenkins/workspace/snowper-pipeline/ && \
                            docker build -t ${DOCKER_USER}/${params.SERVICE}:latest -f ./src/${params.SERVICE}/Dockerfile ./src/${params.SERVICE} && \
                            docker push ${DOCKER_USER}/${params.SERVICE}:latest
                        "
                        """
                    }
                }
            }
        }

        stage('Deploy to Application Servers') {
            steps {
                script {
                    if (params.SERVICE == 'all') {
                        parallel SERVER_MAPPING.collectEntries { service, server ->
                            ["Deploy ${service}" : {
                                sh """
                                ssh ${server} "
                                    docker pull ${DOCKER_USER}/${service}:latest && \
                                    if docker ps -q --filter name=${service}; then docker stop ${service} && docker rm ${service}; fi && \
                                    docker run -d --name ${service} -p ${PORT_MAPPING[service]} ${DOCKER_USER}/${service}:latest
                                "
                                """
                            }]
                        }
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
