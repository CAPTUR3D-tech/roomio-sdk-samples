pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://cdn.roomio.io/sdk/android/maven")
        google()
        mavenCentral()
    }
}

rootProject.name = "RoomioViews"
include(":app")
