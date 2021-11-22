
def artifactory_name = "artifactory"
def artifactory_repo = "conan-local"
def serverName = client.remote.add server: server, repo: artifactory_repo
def recipe_folder = "recipes/zlib/1.2.11"
def recipe_version = "1.2.11"

def server = Artifactory.server artifactory_name
def client = Artifactory.newConanClient()

def preBuild() {
    sh("mkdir build")
}

def Build() {
    dir("build") {
        /*sh("conan install ..")
        sh("cmake ..")
        sh("cmake --build .")
        sh("./myapp")*/

        def b = client.run(command: "install ..")
        server.publishBuildInfo b
    }
}

return this;