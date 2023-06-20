// Below Can Be Passed as Parameters From Jenkins Pipeline Script if required:
// TO RUN ASPM SCAN
def ecrUrl = '${env.ECR_URL}'
def runAspmScan() {
	echo "Inside Run ASPM Scan" 
	withEnv(["AWS_ECR_URL=${ECR_URL}"]) {
	echo "url ::: ${AWS_ECR_URL}"
	sh "echo '' | docker login -AWS -p ${AWS_ECR_URL}"	

    sh "docker pull -q securin-aspm-cli:latest"
    echo "ASPM Image pulled"
    sh "docker container create --name temp -v '${env.WORKSPACE}':/workdir securin-aspm-cli:latest"
    sh "docker cp '${env.WORKSPACE}/.' temp:/workdir"
    echo "Starting ASPM Scan"
    def scanResponse = sh script: "set +x; docker run --name cli --volumes-from temp -v /var/run/docker.sock:/var/run/docker.sock -t securin-aspm-cli:latest -is_compiled=true -standalone", returnStdout: true
    echo "Scan Response:::: ${scanResponse}"
    sh script: "set +x; docker cp cli:/workdir/results/status.txt '${env.WORKSPACE}/.'"
    def scanStatus = readFile "'${env.WORKSPACE}'/status.txt"
    echo "Scan Status ::: ${scanStatus}"
    return scanStatus
  
}
}

// TO RUN ASPM DOCKER CONTAINER SCAN
def runContainerScan(String imageName) {
	if(!imageName?.trim()) {
		return mandatoryParamErrMsg
	}
	sh script: "set +x ; docker rm -f cli-container & > /dev/null"
	echo "Starting ASPM Docker Container Scan"
	scanResponse = sh script: "set +x ; docker run --name cli-container --volumes-from temp -v /var/run/docker.sock:/var/run/docker.sock ${aspmCliImageWithTag} -upload_log true -image_name ${imageName}", returnStdout: true
	echo "Scan Response:::: ${scanResponse}"
	sh script: "set +x ; docker rm -f temp"
	echo "Temp Container Removed"
	def scanStatus = readFile "${env.WORKSPACE}/status.txt"
	echo "Scan Status ::: ${scanStatus}"
	return scanStatus
}
return this
