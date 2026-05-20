package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlinx.browser.window

/**
 * Web has no status bar. Mirror system color scheme via prefers-color-scheme;
 * setStatusBarColor is a noop.
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // noop
    }

    actual fun isSystemDarkMode(): Boolean =
        runCatching {
            window.matchMedia("(prefers-color-scheme: dark)").matches
        }.getOrDefault(false)
}
