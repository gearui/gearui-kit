package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Web status bar controller: browsers have no system status bar, so everything is a no-op.
 * A minimal spike implementation, only so the JS target compiles.
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) = Unit
    actual fun isSystemDarkMode(): Boolean = false
}
