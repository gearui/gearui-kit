package com.gearui.components.watermark

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.TextStyle
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Watermark - 平铺文字水印
 *
 * 采用可组合布局实现平铺效果，避免依赖 Canvas API。
 */
@Composable
fun Watermark(
    content: String,
    modifier: Modifier = Modifier,
    alpha: Float = 0.15f,
    rotate: Float = -22f,
    gapX: Dp = 48.dp,
    gapY: Dp = 32.dp,
    offsetX: Dp = 20.dp,
    offsetY: Dp = 20.dp,
    rows: Int = 5,
    columns: Int = 3,
    textStyle: TextStyle = Typography.BodyMedium
) {
    val colors = Theme.colors
    val normalizedAlpha = alpha.coerceIn(0f, 1f)
    val lines = remember(content) {
        content.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf(content) }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = offsetX, top = offsetY),
            verticalArrangement = Arrangement.spacedBy(gapY)
        ) {
            repeat(rows.coerceAtLeast(1)) {
                Row(horizontalArrangement = Arrangement.spacedBy(gapX)) {
                    repeat(columns.coerceAtLeast(1)) {
                        Column(modifier = Modifier.rotate(rotate)) {
                            lines.forEach { line ->
                                Text(
                                    text = line,
                                    style = textStyle,
                                    color = colors.textPlaceholder.copy(alpha = normalizedAlpha)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
