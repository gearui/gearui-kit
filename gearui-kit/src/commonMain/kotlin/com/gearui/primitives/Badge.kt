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

/**
 * Badge 徽标类型
 */
enum class BadgeType {
    /** 红点样式 - 小圆点，无内容 */
    RedPoint,

    /** 消息样式 - 圆形/椭圆形，显示数字 */
    Message,

    /** 气泡样式 - 左下角小尖角的气泡 */
    Bubble,

    /** 方形样式 - 带圆角的方形 */
    Square,

    /** 角标样式 - 45度斜角标签 */
    Subscript
}

/**
 * Badge 尺寸
 */
enum class BadgeSize {
    /** 大尺寸 - 高度 20dp */
    Large,

    /** 小尺寸 - 高度 16dp（默认） */
    Small
}

/**
 * Badge 圆角大小
 */
enum class BadgeBorder {
    /** 大圆角 - 8dp */
    Large,

    /** 小圆角 - 2dp */
    Small
}

/**
 * Badge 颜色主题
 */
enum class BadgeTheme {
    /** 错误/危险 - 红色（默认） */
    Error,

    /** 主要 - 品牌色 */
    Primary,

    /** 成功 - 绿色 */
    Success,

    /** 警告 - 橙色 */
    Warning,

    /** 中性 - 灰色 */
    Neutral
}

/**
 * Badge - 徽标组件
 *
 * 用于告知用户，该区域的状态变化或者待处理任务的数量。
 *
 * 徽标会部分超出子控件边界显示，而不是完全在控件内部。
 *
 * 支持的类型：
 * - RedPoint: 红点样式，用于简单提醒
 * - Message: 消息样式，显示数字
 * - Bubble: 气泡样式，可显示文字
 * - Square: 方形样式，带圆角
 * - Subscript: 角标样式，45度斜角
 *
 * Example:
 * ```kotlin
 * // 红点徽标
 * Badge(type = BadgeType.RedPoint) {
 *     Icon(...)
 * }
 *
 * // 数字徽标
 * Badge(
 *     type = BadgeType.Message,
 *     count = 99
 * ) {
 *     Icon(...)
 * }
 *
 * // 气泡徽标
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

    // 根据主题获取背景色
    val backgroundColor = color ?: when (theme) {
        BadgeTheme.Error -> colors.danger
        BadgeTheme.Primary -> colors.primary
        BadgeTheme.Success -> colors.success
        BadgeTheme.Warning -> colors.warning
        BadgeTheme.Neutral -> colors.disabled
    }

    // 文字颜色
    val contentColor = textColor ?: colors.textAnti

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
                                topStart = 0.dp,
                                topEnd = 4.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
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
