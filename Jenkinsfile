pipeline {
    agent any

    parameters {
        booleanParam(name: 'FULL_BUILD', defaultValue: false, description: '전체 모듈을 빌드할지 여부')
    }

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
                    def affectedModules = []

                    if (params.FULL_BUILD) {
                        // 전체 빌드 실행 (수동 트리거)
                        affectedModules = ["api-gateway", "eureka-server", "stock-service", "user-service", "portfolio-service"]
                    } else {
                        // 변경된 파일 확인 후 감지
                        def changedFiles = sh(script: "git diff --name-only HEAD^ HEAD", returnStdout: true).trim().split("\n")

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
                    }

                    // 중복 제거 후 환경 변수 설정
                    env.AFFECTED_MODULES = affectedModules.unique().join(" ")
                    if (env.AFFECTED_MODULES.trim().isEmpty()) {
                        currentBuild.result = 'SUCCESS'
                        echo "✅ 변경된 모듈이 없어 빌드 및 배포를 건너뜁니다."
                        return
                    }
                }
            }
        }

        stage('Build & Push Docker Images') {
            when {
                expression { return !env.AFFECTED_MODULES.trim().isEmpty() }  // 변경 사항이 있을 때만 실행
            }
            steps {
                script {
                    echo ${DOCKER_HUB_PASSWORD} | docker login -u ${DOCKER_HUB_USERNAME} --password-stdin

                    env.AFFECTED_MODULES.split(" ").each { module ->
                        def buildArgs = ""

                        if (module == "api-gateway") {
                            buildArgs = "--build-arg SERVER_PORT=\\${APIGATEWAY_SERVER_PORT} " +
                                        "--build-arg EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=\\${APIGATEWAY_EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}"
                        } else if (module == "eureka-server") {
                            buildArgs = "--build-arg SERVER_PORT=\\${EUREKA_SERVER_PORT} " +
                                        "--build-arg EUREKA_INSTANCE_HOSTNAME=\\${EUREKA_INSTANCE_HOSTNAME} " +
                                        "--build-arg ENABLE_SELF_PRESERVATION=\\${EUREKA_SERVER_ENABLE_SELF_PRESERVATION}"
                        } else if (module == "stock-service") {
                            buildArgs = "--build-arg SERVER_PORT=\\${STOCK_SERVER_PORT} " +
                                        "--build-arg SPRING_DATASOURCE_URL=\\${STOCK_SPRING_DATASOURCE_URL} " +
                                        "--build-arg SPRING_DATASOURCE_USERNAME=\\${STOCK_SPRING_DATASOURCE_USERNAME} " +
                                        "--build-arg SPRING_DATASOURCE_PASSWORD=\\${STOCK_SPRING_DATASOURCE_PASSWORD}"
                        } else if (module == "user-service") {
                            buildArgs = "--build-arg SERVER_PORT=\\${USER_SERVER_PORT} " +
                                        "--build-arg SPRING_DATASOURCE_URL=\\${USER_SPRING_DATASOURCE_URL} " +
                                        "--build-arg SPRING_DATASOURCE_USERNAME=\\${USER_SPRING_DATASOURCE_USERNAME} " +
                                        "--build-arg SPRING_DATASOURCE_PASSWORD=\\${USER_SPRING_DATASOURCE_PASSWORD}"
                        } else if (module == "portfolio-service") {
                            buildArgs = "--build-arg SERVER_PORT=\\${PORTFOLIO_SERVER_PORT} " +
                                        "--build-arg SPRING_DATASOURCE_URL=\\${PORTFOLIO_SPRING_DATASOURCE_URL} " +
                                        "--build-arg SPRING_DATASOURCE_USERNAME=\\${PORTFOLIO_SPRING_DATASOURCE_USERNAME} " +
                                        "--build-arg SPRING_DATASOURCE_PASSWORD=\\${PORTFOLIO_SPRING_DATASOURCE_PASSWORD}"
                        }

                        sh """
                        echo ">>> Building ${module}"
                        cd ${module} || exit 1  # ${module} 폴더로 이동
                        chmod +x ./gradlew  # 실행 권한 부여
                        ./gradlew clean build  # 클린 빌드 수행
                        docker build ${buildArgs} -t qpwisu/${module}:latest .
                        docker push qpwisu/${module}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            when {
                expression { return !env.AFFECTED_MODULES.trim().isEmpty() }  // 변경 사항이 있을 때만 실행
            }
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