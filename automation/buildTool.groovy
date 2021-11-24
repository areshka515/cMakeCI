def server
def client
def serverName
def VERSION
def CHANNEL
def CLIENT

def clients = ["nightly", "develeap", "rgo", "google"]

def createVersion() {
    prefix = "origin/"
    branchname = env.GIT_BRANCH.substring(prefix.size())
    CHANNEL = branchname == "master" ? "release" : "feature"
    ver = sh(script: "echo ${branchname} | cut -d / -f 2", returnStdout: true).trim()
    //|| branchname.contains("feature/")
    if(branchname == "master") {
        lasttag = sh(script: "git tag -l --sort=version:refname \"0.0.*\" | tail -1", returnStdout: true).trim()
        def newtag
        if (lasttag.isEmpty()) {
            sh "git tag 0.0.0"
            newtag = "0.0.0"
        } else {
            newtag = lasttag.split('\\.')
            newtag[1] = newtag[1].toInteger() + 1
            newtag = newtag.join('.')
            sh "git tag ${newtag}"
        }
        VERSION = newtag
        CLIENT = clients[0];
    }
}

def preBuild() {
    sh("mkdir build | true")
    server = Artifactory.server "artifactory"
    client = Artifactory.newConanClient()
    serverName = client.remote.add server: server, repo: "conan-local"
    createVersion()
}

def Build() {
    dir("build") {
        String command = "create .. HelloWorld/${VERSION}@${CLIENT}/${CHANNEL}"
        client.run(command: command)
    }
}

def publishToArtifactory() {
    String command = "upload \"*\" --all -r ${serverName} --confirm"
    def b = client.run(command: command)
    server.publishBuildInfo b
}

return this;