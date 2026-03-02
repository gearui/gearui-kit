package com.gearui.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * 状态栏控制器接口
 */
interface StatusBarController {
    /**
     * 设置状态栏颜色
     * @param color 状态栏背景颜色
     * @param darkIcons 是否使用深色图标（true = 深色图标适合浅色背景，false = 浅色图标适合深色背景）
     */
    fun setStatusBarColor(color: Color, darkIcons: Boolean)
}

/**
 * 默认空实现（用于非 Android 平台或未提供控制器时）
 */
object NoOpStatusBarController : StatusBarController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // 空实现
    }
}

/**
 * 状态栏控制器 CompositionLocal
 */
val LocalStatusBarController = staticCompositionLocalOf<StatusBarController> { NoOpStatusBarController }
