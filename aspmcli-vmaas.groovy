// Below Can Be Passed as Parameters From Jenkins Pipeline Script if required:
// TO RUN ASPM SCAN
def runAspmScan() {
	echo "Inside Run ASPM Scan" 
	withEnv(["AWS_ECR_URL=${ECR_URL}"]) {
	echo "url ::: ${AWS_ECR_URL}"
	sh "aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin ${AWS_ECR_URL}"
	def imageUrl = removePrefix("${AWS_ECR_URL}")
    sh "docker pull ${imageUrl}/securin-aspm-cli:latest"
    echo "ASPM Image pulled"
    echo "Starting ASPM Scan"
    def scanResponse = sh script: "docker run --name cli -v ${env.WORKSPACE}:/workdir -v /var/run/docker.sock:/var/run/docker.sock -t ${imageUrl}/securin-aspm-cli:latest -is_compiled=true -standalone", returnStdout: true
    echo "Scan Response:::: ${scanResponse}"
    sh script: "set +x; docker cp cli:/workdir/results/status.txt '${env.WORKSPACE}/.'"
    def scanStatus = readFile "'${env.WORKSPACE}'/status.txt"
    echo "Scan Status ::: ${scanStatus}"
    return scanStatus
  
}
}

String removePrefix(String str) {
	return str.minus("https://")
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
