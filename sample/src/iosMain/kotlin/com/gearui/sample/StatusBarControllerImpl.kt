package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * iOS 状态栏控制器实现
 * iOS 上状态栏颜色由系统控制，这里提供空实现
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // iOS 上状态栏颜色跟随系统，不需要手动设置
    }

    actual fun isSystemDarkMode(): Boolean {
        // TODO: 可以通过 UITraitCollection 获取系统暗色模式状态
        return false
    }
}
