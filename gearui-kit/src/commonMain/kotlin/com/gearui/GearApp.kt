package com.gearui

import androidx.compose.runtime.Composable
import com.gearui.components.toast.ToastHost
import com.gearui.foundation.keyboard.KeyboardDismissContainer
import com.gearui.foundation.keyboard.KeyboardDismissMode
import com.gearui.overlay.GearOverlayRoot
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
 * - Theme（视觉 runtime）
 * - GearOverlayRoot（层级 runtime）
 * - ToastHost（全局轻提示）
 *
 * 架构层级：
 * ```
 * GearApp (ThemeMode + isSystemDark)
 *     ↓
 * ProvideSystemDarkMode (系统深色状态)
 *     ↓
 * Theme (resolved dark boolean → colors)
 *     ↓
 * GearOverlayRoot + ToastHost
 *     ↓
 * 应用内容
 * ```
 *
 * 使用方式：
 * ```kotlin
 * setContent {
 *     val isSystemDark = StatusBarControllerImpl.isSystemDarkMode()
 *     GearApp(
 *         themeMode = ThemeMode.System,
 *         isSystemDark = isSystemDark
 *     ) {
 *         MainPage()
 *     }
 * }
 * ```
 *
 * @param themeMode 主题模式 (Light/Dark/System)
 * @param isSystemDark 系统是否为深色模式（仅当 themeMode 为 System 时生效）
 * @param theme 自定义主题规格（可选）
 * @param keyboardDismissMode 输入框失焦策略（默认点击空白或滚动时自动收起键盘）
 */
@Composable
fun GearApp(
    themeMode: ThemeMode = ThemeMode.Light,
    isSystemDark: Boolean = false,
    theme: ThemeSpec? = null,
    keyboardDismissMode: KeyboardDismissMode = KeyboardDismissMode.OnTapOrScroll,
    content: @Composable () -> Unit
) {
    // 1. 提供系统深色模式状态
    ProvideSystemDarkMode(isSystemDark = isSystemDark) {
        // 2. 应用主题
        Theme(mode = themeMode, theme = theme) {
            // 3. 全局背景容器（使用主题背景色）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Theme.colors.background)
            ) {
                KeyboardDismissContainer(mode = keyboardDismissMode) {
                    // 4. Overlay 层级管理
                    GearOverlayRoot {
                        // 应用主内容
                        content()

                        // 全局浮层 - Toast
                        ToastHost()
                    }
                }
            }
        }
    }
}
