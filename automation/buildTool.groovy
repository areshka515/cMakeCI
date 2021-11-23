def server
def client
def serverName

def createVersion() {
    prefix = "origin/"
    branchname = env.GIT_BRANCH.substring(prefix.size())
    if(branchname == "master") {
        ver = sh(script: "echo ${branchname} | cut -d / -f 2", returnStdout: true).trim()

        lasttag = sh(script: "git tag -l --sort=version:refname \"${ver}.*\" | tail -1", returnStdout: true).trim()
        echo "${lasttag}"
        def newtag
        if (lasttag.isEmpty()) {
            sh "git tag ${ver}.0"
            newtag = "${ver}.0"
        } else {
            newtag = lasttag.split('\\.')
            newtag[1] = newtag[1].toInteger() + 1
            newtag = newtag.join('.')
            sh "git tag ${newtag}"
        }
        NEWTAG = newtag
        echo "${NEWTAG}"
    }
}

def preBuild() {
    //sh("mkdir build")
    //server = Artifactory.server "artifactory"
    //client = Artifactory.newConanClient()
    //serverName = client.remote.add server: server, repo: "conan-local"
    version = createVersion();
}

def Build() {
    dir("build") {
        client.run(command: "create .. HelloWorld/0.1@jenkins/release")

        /*sh("conan install ..")
        sh("cmake ..")
        sh("cmake --build .")
        sh("./myapp")*/
    }
}

def testExecutable() {
    dir("build") {

    }
}

def publish() {
    String command = "upload \"*\" --all -r ${serverName} --confirm"
    def b = client.run(command: command)
    server.publishBuildInfo b
}

return this;