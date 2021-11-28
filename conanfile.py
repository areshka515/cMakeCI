from conans import ConanFile, CMake

class MyAppConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    generators = "cmake"
    requires = "Hello/0.1@user/channel"
    exports_sources = "src/*"

    def imports(self):
        self.copy("*.h", dst="src", src="include")

    def build(self):
        cmake = CMake(self)
        cmake.configure(source_folder="src")
        cmake.build()

    def package(self):
        self.copy("*.h", dst="include", src="src")
        self.copy("*", dst="lib", keep_path=False)

    def package_info(self):
        self.cpp_info.libs = ["HelloWorld"]