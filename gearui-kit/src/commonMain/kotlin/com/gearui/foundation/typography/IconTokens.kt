package com.gearui.foundation.typography

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * IconTokens - 图标尺寸 Token
 *
 * 统一图标尺寸规范:
 * - small: 14dp (Caption/Label 级别文字旁)
 * - medium: 18dp (Body 级别文字旁,最常用)
 * - large: 24dp (Title 级别文字旁/独立图标)
 *
 * 使用原则:
 * - ✅ 强制使用 IconSizes.Default.medium
 * - ❌ 禁止硬编码 .size(18.dp)
 */
data class IconTokens(
    val small: Dp,
    val medium: Dp,
    val large: Dp
)

object IconSizes {
    val Default = IconTokens(
        small = 14.dp,
        medium = 18.dp,
        large = 24.dp
    )
}
