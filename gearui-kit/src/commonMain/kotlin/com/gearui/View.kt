package com.gearui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.setContent
import com.gearui.runtime.RuntimeFlags
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec

/**
 * View - base class for a GearUI screen
 *
 * Mounts the GearUI Runtime automatically:
 * - Theme
 * - OverlayRoot (floating layer container)
 * - ToastHost (lightweight messages)
 *
 * Usage:
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
 * Custom theme:
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
     * Optional custom theme spec
     */
    open fun themeSpec(): ThemeSpec? = null

    /**
     * Optional custom theme mode
     */
    open fun themeMode(): ThemeMode? = null

    /**
     * Runtime feature flags (for enabling runtime capabilities gradually)
     */
    open fun runtimeFlags(): RuntimeFlags = RuntimeFlags()

    /**
     * Whether the View base class wraps App automatically.
     * On by default; override to false when a page needs to own the single App entry point itself.
     */
    open fun autoWrapApp(): Boolean = true

    /**
     * Screen content (subclasses must implement this)
     */
    @Composable
    protected abstract fun Content()

    final override fun didInit() {
        super.didInit()

        setContent {
            if (autoWrapApp()) {
                App(
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
