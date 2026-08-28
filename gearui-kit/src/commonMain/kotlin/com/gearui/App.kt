package com.gearui

import androidx.compose.runtime.Composable
import com.gearui.components.toast.ToastHost
import com.gearui.foundation.keyboard.KeyboardDismissContainer
import com.gearui.foundation.keyboard.KeyboardDismissMode
import com.gearui.i18n.I18nProvider
import com.gearui.i18n.StringsPatch
import com.gearui.i18n.I18nRoot
import com.gearui.overlay.OverlayRoot
import com.gearui.runtime.RuntimeFlags
import com.gearui.runtime.ProvideRuntimeEnvironment
import com.gearui.theme.ProvideSystemDarkMode
import com.gearui.theme.Theme
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier

/**
 * App - the single entry point of a GearUI application
 *
 * Wires up all GearUI infrastructure:
 * - I18n (language runtime, shared by every library)
 * - Theme (visual runtime)
 * - OverlayRoot (layering runtime)
 * - ToastHost (global lightweight messages)
 *
 * Layering:
 * ```
 * App (languageTag + themeMode + ...)
 *     ↓
 * I18nRoot (LocalLanguageTag / LocalFallbackLanguageTag)
 *     ↓
 * I18nProvider (LocalStrings)
 *     ↓
 * ProvideSystemDarkMode (system dark state)
 *     ↓
 * Theme (resolved dark boolean → colors)
 *     ↓
 * ProvideRuntimeEnvironment
 *     ↓
 * OverlayRoot + ToastHost
 *     ↓
 * application content
 * ```
 *
 * Usage:
 * ```kotlin
 * App(
 *     languageTag = userLanguage,
 *     themeMode = ThemeMode.System,
 *     isSystemDark = StatusBarControllerImpl.isSystemDarkMode(),
 *     stringsOverrides = mapOf(
 *         "en-US" to StringsPatch(common = CommonStringsPatch(confirm = "Got it")),
 *     ),
 * ) {
 *     MainPage()
 * }
 * ```
 *
 * Downstream libraries (privchat-ui and friends) only read `LocalLanguageTag.current`
 * internally to follow language changes; the application layer never passes languageTag again. See
 * `docs/I18N_INTEGRATION.md`.
 *
 * @param themeMode theme mode (Light / Dark / System)
 * @param isSystemDark whether the system is in dark mode (only used when themeMode is System)
 * @param theme optional custom theme spec
 * @param languageTag BCP47 language tag (such as "zh-Hans" or "en-US")
 * @param fallbackLanguageTag fallback used when [languageTag] matches no language pack;
 *   applies to GearUI itself and to every downstream library
 * @param stringsOverrides field-level overrides of GearUI's built-in copy, grouped by language tag
 * @param runtimeFlags Runtime behaviour switches
 * @param keyboardDismissMode focus-loss policy for text fields (by default tapping empty space or scrolling hides the keyboard)
 */
@Composable
fun App(
    themeMode: ThemeMode = ThemeMode.Light,
    isSystemDark: Boolean = false,
    theme: ThemeSpec? = null,
    languageTag: String = "en-US",
    fallbackLanguageTag: String = "en-US",
    stringsOverrides: Map<String, StringsPatch> = emptyMap(),
    runtimeFlags: RuntimeFlags = RuntimeFlags(),
    keyboardDismissMode: KeyboardDismissMode = KeyboardDismissMode.OnTapOrScroll,
    content: @Composable () -> Unit,
) {
    I18nRoot(languageTag = languageTag, fallbackLanguageTag = fallbackLanguageTag) {
        I18nProvider(overrides = stringsOverrides) {
            ProvideSystemDarkMode(isSystemDark = isSystemDark) {
                Theme(mode = themeMode, theme = theme) {
                    ProvideRuntimeEnvironment(flags = runtimeFlags) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Theme.colors.background)
                        ) {
                            KeyboardDismissContainer(mode = keyboardDismissMode) {
                                OverlayRoot {
                                    content()
                                    ToastHost()
                                    // 🔴 命令式 ActionSheet 的**唯一**宿主。
                                    //
                                    // ActionSheet 是全局单例状态；宿主若靠各页面自己挂
                                    // （旧约定「Must be placed at the root of the page」），
                                    // 后果有两类，都在生产复现过：
                                    // 1. 从没挂 Host 的页面调 showList → 当页什么都不出，
                                    //    返回到挂了 Host 的页面时弹层突然冒出来；
                                    // 2. 多个存活页面各挂一份 Host → 同一份状态被 show 成
                                    //    多个 overlay 叠在栈里。
                                    // 全局只此一份，弹层永远在最上层、同一时刻只有一个。
                                    com.gearui.components.actionsheet.ActionSheet.Host()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
