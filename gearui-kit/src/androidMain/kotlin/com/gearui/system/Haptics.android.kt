package com.gearui.system

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

internal actual val platformHaptics: HapticFeedback? = null

/**
 * Android's selection tap, for [Haptics.host]: the system's lightest predefined effect
 * (TICK), which follows the user's touch-feedback setting.
 */
class AndroidHapticFeedback(context: Context) : HapticFeedback {
    private val app: Context = context.applicationContext

    // The host declares the permission; a host that did not gets no taps rather than an
    // exception.
    private val permitted: Boolean =
        app.checkCallingOrSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED

    private val vibrator: Vibrator? = run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @SuppressLint("MissingPermission") // checked once, in [permitted]
    override fun selectionChanged() {
        if (!permitted) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(TICK_MILLIS)
        }
    }

    private companion object {
        const val TICK_MILLIS = 12L
    }
}
