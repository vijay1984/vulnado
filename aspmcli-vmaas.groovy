// Below Can Be Passed as Parameters From Jenkins Pipeline Script if required:
String aspmCliImageWithTag = "securin-aspm-cli:latest"

// TO RUN ASPM SCAN
def runAspmScan(String test) {
  withDockerRegistry(credentialsId: 'ASPM-CLI', url: ${env.ECR_URL}){
    sh script: "set +x; docker pull -q ${aspmCliImageWithTag} & > /dev/null"
    echo "ASPM Image pulled"
    sh script: "set +x; docker container create --name temp -v ${env.WORKSPACE}:/workdir ${aspmCliImageWithTag}"
    sh script: "set +x; docker cp '${env.WORKSPACE}/.' temp:/workdir"
    echo "Starting ASPM Scan"
    scanResponse = sh script: "set +x; docker run --name cli --volumes-from temp -v /var/run/docker.sock:/var/run/docker.sock -t ${aspmCliImageWithTag} -is_compiled=true -standalone", returnStdout: true
    echo "Scan Response:::: ${scanResponse}"
    sh script: "set +x; docker cp cli:/workdir/results/status.txt ${env.WORKSPACE}/."
    def scanStatus = readFile "${env.WORKSPACE}/status.txt"
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
