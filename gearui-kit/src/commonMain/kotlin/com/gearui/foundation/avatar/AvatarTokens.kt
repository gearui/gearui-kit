package com.gearui.foundation.avatar

import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.unit.Dp
import com.gearui.foundation.layout.Radius

/**
 * Avatar size tokens.
 *
 * Colors are NOT defined here — components should read from `Theme.colors`.
 */
data class AvatarTokens(
    val size: Dp,
    val radius: Dp,
    val textSizeSp: Int,
    val badgeOffset: Dp,
)

object AvatarSizeTokens {

    val XSmall = AvatarTokens(
        size = 24.dp,
        radius = Radius.circle,
        textSizeSp = 10,
        badgeOffset = 1.dp,
    )

    val Small = AvatarTokens(
        size = 40.dp,
        radius = Radius.circle,
        textSizeSp = 14,
        badgeOffset = 2.dp,
    )

    val Medium = AvatarTokens(
        size = 48.dp,
        radius = Radius.circle,
        textSizeSp = 16,
        badgeOffset = 3.dp,
    )

    val Large = AvatarTokens(
        size = 64.dp,
        radius = Radius.circle,
        textSizeSp = 20,
        badgeOffset = 4.dp,
    )

    val XLarge = AvatarTokens(
        size = 80.dp,
        radius = Radius.circle,
        textSizeSp = 24,
        badgeOffset = 6.dp,
    )
}
