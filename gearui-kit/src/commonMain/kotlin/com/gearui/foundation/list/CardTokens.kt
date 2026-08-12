package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing

/**
 * CardTokens - Card sizing spec
 *
 *
 * ⚠️ Note: colours are not defined here; use Theme.colors.surface
 *
 * Use cases:
 * - aggregated information
 * - form groups
 * - content modules
 */
data class CardTokens(
    val cornerRadius: Dp,
    val padding: Dp,
    val borderWidth: Dp,
    val elevation: Float
)

object CardDefaults {
    /**
     */
    val Default = CardTokens(
        cornerRadius = 8.dp,      // lg — GearUI 卡片默认圆角（冻结后 large→lg）
        padding = Spacing.md,
        borderWidth = BorderWidth.hairline,     // border-first：发丝级描边，不引入 elevation 语义
        elevation = 0f
    )

    /**
     * Flat card (no shadow)
     * For layouts that already separate layers with background colour
     */
    val Flat = Default.copy(
        elevation = 0f
    )

    /**
     * Compact card
     * Less padding, denser information
     */
    val Compact = Default.copy(
        padding = Spacing.md
    )
}
