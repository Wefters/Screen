package dev.wefter.bridge

import android.content.Context
import android.content.res.Configuration
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import org.json.JSONObject

/** Screen brightness, keep-awake and orientation control for the current activity. */
class ScreenPlugin(context: Context, dispatcher: BridgeDispatcher) :
        WefterPlugin(context, dispatcher) {

    private val activity: FragmentActivity?
        get() = context as? FragmentActivity

    /**
     * The window's brightness from 0 to 1, falling back to the system brightness when the window does not
     * set
     * one.
     */
    @WefterMethod
    fun getBrightness(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        act.runOnUiThread {
            try {
                var brightness = act.window.attributes.screenBrightness
                if (brightness < 0) {
                    val sysBrightness =
                            Settings.System.getInt(
                                    context.contentResolver,
                                    Settings.System.SCREEN_BRIGHTNESS,
                                    128
                            )
                    brightness = sysBrightness / 255f
                }
                resolve(callback, JSONObject().put("brightness", brightness.toDouble()))
            } catch (e: Exception) {
                reject(callback, "BRIGHTNESS_FAILED", e.message ?: "Could not get screen brightness.")
            }
        }
    }

    /** Sets the window's brightness, clamped to 0–1. Only affects this app. */
    @WefterMethod
    fun setBrightness(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        if (!payload.has("brightness") || payload.isNull("brightness")) {
            reject(callback, "INVALID_ARGUMENT", "A brightness value between 0.0 and 1.0 is required.")
            return
        }

        val brightnessValue = payload.optDouble("brightness", 1.0).toFloat().coerceIn(0f, 1f)

        act.runOnUiThread {
            try {
                val layoutParams = act.window.attributes
                layoutParams.screenBrightness = brightnessValue
                act.window.attributes = layoutParams
                resolve(callback, JSONObject().put("success", true))
            } catch (e: Exception) {
                reject(callback, "BRIGHTNESS_FAILED", e.message ?: "Could not set screen brightness.")
            }
        }
    }

    /** Keeps the screen on while the activity is visible. */
    @WefterMethod
    fun keepAwake(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        act.runOnUiThread {
            try {
                act.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                resolve(callback, JSONObject().put("success", true))
            } catch (e: Exception) {
                reject(callback, "KEEPAWAKE_FAILED", e.message ?: "Could not enable keep awake.")
            }
        }
    }

    /** Lets the screen turn off again. */
    @WefterMethod
    fun allowSleep(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        act.runOnUiThread {
            try {
                act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                resolve(callback, JSONObject().put("success", true))
            } catch (e: Exception) {
                reject(callback, "ALLOWSLEEP_FAILED", e.message ?: "Could not allow screen sleep.")
            }
        }
    }

    /** The current orientation. */
    @WefterMethod
    fun getOrientation(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        act.runOnUiThread {
            try {
                val orientation = context.resources.configuration.orientation
                @Suppress("DEPRECATION") val rotation = act.windowManager.defaultDisplay.rotation
                val isPrimaryRotation =
                        rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90
                val orientationType =
                        when (orientation) {
                            Configuration.ORIENTATION_LANDSCAPE ->
                                    if (isPrimaryRotation) "landscape-primary"
                                    else "landscape-secondary"
                            Configuration.ORIENTATION_PORTRAIT ->
                                    if (isPrimaryRotation) "portrait-primary"
                                    else "portrait-secondary"
                            else -> "unknown"
                        }
                resolve(callback, JSONObject().put("type", orientationType))
            } catch (e: Exception) {
                reject(callback, "ORIENTATION_FAILED", e.message ?: "Could not get screen orientation.")
            }
        }
    }

    /** Locks the activity to an orientation. */
    @WefterMethod
    fun lockOrientation(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }
        val orientation = payload.optString("orientation", "portrait").lowercase()
        if (orientation !in OrientationLock.names) {
            reject(
                    callback,
                    "INVALID_ORIENTATION",
                    "Unknown orientation: $orientation. Valid orientations are: ${OrientationLock.names.sorted().joinToString(", ")}."
            )
            return
        }
        act.runOnUiThread {
            if (OrientationLock.lock(orientation)) {
                resolve(callback, JSONObject().put("success", true))
            } else {
                reject(callback, "LOCK_FAILED", "Could not lock orientation.")
            }
        }
    }

    /**
     * Returns the activity to the app's own orientation (`orientation` in `wefter.config.json`: portrait by
     * default), as `screen.orientation.unlock()` does in a browser.
     */
    @WefterMethod
    fun unlockOrientation(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }
        act.runOnUiThread {
            if (OrientationLock.unlock()) {
                resolve(callback, JSONObject().put("success", true))
            } else {
                reject(callback, "UNLOCK_FAILED", "Could not unlock orientation.")
            }
        }
    }
}
