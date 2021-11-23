def server
def client
def serverName

def createVersion() {
    prefix = "origin/"
    branchname = env.GIT_BRANCH.substring(prefix.size())
    echo "${branchname}"
    if(branchname == "main") {
        ver = sh(script: "echo ${branchname} | cut -d / -f 2", returnStdout: true).trim()

        lasttag = sh(script: "git tag -l --sort=version:refname \"v${ver}.*\" | tail -1", returnStdout: true).trim()
        def newtag
        if (lasttag == "null") {
            sh "git tag v${ver}.0"
            newtag = "v${ver}.0"
            print("asd")
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
    sh("mkdir build")
    server = Artifactory.server "artifactory"
    client = Artifactory.newConanClient()
    serverName = client.remote.add server: server, repo: "conan-local"
    version = createVersion();
}

def Build() {
    dir("build") {
        client.run(command: "create .. HelloWorld/0.1@release")

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