def call () {
    stage('Deploy Docker') {
        def branch = env.BRANCH_NAME
        if (branch == 'release') {
            withCredentials([usernamePassword(credentialsId: 'nexus-credential', passwordVariable: 'PSW', usernameVariable: 'USER')]) {
                sshagent(["${env.PRIVKEY_SERV_VM}"]) {
                    sh "ssh -o StrictHostKeyChecking=no root@${env.URL_SERV_VM} docker image prune -a -f"
                    sh "ssh root@${env.URL_SERV_VM} 'docker commit web-app ${env.NEXUS_URL_DOCKER}/web:stable'"
                    sh "ssh root@${env.URL_SERV_VM} 'echo ${PSW} | docker login -u ${USER} --password-stdin ${env.NEXUS_URL_DOCKER}'"
                    sh "scp ./docker-compose2.yaml root@${env.URL_SERV_VM}:/root/docker-compose.yaml"
                    sh "ssh root@${env.URL_SERV_VM} 'VERSION=${env.NEXUS_URL_DOCKER}/web:${env.VERSION}-${env.BRANCH_NAME} docker-compose up -d --force-recreate'"
                }  
            }
        }
        if (branch.startsWith('uat')) {
            def GIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
            withCredentials([usernamePassword(credentialsId: 'nexus-credential', passwordVariable: 'PSW', usernameVariable: 'USER')]) {
                sshagent(["${env.PRIVKEY_SERV_VM}"]) {
                    sh "ssh -o StrictHostKeyChecking=no root@${env.URL_SERV_VM} docker image prune -a -f"
                    sh "ssh root@${env.URL_SERV_VM} 'docker commit web-app ${env.NEXUS_URL_DOCKER}/web:stable'"
                    sh "ssh root@${env.URL_SERV_VM} 'echo ${PSW} | docker login -u ${USER} --password-stdin ${env.NEXUS_URL_DOCKER}'"
                    sh "scp ./docker-compose2.yaml root@${env.URL_SERV_VM}:/root/docker-compose.yaml"
                    sh "ssh root@${env.URL_SERV_VM} 'VERSION=${env.NEXUS_URL_DOCKER}/web:${env.VERSION}-${env.BRANCH_NAME}-${GIT_HASH} docker-compose up -d --force-recreate'"
                }  
            }
        }
    }
}