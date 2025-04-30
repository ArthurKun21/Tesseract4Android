plugins {
	alias(libs.plugins.android.library)
}

android {
	namespace = "cz.adaptech.tesseract4android.internals"
	compileSdk = 35
	ndkVersion = "28.1.13356709"

	defaultConfig {
		minSdk = 21
		targetSdk = 35

		externalNativeBuild {
			cmake {
				// Specifies which native libraries or executables to build and package.
				// TODO: Include eyes-two in some build flavor of the library?
				//targets "jpeg", "pngx", "leptonica", "tesseract"

				// Support 16 KB page sizes with NDK r27
				// This can be removed with NDK r28, which supports it by default.
				// arguments "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON"
			}
		}
		ndk {
			// Specify the ABI configurations that Gradle should build and package.
			// By default it compiles all available ABIs.
			//abiFilters 'x86', 'x86_64', 'armeabi-v7a', 'arm64-v8a'
		}
		missingDimensionStrategy("parallelization", "standard")
	}
	externalNativeBuild {
		cmake {
			path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
			proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
		}
		getByName("debug") {
			externalNativeBuild {
				cmake {
					// Force building release version of native libraries even in debug variant.
					// This is for projects that has direct dependency on this library,
					// but doesn't really want its debug version, which is very slow.
					// Note that this only affects native code.
					arguments.add("-DCMAKE_BUILD_TYPE=Release")
				}
			}
		}
	}
	flavorDimensions += "parallelization"
	productFlavors {
		create("standard") {
            dimension = "parallelization"
        }
		create("openmp") {
            dimension = "parallelization"
			externalNativeBuild {
				cmake {
					// NOTE: We must add -static-openmp argument to build it statically,
					// because shared library is not being included in the resulting APK.
					// See: https://github.com/android/ndk/issues/1028
					// Use of that argument shows warnings during build:
					// > C/C++: clang: warning: argument unused during compilation: '-static-openmp' [-Wunused-command-line-argument]
					// But it has no effect on the result.
					cFlags.add("-fopenmp -static-openmp -Wno-unused-command-line-argument")
					cppFlags.add("-fopenmp -static-openmp -Wno-unused-command-line-argument")
				}
			}
		}
	}
	publishing {
		singleVariant("standardRelease") {
			withSourcesJar()
			withJavadocJar()
		}
		singleVariant("openmpRelease") {
			withSourcesJar()
			withJavadocJar()
		}
	}
	compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
	buildFeatures {
        buildConfig = true
    }
}