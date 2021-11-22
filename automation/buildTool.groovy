def preBuild() {
    sh("cmake .");
}

def Build() {
    sh("cmake --build .")
    sh("./asd")
}

return this;