pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "https://jitpack.io") // for other libs
        maven(url = "https://jcenter.bintray.com/")
        maven { url = uri("https://kommunicate.jfrog.io/artifactory/kommunicate-android-sdk") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://kommunicate.jfrog.io/artifactory/kommunicate-android-sdk")
        }
    }
}

rootProject.name = "JobIt"
include(":app")
 