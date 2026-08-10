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
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

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
    /** 自定义实心字形；留空则使用内置星形图标。与 [emptyIcon] 必须成对提供。 */
    icon: String? = null,
    /** 自定义空心字形；留空则使用内置星形图标。 */
    emptyIcon: String? = null,
    size: Dp = 24.dp,
    gap: Dp = 4.dp,
    showText: Boolean = false,
    texts: List<String>? = null
) {
    val colors = Theme.colors
    val displayValue = value
    // 之前用 "★"/"☆" 两个字面量当哨兵：默认值时其实画的是 Icons.star_*，
    // 字符串本身从不渲染。null 把"用内置图标"这件事说清楚，也让
    // check_emoji_as_icon 不必为一个从不上屏的字形误报。
    val useDefaultStarIcons = icon == null && emptyIcon == null
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
                            tint = colors.mutedForeground
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
                            fullActive || halfActive -> icon.orEmpty()
                            else -> emptyIcon.orEmpty()
                        },
                        style = Typography.TitleLarge,
                        color = when {
                            fullActive || halfActive -> colors.warning
                            else -> colors.mutedForeground
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
                color = colors.mutedForeground
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
    descriptions: List<String> = I18n.strings.guide.rateDescriptions
) {
    val colors = Theme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
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
