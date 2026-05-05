package com.gearui

import androidx.compose.runtime.Composable
import com.gearui.components.toast.ToastHost
import com.gearui.foundation.keyboard.KeyboardDismissContainer
import com.gearui.foundation.keyboard.KeyboardDismissMode
import com.gearui.i18n.I18nProvider
import com.gearui.i18n.StringsPatch
import com.gearui.i18n.I18nRoot
import com.gearui.overlay.OverlayRoot
import com.gearui.runtime.GearRuntimeFlags
import com.gearui.runtime.ProvideGearRuntimeEnvironment
import com.gearui.theme.ProvideSystemDarkMode
import com.gearui.theme.Theme
import com.gearui.theme.ThemeMode
import com.gearui.theme.ThemeSpec
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier

/**
 * GearApp - GearUI 应用统一入口
 *
 * 整合所有 GearUI 基础设施：
 * - I18n（语言运行时，对所有库共享）
 * - Theme（视觉 runtime）
 * - OverlayRoot（层级 runtime）
 * - ToastHost（全局轻提示）
 *
 * 架构层级：
 * ```
 * GearApp (languageTag + themeMode + ...)
 *     ↓
 * I18nRoot (LocalLanguageTag / LocalFallbackLanguageTag)
 *     ↓
 * I18nProvider (LocalStrings)
 *     ↓
 * ProvideSystemDarkMode (系统深色状态)
 *     ↓
 * Theme (resolved dark boolean → colors)
 *     ↓
 * ProvideGearRuntimeEnvironment
 *     ↓
 * OverlayRoot + ToastHost
 *     ↓
 * 应用内容
 * ```
 *
 * 使用方式：
 * ```kotlin
 * GearApp(
 *     languageTag = userLanguage,
 *     themeMode = ThemeMode.System,
 *     isSystemDark = StatusBarControllerImpl.isSystemDarkMode(),
 *     stringsOverrides = mapOf(
 *         "zh-Hans" to StringsPatch(buttonConfirm = "确定一下"),
 *     ),
 * ) {
 *     MainPage()
 * }
 * ```
 *
 * 上层库（privchat-ui 等）只需在内部读 `LocalLanguageTag.current` 即可
 * 自动响应语言切换；不需要应用层重复传 languageTag。详见
 * `docs/I18N_INTEGRATION.md`。
 *
 * @param themeMode 主题模式 (Light/Dark/System)
 * @param isSystemDark 系统是否为深色模式（仅当 themeMode 为 System 时生效）
 * @param theme 自定义主题规格（可选）
 * @param languageTag BCP47 语言标签（如 "zh-Hans"、"en-US"）
 * @param fallbackLanguageTag 当 [languageTag] 没有匹配语言包时的 fallback；
 *   作用于 GearUI 自身和所有上层库
 * @param stringsOverrides 字段级覆盖 GearUI 内置文案，按语言 tag 分组
 * @param runtimeFlags Runtime 行为开关
 * @param keyboardDismissMode 输入框失焦策略（默认点击空白或滚动时自动收起键盘）
 */
@Composable
fun GearApp(
    themeMode: ThemeMode = ThemeMode.Light,
    isSystemDark: Boolean = false,
    theme: ThemeSpec? = null,
    languageTag: String = "en-US",
    fallbackLanguageTag: String = "en-US",
    stringsOverrides: Map<String, StringsPatch> = emptyMap(),
    runtimeFlags: GearRuntimeFlags = GearRuntimeFlags(),
    keyboardDismissMode: KeyboardDismissMode = KeyboardDismissMode.OnTapOrScroll,
    content: @Composable () -> Unit,
) {
    I18nRoot(languageTag = languageTag, fallbackLanguageTag = fallbackLanguageTag) {
        I18nProvider(overrides = stringsOverrides) {
            ProvideSystemDarkMode(isSystemDark = isSystemDark) {
                Theme(mode = themeMode, theme = theme) {
                    ProvideGearRuntimeEnvironment(flags = runtimeFlags) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Theme.colors.background)
                        ) {
                            KeyboardDismissContainer(mode = keyboardDismissMode) {
                                OverlayRoot {
                                    content()
                                    ToastHost()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
