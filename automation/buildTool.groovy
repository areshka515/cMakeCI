def preBuild() {
    sh("mkdir build")
}

def Build() {

    dir("build") {
        def server = Artifactory.server "artifactory"
        def client = Artifactory.newConanClient()
        /*sh("conan install ..")
        sh("cmake ..")
        sh("cmake --build .")
        sh("./myapp")*/

        def b = client.run(command: "install ..")
        server.publishBuildInfo b
    }
}

return this;