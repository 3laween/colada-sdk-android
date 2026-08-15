# Changelog

All notable changes to the Colada Android SDK are documented here. Format loosely follows
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/); versions follow semver.

## [Unreleased]

No version has been published yet. This entry tracks what the SDK currently does, ahead of
its first release.

### Added

- Install attribution: resolves which ad campaign acquired an install from the Play
  Install Referrer, a deep link, or a clipboard-based signal — whichever is available and
  fastest, with no action required from your app beyond initialization and forwarding deep
  links.
- `Colada.addAttributionListener` / `removeAttributionListener`, with immediate replay of
  the last result for listeners registered after attribution already resolved.
- `Colada.consumeDeferredDeepLink()` — one-shot access to the destination a deferred deep
  link resolved to, for routing a new user straight to the right place in your app.
- `Colada.handleDeepLink` — forward `Intent`, `Uri`, or a raw `String` deep link; safe to
  call unconditionally and deduplicates a link delivered through both `onCreate` and
  `onNewIntent`.
- `Colada.setUser` / `clearUser` / `externalUserId` — attach your own user identity to
  attribution and events.
- `Colada.track()` — nine typed events (`CompleteRegistration`, `Login`, `Purchase`,
  `Subscribe`, `AddToCart`, `InitiateCheckout`, `ViewContent`, `PlaceAnOrder`, `Search`)
  plus a wire-name/`Map` overload for non-Kotlin callers (e.g. cross-platform bridges).
  Events tracked before `setUser()` is called are held and released once a user is set —
  never dropped.
- A durable, on-device event queue: `track()` persists an event to local storage before any
  network attempt, so a killed app or a lost connection cannot lose it. Delivery continues
  in the background, including after the app process has been killed, via the OS scheduler.
- `Colada.flush()` — request immediate delivery of anything still queued.
- `ColadaConfig.existingDeviceId` — a one-time migration hook for apps moving from another
  attribution SDK that already have a stable per-install identifier.
- `ColadaConfig.logSink` — pipe SDK log output into your own logging/crash-reporting
  pipeline, or across a platform-channel bridge.
- `ColadaConfig.strictMode` — turn silent degradation into loud failure during development,
  without changing the SDK's never-crash behavior in release builds.
- Every public declaration carries full API documentation (KDoc).
- The public API is frozen and guarded by a binary-compatibility check — a change to any
  public signature is visible in code review before it ships.

### Compatibility

See [COMPATIBILITY.md](COMPATIBILITY.md) for the supported Android, AGP, Gradle, Kotlin,
and JDK versions.
