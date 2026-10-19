        stage('Credential Scanning') {
            steps {
                echo 'Running Gitleaks Credential Scan on salary-api...'
                sh """
                    # 1. Download Gitleaks binary if not present
                    if [ ! -f ./gitleaks ]; then
                        echo "Downloading Gitleaks v${GITLEAKS_VERSION}..."
                        curl -sSL "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz" | tar -xz gitleaks
                        chmod +x ./gitleaks
                    fi

                    # 2. Add the known sample README key to .gitleaksignore inside salary-api
                    echo "b5f56681961eba0f07c1b4bb23d463b117b32e22:README.md:aws-access-token:91" > salary-api/.gitleaksignore

                    # 3. Run scan with the ignore file
                    ./gitleaks detect \
                      --source="salary-api" \
                      --gitleaks-ignore-path="salary-api/.gitleaksignore" \
                      --verbose \
                      --report-path="salary-api/${env.REPORT_NAME}"
                """
            }
        }
