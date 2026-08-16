package dev.wefter.bridge

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import org.json.JSONObject

class ScreenPlugin(context: Context, dispatcher: BridgeDispatcher) :
        WefterPlugin(context, dispatcher) {

    private val activity: FragmentActivity?
        get() = context as? FragmentActivity

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

    @WefterMethod
    fun lockOrientation(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        val orientationStr = payload.optString("orientation", "portrait").lowercase()

        val requestedOrientation =
                when (orientationStr) {
                    "portrait", "portrait-primary" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    "portrait-secondary" -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    "landscape", "landscape-primary" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    "landscape-secondary" -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

        act.runOnUiThread {
            try {
                act.requestedOrientation = requestedOrientation
                resolve(callback, JSONObject().put("success", true))
            } catch (e: Exception) {
                reject(callback, "LOCK_FAILED", e.message ?: "Could not lock orientation.")
            }
        }
    }

    @WefterMethod
    fun unlockOrientation(payload: JSONObject, callback: (Result<Any>) -> Unit) {
        val act = activity
        if (act == null) {
            reject(callback, "NO_ACTIVITY", "Activity instance is required.")
            return
        }

        act.runOnUiThread {
            try {
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                resolve(callback, JSONObject().put("success", true))
            } catch (e: Exception) {
                reject(callback, "UNLOCK_FAILED", e.message ?: "Could not unlock orientation.")
            }
        }
    }
}
