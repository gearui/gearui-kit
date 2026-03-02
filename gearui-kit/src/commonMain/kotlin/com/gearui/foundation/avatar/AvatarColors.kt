package com.gearui.foundation.avatar

import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * @deprecated 使用 Theme.colors 替代
 *
 * Avatar 颜色应通过 Theme.colors 获取：
 * - background: colors.surfaceVariant
 * - content: colors.textSecondary
 */
@Deprecated("Use Theme.colors instead")
data class AvatarColors(
    val background: Color,
    val content: Color
)

/**
 * @deprecated 使用 Theme.colors 替代
 */
@Deprecated("Use Theme.colors instead")
object AvatarColorTokens {

    val Default = AvatarColors(
        background = Color(0xFFF2F3F5),
        content = Color(0xFF666666)
    )

    val Primary = AvatarColors(
        background = Color(0xFF1677FF),
        content = Color.White
    )
}
