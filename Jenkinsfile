pipeline {
    agent any

    environment {
        APP_REPO_URL     = 'https://github.com/OT-MICROSERVICES/salary-api.git'
        APP_BRANCH       = 'main'
        REPORT_NAME      = 'salary-api-cred-report.json'
        GITLEAKS_VERSION = '8.18.2'
    }

    stages {
        stage('Checkout Target Code') {
            steps {
                echo "Cloning salary-api repository from ${env.APP_REPO_URL}..."
                
                // Clone salary-api into a dedicated directory
                dir('salary-api') {
                    git branch: env.APP_BRANCH,
                        url: env.APP_REPO_URL
                }
            }
        }

        stage('Credential Scanning') {
            steps {
                echo 'Running Gitleaks Credential Scan...'
                sh """
                    # 1. Download standalone Gitleaks binary if not already cached
                    if [ ! -f ./gitleaks ]; then
                        echo "Downloading Gitleaks v${GITLEAKS_VERSION}..."
                        curl -sSL "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz" | tar -xz gitleaks
                        chmod +x ./gitleaks
                    fi

                    # 2. Allowlist the sample AWS documentation token in README.md (line 91)
                    echo "b5f56681961eba0f07c1b4bb23d463b117b32e22:README.md:aws-access-token:91" > salary-api/.gitleaksignore

                    # 3. Execute Gitleaks scanner
                    ./gitleaks detect \
                      --source="salary-api" \
                      --gitleaks-ignore-path="salary-api/.gitleaksignore" \
                      --verbose \
                      --report-path="salary-api/${env.REPORT_NAME}"
                """
            }
        }
    }

    post {
        always {
            // Save the report under Jenkins build artifacts
            archiveArtifacts artifacts: "salary-api/${env.REPORT_NAME}", allowEmptyArchive: true
            echo "Report generated and archived: salary-api/${env.REPORT_NAME}"
        }
        success {
            echo "CREDENTIAL SCAN PASSED: No unauthorized secrets detected!"
        }
        failure {
            echo "CREDENTIAL SCAN FAILED: Unauthorized credentials found!"
        }
    }
}
