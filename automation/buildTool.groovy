def conanServer
def conanClient
def conanServerName
def VERSION
def CHANNEL // [release, nightly, feature]
def CLIENT // [nightly, develeap]

def createVersion() {
    prefix = "origin/"
    branchname = env.GIT_BRANCH.substring(prefix.size())
    CHANNEL = branchname == "master" ? "stable" : "release"
    //ver = sh(script: "echo ${branchname} | cut -d _ -f 2", returnStdout: true).trim()
    echo "${branchname}"
    //|| branchname.contains("feature/")
    if(branchname == "master") {
        lasttag = sh(script: "git tag -l --sort=version:refname \"0.0.*\" | tail -1", returnStdout: true).trim()
        def newtag
        if (lasttag.isEmpty()) {
            sh "git tag 0.0.0"
            newtag = "0.0.0"
        } else {
            newtag = lasttag.split('\\.')
            newtag[2] = newtag[2].toInteger() + 1
            newtag = newtag.join('.')
            sh "git tag ${newtag}"
        }
        VERSION = newtag
        CLIENT = "rgo"
    }
    else if(branchname.startsWith("release")) {
        releasebranch = branchname.split('\\_')
        echo "${releasebranch}"
    }
}

def preBuild() {
    sh("mkdir build | true")
    conanServer = Artifactory.server "artifactory"
    conanClient = Artifactory.newConanClient()
    conanServerName = conanClient.remote.add server: conanServer, repo: "conan-local"
    createVersion()
}

def Build() {
    dir("build") {
        String command = "create .. HelloWorld/${VERSION}@${CLIENT}/${CHANNEL}"
        conanClient.run(command: command)
    }
}

def publishToArtifactory() {
    String command = "upload \"*\" --all -r ${conanServerName} --confirm"
    def b = conanClient.run(command: command)
    conanServer.publishBuildInfo b
}

return this;