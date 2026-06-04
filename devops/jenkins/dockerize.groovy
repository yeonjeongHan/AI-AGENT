pipeline {
    agent any

    environment {
        VM_HOST    = '20.249.208.74'     // VM IP
        VM_USER    = 'yjtest'            // VM 계정 ID
        REMOTE_DIR = '/home/yjtest/app-build'    // VM 파일 경로
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Package Source') {
            steps {
                sh '''
                    rm -f source.tar.gz
                    #tar --warning=no-file-changed --exclude=.git --exclude=target --exclude=source.tar.gz -czf source.tar.gz .
                
                    ls -al source.tar.gz
                    ls -al devops/jenkins/settings.xml
                '''
            }
        }

		stage('Upload Source to Azure VM') {
		    steps {
		        withCredentials([sshUserPrivateKey(
		            credentialsId: 'SHH-yjtest',
		            keyFileVariable: 'SSH_KEY',
		            usernameVariable: 'SSH_USER'
		        )]) {
		            sh """
		             echo "Current Jenkins host IP:"
    					   hostname -I
		                chmod 600 ${SSH_KEY}
		
		                ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no \
		                    ${SSH_USER}@${VM_HOST} \
		                    "mkdir -p ${REMOTE_DIR}"
		
		                scp -i ${SSH_KEY} -o StrictHostKeyChecking=no \
		                    source.tar.gz \
		                    ${SSH_USER}@${VM_HOST}:${REMOTE_DIR}/
		
		                scp -i ${SSH_KEY} -o StrictHostKeyChecking=no \
		                    AI-AGENT/devops/jenkins/settings.xml \
		                    ${SSH_USER}@${VM_HOST}:${REMOTE_DIR}/settings.xml
		            """
		        }
		    }
		}


     stage('Build on Azure VM') {
		    steps {
		        withCredentials([sshUserPrivateKey(
		            credentialsId: 'SHH-yjtest',
		            keyFileVariable: 'SSH_KEY',
		            usernameVariable: 'SSH_USER'
		        )]) {
		            sh '''
		                set +x
		                sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no "$SSH_USER@$VM_HOST" '
		                    set -e
		                    cd '"$REMOTE_DIR"'
		                    rm -rf source
		                    mkdir -p source
		                    tar -xzf source.tar.gz -C source
		                    cd source
		
		                    echo "=== JAVA ==="
		                    java -version || true
		
		                    echo "=== MAVEN ==="
		                    mvn -v || true
		
		                    mvn clean package -DskipTests --settings '"$REMOTE_DIR"'/settings.xml
		
		                    echo "=== TARGET ==="
		                    ls -al target
		                '
		            '''
		        }
		    }
		}

        stage('Verify WAR on Azure VM') {
            steps {
                sshagent(credentials: ['vm-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${VM_USER}@${VM_HOST} '
                            ls -al ${REMOTE_DIR}/source/target/*.war
                        '
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Azure VM에서 빌드 완료'
        }
        failure {
            echo 'Azure VM 빌드 실패. 콘솔 로그 확인 필요'
        }
    }
}
