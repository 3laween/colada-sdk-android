# Getting started

**Audience:** the developer adding Colada to an Android app. Fifteen minutes, start to
finish, assuming a project that already builds.

**Also read:** [`events.md`](events.md) (which event to fire when),
[`troubleshooting.md`](troubleshooting.md) (when something doesn't attribute).

Every code block below also appears, compiling, in [`example/`](../example) — if something
doesn't work as described, diff your integration against the example app first.

---

## Before you start

| Requirement | What it means |
|---|---|
| **JDK 17** (build-time) | The Gradle wrapper downloads it automatically if missing |
| **compileSdk 36** | Target the current API level |
| **minSdk 23** | Android 6.0+ |
| **AGP 8.13.2 + Gradle 8.14.5** | See [`COMPATIBILITY.md`](../COMPATIBILITY.md) for why these travel together |
| **A Colada tenant key** | `pk_live_` + 64 hex characters, issued by Colada — ask your Colada contact |

One thing about the key: **it is public by design** — it ships inside your APK and
identifies your app to Colada, it is not a password. Still, keep it out of source control:
put it in a gitignored `local.properties` and read it via `BuildConfig`, the way
[`example/`](../example) does, rather than hardcoding it in a committed file.

## Step 1 — Add the dependency

```kotlin
// build.gradle.kts (app module)
dependencies {
    implementation("io.coladaapp:colada-android:0.17.0")
}
```

`colada-android` depends on `colada-core` and re-exports its public types, so you don't
need to declare `colada-core` yourself.

## Step 2 — Initialize once, from `Application.onCreate()`

The SDK must be started before *anything else* touches it, and from the `Application`
class — not from an Activity:

```kotlin
class ExampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Colada.initialize(
            context = this,
            config = ColadaConfig(publicTenantKey = BuildConfig.COLADA_TENANT_KEY),
        )
    }
}
```

Always initialize from `Application.onCreate()`, not an Activity — the SDK may need to run
before any screen exists, and starting it later can miss that.

Register your Application class in the manifest (`android:name=".ExampleApp"` on your
`<application>` element).

See [`configuration.md`](configuration.md) for the full set of `ColadaConfig` options
(`debug`, `strictMode`, `existingDeviceId`, `logSink`).

## Step 3 — Forward deep links

Covered in full in [`deep-links.md`](deep-links.md); the short version:

1. Add an intent filter for your scheme (or an App Link) to the Activity that should
   receive it.
2. Call `Colada.handleDeepLink(intent)` from **both** `onCreate` and `onNewIntent` — the
   second one is the one everyone forgets, and missing it silently breaks attribution for
   warm launches (the app already running when the link arrives).

## Step 4 — Identify the user

Every event is attributed to whatever user is current. Call this on sign-up, login, and
session restore:

```kotlin
fun onUserAuthenticated(userId: String) {
    Colada.setUser(userId)
}
```

Events tracked before any user is set are **held** — released by the next `setUser` or
`clearUser`, never lost. But `setUser` *before* `track` is still the contract
to write your code around; see [`events.md`](events.md) for why a
`CompleteRegistration` fired before its user exists is worth avoiding.

## Step 5 — Act on attribution

Attribution resolves asynchronously after `initialize()` (and again after a deep link
arrives). See [`attribution.md`](attribution.md) for the full pattern — the short version:

```kotlin
Colada.addAttributionListener { result ->
    if (result.matched) {
        result.deferredDeepLink?.storeId?.let { navigateTo(it) }
    }
}
```

Registering late can never make you miss a result: the listener fires immediately with the
existing result if attribution already resolved before you registered.

## Step 6 — Track events

```kotlin
Colada.track(ColadaEvent.Purchase(amount = 49.99, currency = "USD", orderId = "ORD-123"))
```

The nine event types, which one to fire when, and required fields are all in
[`events.md`](events.md).

## Step 7 — Verify

1. **Run the example app first.** `cd example && ./gradlew :app:installDebug` with a device
   or emulator connected, and your tenant key in `example/local.properties`
   (`colada.tenantKey=pk_live_...` — copy `local.properties.example` to get started).
2. **Logcat is the fastest signal.** `adb logcat -s ColadaSDK:V` — a successful handshake
   logs the resolved attribution; a tracked event logs `enqueued` / `accepted`.
3. If attribution reports `matched: false`, that's often correct, not a bug — start with
   the decision tree in [`troubleshooting.md`](troubleshooting.md).
