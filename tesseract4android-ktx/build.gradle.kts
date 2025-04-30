import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register

plugins {
	alias(libs.plugins.android.library)
	id("maven-publish")
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "cz.adaptech.tesseract4android.ktx"
	compileSdk = 35

	defaultConfig {
		minSdk = 21
		targetSdk = 35

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		missingDimensionStrategy("parallelization", "standard")
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
			proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
		}
	}
	flavorDimensions += "parallelization"
	productFlavors {
		create("standard") {
			dimension = "parallelization"
		}
		create("openmp") {
			dimension = "parallelization"
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

dependencies {
	// Intentionally use old version of annotation library which doesn't depend on kotlin-stdlib
	// to not unnecessarily complicate client projects due to potential duplicate class build errors
	// caused by https://kotlinlang.org/docs/whatsnew18.html#updated-jvm-compilation-target
	//noinspection GradleDependency
	"standardImplementation"(project(":internals"))
	"openmpImplementation"(project(":internals"))

	implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android) 
    testImplementation(libs.kotlinx.coroutines.test)

	implementation(libs.androidx.annotation)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.test.runner)
	androidTestImplementation(libs.androidx.test.rules)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.androidx.test.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("standard") {
                from(components.findByName("standardRelease"))

                groupId = "cz.adaptech"
                artifactId = "tesseract4android"
                version = rootProject.extra["tesseract4AndroidVersion"] as String
            }
            register<MavenPublication>("openmp") {
                from(components.findByName("openmpRelease"))

                groupId = "cz.adaptech"
                artifactId = "tesseract4android-openmp"
                version = rootProject.extra["tesseract4AndroidVersion"] as String
            }
        }
    }
}
