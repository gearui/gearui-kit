package com.gearui.foundation.tab

import com.tencent.kuikly.compose.ui.unit.*
import com.gearui.foundation.layout.Spacing

/**
 * Tab sizing spec
 *
 * Reference: the GearUI component sizing spec
 *
 * ⚠️ Note: colours are not defined here; use Theme.colors
 */
data class TabTokens(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconTextSpacing: Dp,
    val indicatorHeight: Dp,
    val textSizeSp: Int
)

object TabSizeTokens {

    val Small = TabTokens(
        height = 36.dp,
        horizontalPadding = Spacing.md,
        iconTextSpacing = 4.dp,
        indicatorHeight = 2.dp,
        textSizeSp = 12
    )

    val Medium = TabTokens(
        height = 48.dp,
        horizontalPadding = Spacing.lg,
        iconTextSpacing = 6.dp,
        indicatorHeight = 2.dp,
        textSizeSp = 14
    )

    val Large = TabTokens(
        height = 52.dp,
        horizontalPadding = 20.dp,
        iconTextSpacing = 8.dp,
        indicatorHeight = 2.dp,
        textSizeSp = 16
    )
}
