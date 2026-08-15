package io.coladaapp.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.coladaapp.sdk.AttributionListener
import io.coladaapp.sdk.AttributionResult
import io.coladaapp.sdk.Colada
import io.coladaapp.sdk.ColadaEvent

/**
 * Exercises every public entry point of the SDK: initialization already happened in
 * [ExampleApp], so this Activity covers deep-link forwarding, user identity, attribution,
 * and event tracking — the parts of the docs quickstart that need a screen to demonstrate.
 */
class MainActivity : Activity() {

    private lateinit var output: TextView

    private val attributionListener = AttributionListener { result ->
        log("Attribution resolved: matched=${result.matched}, method=${result.matchMethod}")
        showDeferredDeepLinkIfAny(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        output = findViewById(R.id.output)
        val userIdInput = findViewById<EditText>(R.id.userIdInput)

        findViewById<Button>(R.id.setUserButton).setOnClickListener {
            val userId = userIdInput.text.toString().ifBlank { "demo-user-1" }
            Colada.setUser(userId)
            log("setUser(\"$userId\")")
        }

        findViewById<Button>(R.id.clearUserButton).setOnClickListener {
            Colada.clearUser()
            log("clearUser()")
        }

        findViewById<Button>(R.id.trackViewContentButton).setOnClickListener {
            Colada.track(ColadaEvent.ViewContent())
            log("track(ViewContent)")
        }

        findViewById<Button>(R.id.trackPurchaseButton).setOnClickListener {
            Colada.track(ColadaEvent.Purchase(amount = 49.99, currency = "USD", orderId = "ORD-123"))
            log("track(Purchase amount=49.99 USD orderId=ORD-123)")
        }

        findViewById<Button>(R.id.flushButton).setOnClickListener {
            Colada.flush()
            log("flush() — requested immediate delivery of the on-device queue")
        }

        findViewById<Button>(R.id.deviceIdButton).setOnClickListener {
            log("deviceId = ${Colada.deviceId}")
        }

        // Registering the listener here is safe even if attribution already resolved
        // before this Activity was created — the SDK replays the last result immediately
        // to a newly registered listener. See docs/attribution.md.
        Colada.addAttributionListener(attributionListener)
        Colada.attribution?.let { showDeferredDeepLinkIfAny(it) }

        // A cold start delivers the link here.
        Colada.handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // A link arriving while the app is already running is delivered here, not to
        // onCreate. Missing this branch silently breaks attribution for warm launches —
        // see docs/deep-links.md.
        Colada.handleDeepLink(intent)
        log("handleDeepLink(intent) from onNewIntent")
    }

    override fun onDestroy() {
        Colada.removeAttributionListener(attributionListener)
        super.onDestroy()
    }

    private fun showDeferredDeepLinkIfAny(result: AttributionResult) {
        val link = Colada.consumeDeferredDeepLink()
        if (link != null) {
            log("Deferred deep link consumed: storeId=${link.storeId}")
        }
    }

    private fun log(message: String) {
        output.append("$message\n")
    }
}
