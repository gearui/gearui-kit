package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.BorderStroke
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.ColumnScope
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.interaction.createMutableInteractionSource
import com.gearui.foundation.primitives.Surface
import com.gearui.foundation.primitives.SurfaceColorTokens
import com.gearui.foundation.primitives.SurfaceTokens
import com.gearui.foundation.CardSpecs
import com.gearui.theme.Theme

/**
 * Card - 100% Theme 驱动的内容容器卡片
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 改造要点：
 * - 移除 CardTokens 的颜色硬编码
 * - 直接使用 Theme.colors.surface
 * - 边框使用 colors.border
 *
 * 职责：
 * - 提供圆角容器
 * - 统一内边距
 * - 统一阴影
 * - 可选点击交互
 *
 * Example:
 * ```
 * Card {
 *     Text("Title")
 *     Text("Content...")
 * }
 *
 * Card(onClick = { }) {
 *     // 可点击卡片
 * }
 * ```
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    borderColor: Color? = null,
    cornerRadius: Float = CardSpecs.radius.value,
    padding: PaddingValues = PaddingValues(CardSpecs.padding),
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    // 颜色语义映射
    val finalContainerColor = containerColor ?: colors.surface
    val finalBorderColor = borderColor ?: colors.border

    val surfaceTokens = SurfaceTokens(
        height = 0.dp,  // Card 高度由内容决定
        radius = cornerRadius.dp,
        borderWidth = CardSpecs.borderWidth,
        padding = PaddingValues(0.dp)  // Card 自己控制 padding
    )

    val surfaceColors = SurfaceColorTokens(
        background = finalContainerColor,
        disabledBackground = finalContainerColor,
        pressedBackground = finalContainerColor,
        border = finalBorderColor
    )

    Surface(
        modifier = modifier,
        enabled = enabled,
        tokens = surfaceTokens,
        colors = surfaceColors,
        interactionSource = interactionSource,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}
