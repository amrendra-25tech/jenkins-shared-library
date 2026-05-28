def call(Map config = [:]) {
    // 1. Extract inputs with sensible defaults for Grafana
    def slackChannel   = config.get('SLACK_CHANNEL_NAME', 'monitoring-alerts')
    def environment    = config.get('ENVIRONMENT', 'prod')
    def codeBasePath   = config.get('CODE_BASE_PATH', "ansible/grafana/${environment}")
    def actionMessage  = config.get('ACTION_MESSAGE', "Grafana stack update in ${environment}")
    def keepApproval   = config.get('KEEP_APPROVAL_STAGE', true)

    pipeline {
        agent any

        stages {
            // STEP 1: Clone 
            stage('Clone Core Repository') {
                steps {
                    cleanWs()
                    checkout scm
                    echo "Grafana deployment assets loaded from path: ${codeBasePath}"
                }
            }

            // STEP 2: User Approval 
            stage('User Approval Gate') {
                when {
                    expression { return keepApproval == true || keepApproval == 'true' }
                }
                steps {
                    script {
                        slackSend(channel: slackChannel, color: '#FF9900', message: "⏳ PENDING APPROVAL: ${actionMessage}. Confirming deployment to ${environment}.")
                        
                        timeout(time: 10, unit: 'MINUTES') {
                            input message: "Deploy Grafana changes to ${environment}?", ok: 'Approve & Push'
                        }
                    }
                }
            }

            // STEP 3: Playbook Execution
            stage('Grafana Playbook Execution') {
                steps {
                    script {
                        echo "Executing Ansible playbook for Grafana [Environment: ${environment}]..."
                        
                        // Dynamically runs your specific Grafana setup using the provided path
                        ansiblePlaybook(
                            playbook: "${codeBasePath}/grafana.yml",
                            inventory: "${codeBasePath}/hosts.ini",
                            colorized: true,
                            extraVars: [
                                env_target: environment
                            ]
                        )
                    }
                }
            }
        }

        // STEP 4: Notification
        post {
            success {
                slackSend(channel: slackChannel, color: '#36A64F', message: "✅ SUCCESS: ${actionMessage} has been deployed successfully. Dashboards are live!")
            }
            failure {
                slackSend(channel: slackChannel, color: '#D00000', message: "❌ FAILURE: ${actionMessage} failed during the Ansible run. Check the Jenkins console output immediately.")
            }
        }
    }
}
