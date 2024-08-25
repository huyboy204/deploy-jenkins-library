def call () {
    stage('Deploy artifact on VM') {
        def branch = env.BRANCH_NAME
        if (branch == 'release') {
            retry(2) {
                timeout(time: 3, unit: 'MINUTES') {
                    withCredentials([string(credentialsId: "${NEXUS_CRED}", variable: 'NEXUS_ACC')]) {
                        sshagent(["${PRIVKEY_SERV_VM}"]) {
                            sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} cp /opt/web-Spring.jar /opt/web-Sring2.jar"
                            sh "ssh root@${URL_SERV_VM} curl -v -u ${NEXUS_ACC} -o /opt/web-Spring.jar http://${NEXUS_URL}/repository/${NEXUS_PRO_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT_ID}/${env.BRANCH_NAME}/${env.VERSION}-${env.BRANCH_NAME}.jar"
                        }
                    }
                }
            }
        }

        if (branch.startsWith('uat')) {
            def GIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
            retry(2) {
                timeout(time: 3, unit: 'MINUTES') {
                    withCredentials([string(credentialsId: "${NEXUS_CRED}", variable: 'NEXUS_ACC')]) {
                        sshagent(["${PRIVKEY_SERV_VM}"]) {
                            sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} cp /opt/web-Spring.jar /opt/web-Sring2.jar"
                            sh "ssh root@${URL_SERV_VM} curl -v -u ${NEXUS_ACC} -o /opt/web-Spring.jar http://${NEXUS_URL}/repository/${NEXUS_PRO_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT_ID}/${env.BRANCH_NAME}/${env.VERSION}-${env.BRANCH_NAME}-${GIT_HASH}.jar"
                        }
                    }
                }
            }
        }
        
        sshagent(["${PRIVKEY_SERV_VM}"]) {
            sh 'ssh root@$URL_SERV_VM systemctl restart web-Spring'  
        }
    }
}