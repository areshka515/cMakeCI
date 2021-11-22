def preBuild() {
    sh("mkdir build")

    def artifactory_name = "artifactory"
    def artifactory_repo = "conan-local"
    def repo_url = 'https://github.com/memsharded/example-boost-poco.git'
    def repo_branch = 'master'

    
    def server = Artifactory.server artifactory_name
    def client = Artifactory.newConanClient()
}

def Build() {
    dir("build") {
        sh("conan install ..")
        sh("cmake ..")
        sh("cmake --build .")
        sh("./myapp")
    }
}

return this;