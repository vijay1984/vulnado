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
    aspmCli = load "aspmcli-vmaas.groovy"
    echo "${aspmCli}"
    def cliScanStatus = aspmCli.runAspmScan()
    echo "${cliScanStatus}"
  }
  //Below stage to be called after docker build is completed.
  stage('SECURIN ASPM CONTAINER SCAN'){
    def cliScanStatus = aspmCli.runContainerScan("dockerImageName")
    echo "${cliScanStatus}"
  }
}
