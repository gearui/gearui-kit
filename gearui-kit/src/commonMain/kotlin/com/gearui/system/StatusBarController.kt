package com.gearui.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Status bar controller interface
 */
interface StatusBarController {
    /**
     * Sets the status bar colour
     * @param color status bar background colour
     * @param darkIcons whether to use dark icons (true = dark icons, for a light background; false = light icons, for a dark background)
     */
    fun setStatusBarColor(color: Color, darkIcons: Boolean)
}

/**
 * Default no-op implementation (for non-Android platforms, or when no controller is supplied)
 */
object NoOpStatusBarController : StatusBarController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // No-op
    }
}

/**
 * Status bar controller CompositionLocal
 */
val LocalStatusBarController = staticCompositionLocalOf<StatusBarController> { NoOpStatusBarController }
