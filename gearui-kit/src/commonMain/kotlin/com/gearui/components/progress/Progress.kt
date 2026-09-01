package com.gearui.components.progress

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import kotlin.math.roundToInt
import com.gearui.foundation.layout.Spacing

/**
 * Progress - fully Theme-driven progress bar
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - linear progress bar
 * - circular progress bar
 * - status colours (success / warning / danger)
 * - label placement (inside / right)
 * - animated transitions
 */
@Composable
fun LinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    status: ProgressStatus = ProgressStatus.PRIMARY,
    showLabel: Boolean = true,
    labelPosition: ProgressLabelPosition = ProgressLabelPosition.RIGHT,
    height: Dp = 8.dp,
    animated: Boolean = true
) {
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val shapes = Theme.shapes

    val normalizedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) normalizedProgress else normalizedProgress,
        animationSpec = tween(durationMillis = 300)
    )

    val progressColor = when (status) {
        ProgressStatus.PRIMARY -> colors.primary
        ProgressStatus.SUCCESS -> colors.success
        ProgressStatus.WARNING -> colors.warning
        ProgressStatus.DANGER -> colors.destructive
    }

    // Colour of the text inside the bar (which sits on solid progressColor past 50%), taken from the matching status foreground
    val progressForeground = when (status) {
        ProgressStatus.PRIMARY -> colors.primaryForeground
        ProgressStatus.SUCCESS -> colors.successForeground
        ProgressStatus.WARNING -> colors.warningForeground
        ProgressStatus.DANGER -> colors.destructiveForeground
    }

    when (labelPosition) {
        ProgressLabelPosition.RIGHT -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(height)
                        .clip(shapes.sm)
                        .background(colors.muted)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(progressColor)
                    )
                }

                // Right-hand label
                if (showLabel) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "${(normalizedProgress * 100).roundToInt()}%",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        ProgressLabelPosition.INSIDE -> {
            Box(
                modifier = modifier
                    .height(height.coerceAtLeast(24.dp))
                    .clip(shapes.sm)
                    .background(colors.muted),
                contentAlignment = Alignment.Center
            ) {
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(progressColor)
                        .align(Alignment.CenterStart)
                )

                // Inline label
                if (showLabel) {
                    Text(
                        text = "${(normalizedProgress * 100).roundToInt()}%",
                        style = Typography.BodySmall,
                        color = if (animatedProgress > 0.5f) progressForeground else colors.foreground
                    )
                }
            }
        }
    }
}

/**
 * CircularProgress - circular progress bar
 */
@Composable
fun CircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    status: ProgressStatus = ProgressStatus.PRIMARY,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    showLabel: Boolean = true,
    animated: Boolean = true
) {
    val colors = Theme.colors

    val normalizedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) normalizedProgress else normalizedProgress,
        animationSpec = tween(durationMillis = 300)
    )

    val progressColor = when (status) {
        ProgressStatus.PRIMARY -> colors.primary
        ProgressStatus.SUCCESS -> colors.success
        ProgressStatus.WARNING -> colors.warning
        ProgressStatus.DANGER -> colors.destructive
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = 360f * animatedProgress

            // Background ring
            drawCircle(
                color = colors.muted,
                style = Stroke(width = strokeWidth.toPx())
            )

            // Progress ring
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // Centre label
        if (showLabel) {
            Text(
                text = "${(normalizedProgress * 100).roundToInt()}%",
                style = Typography.BodySmall,
                color = colors.foreground
            )
        }
    }
}

/**
 * ProgressStatus - progress bar status
 */
enum class ProgressStatus {
    /** primary */
    PRIMARY,

    /** success */
    SUCCESS,

    /** warning */
    WARNING,

    /** danger */
    DANGER
}

/**
 * ProgressLabelPosition - label placement
 */
enum class ProgressLabelPosition {
    /** inside the bar */
    INSIDE,

    /** to the right */
    RIGHT
}
