pipeline {
    agent any

    environment {
        APP_REPO_URL = 'https://github.com/OT-MICROSERVICES/salary-api.git'
        APP_BRANCH   = 'main'
        REPORT_NAME  = 'salary-api-cred-report.json'
    }

    stages {
        stage('Checkout Salary API Repo') {
            steps {
                echo "Cloning target repository: ${env.APP_REPO_URL} (${env.APP_BRANCH})..."
                
                // Clones the salary-api repo into a dedicated 'salary-api' folder
                dir('salary-api') {
                    git branch: env.APP_BRANCH,
                        url: env.APP_REPO_URL
                }
            }
        }

        stage('Credential Scanning') {
            steps {
                echo 'Running Gitleaks Credential Scan on salary-api...'
                
                // Mounts the salary-api folder into Gitleaks container to scan it
                sh """
                    docker run --rm \
                      -v "\$(pwd)/salary-api":/repo \
                      zricethezav/gitleaks:latest detect \
                      --source="/repo" \
                      --verbose \
                      --report-path="/repo/${env.REPORT_NAME}"
                """
            }
        }
    }

    post {
        always {
            // Archives the scan report in Jenkins for reviewers to inspect
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
