# Configuration

`ColadaConfig` is passed once to `Colada.initialize()`. Only `publicTenantKey` is required
— every other option has a sensible default, and most integrations never touch the rest.

```kotlin
Colada.initialize(
    context = this,
    config = ColadaConfig(publicTenantKey = BuildConfig.COLADA_TENANT_KEY),
)
```

## `publicTenantKey` (required)

Your app's public API key, issued by Colada. Format: `pk_live_` followed by 64 hexadecimal
characters. Hardcode it per app build via `BuildConfig` (see
[`getting-started.md`](getting-started.md)) — never read it from a deep link or any other
source your app doesn't fully control.

## `debug`

`Boolean`, defaults to `false`. Enables verbose SDK logging.

```kotlin
ColadaConfig(publicTenantKey = key, debug = BuildConfig.DEBUG)
```

## `strictMode`

`Boolean`, defaults to `false`. When `true`, an invalid config or an internal error throws
instead of being handled silently. **Enable in debug builds only, never in release.**

```kotlin
ColadaConfig(publicTenantKey = key, strictMode = BuildConfig.DEBUG)
```

## `existingDeviceId`

`String?`, defaults to `null`. **Migration only** — if your app already keeps a stable
per-install identifier (most commonly from a previous attribution SDK), pass it here once so
Colada adopts it instead of generating a new one. New integrations should leave this `null`.

## `logSink`

`ColadaLogSink?`, defaults to `null`. A callback that receives every SDK log message, useful
for routing SDK logs into your own crash reporter or across a platform-channel bridge (e.g.
a Flutter or React Native wrapper).

```kotlin
ColadaConfig(
    publicTenantKey = key,
    logSink = { level, message, error ->
        if (level == ColadaLogLevel.ERROR) MyCrashReporter.log("$message $error")
    },
)
```

`logSink` is called from background threads — implementations must be thread-safe and must
not block.

## What you don't need to configure

There is no separate staging/sandbox endpoint — every `ColadaConfig` talks to the same
Colada backend. To distinguish integration testing from real user activity, use a clearly
marked test user identifier (e.g. a distinct `setUser()` prefix) rather than looking for an
environment switch.
