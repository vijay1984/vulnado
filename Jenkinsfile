def aspmCli
node{
  stage('SCM checkout'){
    git 'https://github.com/YugeshCSW/vulnado'
  }
  stage('SECURIN ASPM SCAN'){
    checkout scm
    def ecrUrl = '${env.ECR_URL}'
    echo "ECR URL::: ${ecrUrl}"
    aspmCli = load "aspmcli-vmaas.groovy"
    def cliScanStatus = aspmCli.runAspmScan()
    //echo "${cliScanStatus}"
  }
  //Below stage to be called after the docker build is completed.
  /*stage('SECURIN ASPM CONTAINER SCAN'){
    def cliScanStatus = aspmCli.runContainerScan("dockerImageName")
    echo "${cliScanStatus}"
  }*/
}
