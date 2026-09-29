package com.gearui.sample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.graphics.Color
import kotlinx.browser.window

/** Browser colour-scheme state; only the status-bar setter is a no-op on Web. */
actual object StatusBarControllerImpl {
    private val colorScheme = window.asDynamic().matchMedia("(prefers-color-scheme: dark)")
    private var systemDark by mutableStateOf(colorScheme.matches as Boolean)

    init {
        colorScheme.addEventListener("change", { event: dynamic ->
            systemDark = event.matches as Boolean
        })
    }

    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) = Unit
    actual fun isSystemDarkMode(): Boolean = systemDark
}
