pipeline {
    agent any

    parameters {
        booleanParam(name: 'FULL_BUILD', defaultValue: false, description: '전체 모듈을 빌드할지 여부')
    }

    environment {
        DOCKER_HUB_USERNAME = 'qpwisu'
        ENV_FILE = "/home/ubuntu/common.env" // EC2에 저장된 환경 변수 파일 경로
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    checkout scm
                }
            }
        }

        stage('Load Environment Variables') {
            steps {
                script {
                    def envVars = sh(script: "cat ${ENV_FILE} | grep -v '^#' | xargs", returnStdout: true).trim()
                    def envList = envVars.split(" ")
                    envList.each { envVar ->
                        def keyValue = envVar.split("=")
                        if (keyValue.size() == 2) {
                            env.put(keyValue[0], keyValue[1])  // putAt 대신 put 사용
                        }
                    }
                    echo "✅ Environment variables loaded successfully."
                }
            }
        }

        stage('Detect Changed Modules') {
            steps {
                script {
                    def affectedModules = []

                    if (params.FULL_BUILD) {
                        affectedModules = ["api-gateway", "eureka-server", "stock-service", "user-service", "portfolio-service"]
                    } else {
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

                    env.put("AFFECTED_MODULES", affectedModules.unique().join(" "))  // putAt 대신 put 사용
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
                expression { return !env.AFFECTED_MODULES.trim().isEmpty() }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-password', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                    script {
                        sh "echo \${DOCKER_HUB_PASSWORD} | docker login -u \${DOCKER_HUB_USERNAME} --password-stdin"

                        env.AFFECTED_MODULES.split(" ").each { module ->
                            def buildArgs = ""

                            if (module == "api-gateway") {
                                buildArgs = "--build-arg SERVER_PORT=${env.APIGATEWAY_SERVER_PORT} " +
                                            "--build-arg EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=${env.APIGATEWAY_EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}"
                            } else if (module == "eureka-server") {
                                buildArgs = "--build-arg SERVER_PORT=${env.EUREKA_SERVER_PORT} " +
                                            "--build-arg EUREKA_INSTANCE_HOSTNAME=${env.EUREKA_INSTANCE_HOSTNAME} " +
                                            "--build-arg ENABLE_SELF_PRESERVATION=${env.EUREKA_SERVER_ENABLE_SELF_PRESERVATION}"
                            } else if (module == "stock-service") {
                                buildArgs = "--build-arg SERVER_PORT=${env.STOCK_SERVER_PORT} " +
                                            "--build-arg SPRING_DATASOURCE_URL=${env.STOCK_SPRING_DATASOURCE_URL} " +
                                            "--build-arg SPRING_DATASOURCE_USERNAME=${env.STOCK_SPRING_DATASOURCE_USERNAME} " +
                                            "--build-arg SPRING_DATASOURCE_PASSWORD=${env.STOCK_SPRING_DATASOURCE_PASSWORD}"
                            } else if (module == "user-service") {
                                buildArgs = "--build-arg SERVER_PORT=${env.USER_SERVER_PORT} " +
                                            "--build-arg SPRING_DATASOURCE_URL=${env.USER_SPRING_DATASOURCE_URL} " +
                                            "--build-arg SPRING_DATASOURCE_USERNAME=${env.USER_SPRING_DATASOURCE_USERNAME} " +
                                            "--build-arg SPRING_DATASOURCE_PASSWORD=${env.USER_SPRING_DATASOURCE_PASSWORD}"
                            } else if (module == "portfolio-service") {
                                buildArgs = "--build-arg SERVER_PORT=${env.PORTFOLIO_SERVER_PORT} " +
                                            "--build-arg SPRING_DATASOURCE_URL=${env.PORTFOLIO_SPRING_DATASOURCE_URL} " +
                                            "--build-arg SPRING_DATASOURCE_USERNAME=${env.PORTFOLIO_SPRING_DATASOURCE_USERNAME} " +
                                            "--build-arg SPRING_DATASOURCE_PASSWORD=${env.PORTFOLIO_SPRING_DATASOURCE_PASSWORD}"
                            }

                            sh """
                            echo ">>> Building ${module}"
                            cd ${module} || exit 1
                            chmod +x ./gradlew
                            ./gradlew clean build
                            docker build ${buildArgs} -t qpwisu/${module}:latest .
                            docker push qpwisu/${module}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            when {
                expression { return !env.AFFECTED_MODULES.trim().isEmpty() }
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