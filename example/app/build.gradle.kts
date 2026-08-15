// The example app — a minimal, real Android app that consumes Colada the same way any
// integrator would: as a published Maven artifact, never as a source/project dependency.
// If you're used to pub.dev, this file is the closest thing to a Flutter app's
// pubspec.yaml + the platform-specific wiring, combined.

import java.util.Properties

plugins {
    id("com.android.application") version "8.13.2"
    id("org.jetbrains.kotlin.android") version "2.2.21"
}

// Reads local.properties the same way the SDK's own sample does: a gitignored file that
// never reaches version control. See local.properties.example at the repo root of
// example/ for the expected keys.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "io.coladaapp.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.coladaapp.example"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Exposed to Kotlin as BuildConfig.COLADA_TENANT_KEY — never hardcode the key
        // itself in source.
        buildConfigField(
            "String",
            "COLADA_TENANT_KEY",
            "\"${localProperties.getProperty("colada.tenantKey", "")}\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // The published Maven coordinate — exactly what an external integrator would add.
    // Resolved from mavenLocal() until the SDK reaches Maven Central (see
    // settings.gradle.kts). Never `implementation(project(":colada-android"))` — that
    // would depend on the private source module, which this app must not be able to see.
    implementation("io.coladaapp:colada-android:0.17.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
}
