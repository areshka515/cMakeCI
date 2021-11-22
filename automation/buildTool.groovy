def preBuild() {
    sh("mkdir build")
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