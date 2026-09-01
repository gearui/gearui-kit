package com.gearui.components.loading

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.LoadingIndicator
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.gearui.theme.Theme
import com.gearui.overlay.OverlayDefaults
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.gearui.foundation.typography.Typography

/**
 * Loading - loading indicator
 *
 * - three icon types: circle, point, activity
 * - three sizes: small / medium / large
 * - horizontal or vertical layout
 * - optional caption
 * - custom colour and animation speed
 *
 * @param size the size
 * @param icon icon type
 * @param text caption
 * @param layout layout direction
 * @param color custom colour
 * @param duration animation period in milliseconds
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
        // Icon
        when (icon) {
            LoadingIcon.CIRCLE -> {
                LoadingIndicator(
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

        // Text
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
                color = colors.mutedForeground
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
 * ActivityLoadingIndicator - spoke indicator (iOS style)
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
 * PointLoadingIndicator - dot indicator (three bouncing dots)
 */
@Composable
private fun PointLoadingIndicator(
    size: Dp,
    color: Color,
    duration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Three dot animations, offset by a third of the period each
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
 * Loading size
 */
enum class LoadingSize {
    /** small - 18dp */
    SMALL,

    /** medium - 21dp (default) */
    MEDIUM,

    /** large - 24dp */
    LARGE
}

/**
 * Loading icon type
 */
enum class LoadingIcon {
    /** spinning circle */
    CIRCLE,

    /** spokes (iOS style) */
    ACTIVITY,

    /** bouncing dots */
    POINT
}

/**
 * Loading layout direction
 */
enum class LoadingLayout {
    /** horizontal (icon left, text right) */
    HORIZONTAL,

    /** vertical (icon top, text below) */
    VERTICAL
}

/**
 * FullScreenLoading - fullscreen scrim loader
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
            .background(OverlayDefaults.scrimColor),
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
