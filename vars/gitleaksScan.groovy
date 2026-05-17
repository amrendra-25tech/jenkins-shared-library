def call() {

    sh '''
    gitleaks detect \
    --source . \
    --report-format json \
    --report-path gitleaks-report.json || true
    '''

}
