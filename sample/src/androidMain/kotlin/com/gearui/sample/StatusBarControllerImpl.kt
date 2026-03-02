package com.gearui.sample

import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.toArgb

/**
 * Android 系统栏控制器实现
 * 控制状态栏和导航栏（输入法背景）颜色
 */
actual object StatusBarControllerImpl {

    private var activity: AppCompatActivity? = null

    fun register(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun unregister() {
        this.activity = null
    }

    /**
     * 检测系统是否为深色模式
     */
    actual fun isSystemDarkMode(): Boolean {
        val activity = this.activity ?: return false
        val uiMode = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        val activity = this.activity ?: return

        activity.runOnUiThread {
            // 设置状态栏背景颜色
            activity.window.statusBarColor = color.toArgb()

            // 设置导航栏背景颜色（输入法弹出时的背景）
            activity.window.navigationBarColor = color.toArgb()

            // 设置系统栏图标颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = activity.window.insetsController
                if (darkIcons) {
                    // 浅色背景 - 深色图标
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    // 深色背景 - 浅色图标
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
