def aspmCli
node{
  stage('SCM checkout'){
    git 'https://github.com/YugeshCSW/vulnado'
  }
  stage('Complie-package'){
    def maven = tool name: 'Maven-auto', type: 'maven'
    sh "${maven}/bin/mvn package"
  }
  stage('SECURIN ASPM SCAN'){
    docker.withRegistry("607019071572.dkr.ecr.us-west-2.amazonaws.com", "ecr:us-east-1:ASPM-CLI") {
    docker.image("securin-aspm-cli:latest").pull()}
    {
    echo "ASPM Image pulled"
    sh script: "set +x; docker container create --name temp -v '${env.WORKSPACE}:/workdir' securin-aspm-cli:latest"
    sh script: "set +x; docker cp '${env.WORKSPACE}/.' temp:/workdir"
    echo "Starting ASPM Scan"
    def scanResponse = sh script: "set +x; docker run --name cli --volumes-from temp -v /var/run/docker.sock:/var/run/docker.sock -t 607019071572.dkr.ecr.us-west-2.amazonaws.com/securin-aspm-cli:latest -is_compiled=true -standalone", returnStdout: true
    echo "Scan Response:::: ${scanResponse}"
    sh script: "set +x; docker cp cli:/workdir/results/status.txt ${env.WORKSPACE}/."
    def scanStatus = readFile "${env.WORKSPACE}/status.txt"
    echo "Scan Status ::: ${scanStatus}"
    return scanStatus
    }
    //checkout scm
    //def testscan = load "aspmcli-vmaas.groovy"
    //echo "${testscan}"
    //def cliScanStatus = testscan.runAspmScan()
    //echo "${cliScanStatus}"
  }
  //Below stage to be called after the docker build is completed.
  stage('SECURIN ASPM CONTAINER SCAN'){
    def cliScanStatus = testscan.runContainerScan("dockerImageName")
    echo "${cliScanStatus}"
  }
}
