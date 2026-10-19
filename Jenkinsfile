pipeline {
    agent any

    environment {
        APP_REPO_URL     = 'https://github.com/OT-MICROSERVICES/salary-api.git'
        APP_BRANCH       = 'main'
        REPORT_NAME      = 'salary-api-cred-report.json'
        GITLEAKS_VERSION = '8.18.2'
    }

    stages {
        stage('Checkout Salary API Repo') {
            steps {
                echo "Cloning target repository: ${env.APP_REPO_URL} (${env.APP_BRANCH})..."
                
                dir('salary-api') {
                    git branch: env.APP_BRANCH,
                        url: env.APP_REPO_URL
                }
            }
        }

        stage('Credential Scanning') {
            steps {
                echo 'Running Gitleaks Credential Scan on salary-api...'
                sh """
                    # 1. Download standalone Gitleaks binary if not present
                    if [ ! -f ./gitleaks ]; then
                        echo "Downloading Gitleaks v${GITLEAKS_VERSION}..."
                        curl -sSL "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz" | tar -xz gitleaks
                        chmod +x ./gitleaks
                    fi

                    # 2. Run scan directly on salary-api folder
                    ./gitleaks detect \
                      --source="salary-api" \
                      --verbose \
                      --report-path="salary-api/${env.REPORT_NAME}"
                """
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: "salary-api/${env.REPORT_NAME}", allowEmptyArchive: true
            echo "Archived report: salary-api/${env.REPORT_NAME}"
        }
        success {
            echo "SUCCESS: No hardcoded credentials detected in salary-api!"
        }
        failure {
            echo "FAILED: Secrets/Credentials found in salary-api! Review the report."
        }
    }
}
