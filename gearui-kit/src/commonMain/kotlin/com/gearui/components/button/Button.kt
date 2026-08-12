package com.gearui.components.button

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.CircularProgressIndicator
import com.gearui.foundation.primitives.Icon as FoundationIcon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Button - fully Theme-driven
 *
 * Supports:
 * - 4 types: fill, outline, text, ghost
 * - 6 colour themes: primary, danger, warning, success, default, light
 * - 4 sizes: large, medium, small, extraSmall
 * - 5 shapes: rectangle, round, square, circle, filled
 * - icons: leading or trailing
 * - states: loading, disabled
 * - full-width mode: block
 */
@Composable
fun Button(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: ButtonTheme = ButtonTheme.PRIMARY,
    type: ButtonType = ButtonType.FILL,
    size: ButtonSize = ButtonSize.MEDIUM,
    shape: ButtonShape = ButtonShape.RECTANGLE,
    disabled: Boolean = false,
    loading: Boolean = false,
    block: Boolean = false,
    icon: String? = null,
    iconWidget: (@Composable () -> Unit)? = null,
    iconPosition: ButtonIconPosition = ButtonIconPosition.LEFT,
    iconTextSpacing: Dp = 8.dp
) {
    val colors = Theme.colors

    // Size configuration
    val height: Dp = when (size) {
        ButtonSize.LARGE -> 48.dp
        ButtonSize.MEDIUM -> 40.dp
        ButtonSize.SMALL -> 32.dp
        ButtonSize.EXTRA_SMALL -> 28.dp
    }

    val paddingH: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 12.dp
        ButtonSize.EXTRA_SMALL -> 8.dp
    }

    val loadingSize: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 14.dp
        ButtonSize.EXTRA_SMALL -> 12.dp
    }

    val iconSize: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 18.dp
        ButtonSize.SMALL -> 16.dp
        ButtonSize.EXTRA_SMALL -> 14.dp
    }

    // Shape configuration
    val buttonShape: Shape = when (shape) {
        ButtonShape.RECTANGLE -> Theme.shapes.lg
        ButtonShape.ROUND -> RoundedCornerShape(height / 2)
        ButtonShape.SQUARE -> Theme.shapes.lg
        ButtonShape.CIRCLE -> CircleShape
        ButtonShape.FILLED -> RoundedCornerShape(height / 2)
    }

    // Whether it is square / circular (icon only, no text)
    val hasIcon = icon != null || iconWidget != null
    val isIconOnly = text.isEmpty() && hasIcon
    val buttonWidth = if (isIconOnly && (shape == ButtonShape.SQUARE || shape == ButtonShape.CIRCLE)) {
        height
    } else {
        Dp.Unspecified
    }

    // Colour configuration
    val (containerColor, contentColor, borderColor) = getButtonColors(
        theme = theme,
        type = type,
        disabled = disabled,
        colors = colors
    )

    val isEnabled = !disabled && !loading

    val resolvedIcon: (@Composable () -> Unit)? = when {
        iconWidget != null -> iconWidget
        icon != null -> {
            {
                FoundationIcon(
                    name = icon,
                    size = iconSize,
                    tint = contentColor,
                    preferSvg = true
                )
            }
        }
        else -> null
    }

    // Button modifier
    val buttonModifier = modifier
        .then(if (block) Modifier.fillMaxWidth() else Modifier)
        .then(if (buttonWidth != Dp.Unspecified) Modifier.width(buttonWidth) else Modifier)
        .height(height)
        .clip(buttonShape)
        .then(
            when (type) {
                ButtonType.FILL -> Modifier.background(containerColor)
                ButtonType.OUTLINE -> Modifier
                    .background(Color.Transparent)
                    .border(BorderWidth.thin, borderColor, buttonShape)
                ButtonType.TEXT -> Modifier.background(Color.Transparent)
                ButtonType.GHOST -> Modifier.background(Color.Transparent)
            }
        )
        .clickable(enabled = isEnabled) { onClick() }
        .padding(horizontal = if (isIconOnly) 0.dp else paddingH)

    Box(
        modifier = buttonModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Loading
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(loadingSize),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
            }

            // Leading icon
            if (!loading && resolvedIcon != null && iconPosition == ButtonIconPosition.LEFT) {
                resolvedIcon()
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(iconTextSpacing))
                }
            }

            // Text
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    style = when (size) {
                        ButtonSize.LARGE -> Typography.BodyLarge
                        ButtonSize.MEDIUM -> Typography.BodyMedium
                        ButtonSize.SMALL -> Typography.BodySmall
                        ButtonSize.EXTRA_SMALL -> Typography.BodySmall
                    },
                    color = contentColor
                )
            }

            // Trailing icon
            if (!loading && resolvedIcon != null && iconPosition == ButtonIconPosition.RIGHT) {
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(iconTextSpacing))
                }
                resolvedIcon()
            }
        }
    }
}

