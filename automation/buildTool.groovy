def preBuild() {
    sh("mkdir build")
}

def Build() {
    sh("cmake --build .")
    sh("./asd")
}

return this;