// Below Can Be Passed as Parameters From Jenkins Pipeline Script if required:
// TO RUN ASPM SCAN
def runAspmScan() {
	echo "Inside Run ASPM Scan" 
	withEnv(["AWS_ECR_URL=${ECR_URL}"]) {
	echo "url ::: ${AWS_ECR_URL}"
	sh "aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin ${AWS_ECR_URL}"
	def imageUrl = removePrefix("${AWS_ECR_URL}")
    sh "docker pull ${imageUrl}/securin-aspm-cli:latest"
    String containerName = generateRandomContainerName()
    echo "ASPM Image pulled"
    echo "Starting ASPM Scan"
    def scanResponse = sh script: "docker run --name '${containerName}' -v '${WORKSPACE}':/workdir -v /var/run/docker.sock:/var/run/docker.sock -t ${imageUrl}/securin-aspm-cli:latest -is_compiled=true -standalone", returnStdout: true
    echo "Scan Response:::: ${scanResponse}"
    sh "docker rm -f ${containerName}"
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
	withEnv(["AWS_ECR_URL=${ECR_URL}"]) {
	echo "Starting ASPM Docker Container Scan"
	def imageUrl = removePrefix("${AWS_ECR_URL}")
	String containerName = generateRandomContainerName()
	scanResponse = sh script: "docker run --name '$containerName}' -v '${WORKSPACE}':/workdir -v /var/run/docker.sock:/var/run/docker.sock ${imageUrl}/securin-aspm-cli:latest -upload_log true -image_name ${imageName}", returnStdout: true
	echo "Scan Response:::: ${scanResponse}"
	sh "docker rm -f ${containerName}"
	}
}
return this

String generateRandomContainerName() {
	def length = 16
	def allChars = ['a'..'z', 'A'..'Z', 0..9].flatten()
	java.util.Random random = new java.util.Random(java.lang.System.currentTimeMillis())
	def randomChars = (0..length - 1).collect { allChars[random.nextInt(allChars.size())] }
	def randomString = randomChars.join('')
	return randomString
}
