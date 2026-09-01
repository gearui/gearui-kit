package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.tencent.kuikly.compose.animation.core.LinearEasing
import com.tencent.kuikly.compose.animation.core.RepeatMode
import com.tencent.kuikly.compose.animation.core.animateFloat
import com.tencent.kuikly.compose.animation.core.infiniteRepeatable
import com.tencent.kuikly.compose.animation.core.rememberInfiniteTransition
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * LoadingIndicator - spinning circular indicator primitive
 *
 * The single spinner implementation for the whole kit. Theme-free on purpose:
 * callers pass the resolved colour so it works on any surface (page background,
 * filled button, scrim). Replaces the Material3 CircularProgressIndicator
 * that components used to borrow.
 *
 * @param size indicator diameter
 * @param color arc colour
 * @param strokeWidth arc stroke width
 * @param duration one full rotation in milliseconds
 */
@Composable
fun LoadingIndicator(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 3.dp,
    duration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.size(size)) {
        val sweepAngle = 270f
        val startAngle = rotation - 90f
        val stroke = strokeWidth.toPx()

        // Inset the drawing area by the stroke width so it is not clipped
        val arcSize = this.size.width - stroke
        val topLeft = Offset(stroke / 2, stroke / 2)

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = Size(arcSize, arcSize),
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round
            )
        )
    }
}
