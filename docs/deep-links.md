# Deep links

Colada needs to see the links that open your app to attribute installs and opens
accurately. Wiring it up is two half-steps, and the second one is the one integrations most
commonly miss.

## 1. Register the intent filter(s) in your manifest

A custom scheme works everywhere with no server-side setup, and is the fastest way to get
started:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="yourapp" />
</intent-filter>
```

For production, an Android [App Link](https://developer.android.com/training/app-links)
over `https` is generally preferable — it degrades gracefully to opening a real web page
when your app isn't installed, and doesn't need a disambiguation dialog. That needs an
`assetlinks.json` served from your domain and `android:autoVerify="true"` on the filter;
both are your app's responsibility, not something the SDK sets up for you.

## 2. Forward the intent from every entry point

A link arrives in `onCreate` when the app is launched cold, and in `onNewIntent` when the
app is already running. **`onNewIntent` is the one everyone forgets** — missing it silently
breaks attribution for warm launches, and the gap rarely shows up in manual testing because
a fresh install (the case people test) always goes through `onCreate`.

```kotlin
class DeepLinkActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Colada.handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Colada.handleDeepLink(intent)
    }
}
```

For a link to reach the *same* running Activity instance via `onNewIntent` instead of
creating a new one on top of it, declare that Activity `android:launchMode="singleTask"` in
your manifest.

`handleDeepLink` is safe to call unconditionally, including from both `onCreate` and
`onNewIntent` for what turns out to be the same link — with no link attached it does
nothing, and calling it more than once for the same link is harmless.

## Overloads

`Colada.handleDeepLink` accepts an `Intent`, a `Uri`, or a raw `String`, whichever is
convenient at your call site:

```kotlin
Colada.handleDeepLink(intent)
Colada.handleDeepLink(intent.data)
Colada.handleDeepLink("yourapp://open?utm_source=campaign")
```

## What happens after a link is forwarded

Forwarding a deep link updates your attribution result — see
[`attribution.md`](attribution.md) for how to read it.
