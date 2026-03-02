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
import com.tencent.kuikly.compose.material3.Text
import kotlin.math.roundToInt

/**
 * Progress - 100% Theme 驱动的进度条组件
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 线性进度条
 * - 环形进度条
 * - 支持状态色（成功/警告/危险）
 * - 标签显示（内部/右侧）
 * - 动画过渡
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
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val typography = Theme.typography
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
        ProgressStatus.DANGER -> colors.danger
    }

    when (labelPosition) {
        ProgressLabelPosition.RIGHT -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 进度条
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(height)
                        .clip(shapes.small)
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(progressColor)
                    )
                }

                // 右侧标签
                if (showLabel) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(normalizedProgress * 100).roundToInt()}%",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        ProgressLabelPosition.INSIDE -> {
            Box(
                modifier = modifier
                    .height(height.coerceAtLeast(24.dp))
                    .clip(shapes.small)
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // 进度条
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(progressColor)
                        .align(Alignment.CenterStart)
                )

                // 内部标签
                if (showLabel) {
                    Text(
                        text = "${(normalizedProgress * 100).roundToInt()}%",
                        style = typography.bodySmall,
                        color = if (animatedProgress > 0.5f) colors.onPrimary else colors.textPrimary
                    )
                }
            }
        }
    }
}

/**
 * CircularProgress - 环形进度条
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
    val typography = Theme.typography

    val normalizedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) normalizedProgress else normalizedProgress,
        animationSpec = tween(durationMillis = 300)
    )

    val progressColor = when (status) {
        ProgressStatus.PRIMARY -> colors.primary
        ProgressStatus.SUCCESS -> colors.success
        ProgressStatus.WARNING -> colors.warning
        ProgressStatus.DANGER -> colors.danger
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = 360f * animatedProgress

            // 背景圆环
            drawCircle(
                color = colors.surfaceVariant,
                style = Stroke(width = strokeWidth.toPx())
            )

            // 进度圆环
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

        // 中心标签
        if (showLabel) {
            Text(
                text = "${(normalizedProgress * 100).roundToInt()}%",
                style = typography.bodySmall,
                color = colors.textPrimary
            )
        }
    }
}

/**
 * ProgressStatus - 进度条状态
 */
enum class ProgressStatus {
    /** 主要色 */
    PRIMARY,

    /** 成功 */
    SUCCESS,

    /** 警告 */
    WARNING,

    /** 危险 */
    DANGER
}

/**
 * ProgressLabelPosition - 标签位置
 */
enum class ProgressLabelPosition {
    /** 内部显示 */
    INSIDE,

    /** 右侧显示 */
    RIGHT
}
