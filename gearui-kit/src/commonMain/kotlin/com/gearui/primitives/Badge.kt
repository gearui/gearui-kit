package com.gearui.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.text.BasicText
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Radius

/**
 * Badge type
 */
enum class BadgeType {
    /** Red dot - a small dot with no content */
    RedPoint,

    /** Message - round or oval, showing a number */
    Message,

    /** Bubble - a bubble with a small tail at the bottom left */
    Bubble,

    /** Square - a rounded square */
    Square,

    /** Subscript - a 45-degree corner label */
    Subscript
}

/**
 * Badge size
 */
enum class BadgeSize {
    /** Large - 20dp tall */
    Large,

    /** Small - 16dp tall (default) */
    Small
}

/**
 * Badge corner radius
 */
enum class BadgeBorder {
    /** Large - 8dp */
    Large,

    /** Small - 2dp */
    Small
}

/**
 * Badge colour theme
 */
enum class BadgeTheme {
    /** Error / danger - red (default) */
    Error,

    /** Primary - brand colour */
    Primary,

    /** Success - green */
    Success,

    /** Warning - orange */
    Warning,

    /** Neutral - grey */
    Neutral
}

/**
 * Badge
 *
 * Signals a change of state, or a count of pending items, for the area it marks.
 *
 * A badge deliberately overhangs its child rather than sitting fully inside it.
 *
 * Types:
 * - RedPoint: a plain dot, for a simple "something changed"
 * - Message: a number
 * - Bubble: text in a bubble
 * - Square: a rounded square
 * - Subscript: a 45-degree corner label
 *
 * Example:
 * ```kotlin
 * // red dot
 * Badge(type = BadgeType.RedPoint) {
 *     Icon(...)
 * }
 *
 * // numeric badge
 * Badge(
 *     type = BadgeType.Message,
 *     count = 99
 * ) {
 *     Icon(...)
 * }
 *
 * // bubble badge
 * Badge(
 *     type = BadgeType.Bubble,
 *     message = "NEW"
 * ) {
 *     Icon(...)
 * }
 * ```
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    type: BadgeType = BadgeType.Message,
    count: Int? = null,
    maxCount: Int = 99,
    message: String? = null,
    size: BadgeSize = BadgeSize.Small,
    border: BadgeBorder = BadgeBorder.Large,
    theme: BadgeTheme = BadgeTheme.Error,
    color: Color? = null,
    textColor: Color? = null,
    showZero: Boolean = true,
    offset: Pair<Dp, Dp>? = null,
    alignment: Alignment = Alignment.TopEnd,
    content: @Composable (() -> Unit)? = null
) {
    val colors = Theme.colors

    // Background colour for the theme
    val backgroundColor = color ?: when (theme) {
        BadgeTheme.Error -> colors.destructive
        BadgeTheme.Primary -> colors.primary
        BadgeTheme.Success -> colors.success
        BadgeTheme.Warning -> colors.warning
        BadgeTheme.Neutral -> colors.mutedForeground
    }

    // Content colour: take the matching foreground for the theme, so it adapts to light and dark
    val contentColor = textColor ?: when (theme) {
        BadgeTheme.Error -> colors.destructiveForeground
        BadgeTheme.Primary -> colors.primaryForeground
        BadgeTheme.Success -> colors.successForeground
        BadgeTheme.Warning -> colors.warningForeground
        BadgeTheme.Neutral -> colors.primaryForeground
    }

    // 计算显示的文本
    val displayText = remember(count, maxCount, message) {
        when {
            message != null -> message
            count != null -> {
                if (count > maxCount) "${maxCount}+" else count.toString()
            }
            else -> "0"
        }
    }

    // 是否显示徽标
    val visible = remember(count, message, showZero) {
        when {
            message != null -> true
            count != null -> showZero || count != 0
            else -> showZero
        }
    }

    // 尺寸计算
    val badgeHeight = when (size) {
        BadgeSize.Large -> 20.dp
        BadgeSize.Small -> 16.dp
    }

    val dotSize = when (size) {
        BadgeSize.Large -> 10.dp
        BadgeSize.Small -> 8.dp
    }

    // 计算默认偏移量 - 让徽标部分超出子控件边界
    val defaultOffset = when (type) {
        BadgeType.RedPoint -> Pair(dotSize / 2, -(dotSize / 2))
        BadgeType.Message -> {
            val halfHeight = badgeHeight / 2
            Pair(halfHeight, -halfHeight)
        }
        BadgeType.Bubble -> Pair(8.dp, (-4).dp)
        BadgeType.Square -> {
            val halfHeight = badgeHeight / 2
            Pair(halfHeight, -halfHeight)
        }
        BadgeType.Subscript -> Pair(0.dp, 0.dp) // 角标不需要偏移
    }

    val actualOffset = offset ?: defaultOffset

    // 如果有 content，使用 Box 叠加；否则只显示徽标
    if (content != null) {
        Box(modifier = modifier) {
            content()

            if (visible) {
                Box(
                    modifier = Modifier
                        .align(alignment)
                        .offset(x = actualOffset.first, y = actualOffset.second)
                ) {
                    BadgeContent(
                        type = type,
                        displayText = displayText,
                        badgeHeight = badgeHeight,
                        dotSize = dotSize,
                        border = border,
                        backgroundColor = backgroundColor,
                        contentColor = contentColor,
                        size = size
                    )
                }
            }
        }
    } else {
        // 独立徽标
        if (visible) {
            BadgeContent(
                type = type,
                displayText = displayText,
                badgeHeight = badgeHeight,
                dotSize = dotSize,
                border = border,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                size = size,
                modifier = modifier
            )
        }
    }
}

/**
 * 徽标内容渲染
 */
