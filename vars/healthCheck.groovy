def call () {
    stage('Health Check') {
        sleep(30)
        def response = httpRequest url: "http://${URL_SERV_VM}"
        println("Status: ${response.status}")
        sleep(10)
        def response2 = httpRequest url: "http://${URL_SERV_VM}"
        println("Status: ${response2.status}")
    }
}