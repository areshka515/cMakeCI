def conanServer
def conanClient
def conanServerName
def NAME // name of the build
def VERSION // version of the artifcat
def CHANNEL // [release, nightly, feature]
def CLIENT // [nightly, develeap]

def createVersion() {
    prefix = "origin/"
    branchname = env.GIT_BRANCH.substring(prefix.size())
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
        NAME = "HelloWorld"
        VERSION = newtag
        CLIENT = "rgo"
        CHANNEL = "stable"
    }
    else if(branchname.startsWith("release")) {
        releasebranch = branchname.split('\\_')
        lasttag = sh(script: "git tag -l --sort=version:refname \"${releasebranch[3]}.*\" | tail -1", returnStdout: true).trim()
        def newtag
        if (lasttag.isEmpty()) {
            sh "git tag ${releasebranch[3]}.0"
            newtag = "${releasebranch[3]}.0"
        } else {
            newtag = lasttag.split('\\.')
            newtag[1] = newtag[1].toInteger() + 1
            newtag = newtag.join('.')
            sh "git tag ${newtag}"
        }
        NAME = releasebranch[2]
        VERSION = newtag
        CLIENT = releasebranch[1]
        CHANNEL = "release"
    }
}

def preBuild() {
    conanServer = Artifactory.server "artifactory"
    conanClient = Artifactory.newConanClient()
    conanServerName = conanClient.remote.add server: conanServer, repo: "conan-local"
    createVersion()
    sh("mkdir build | true")
}

def ImportHeader() {
    conanClient.run(command: "install conanfile.txt --install-folder header_build")
}

def Build() {
    dir("build") {
        String command = "create . ${NAME}/${VERSION}@${CLIENT}/${CHANNEL}"
        conanClient.run(command: command)
    }
}

def publishToArtifactory() {
    String command = "upload \"*\" --all -r ${conanServerName} --confirm"
    def b = conanClient.run(command: command)
    conanServer.publishBuildInfo b
}

return this;