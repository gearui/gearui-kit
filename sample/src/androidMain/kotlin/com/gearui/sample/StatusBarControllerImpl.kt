package com.gearui.sample

import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.toArgb

/**
 * Android system bar controller implementation
 * Tints the status bar and keeps the bottom system navigation bar transparent.
 */
actual object StatusBarControllerImpl {

    private var activity: AppCompatActivity? = null

    fun register(activity: AppCompatActivity) {
        this.activity = activity
        keepNavigationBarTransparent(activity.window)
    }

    @Suppress("DEPRECATION")
    private fun keepNavigationBarTransparent(window: Window) {
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Otherwise Android may add its own three-button navigation scrim.
            window.isNavigationBarContrastEnforced = false
        }
    }

    fun unregister() {
        this.activity = null
    }

    /**
     * Whether the system is in dark mode
     */
    actual fun isSystemDarkMode(): Boolean {
        val activity = this.activity ?: return false
        val uiMode = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        val activity = this.activity ?: return

        activity.runOnUiThread {
            // Status bar background colour
            activity.window.statusBarColor = color.toArgb()

            // Theme changes must not paint a separate strip over the page background.
            keepNavigationBarTransparent(activity.window)

            // System bar icon colour
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = activity.window.insetsController
                if (darkIcons) {
                    // Light background - dark icons
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    // Dark background - light icons
                    controller?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                if (darkIcons) {
                    activity.window.decorView.systemUiVisibility =
                        activity.window.decorView.systemUiVisibility or
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    activity.window.decorView.systemUiVisibility =
                        activity.window.decorView.systemUiVisibility and
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
            }
        }
    }
}
