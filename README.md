# Colada Android SDK

Native Android (Kotlin) SDK for Colada's mobile attribution platform.

Attribution answers one question: **where did this install come from?** The SDK reads the
signals available on-device — the Play Install Referrer, a deep link, or a clipboard-based
signal — resolves which campaign acquired the user, and durably delivers the events that
measure what that campaign did afterwards. **It's designed to never block or crash your
app.**

This repository is the public, developer-facing home for the SDK: documentation and release
history. The SDK's implementation is closed-source and distributed only as a compiled Maven
artifact — see [Installation](#installation) below.

---

## Requirements

| Tool | Version |
|---|---|
| Min Android | API 23 (Android 6.0+) |
| compileSdk / targetSdk | 36 |
| AGP | 8.13.2 |
| Gradle | 8.14.5 |
| Kotlin | 2.2.21 |
| JDK (build-time only) | 17 |

Full detail, including why AGP and Gradle need to move together, is in
[`COMPATIBILITY.md`](COMPATIBILITY.md).

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.coladaapp:colada-android:0.17.0")
}
```

> **Status note:** the SDK is not yet published to Maven Central. Until it is, this
> coordinate resolves from `mavenLocal()` instead — add `mavenLocal()` to your
> `settings.gradle.kts` repositories. Once published, this line works as written, with no
> other change to your build.

`colada-android` depends on `colada-core` and re-exports its public types
(`AttributionResult`, `ColadaEvent`, `DeferredDeepLink`, etc.), so this single line is
everything you need to add — no second dependency to declare.

## Five-minute quickstart

```kotlin
// 1. Initialize once, from your Application class — not an Activity.
class ExampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Colada.initialize(
            context = this,
            config = ColadaConfig(publicTenantKey = BuildConfig.COLADA_TENANT_KEY),
        )
    }
}

// 2. Identify the user, on sign-up / login / session restore.
Colada.setUser(userId)

// 3. Track an event.
Colada.track(ColadaEvent.Purchase(amount = 49.99, currency = "USD", orderId = "ORD-123"))

// 4. Act on attribution — fires immediately if it already resolved.
Colada.addAttributionListener { result ->
    if (result.matched) {
        result.deferredDeepLink?.storeId?.let { navigateTo(it) }
    }
}
```

Deep-link forwarding (the one step every integration needs but this snippet can't show in
three lines) is in [`docs/deep-links.md`](docs/deep-links.md) — read that before you ship.

## Documentation

| Doc | Answers |
|---|---|
| [`docs/getting-started.md`](docs/getting-started.md) | Step-by-step integration: dependency, initialize, deep links, user identity, attribution, verify |
| [`docs/configuration.md`](docs/configuration.md) | Every `ColadaConfig` option |
| [`docs/events.md`](docs/events.md) | The nine events, the ordering contract, wrappers |
| [`docs/attribution.md`](docs/attribution.md) | *(Optional)* Reading attribution results, deferred deep links |
| [`docs/deep-links.md`](docs/deep-links.md) | Manifest setup and forwarding deep links correctly |
| [`docs/troubleshooting.md`](docs/troubleshooting.md) | "Why did attribution say not-matched?" and other common issues |
| [`COMPATIBILITY.md`](COMPATIBILITY.md) | Supported Android/AGP/Gradle/Kotlin/JDK versions |
| [`CHANGELOG.md`](CHANGELOG.md) | Release history |

## License

This repository (documentation and its own source) is licensed under the
[MIT License](LICENSE). The compiled SDK distributed via Maven is Colada's proprietary
software — see the license terms attached to the published artifact.
