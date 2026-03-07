package com.gearui.foundation.avatar

import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.unit.Dp
import com.gearui.foundation.AvatarSpecs
import com.gearui.foundation.layout.Radius

/**
 * Avatar 尺寸规范
 *
 * 参考: 内部组件规范avatar/td_avatar.dart
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors
 */
data class AvatarTokens(
    val size: Dp,
    val radius: Dp,
    val textSizeSp: Int,
    val badgeOffset: Dp
)

object AvatarSizeTokens {

    val XSmall = AvatarTokens(
        size = 24.dp,
        radius = Radius.circle,
        textSizeSp = 10,
        badgeOffset = 1.dp
    )

    val Small = AvatarTokens(
        size = AvatarSpecs.Size.small,
        radius = Radius.circle,
        textSizeSp = 14,
        badgeOffset = 2.dp
    )

    val Medium = AvatarTokens(
        size = AvatarSpecs.Size.medium,
        radius = Radius.circle,
        textSizeSp = 16,
        badgeOffset = 3.dp
    )

    val Large = AvatarTokens(
        size = AvatarSpecs.Size.large,
        radius = Radius.circle,
        textSizeSp = 20,
        badgeOffset = 4.dp
    )

    val XLarge = AvatarTokens(
        size = 80.dp,
        radius = Radius.circle,
        textSizeSp = 24,
        badgeOffset = 6.dp
    )
}
