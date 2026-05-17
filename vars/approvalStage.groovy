def call() {

    stage('Approval Before Build') {

        input message: 'Do you want to continue with Maven Build and Test?',
              ok: 'Proceed'

    }
}
