# Compatibility

| Colada Android SDK | Minimum Android | compileSdk / targetSdk | AGP | Gradle | Kotlin | JDK |
|---|---|---|---|---|---|---|
| 0.1.1 | API 23 (Android 6.0) | 36 | 8.13.2 | 8.14.5 | 2.2.21 | 17 (build-time only) |

**Reading this table:** the SDK is built and tested against the versions in its own row.
Newer AGP/Gradle/Kotlin in your app usually work too — Android libraries are generally
forward-compatible with newer build tooling — but the combination in the table is the one
verified before each release.

## Minimum Android version

**API 23 (Android 6.0)** covers over 99% of active devices as of this writing. The SDK is
verified on real API 23, 29, and current-API emulators/devices before each release, not
just declared as a minimum.

## JDK requirement

JDK 17 is required to *build* an app that includes the SDK (a Gradle/AGP requirement, not
something the SDK itself imposes at runtime). If you use the Gradle wrapper (`./gradlew`),
it downloads the correct JDK automatically via the Foojay toolchain resolver — you do not
need to install it yourself.

## AGP and Gradle: a matched set

AGP and Gradle must be upgraded together, not independently:

- An AGP major version newer than your Android Studio release can fail IDE sync entirely,
  even when the command line builds fine.
- A Gradle version can drop an internal API an older AGP depends on, which fails
  configuration outright.

If you hit a sync or configuration failure after a routine dependency bump, check whether
AGP and Gradle drifted out of the combination your IDE actually supports before assuming
the SDK is at fault.

## Kotlin

The published artifact is compiled with Kotlin 2.2.21. Gradle's dependency resolution
generally tolerates a host app on a nearby Kotlin version, but keeping your project on the
same minor line avoids surprises with newer language features.

## Multi-module note

`io.coladaapp:colada-android` depends on `io.coladaapp:colada-core` and re-exports its
public types (`AttributionResult`, `ColadaEvent`, `DeferredDeepLink`, etc.) as an API
dependency. Adding `colada-android` alone is sufficient — Gradle resolves `colada-core`
transitively, and you do not need to declare it yourself.
