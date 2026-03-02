package com.gearui.components.backtop

import com.tencent.kuikly.compose.animation.AnimatedVisibility
import com.tencent.kuikly.compose.animation.fadeIn
import com.tencent.kuikly.compose.animation.fadeOut
import com.tencent.kuikly.compose.animation.scaleIn
import com.tencent.kuikly.compose.animation.scaleOut
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * BackTop 样式
 *
 */
enum class BackTopStyle {
    /** 圆形 - 48dp */
    CIRCLE,
    /** 半圆形 - 贴边显示 */
    HALF_CIRCLE
}

/**
 * BackTop 主题
 *
 */
enum class BackTopTheme {
    /** 亮色主题 - 白底黑字 */
    LIGHT,
    /** 暗色主题 - 黑底白字 */
    DARK
}

/**
 * BackTop - 返回顶部按钮
 *
 *
 * Features:
 * - 两种样式：圆形 (CIRCLE) 和半圆形 (HALF_CIRCLE)
 * - 两种主题：亮色 (LIGHT) 和暗色 (DARK)
 * - 可选显示文字
 * - 平滑的显示/隐藏动画
 *
 * Example:
 * ```
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // Your scrollable content
 *
 *     BackTop(
 *         visible = scrollOffset > 100,
 *         onClick = { scrollToTop() },
 *         style = BackTopStyle.CIRCLE,
 *         theme = BackTopTheme.LIGHT
 *     )
 * }
 * ```
 */
@Composable
fun BackTop(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: BackTopStyle = BackTopStyle.CIRCLE,
    theme: BackTopTheme = BackTopTheme.LIGHT,
    showText: Boolean = false,
    icon: String = "↑",
    text: String = "顶部",
    offset: Pair<Dp, Dp> = 16.dp to 16.dp // (right, bottom)
) {
    val colors = Theme.colors

    // 根据主题确定颜色
    val backgroundColor = when (theme) {
        BackTopTheme.LIGHT -> colors.surface
        BackTopTheme.DARK -> colors.inverseSurface
    }

    val contentColor = when (theme) {
        BackTopTheme.LIGHT -> colors.textPrimary
        BackTopTheme.DARK -> colors.inverseOnSurface
    }

    val borderColor = when (theme) {
        BackTopTheme.LIGHT -> colors.border
        BackTopTheme.DARK -> colors.stroke
    }

    // 根据样式确定尺寸和形状
    val circleSize = 48.dp
    val halfCircleWidth = 24.dp
    val halfCircleHeight = 40.dp

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        when (style) {
            BackTopStyle.CIRCLE -> {
                // 圆形样式
                Box(
                    modifier = Modifier
                        .offset(x = -offset.first, y = -offset.second)
                        .shadow(4.dp, CircleShape)
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(1.dp, borderColor, CircleShape)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (showText) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = icon,
                                style = Typography.BodySmall,
                                color = contentColor
                            )
                            Text(
                                text = text,
                                style = Typography.BodyExtraSmall,
                                color = contentColor
                            )
                        }
                    } else {
                        Text(
                            text = icon,
                            style = Typography.HeadlineSmall,
                            color = contentColor
                        )
                    }
                }
            }

            BackTopStyle.HALF_CIRCLE -> {
                // 半圆形样式 - 贴右边显示
                val halfCircleShape = RoundedCornerShape(
                    topStart = halfCircleHeight / 2,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = halfCircleHeight / 2
                )

                Box(
                    modifier = Modifier
                        .offset(x = 0.dp, y = -offset.second)
                        .shadow(4.dp, halfCircleShape)
                        .width(halfCircleWidth)
                        .height(halfCircleHeight)
                        .clip(halfCircleShape)
                        .background(backgroundColor)
                        .border(1.dp, borderColor, halfCircleShape)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (showText) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = icon,
                                style = Typography.BodyExtraSmall,
                                color = contentColor
                            )
                            Text(
                                text = text,
                                style = Typography.BodyExtraSmall,
                                color = contentColor
                            )
                        }
                    } else {
                        Text(
                            text = icon,
                            style = Typography.BodyMedium,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * BackTop with custom content
 *
 * 完全自定义内容的返回顶部按钮
 */
@Composable
fun BackTopCustom(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: BackTopTheme = BackTopTheme.LIGHT,
    offset: Pair<Dp, Dp> = 16.dp to 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = Theme.colors

    // 根据主题确定颜色
    val backgroundColor = when (theme) {
        BackTopTheme.LIGHT -> colors.surface
        BackTopTheme.DARK -> colors.inverseSurface
    }

    val borderColor = when (theme) {
        BackTopTheme.LIGHT -> colors.border
        BackTopTheme.DARK -> colors.stroke
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .offset(x = -offset.first, y = -offset.second)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(12.dp)
        ) {
            content()
        }
    }
}
