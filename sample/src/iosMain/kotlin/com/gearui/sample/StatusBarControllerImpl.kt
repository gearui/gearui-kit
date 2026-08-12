package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * iOS status bar controller implementation
 * On iOS the status bar colour is controlled by the system, so this is a no-op
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // On iOS the status bar follows the system; nothing to set
    }

    actual fun isSystemDarkMode(): Boolean {
        // TODO: the system dark mode state can be read from UITraitCollection
        return false
    }
}
