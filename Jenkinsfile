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
                    def envVars = sh(script: "cat ${ENV_FILE} | grep -v '^#'", returnStdout: true).trim().split("\n")
                    def envList = envVars.collect { it.replace("\"", "").trim() }.findAll { it.contains("=") }
                    def formattedEnvVars = envList.collect { "export " + it }
                    writeFile(file: 'env_export.sh', text: formattedEnvVars.join("\n"))
                    sh "chmod +x env_export.sh"
                    echo "✅ Environment variables loaded successfully."
                }
            }
        }

        stage('Detect Changed Modules') {
            steps {
                script {
                    def affectedModules = []

                    if (params.FULL_BUILD) {
                        affectedModules = ["eureka-server","api-gateway", "stock-service", "user-service", "portfolio-service"]
                    } else {
                        def changedFiles = sh(script: "git diff --name-only HEAD^ HEAD", returnStdout: true).trim().split("\n")

                        if (changedFiles.any { it.startsWith("util-service/") }) {
                            affectedModules.addAll(["eureka-server", "api-gateway", "stock-service", "user-service", "portfolio-service"])
                        }
                        if (changedFiles.any { it.startsWith("eureka-server/") }) {
                            affectedModules.add("eureka-server")
                        }
                        if (changedFiles.any { it.startsWith("api-gateway/") }) {
                            affectedModules.add("api-gateway")
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
                            sh """
                            echo ">>> Building ${module}"
                            cd ${module} || exit 1
                            chmod +x ./gradlew
                            ./gradlew clean build -x test
                            docker build --build-arg SERVER_PORT=\${SERVER_PORT} -t qpwisu/${module}:latest .
                            docker push qpwisu/${module}:latest
                            docker image prune -a -f
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
                            targetServer = "api-gateway"
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
                        ssh ${targetServer} 'cd /home/ubuntu && docker-compose pull && docker-compose up -d ${module}'
                        docker image prune -a -f
                        """
//                         sh """
//                         scp ${ENV_FILE} ubuntu@${targetServer}:/home/ubuntu/common.env
//                         ssh ubuntu@${targetServer} 'cd /home/ubuntu && docker-compose pull && docker-compose --env-file /home/ubuntu/common.env up -d ${module}'
//                         """
                    }
                }
            }
        }
    }
}