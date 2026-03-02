package com.gearui.foundation.badge

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * @deprecated 使用 Theme.colors 替代
 *
 * Badge 颜色应通过 Theme.colors 获取：
 * - Error: colors.danger + colors.onPrimary
 * - Primary: colors.primary + colors.onPrimary
 * - Success: colors.success + colors.onPrimary
 */
@Deprecated("Use Theme.colors instead")
data class BadgeColors(
    val background: Color,
    val content: Color
)

/**
 * @deprecated 使用 Theme.colors 替代
 */
@Deprecated("Use Theme.colors instead")
object BadgeColorTokens {

    val Error = BadgeColors(
        background = Color(0xFFFF3B30),
        content = Color.White
    )

    val Primary = BadgeColors(
        background = Color(0xFF1677FF),
        content = Color.White
    )

    val Success = BadgeColors(
        background = Color(0xFF52C41A),
        content = Color.White
    )

    val Neutral = BadgeColors(
        background = Color(0xFF999999),
        content = Color.White
    )
}
