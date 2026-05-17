def call() {

    archiveArtifacts artifacts: 'gitleaks-report.json',
    allowEmptyArchive: true,
    fingerprint: true

}
