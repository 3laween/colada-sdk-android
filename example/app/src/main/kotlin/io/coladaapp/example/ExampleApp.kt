package io.coladaapp.example

import android.app.Application
import android.util.Log
import io.coladaapp.sdk.Colada
import io.coladaapp.sdk.ColadaConfig
import io.coladaapp.sdk.ColadaLogLevel

/**
 * Initializes Colada once, from the Application class — not from an Activity.
 *
 * This matters: the SDK's offline event queue is delivered by a WorkManager job that can
 * start this process with no Activity at all. If the SDK were only initialized when a
 * screen opens, that delivery would find nothing initialized and the queue would sit
 * waiting for the user to come back. See docs/getting-started.md.
 */
class ExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Colada.initialize(
            context = this,
            config = ColadaConfig(
                publicTenantKey = BuildConfig.COLADA_TENANT_KEY,
                debug = BuildConfig.DEBUG,
                strictMode = BuildConfig.DEBUG,
                // Pipes SDK log lines into logcat under a single tag, in addition to the
                // SDK's own internal logging. Optional — most integrations skip this and
                // just read `adb logcat -s ColadaSDK:V`.
                logSink = { level, message, throwable ->
                    val priority = when (level) {
                        ColadaLogLevel.DEBUG -> Log.DEBUG
                        ColadaLogLevel.INFO -> Log.INFO
                        ColadaLogLevel.WARN -> Log.WARN
                        ColadaLogLevel.ERROR -> Log.ERROR
                    }
                    Log.println(priority, "ColadaExample", message)
                    throwable?.let { Log.e("ColadaExample", "attached throwable", it) }
                },
            ),
        )
    }
}
