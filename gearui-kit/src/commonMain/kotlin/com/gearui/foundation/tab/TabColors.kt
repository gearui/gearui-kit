package com.gearui.foundation.tab

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * @deprecated 使用 Theme.colors 替代
 *
 * Tab 颜色应通过 Theme.colors 获取：
 * - selectedText: colors.primary
 * - unselectedText: colors.textSecondary
 * - indicator: colors.primary
 * - background: colors.background
 */
@Deprecated("Use Theme.colors instead")
data class TabColors(
    val selectedText: Color,
    val unselectedText: Color,
    val indicator: Color,
    val background: Color
)

/**
 * @deprecated 使用 Theme.colors 替代
 */
@Deprecated("Use Theme.colors instead")
object TabColorTokens {

    val Default = TabColors(
        selectedText = Color(0xFF1677FF),
        unselectedText = Color(0xFF666666),
        indicator = Color(0xFF1677FF),
        background = Color.White
    )
}
