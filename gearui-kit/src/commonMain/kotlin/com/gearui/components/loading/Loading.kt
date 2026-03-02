package com.gearui.components.loading

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Size
import com.gearui.theme.Theme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.gearui.foundation.typography.Typography

/**
 * Loading - 加载指示器组件
 *
 * - 三种图标类型：圆形(circle)、点状(point)、菊花状(activity)
 * - 三种尺寸：小/中/大
 * - 横向/纵向布局
 * - 可选文字说明
 * - 自定义颜色和动画速度
 *
 * @param size 尺寸
 * @param icon 图标类型
 * @param text 文字说明
 * @param layout 布局方向
 * @param color 自定义颜色
 * @param duration 动画周期（毫秒）
 */
@Composable
fun Loading(
    modifier: Modifier = Modifier,
    size: LoadingSize = LoadingSize.MEDIUM,
    icon: LoadingIcon = LoadingIcon.CIRCLE,
    text: String? = null,
    layout: LoadingLayout = LoadingLayout.VERTICAL,
    color: Color? = null,
    duration: Int = 1000
) {
    val colors = Theme.colors

    val indicatorColor = color ?: colors.primary
    val indicatorSize = when (size) {
        LoadingSize.SMALL -> 18.dp
        LoadingSize.MEDIUM -> 21.dp
        LoadingSize.LARGE -> 24.dp
    }
    val strokeWidth = when (size) {
        LoadingSize.SMALL -> 3.dp
        LoadingSize.MEDIUM -> 3.5.dp
        LoadingSize.LARGE -> 4.dp
    }

    val content: @Composable () -> Unit = {
        // 图标
        when (icon) {
            LoadingIcon.CIRCLE -> {
                CircularLoadingIndicator(
                    size = indicatorSize,
                    color = indicatorColor,
                    strokeWidth = strokeWidth,
                    duration = duration
                )
            }
            LoadingIcon.ACTIVITY -> {
                ActivityLoadingIndicator(
                    size = indicatorSize,
                    color = indicatorColor,
                    duration = duration
                )
            }
            LoadingIcon.POINT -> {
                PointLoadingIndicator(
                    size = indicatorSize,
                    color = indicatorColor,
                    duration = duration
                )
            }
        }

        // 文字
        if (text != null) {
            val spacing = when (size) {
                LoadingSize.SMALL -> 6.dp
                LoadingSize.MEDIUM -> 8.dp
                LoadingSize.LARGE -> 10.dp
            }

            Spacer(
                modifier = if (layout == LoadingLayout.HORIZONTAL) {
                    Modifier.width(spacing)
                } else {
                    Modifier.height(spacing)
                }
            )

            Text(
                text = text,
                style = when (size) {
                    LoadingSize.SMALL -> Typography.BodySmall
                    LoadingSize.MEDIUM -> Typography.BodyMedium
                    LoadingSize.LARGE -> Typography.BodyLarge
                },
                color = colors.textSecondary
            )
        }
    }

    when (layout) {
        LoadingLayout.HORIZONTAL -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }

        LoadingLayout.VERTICAL -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}

/**
 * CircularLoadingIndicator - 圆形旋转加载指示器
 */
@Composable
private fun CircularLoadingIndicator(
    size: Dp,
    color: Color,
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

    Canvas(modifier = Modifier.size(size)) {
        val sweepAngle = 270f
        val startAngle = rotation - 90f
        val stroke = strokeWidth.toPx()

        // 计算绘制区域，留出 stroke 的边距防止裁切
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

/**
 * ActivityLoadingIndicator - 菊花状加载指示器（类似 iOS 风格）
 */
@Composable
private fun ActivityLoadingIndicator(
    size: Dp,
    color: Color,
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

    val petalCount = 12

    Canvas(modifier = Modifier.size(size)) {
        val centerX = this.size.width / 2
        val centerY = this.size.height / 2
        val outerRadius = this.size.width / 2 * 0.9f
        val innerRadius = this.size.width / 2 * 0.4f
        val petalWidth = this.size.width / 10

        for (i in 0 until petalCount) {
            val angle = (i * 360f / petalCount + rotation) * PI.toFloat() / 180f
            val alpha = (i + 1).toFloat() / petalCount

            val startX = centerX + innerRadius * cos(angle)
            val startY = centerY + innerRadius * sin(angle)
            val endX = centerX + outerRadius * cos(angle)
            val endY = centerY + outerRadius * sin(angle)

            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = petalWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * PointLoadingIndicator - 点状加载指示器（三点跳动）
 */
@Composable
private fun PointLoadingIndicator(
    size: Dp,
    color: Color,
    duration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition()

    // 三个点的动画，错开 1/3 周期
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                0f at 0
                1f at duration / 4
                0f at duration / 2
                0f at duration
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                0f at 0
                0f at duration / 6
                1f at duration / 6 + duration / 4
                0f at duration / 6 + duration / 2
                0f at duration
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val offset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                0f at 0
                0f at duration / 3
                1f at duration / 3 + duration / 4
                0f at duration / 3 + duration / 2
                0f at duration
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Row(
        modifier = Modifier.height(size),
        horizontalArrangement = Arrangement.spacedBy(size / 4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotSize = size / 3
        val maxOffset = size / 4

        Canvas(modifier = Modifier.size(dotSize).offset(y = -maxOffset * offset1)) {
            drawCircle(color = color)
        }
        Canvas(modifier = Modifier.size(dotSize).offset(y = -maxOffset * offset2)) {
            drawCircle(color = color)
        }
        Canvas(modifier = Modifier.size(dotSize).offset(y = -maxOffset * offset3)) {
            drawCircle(color = color)
        }
    }
}

/**
 * Loading 尺寸枚举
 */
enum class LoadingSize {
    /** 小尺寸 - 18dp */
    SMALL,

    /** 中尺寸 - 21dp (默认) */
    MEDIUM,

    /** 大尺寸 - 24dp */
    LARGE
}

/**
 * Loading 图标类型
 */
enum class LoadingIcon {
    /** 圆形旋转 */
    CIRCLE,

    /** 菊花状（iOS 风格） */
    ACTIVITY,

    /** 点状跳动 */
    POINT
}

/**
 * Loading 布局方向
 */
enum class LoadingLayout {
    /** 横向布局 (图标在左，文字在右) */
    HORIZONTAL,

    /** 纵向布局 (图标在上，文字在下) */
    VERTICAL
}

/**
 * FullScreenLoading - 全屏遮罩加载
 */
@Composable
fun FullScreenLoading(
    visible: Boolean,
    text: String? = null,
    icon: LoadingIcon = LoadingIcon.CIRCLE,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val colors = Theme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.mask),
        contentAlignment = Alignment.Center
    ) {
        Loading(
            size = LoadingSize.LARGE,
            icon = icon,
            text = text,
            layout = LoadingLayout.VERTICAL
        )
    }
}
