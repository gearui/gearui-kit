package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.ColumnScope
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.interaction.createMutableInteractionSource
import com.gearui.foundation.primitives.SurfaceWithShape
import com.gearui.foundation.primitives.SurfaceColorTokens
import com.gearui.foundation.primitives.SurfaceTokens
import com.gearui.foundation.list.CardDefaults
import com.gearui.theme.Theme
import com.gearui.theme.Shapes
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.material.DecoratedSurface
import com.gearui.foundation.material.surfaceShadowStyles
import com.gearui.foundation.material.SurfaceBorder
import com.gearui.foundation.border.BorderWidth

/** NaN means follow the active theme. Explicit finite radii remain local overrides. */
internal fun resolveCardShape(cornerRadius: Float, shapes: Shapes): Shape =
    if (!cornerRadius.isFinite()) shapes.xl else RoundedCornerShape(cornerRadius.coerceAtLeast(0f).dp)

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
    cornerRadius: Float = Float.NaN,
    padding: PaddingValues = PaddingValues(CardDefaults.Default.padding),
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = Theme.colors
    val shape = resolveCardShape(cornerRadius, Theme.shapes)

    // Semantic colour mapping
    val finalContainerColor = containerColor ?: colors.surface
    val finalBorderColor = borderColor ?: colors.border

    val surfaceTokens = SurfaceTokens(
        height = 0.dp,  // Content determines the height.
        radius = CardDefaults.Default.cornerRadius,
        borderWidth = BorderWidth.none,
        padding = PaddingValues(0.dp)  // The content column owns padding.
    )

    val surfaceColors = SurfaceColorTokens(
        background = finalContainerColor,
        disabledBackground = finalContainerColor,
        pressedBackground = finalContainerColor,
        border = finalBorderColor,
        disabledBorder = finalBorderColor
    )

    DecoratedSurface(
        modifier = modifier.graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity },
        shape = shape,
        shadows = if (enabled) surfaceShadowStyles().surface else emptyList(),
        border = SurfaceBorder(finalBorderColor, CardDefaults.Default.borderWidth),
    ) {
    SurfaceWithShape(
        modifier = Modifier,
        enabled = enabled,
        tokens = surfaceTokens,
        colors = surfaceColors,
        interactionSource = interactionSource,
        onClick = onClick,
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
    }
}
