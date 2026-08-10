package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth

/**
 * DividerTokens - 分割线尺寸 Token
 *
 * 参考: 内部组件规范divider/td_divider.dart
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors.border
 */
data class DividerTokens(
    val thickness: Dp,
    val insetStart: Dp,
    val insetEnd: Dp
)

/**
 * Dividers - 预设分割线样式
 */
object Dividers {
    val Full = DividerTokens(
        thickness = BorderWidth.hairline,
        insetStart = Spacing.none,
        insetEnd = Spacing.none
    )

    /** 缩进分割线 (列表项) */
    val Inset = DividerTokens(
        thickness = BorderWidth.hairline,
        insetStart = Spacing.lg,
        insetEnd = Spacing.none
    )

    /** Section 分隔块 (8dp 灰色块)。这里的 thickness 是一段留白高度而非描边，所以走 Spacing。 */
    val Section = DividerTokens(
        thickness = Spacing.sm,
        insetStart = Spacing.none,
        insetEnd = Spacing.none
    )
}
