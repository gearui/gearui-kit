package com.gearui.sample

import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Android system bar controller implementation
 * Keeps both system bars transparent and sets their icon colour; pages paint what is behind them.
 */
actual object StatusBarControllerImpl {

    private var activity: AppCompatActivity? = null

    fun register(activity: AppCompatActivity) {
        this.activity = activity
        keepSystemBarsTransparent(activity.window)
    }

    @Suppress("DEPRECATION")
    /**
     * Both system bars stay transparent: the pages draw behind them (edge-to-edge)
     * and paint their own insets, so everything under the bars moves with its page.
     */
    private fun keepSystemBarsTransparent(window: Window) {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Otherwise Android adds its own scrims "for legibility", which darken the
            // page's top and bottom a shade.
            window.isStatusBarContrastEnforced = false
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

    /** Picks light or dark status bar icons for a page whose top is [color]; the page paints the colour itself. */
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        val activity = this.activity ?: return

        activity.runOnUiThread {
            // The status bar strip is the page's own top inset (PageScaffold's
            // topSafeAreaColor), so it slides with the page on a swipe back. A colour
            // set here would be a window-level strip that stays put while the page moves
            // (and is ignored from Android 15 anyway), so only the icons change.
            keepSystemBarsTransparent(activity.window)

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
