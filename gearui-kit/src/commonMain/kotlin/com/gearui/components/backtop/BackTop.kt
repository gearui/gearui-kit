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
import com.gearui.i18n.I18n
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing

/**
 * BackTop style
 *
 */
enum class BackTopStyle {
    /** circle - 48dp */
    CIRCLE,
    /** half circle - hugs the edge */
    HALF_CIRCLE
}

/**
 * BackTop theme
 *
 */
enum class BackTopTheme {
    /** light - dark text on white */
    LIGHT,
    /** dark - white text on black */
    DARK
}

/**
 * BackTop - back-to-top button
 *
 *
 * Features:
 * - two styles: CIRCLE and HALF_CIRCLE
 * - two themes: LIGHT and DARK
 * - optional text
 * - smooth show / hide animation
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
    text: String = I18n.strings.common.backToTop,
    offset: Pair<Dp, Dp> = 16.dp to 16.dp // (right, bottom)
) {
    val colors = Theme.colors

    // Colours from the theme
    val backgroundColor = when (theme) {
        BackTopTheme.LIGHT -> colors.surface
        BackTopTheme.DARK -> colors.foreground
    }

    val contentColor = when (theme) {
        BackTopTheme.LIGHT -> colors.foreground
        BackTopTheme.DARK -> colors.background
    }

    val borderColor = when (theme) {
        BackTopTheme.LIGHT -> colors.border
        BackTopTheme.DARK -> colors.border
    }

    // Size and shape from the style
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
                // Circle style
                Box(
                    modifier = Modifier
                        .offset(x = -offset.first, y = -offset.second)
                        .shadow(Elevation.raised, CircleShape)
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(BorderWidth.thin, borderColor, CircleShape)
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
                // Half circle style - hugs the right edge
                // shape-exempt: half-circle radius derives from the control's height
                val halfCircleShape = RoundedCornerShape(
                    topStart = halfCircleHeight / 2,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = halfCircleHeight / 2
                )

                Box(
                    modifier = Modifier
                        .offset(x = 0.dp, y = -offset.second)
                        .shadow(Elevation.raised, halfCircleShape)
                        .width(halfCircleWidth)
                        .height(halfCircleHeight)
                        .clip(halfCircleShape)
                        .background(backgroundColor)
                        .border(BorderWidth.thin, borderColor, halfCircleShape)
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
 * Back-to-top button with fully custom content
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

    // Colours from the theme
    val backgroundColor = when (theme) {
        BackTopTheme.LIGHT -> colors.surface
        BackTopTheme.DARK -> colors.foreground
    }

    val borderColor = when (theme) {
        BackTopTheme.LIGHT -> colors.border
        BackTopTheme.DARK -> colors.border
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
                .shadow(Elevation.raised, Theme.shapes.lg)
                .clip(Theme.shapes.lg)
                .background(backgroundColor)
                .border(BorderWidth.thin, borderColor, Theme.shapes.lg)
                .clickable(onClick = onClick)
                .padding(Spacing.md)
        ) {
            content()
        }
    }
}
