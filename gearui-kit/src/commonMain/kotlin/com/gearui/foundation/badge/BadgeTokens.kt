package com.gearui.foundation.badge

import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/**
 * Badge size tokens.
 *
 * Semantics:
 *   Dot   → online / status indicator
 *   Small → 1-2 digit count
 *   Large → 3+ digit count
 *
 * Colors are NOT defined here — components should read from `Theme.colors`.
 */
data class BadgeTokens(
    val minSize: Dp,
    val horizontalPadding: Dp,
    val fontSizeSp: Int,
    val fontWeight: FontWeight,
    val radius: Dp,
)

object BadgeSizeTokens {

    val Dot = BadgeTokens(
        minSize = 8.dp,
        horizontalPadding = 0.dp,
        fontSizeSp = 0,
        fontWeight = FontWeight.Normal,
        radius = 4.dp,
    )

    val Small = BadgeTokens(
        minSize = 16.dp,
        horizontalPadding = 4.dp,
        fontSizeSp = 10,
        fontWeight = FontWeight.Medium,
        radius = 2.dp,
    )

    val Large = BadgeTokens(
        minSize = 20.dp,
        horizontalPadding = 6.dp,
        fontSizeSp = 12,
        fontWeight = FontWeight.SemiBold,
        radius = 8.dp,
    )
}
