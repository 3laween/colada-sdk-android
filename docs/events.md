# Events — when to fire each one

**Audience:** the developer adding event tracking to an app. The question this file
answers is *which event, at which moment*.

## The nine events

| Event | Required fields | When to fire |
|---|---|---|
| `CompleteRegistration` | — | **After** your sign-up request returns. Not on every app open. |
| `Login` | — | On actual authentication. **Not** on app open, session restore, or screen view. |
| `Purchase` | `amount`, `currency`, `orderId` | After the payment completes. Once per order. |
| `Subscribe` | `amount`, `currency` | When the subscription is activated. |
| `AddToCart` | — | An item is added to the cart. |
| `InitiateCheckout` | — | A checkout flow starts. |
| `ViewContent` | — | A product or content view. |
| `PlaceAnOrder` | — | An order is placed. |
| `Search` | — | A search happens. |

## The two rules that govern everything below

### 1. `setUser` before `track`, always

Every event is stored against the user that is current at the moment `track()` is called.
An event tracked before `setUser()` is **held** until the next `setUser` or `clearUser` —
never lost, but treat `setUser` → `track`, in that order, as the contract to write your code
around.

```kotlin
fun onUserSignedUp(userId: String, phoneNumber: String?) {
    Colada.setUser(userId)
    Colada.track(ColadaEvent.CompleteRegistration(phoneNumber = phoneNumber))
}
```

### 2. `Login` means "the user actually authenticated", not "the app opened"

Fire it when the user enters credentials, taps "sign in with Google", or otherwise proves
who they are. Restoring a saved session — the app opens and you already know the user —
calls `setUser` and fires nothing:

```kotlin
fun onUserLoggedIn(userId: String) {
    Colada.setUser(userId)
    Colada.track(ColadaEvent.Login())
}
```

## `CompleteRegistration` — fire after signup returns, not before

Fire this **after** your own sign-up request has returned, not as soon as the user finishes
typing — the user needs to exist server-side first.

`name`, `email`, and `phoneNumber` are accepted **only on `CompleteRegistration`**. They're
optional and materially improve match quality with ad platforms:

```kotlin
fun onUserSignedUpWithProfile(userId: String, name: String?, email: String?, phoneNumber: String?) {
    Colada.setUser(userId)
    Colada.track(ColadaEvent.CompleteRegistration(phoneNumber = phoneNumber, name = name, email = email))
}
```

## `Purchase` — once per order

`orderId` is required. The SDK does not deduplicate purchases on-device, so guard against
duplicate submission in your own checkout flow the same way you would for any other
side-effecting network call.

```kotlin
fun onPurchaseCompleted(amount: Double, currency: String, orderId: String) {
    Colada.track(ColadaEvent.Purchase(amount = amount, currency = currency, orderId = orderId))
}
```

## `Subscribe`, `AddToCart`, `InitiateCheckout`, `ViewContent`, `PlaceAnOrder`, `Search`

These carry no required fields; fire them at the natural moment, once per user action.

```kotlin
fun onSubscriptionActivated(amount: Double, currency: String) {
    Colada.track(ColadaEvent.Subscribe(amount = amount, currency = currency))
}

fun onContentView() {
    Colada.track(ColadaEvent.ViewContent())
}
```

## Bridging from another language

If you're calling from a platform channel (Flutter, React Native, C#) rather than Kotlin,
use the wire-name overload with a plain map:

```kotlin
fun trackFromWrapper(eventName: String, metadata: Map<String, Any?>) {
    Colada.track(eventName, metadata)
}
```

Every event also accepts extra custom fields beyond its declared ones:

```kotlin
Colada.track(ColadaEvent.of("AddToCart", mapOf("storeId" to "store_789")))
```

`CompleteRegistration` retains everything you attach; other events only read their own
declared fields.

## Delivery

Tracked events are delivered reliably — an app kill or a dropped connection won't lose them,
and you don't need to handle retries yourself. `Colada.flush()` requests immediate delivery
of anything not yet sent.
