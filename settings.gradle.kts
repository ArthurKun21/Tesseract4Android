pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // fallback for the rest of the dependencies
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }
}

include(":tesseract4android")
include(":tesseract4android-ktx")
include(":internals")
if (System.getenv("JITPACK").isNullOrBlank()) {
    include(":sample")
}
