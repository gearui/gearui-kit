package com.gearui.sample

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Web 状态栏控制器：浏览器没有系统状态栏，全部空实现。
 * Spike 用最小实现，仅为让 JS 目标能编译。
 */
actual object StatusBarControllerImpl {
    actual fun setStatusBarColor(color: Color, darkIcons: Boolean) = Unit
    actual fun isSystemDarkMode(): Boolean = false
}
