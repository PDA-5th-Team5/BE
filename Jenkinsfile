pipeline {
    agent any

    environment {
        DOCKER_HUB_USERNAME = 'qpwisu'
        DOCKER_HUB_PASSWORD = credentials('docker-hub-password')  // Jenkins에서 저장한 Docker Hub 패스워드
        ENV_FILE = "~/env/common.env"
    }

    triggers {
        githubPush()  // GitHub Webhook을 감지하여 실행
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    checkout scm  // GitHub에서 최신 코드 체크아웃
                    sh "git pull origin develop"  // 최신 코드 반영
                }
            }
        }

        stage('Detect Changed Modules') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim().split("\n")
                    def affectedModules = []

                    // util-service가 변경되면 모든 관련 서비스 재빌드
                    if (changedFiles.any { it.startsWith("util-service/") }) {
                        affectedModules.addAll(["api-gateway", "eureka-server", "stock-service", "user-service", "portfolio-service"])
                    }

                    if (changedFiles.any { it.startsWith("api-gateway/") }) {
                        affectedModules.add("api-gateway")
                    }
                    if (changedFiles.any { it.startsWith("eureka-server/") }) {
                        affectedModules.add("eureka-server")
                    }
                    if (changedFiles.any { it.startsWith("stock-service/") }) {
                        affectedModules.add("stock-service")
                    }
                    if (changedFiles.any { it.startsWith("user-service/") }) {
                        affectedModules.add("user-service")
                    }
                    if (changedFiles.any { it.startsWith("portfolio-service/") }) {
                        affectedModules.add("portfolio-service")
                    }

                    env.AFFECTED_MODULES = affectedModules.unique().join(" ")
                }
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    env.AFFECTED_MODULES.split(" ").each { module ->
                        sh """
                        cd ${module}
                        ./gradlew clean build  # 전체 클린 빌드 수행
                        docker build --build-arg SERVER_PORT=${module.toUpperCase()}_SERVER_PORT \
                                     --build-arg SPRING_DATASOURCE_URL=${module.toUpperCase()}_SPRING_DATASOURCE_URL \
                                     --build-arg SPRING_DATASOURCE_USERNAME=${module.toUpperCase()}_SPRING_DATASOURCE_USERNAME \
                                     --build-arg SPRING_DATASOURCE_PASSWORD=${module.toUpperCase()}_SPRING_DATASOURCE_PASSWORD \
                                     -t qpwisu/${module}:latest .
                        docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_PASSWORD}
                        docker push qpwisu/${module}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    env.AFFECTED_MODULES.split(" ").each { module ->
                        def targetServer = ""
                        if (module == "api-gateway" || module == "eureka-server") {
                            targetServer = "api-gateway-eureka"
                        } else if (module == "stock-service") {
                            targetServer = "stock"
                        } else if (module == "user-service") {
                            targetServer = "user"
                        } else if (module == "portfolio-service") {
                            targetServer = "portfolio"
                        }

                        sh """
                        # .env 파일 복사 후 실행
                        scp ${ENV_FILE} ubuntu@${targetServer}:/home/ubuntu/common.env
                        ssh ${targetServer} 'cd /home/ubuntu && docker-compose pull && docker-compose --env-file /home/ubuntu/common.env up -d ${module}'
                        """
                    }
                }
            }
        }
    }
}