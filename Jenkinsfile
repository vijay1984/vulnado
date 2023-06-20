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
    checkout scm
    def aspmCliScan = load "aspmcli-vmaas.groovy"
    echo "${aspmCliScan}"
    def cliScanStatus = aspmCliScan.runAspmScan()
    echo "${cliScanStatus}"
  }
  //Below stage to be called after the docker build is completed.
  stage('SECURIN ASPM CONTAINER SCAN'){
    def cliScanStatus = aspmCliScan.runContainerScan("dockerImageName")
    echo "${cliScanStatus}"
  }
}
