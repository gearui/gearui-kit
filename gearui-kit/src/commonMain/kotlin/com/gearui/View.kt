package com.gearui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.setContent
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec

/**
 * View - GearUI 界面基类
 *
 * 自动挂载 GearUI Runtime：
 * - Theme（主题）
 * - GearOverlayRoot（浮层容器）
 * - ToastHost（轻提示）
 *
 * 使用方式：
 * ```kotlin
 * @Page("MainDemo")
 * class MainDemo : View() {
 *
 *     @Composable
 *     override fun Content() {
 *         MainDemoContent()
 *     }
 * }
 * ```
 *
 * 自定义主题：
 * ```kotlin
 * @Page("DarkPage")
 * class DarkPage : View() {
 *
 *     override fun themeMode() = ThemeMode.Dark
 *
 *     @Composable
 *     override fun Content() {
 *         DarkContent()
 *     }
 * }
 * ```
 */
abstract class View : ComposeContainer() {

    /**
     * 自定义主题规格（可选覆盖）
     */
    open fun themeSpec(): ThemeSpec? = null

    /**
     * 自定义主题模式（可选覆盖）
     */
    open fun themeMode(): ThemeMode? = null

    /**
     * 界面内容（子类必须实现）
     */
    @Composable
    protected abstract fun Content()

    final override fun didInit() {
        super.didInit()

        setContent {
            GearApp(
                themeMode = themeMode() ?: ThemeMode.Light,
                theme = themeSpec()
            ) {
                Content()
            }
        }
    }
}
