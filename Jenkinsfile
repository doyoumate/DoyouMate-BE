pipeline {
    agent {
        kubernetes {
            yaml(
                '''
                apiVersion: v1
                kind: Pod
                spec:
                  serviceAccountName: jenkins
                  containers:
                  - name: yq
                    image: mikefarah/yq
                    tty: true
                    command: ['sleep']
                    args: ['infinity']
                '''
            )
        }
    }

    stages {
        stage('Git clone') {
            steps {
                git(
                    url: 'https://github.com/doyoumate/DoyouMate-BE.git',
                    branch: 'dev',
                    credentialsId: 'git'
                )
                script {
                    env.tag = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                }
            }
        }

//         stage('Test and create document') {
//             steps {
//                 sh 'mkdir ./api/src/main/resources/static'
//                 sh 'mkdir ./api/src/main/resources/static/docs'
//
//                 sh './gradlew test'
//             }
//         }

        stage('Build and deploy image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'username', passwordVariable: 'password')]) {
                        sh('./gradlew :api:jib -Djib.to.tags=$tag -Djib.to.auth.username=$username -Djib.to.auth.password=$password')
                        sh('./gradlew :batch:jib -Djib.to.tags=$tag -Djib.to.auth.username=$username -Djib.to.auth.password=$password')
                    }
                }
            }
        }

        stage('Edit manifests') {
            steps {
                container('yq') {
                    script {
                        dir('helm') {
                            git(
                                url: 'https://github.com/doyoumate/Helm.git',
                                branch: 'dev',
                                credentialsId: 'git'
                            )
                            sh("yq e -i -P '.controller.api.image.tag = \"$tag\"' values.yaml")
                            sh("yq e -i -P '.controller.batch.image.tag = \"$tag\"' values.yaml")
                        }
                    }
                }

                script {
                    dir('helm') {
                        withCredentials([gitUsernamePassword(credentialsId: 'git')]) {
                            sh('git config --global user.email "<>"')
                            sh('git config --global user.name "jenkins"')
                            sh('git add .')
                            sh('git commit -m "Feat: Update image tags($tag)"')
                            sh('git push origin dev')
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            withCredentials([string(credentialsId: 'discord', variable: 'discord_webhook')]) {
                discordSend(
                    title: "$JOB_NAME: ${currentBuild.displayName} 성공",
                    description: """
                        Image tag: $tag
                        Execution time: ${currentBuild.duration / 1000}s
                    """,
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: discord_webhook
                )
            }
        }

        failure {
            withCredentials([string(credentialsId: 'discord', variable: 'discord_webhook')]) {
                discordSend(
                    title: "$JOB_NAME: ${currentBuild.displayName} 실패",
                    description: """
                        Image tag: $tag
                        Execution time: ${currentBuild.duration / 1000}s
                    """,
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: discord_webhook
                )
            }
        }
    }
}
