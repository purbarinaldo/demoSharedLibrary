def call(Map config) {

    pipeline {
        agent any

        stages {
            stage('Checkout') {
                steps {
                    checkout([$class: 'GitSCM',
                            branches: [[name: '*/main']],
                            userRemoteConfigs: [[
                                url: 'git@github.com:purbarinaldo/demoSharedLibrary.git',
                                credentialsId: 'githubCred'
                            ]]
                    ])
                }
            }

            stage('Build') {
                steps {
                    sh config.buildCommand
                }
            }

            stage('Test') {
                steps {
                    sh config.testCommand
                }
            }

            stage('SonarQube') {
                steps {
                    withSonarQubeEnv('SonarQube') {
                        sh config.sonarCommand
                    }
                }
            }

            stage('Deploy') {
                when {
                    expression { config.deploy }
                }
                steps {
                    sh config.deployCommand
                }
            }
        }
    }
}