@Composable
private fun BadgeContent(
    type: BadgeType,
    displayText: String,
    badgeHeight: Dp,
    dotSize: Dp,
    border: BadgeBorder,
    backgroundColor: Color,
    contentColor: Color,
    size: BadgeSize,
    modifier: Modifier = Modifier
) {
    // 根据尺寸计算字体大小
    val fontSize = when (size) {
        BadgeSize.Large -> 12.sp
        BadgeSize.Small -> 10.sp
    }

    // 使用 Kuikly 的 TextStyle，设置行高等于字体大小确保垂直居中
    val textStyle = TextStyle(
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        lineHeight = fontSize,
        color = contentColor
    )

    when (type) {
        BadgeType.RedPoint -> {
            // 红点样式 - 小圆点
            Box(
                modifier = modifier
                    .size(dotSize)
                    .clip(RoundedCornerShape(dotSize / 2))
                    .background(backgroundColor)
            )
        }

        BadgeType.Message -> {
            // 消息样式 - 圆形/椭圆形数字
            val isSingleChar = displayText.length == 1
            val horizontalPadding = if (isSingleChar) 0.dp else 5.dp

            Row(
                modifier = modifier
                    .defaultMinSize(minWidth = badgeHeight, minHeight = badgeHeight)
                    .height(badgeHeight)
                    .clip(RoundedCornerShape(badgeHeight / 2))
                    .background(backgroundColor)
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = displayText,
                    style = textStyle
                )
            }
        }

        BadgeType.Bubble -> {
            // 气泡样式 - 左下角小尖角
            Row(
                modifier = modifier
                    .height(badgeHeight)
                    .clip(
                        // shape-exempt: pill radius derives from the badge's own height
                        RoundedCornerShape(
                            topStart = badgeHeight / 2,
                            topEnd = badgeHeight / 2,
                            bottomStart = 1.dp,
                            bottomEnd = badgeHeight / 2
                        )
                    )
                    .background(backgroundColor)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = displayText,
                    style = textStyle
                )
            }
        }

        BadgeType.Square -> {
            // 方形样式
            val cornerRadius = when (border) {
                BadgeBorder.Large -> 8.dp
                BadgeBorder.Small -> 2.dp
            }

            Row(
                modifier = modifier
                    .height(badgeHeight)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(backgroundColor)
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = displayText,
                    style = textStyle
                )
            }
        }

        BadgeType.Subscript -> {
            // 角标样式 - 三角形裁剪
            // 由于 Kuikly 不支持 CustomClipper，使用简化的实现
            Box(
                modifier = modifier
                    .size(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = Radius.none,
                                topEnd = Radius.sm,
                                bottomStart = Radius.none,
                                bottomEnd = Radius.none
                            )
                        )
                        .background(backgroundColor)
                ) {
                    // 文字显示在右上角
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp, end = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicText(
                            text = displayText,
                            style = textStyle
                        )
                    }
                }
            }
        }
    }
}
