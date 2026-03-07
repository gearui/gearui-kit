package com.gearui.foundation.badge

import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.foundation.BadgeSpecs

/**
 * Badge 尺寸规范
 *
 * 参考: 内部组件规范badge/td_badge.dart
 *
 * 语义：
 * Dot   → 在线/状态提示
 * Small → 1~2 位数字
 * Large → 3 位以上
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors
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
        minSize = BadgeSpecs.dotSize,
        horizontalPadding = 0.dp,
        fontSizeSp = 0,
        fontWeight = FontWeight.Normal,
        radius = BadgeSpecs.dotSize / 2
    )

    val Small = BadgeTokens(
        minSize = BadgeSpecs.Size.small,
        horizontalPadding = 4.dp,
        fontSizeSp = 10,
        fontWeight = FontWeight.Medium,
        radius = BadgeSpecs.radiusSmall
    )

    val Large = BadgeTokens(
        minSize = BadgeSpecs.Size.large,
        horizontalPadding = 6.dp,
        fontSizeSp = 12,
        fontWeight = FontWeight.SemiBold,
        radius = BadgeSpecs.radiusLarge
    )
}
