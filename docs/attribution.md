# Attribution

Attribution answers one question: **where did this install come from?** Colada resolves it
automatically after `Colada.initialize()` (and again whenever a new deep link arrives) —
your app doesn't request it, it's notified.

**This page is optional.** Attribution is measured and sent to Colada whether or not your
app ever reads the result — nothing here is required setup. Use this page only if you want
your app to *react* to where the install came from, e.g. routing a new user straight to a
specific screen.

## Reading the result

`AttributionResult` carries:

| Property | Meaning |
|---|---|
| `matched` | `true` if this install/open was attributed to a campaign; `false` for an organic install. Both are normal outcomes. |
| `matchMethod` | How it was matched. |
| `utmSource` / `utmMedium` / `utmCampaign` / `utmContent` / `utmTerm` | Standard UTM parameters, when present. |
| `clickId` | The ad click identifier, when present. |
| `deferredDeepLink` | Where to route the user, if the click carried a destination. See below. |
| `extras` | Any additional key/value data the click carried. |

**`matched: false` is a legitimate result, not an error** — most installs on any app are
organic. See [`troubleshooting.md`](troubleshooting.md) if you're checking whether a
*specific* result should have matched and didn't.

## Two ways to act on it

### 1. Listen for it

The listener fires on the main thread, and fires **immediately** with the existing result
if you register after attribution already resolved — registering late can never make you
miss it:

```kotlin
Colada.addAttributionListener { result ->
    if (result.matched) {
        result.deferredDeepLink?.storeId?.let { navigateToStore(it) }
    }
}
```

Remove the listener when your component is destroyed:

```kotlin
override fun onDestroy() {
    Colada.removeAttributionListener(myListener)
    super.onDestroy()
}
```

### 2. Read it directly

```kotlin
val current: AttributionResult? = Colada.attribution
```

Useful for a one-off check (e.g. logging) rather than reacting to the moment attribution
resolves.

## Deferred deep links

A **deferred deep link** is what happens when the ad click that acquired the install also
specified a destination inside your app — so a brand-new user lands directly on the right
screen instead of your default home screen.

```kotlin
val link: DeferredDeepLink? = Colada.consumeDeferredDeepLink()
link?.storeId?.let { navigateToStore(it) }
```

- **It's one-shot** — consuming it clears it, so call it from exactly one place, typically
  after a successful login or once your navigation stack is ready.
- **You only get one when matched** — an organic install has no deferred deep link.

If you're already reacting to the attribution listener, read `result.deferredDeepLink`
directly from the result rather than also calling `consumeDeferredDeepLink()` elsewhere —
the second caller gets nothing.
