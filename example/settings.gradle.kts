// Standalone Gradle build for the Colada SDK example app.
//
// This project is deliberately NOT part of the same Gradle build as the SDK itself — it
// has no `includeBuild` or `project(":...")` reference to the SDK's source. It resolves
// the SDK the same way a real customer app would: as a Maven coordinate. That is the whole
// point of this module (see the two-repo architecture notes) — it is the first proof that
// the published, frozen public API is sufficient on its own, with no internal opt-ins.

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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // mavenLocal() FIRST: until the SDK is on Maven Central, this is the only place
        // io.coladaapp:colada-android resolves from. It must be populated by running
        // `./gradlew publishToMavenLocal` in colada-sdk-android-core first.
        //
        // TODO(GA release): once io.coladaapp:colada-android is published to Maven
        // Central, remove mavenLocal() from this list (or move it after mavenCentral())
        // so the example always builds against the real published artifact.
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "colada-example"

include(":app")
