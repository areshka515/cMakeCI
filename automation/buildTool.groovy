def preBuild() {
    sh("mkdir build")
}

def Build() {

    dir("build") {
        def server = Artifactory.server "artifactory"
        def client = Artifactory.newConanClient()
        def serverName = client.remote.add server: server, repo: "conan-local"

        client.run(command: "create .. HelloWorld/0.1@jenkins/release")

        /*sh("conan install ..")
        sh("cmake ..")
        sh("cmake --build .")
        sh("./myapp")*/

        String command = "upload \"*\" --all -r ${serverName} --confirm"
        def b = client.run(command: command)
        server.publishBuildInfo b
    }
}

return this;