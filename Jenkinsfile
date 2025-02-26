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
                    def envVars = sh(script: "set -o allexport; source ${ENV_FILE}; set +o allexport; env | grep -E 'APIGATEWAY|EUREKA|STOCK|USER|PORTFOLIO'", returnStdout: true).trim()
                    def envList = envVars.split("\n")
                    withEnv(envList) {
                        echo "✅ Environment variables loaded successfully."
                    }
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

                        def moduleMappings = [
                            "util-service/"     : ["api-gateway", "eureka-server", "stock-service", "user-service", "portfolio-service"],
                            "api-gateway/"      : ["api-gateway"],
                            "eureka-server/"    : ["eureka-server"],
                            "stock-service/"    : ["stock-service"],
                            "user-service/"     : ["user-service"],
                            "portfolio-service/": ["portfolio-service"]
                        ]

                        moduleMappings.each { prefix, modules ->
                            if (changedFiles.any { it.startsWith(prefix) }) {
                                affectedModules.addAll(modules)
                            }
                        }
                    }

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
                expression { return !env.AFFECTED_MODULES.trim().isEmpty() }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-password', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                    script {
                        sh "echo \${DOCKER_HUB_PASSWORD} | docker login -u \${DOCKER_HUB_USERNAME} --password-stdin"

                        env.AFFECTED_MODULES.split(" ").each { module ->
                            def buildArgs = ""

                            def moduleArgs = [
                                "api-gateway": [
                                    "--build-arg SERVER_PORT=\${APIGATEWAY_SERVER_PORT}",
                                    "--build-arg EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=\${APIGATEWAY_EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}"
                                ],
                                "eureka-server": [
                                    "--build-arg SERVER_PORT=\${EUREKA_SERVER_PORT}",
                                    "--build-arg EUREKA_INSTANCE_HOSTNAME=\${EUREKA_INSTANCE_HOSTNAME}",
                                    "--build-arg ENABLE_SELF_PRESERVATION=\${EUREKA_SERVER_ENABLE_SELF_PRESERVATION}"
                                ],
                                "stock-service": [
                                    "--build-arg SERVER_PORT=\${STOCK_SERVER_PORT}",
                                    "--build-arg SPRING_DATASOURCE_URL=\${STOCK_SPRING_DATASOURCE_URL}",
                                    "--build-arg SPRING_DATASOURCE_USERNAME=\${STOCK_SPRING_DATASOURCE_USERNAME}",
                                    "--build-arg SPRING_DATASOURCE_PASSWORD=\${STOCK_SPRING_DATASOURCE_PASSWORD}"
                                ],
                                "user-service": [
                                    "--build-arg SERVER_PORT=\${USER_SERVER_PORT}",
                                    "--build-arg SPRING_DATASOURCE_URL=\${USER_SPRING_DATASOURCE_URL}",
                                    "--build-arg SPRING_DATASOURCE_USERNAME=\${USER_SPRING_DATASOURCE_USERNAME}",
                                    "--build-arg SPRING_DATASOURCE_PASSWORD=\${USER_SPRING_DATASOURCE_PASSWORD}"
                                ],
                                "portfolio-service": [
                                    "--build-arg SERVER_PORT=\${PORTFOLIO_SERVER_PORT}",
                                    "--build-arg SPRING_DATASOURCE_URL=\${PORTFOLIO_SPRING_DATASOURCE_URL}",
                                    "--build-arg SPRING_DATASOURCE_USERNAME=\${PORTFOLIO_SPRING_DATASOURCE_USERNAME}",
                                    "--build-arg SPRING_DATASOURCE_PASSWORD=\${PORTFOLIO_SPRING_DATASOURCE_PASSWORD}"
                                ]
                            ]

                            if (moduleArgs.containsKey(module)) {
                                buildArgs = moduleArgs[module].join(" ")
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
                        def targetServer = [
                            "api-gateway": "api-gateway-eureka",
                            "eureka-server": "api-gateway-eureka",
                            "stock-service": "stock",
                            "user-service": "user",
                            "portfolio-service": "portfolio"
                        ][module] ?: ""

                        if (targetServer) {
                            sh """
                            scp ${ENV_FILE} ubuntu@${targetServer}:/home/ubuntu/common.env
                            ssh ${targetServer} 'cd /home/ubuntu && docker-compose pull && docker-compose --env-file /home/ubuntu/common.env up -d ${module}'
                            """
                        }
                    }
                }
            }
        }
    }
}