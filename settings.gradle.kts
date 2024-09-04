pluginManagement {
    repositories {
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
        google()
        mavenCentral()
        // Thirdparty dependencies of TBD projects not in Maven Central
        maven("https://blockxyz.jfrog.io/artifactory/tbd-oss-thirdparty-maven2/")
        maven("https://jitpack.io")
    }
}

rootProject.name = "FourthWall"
include(":app")
 