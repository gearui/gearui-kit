package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth

/**
 * DividerTokens - divider sizing tokens
 *
 * Reference: internal component spec divider/td_divider.dart
 *
 * ⚠️ Note: colours are not defined here; use Theme.colors.border
 */
data class DividerTokens(
    val thickness: Dp,
    val insetStart: Dp,
    val insetEnd: Dp
)

/**
 * Dividers - preset divider styles
 */
object Dividers {
    val Full = DividerTokens(
        thickness = BorderWidth.hairline,
        insetStart = Spacing.none,
        insetEnd = Spacing.none
    )

    /** inset divider (list rows) */
    val Inset = DividerTokens(
        thickness = BorderWidth.hairline,
        insetStart = Spacing.lg,
        insetEnd = Spacing.none
    )

    /** Section separator block (an 8dp grey block). The thickness here is a height of blank space rather than a stroke, so it comes from Spacing. */
    val Section = DividerTokens(
        thickness = Spacing.sm,
        insetStart = Spacing.none,
        insetEnd = Spacing.none
    )
}
