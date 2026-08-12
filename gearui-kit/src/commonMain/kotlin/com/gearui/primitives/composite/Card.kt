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
import com.gearui.foundation.list.CardDefaults
import com.gearui.theme.Theme

/**
 * Card - fully Theme-driven content container
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Rework notes:
 * - the hardcoded colours in CardTokens are gone
 * - Theme.colors.surface is used directly
 * - the border uses colors.border
 *
 * Responsibilities:
 * - a rounded container
 * - consistent inner padding
 * - consistent shadow
 * - optional click interaction
 *
 * Example:
 * ```
 * Card {
 *     Text("Title")
 *     Text("Content...")
 * }
 *
 * Card(onClick = { }) {
 *     // clickable card
 * }
 * ```
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    borderColor: Color? = null,
    cornerRadius: Float = CardDefaults.Default.cornerRadius.value,
    padding: PaddingValues = PaddingValues(CardDefaults.Default.padding),
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    // Semantic colour mapping
    val finalContainerColor = containerColor ?: colors.surface
    val finalBorderColor = borderColor ?: colors.border

    val surfaceTokens = SurfaceTokens(
        height = 0.dp,  // Card 高度由内容决定
        radius = cornerRadius.dp,
        borderWidth = CardDefaults.Default.borderWidth,
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
