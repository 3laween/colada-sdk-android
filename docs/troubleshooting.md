# Troubleshooting

Quick answers to what comes up most often during integration. The fastest signal in any
debugging session is the SDK's own log: `adb logcat -s ColadaSDK:V`.

## "Attribution says `matched: false`. Is something broken?"

Not necessarily — most installs on any app are organic, and `matched: false` is a correct,
expected result for those. If you're testing a specific ad click and expected a match:

- Confirm this was the **first run after install** — attribution resolves once and is
  stored. To re-test, clear app data or reinstall.
- If testing a deep link, confirm `Colada.handleDeepLink(intent)` is called from **both**
  `onCreate` and `onNewIntent` — see [`deep-links.md`](deep-links.md).

## "Attribution never resolves / nothing in the log."

1. Is `Colada.isInitialized` `true`? A config error in the log (bad key, wrong prefix)
   means the SDK is disabled by design — fix the key first.
2. Is `Colada.initialize()` called from your `Application` class, not an Activity? See
   [`getting-started.md`](getting-started.md), step 2.
3. Check `adb logcat -s ColadaSDK:V` — if nothing logs at all, the problem is above the SDK
   (no network, or the SDK never initialized).

## "My event never shows up at Colada."

Events are delivered reliably, so "lost" is rare. Most common cause: it was tracked
**before `setUser()`** and is still held — call `setUser()` to release it (see
[`events.md`](events.md)). `Colada.flush()` forces an immediate delivery attempt.

## "The deferred deep link doesn't navigate."

- `consumeDeferredDeepLink()` is **one-shot** — call it from exactly one place in your app.
- An organic install has no deferred deep link at all; check `AttributionResult.matched`
  first.

## "It works in debug but not in release."

Check your own code for an `if (BuildConfig.DEBUG)` guard around `initialize()` or deep-link
forwarding — that's the most common cause. The SDK itself behaves the same in both build
types.

## Still stuck?

Diff your integration against [`example/`](../example) with your own tenant key — if the
example app behaves as expected and your app doesn't, the difference is in your
integration.