/**
 * Resolves the button colours
 */
@Composable
private fun getButtonColors(
    theme: ButtonTheme,
    type: ButtonType,
    disabled: Boolean,
    colors: com.gearui.theme.Colors
): Triple<Color, Color, Color> {
    // Theme base colour
    val primaryColor: Color
    val lightColor: Color

    when (theme) {
        ButtonTheme.PRIMARY -> {
            primaryColor = colors.primary
            lightColor = colors.muted
        }
        ButtonTheme.DANGER -> {
            primaryColor = colors.destructive
            lightColor = colors.destructive.copy(alpha = 0.12f)
        }
        ButtonTheme.WARNING -> {
            primaryColor = colors.warning
            lightColor = colors.warning.copy(alpha = 0.12f)
        }
        ButtonTheme.SUCCESS -> {
            primaryColor = colors.success
            lightColor = colors.success.copy(alpha = 0.12f)
        }
        ButtonTheme.DEFAULT -> {
            primaryColor = colors.muted
            lightColor = colors.surface
        }
        ButtonTheme.LIGHT -> {
            primaryColor = colors.muted
            lightColor = colors.surface
        }
    }

    // Text colour on a solid coloured fill: the matching foreground for the theme (adapting to light/dark), no longer primaryForeground for everything
    val onFillColor = when (theme) {
        ButtonTheme.DANGER -> colors.destructiveForeground
        ButtonTheme.WARNING -> colors.warningForeground
        ButtonTheme.SUCCESS -> colors.successForeground
        else -> colors.primaryForeground
    }

    return when (type) {
        ButtonType.FILL -> {
            if (disabled) {
                Triple(
                    if (theme == ButtonTheme.DEFAULT) colors.muted else lightColor,
                    colors.mutedForeground,
                    Color.Transparent
                )
            } else {
                Triple(
                    if (theme == ButtonTheme.LIGHT) lightColor else primaryColor,
                    if (theme == ButtonTheme.DEFAULT) colors.foreground
                    else if (theme == ButtonTheme.LIGHT) colors.primary
                    else onFillColor,
                    Color.Transparent
                )
            }
        }
        ButtonType.OUTLINE -> {
            if (disabled) {
                Triple(
                    colors.surface,
                    colors.mutedForeground,
                    colors.mutedForeground
                )
            } else {
                Triple(
                    colors.surface,
                    if (theme == ButtonTheme.DEFAULT) colors.foreground else primaryColor,
                    colors.border
                )
            }
        }
        ButtonType.TEXT -> {
            if (disabled) {
                Triple(
                    Color.Transparent,
                    colors.mutedForeground,
                    Color.Transparent
                )
            } else {
                Triple(
                    Color.Transparent,
                    if (theme == ButtonTheme.DEFAULT) colors.foreground else primaryColor,
                    Color.Transparent
                )
            }
        }
        ButtonType.GHOST -> {
            if (disabled) {
                Triple(
                    Color.Transparent,
                    colors.mutedForeground,
                    Color.Transparent
                )
            } else {
                Triple(
                    Color.Transparent,
                    if (theme == ButtonTheme.DEFAULT) colors.foreground else primaryColor,
                    Color.Transparent
                )
            }
        }
    }
}
