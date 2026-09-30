package com.gearui.components.watermark

import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.foundation.layout.wrapContentWidth
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.gearui.foundation.layout.Spacing
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.TextStyle
import com.gearui.theme.Theme

/**
 * Watermark - tiled text watermark
 *
 * Tiling is built out of composable layout rather than the Canvas API.
 */
@Composable
fun Watermark(
    content: String,
    modifier: Modifier = Modifier,
    alpha: Float = 0.15f,
    rotate: Float = -22f,
    gapX: Dp = Spacing.huge,
    gapY: Dp = Spacing.xxl,
    offsetX: Dp = Spacing.lg,
    offsetY: Dp = Spacing.lg,
    rows: Int = 5,
    columns: Int = 3,
    textStyle: TextStyle = Theme.typography.bodyMedium
) {
    val colors = Theme.colors
    val normalizedAlpha = alpha.coerceIn(0f, 1f)
    val lines = remember(content) {
        content.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf(content) }
    }

    // Tiles run past the edge and are cut there; squeezed into the width, the last
    // column wrapped its text mid-word.
    Box(modifier = modifier.clipToBounds()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = offsetX, top = offsetY),
            verticalArrangement = Arrangement.spacedBy(gapY)
        ) {
            repeat(rows.coerceAtLeast(1)) {
                Row(
                    modifier = Modifier.wrapContentWidth(align = Alignment.Start, unbounded = true),
                    horizontalArrangement = Arrangement.spacedBy(gapX),
                ) {
                    repeat(columns.coerceAtLeast(1)) {
                        Column(modifier = Modifier.rotate(rotate)) {
                            lines.forEach { line ->
                                Text(
                                    text = line,
                                    style = textStyle,
                                    color = colors.mutedForeground.copy(alpha = normalizedAlpha),
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
