package com.gearui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.setContent
import com.gearui.runtime.RuntimeFlags
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec

/**
 * View - GearUI 界面基类
 *
 * 自动挂载 GearUI Runtime：
 * - Theme（主题）
 * - OverlayRoot（浮层容器）
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
     * Runtime feature flags（用于渐进启用 runtime 规范能力）
     */
    open fun runtimeFlags(): RuntimeFlags = RuntimeFlags()

    /**
     * 是否由 View 基类自动包装 GearApp。
     * 默认开启；如页面需要自行控制唯一 GearApp 入口，可覆写为 false。
     */
    open fun autoWrapGearApp(): Boolean = true

    /**
     * 界面内容（子类必须实现）
     */
    @Composable
    protected abstract fun Content()

    final override fun didInit() {
        super.didInit()

        setContent {
            if (autoWrapGearApp()) {
                GearApp(
                    themeMode = themeMode() ?: ThemeMode.Light,
                    theme = themeSpec(),
                    runtimeFlags = runtimeFlags()
                ) {
                    Content()
                }
            } else {
                Content()
            }
        }
    }
}
