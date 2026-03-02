package com.gearui.components.rate

import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme

/**
 * Rate - Rating component
 *
 * 评分组件
 *
 * Features:
 * - Half star support
 * - Custom icon
 * - Custom count
 * - Read-only mode
 * - Text display
 *
 * Example:
 * ```
 * var rating by remember { mutableStateOf(3.5f) }
 *
 * Rate(
 *     value = rating,
 *     onValueChange = { rating = it },
 *     allowHalf = true
 * )
 * ```
 */
@Composable
fun Rate(
    value: Float,
    onValueChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    count: Int = 5,
    allowHalf: Boolean = false,
    readonly: Boolean = false,
    icon: String = "★",
    emptyIcon: String = "☆",
    size: Dp = 24.dp,
    gap: Dp = 4.dp,
    showText: Boolean = false,
    texts: List<String>? = null
) {
    val colors = Theme.colors
    val displayValue = value
    val useDefaultStarIcons = icon == "★" && emptyIcon == "☆"
    val clampedValue = displayValue.coerceIn(0f, count.toFloat())
    val totalWidth = (size.value * count + gap.value * (count - 1).coerceAtLeast(0)).dp
    val fullStars = clampedValue.toInt()
    val partial = (clampedValue - fullStars).coerceIn(0f, 1f)
    val filledWidth = (fullStars * (size.value + gap.value) + partial * size.value).dp

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        if (useDefaultStarIcons) {
            // Base layer: empty stars, fixed full width.
            // Top layer: filled stars, clipped by score width.
            Box(
                modifier = Modifier
                    .width(totalWidth)
                    .height(size)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(count) {
                        Icon(
                            name = Icons.star_border,
                            size = size,
                            tint = colors.textDisabled
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(filledWidth)
                        .clipToBounds()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(totalWidth),
                        horizontalArrangement = Arrangement.spacedBy(gap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(count) {
                            Icon(
                                name = Icons.star_rate,
                                size = size,
                                tint = colors.warning
                            )
                        }
                    }
                }

                if (!readonly && onValueChange != null) {
                    // Interaction layer: keep click behavior without affecting visual layers.
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(gap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(count) { index ->
                            val starValue = index + 1f
                            Box(
                                modifier = Modifier
                                    .size(size)
                                    .clickable {
                                        val newValue = if (allowHalf && value == starValue) {
                                            starValue - 0.5f
                                        } else {
                                            starValue
                                        }
                                        onValueChange(newValue)
                                    }
                            )
                        }
                    }
                }
            }
        } else {
            repeat(count) { index ->
                val starValue = index + 1f
                val fullActive = displayValue >= starValue
                val halfActive = allowHalf && displayValue >= starValue - 0.5f && displayValue < starValue

                Box(
                    modifier = Modifier
                        .size(size)
                        .then(
                            if (!readonly && onValueChange != null) {
                                Modifier.clickable {
                                    val newValue = if (allowHalf && value == starValue) {
                                        starValue - 0.5f
                                    } else {
                                        starValue
                                    }
                                    onValueChange(newValue)
                                }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            fullActive || halfActive -> icon
                            else -> emptyIcon
                        },
                        style = Typography.TitleLarge,
                        color = when {
                            fullActive || halfActive -> colors.warning
                            else -> colors.textDisabled
                        }
                    )
                }
            }
        }

        // Text
        if (showText) {
            val displayText = if (texts != null && value > 0 && value <= texts.size) {
                texts[value.toInt() - 1]
            } else {
                value.toString()
            }

            Text(
                text = displayText,
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

/**
 * Rate with description
 */
@Composable
fun RateWithDescription(
    value: Float,
    onValueChange: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    count: Int = 5,
    allowHalf: Boolean = false,
    descriptions: List<String> = listOf("很差", "较差", "一般", "满意", "很满意")
) {
    val colors = Theme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Rate(
            value = value,
            onValueChange = onValueChange,
            count = count,
            allowHalf = allowHalf
        )

        if (value > 0 && value <= descriptions.size) {
            Text(
                text = descriptions[value.toInt() - 1],
                style = Typography.BodyMedium,
                color = colors.primary
            )
        }
    }
}

/**
 * Readonly rate display
 */
@Composable
fun RateDisplay(
    value: Float,
    modifier: Modifier = Modifier,
    count: Int = 5,
    size: Dp = 20.dp,
    showValue: Boolean = true
) {
    Rate(
        value = value,
        onValueChange = null,
        count = count,
        readonly = true,
        size = size,
        showText = showValue,
        modifier = modifier
    )
}
