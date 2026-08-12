package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * HarmonyOS status bar controller.
 *
 * Empty for the same reason as the iOS actual: the status bar is owned by the
 * system, and the ArkTS host decides its appearance. If the host ever needs to
 * be driven from Kotlin, that goes through a Kuikly module rather than here.
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) = Unit
    actual fun isSystemDarkMode(): Boolean = false
}
