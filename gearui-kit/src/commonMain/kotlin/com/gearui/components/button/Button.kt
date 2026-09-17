package com.gearui.components.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.animation.core.Easing
import com.gearui.foundation.button.buttonHighlightColor
import com.gearui.foundation.button.buttonHighlightProgress
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.feedbackDuration
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.interaction.collectIsHoveredAsState
import com.tencent.kuikly.compose.foundation.interaction.collectIsFocusedAsState
import com.tencent.kuikly.compose.ui.draw.drawWithContent
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.drawOutline
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.graphics.drawscope.translate
import com.tencent.kuikly.compose.ui.input.InputMode
import com.tencent.kuikly.compose.ui.platform.LocalInputModeManager
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.gearui.foundation.button.resolveButtonVisual
import com.gearui.foundation.button.lightButtonColors
import com.gearui.theme.LocalButtonColors
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.gearui.foundation.primitives.Icon as FoundationIcon
import com.gearui.foundation.primitives.LoadingIndicator
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry

/**
 * Button - fully Theme-driven
 *
 * Supports:
 * - 3 types: fill, outline, text
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
    iconTextSpacing: Dp = Dp.Unspecified
) {
    val colors = Theme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val hovered = interactionSource.collectIsHoveredAsState().value
    val focused = interactionSource.collectIsFocusedAsState().value
    val stateColors = if (theme == ButtonTheme.LIGHT) lightButtonColors(LocalButtonColors.current)
        else LocalButtonColors.current
    val isEnabled = !disabled && !loading
    val density = LocalDensity.current.density
    var measuredWidth by remember { mutableStateOf(0f) }
    val motion = Theme.motion
    val pressDuration = motion.feedbackDuration(FeedbackDefaults.pressDuration)
    val highlightDuration = motion.feedbackDuration(FeedbackDefaults.highlightDuration)
    val targetScale = com.gearui.foundation.button.buttonPressScale(measuredWidth, pressed, isEnabled, pressDuration)
    val pressScale by animateFloatAsState(targetScale,
        tween(pressDuration, easing = FeedbackDefaults.pressEasing))
    val highlight by animateFloatAsState(if ((pressed || hovered) && isEnabled) FeedbackDefaults.highlightOpacity else 0f,
        tween(highlightDuration, easing = Easing(::buttonHighlightProgress)))
    val showFocusRing = isEnabled && focused &&
        LocalInputModeManager.current.inputMode == InputMode.Keyboard

    // Size configuration
    val height: Dp = when (size) {
        ButtonSize.LARGE -> ControlGeometry.controlLarge
        ButtonSize.MEDIUM -> ControlGeometry.controlMedium
        ButtonSize.SMALL -> ControlGeometry.controlSmall
        ButtonSize.EXTRA_SMALL -> ControlGeometry.controlExtraSmall
    }

    val paddingH: Dp = when (size) {
        ButtonSize.LARGE -> ControlGeometry.buttonPaddingLarge
        ButtonSize.MEDIUM -> ControlGeometry.buttonPaddingMedium
        ButtonSize.SMALL -> ControlGeometry.buttonPaddingSmall
        ButtonSize.EXTRA_SMALL -> ControlGeometry.buttonPaddingExtraSmall
    }

    val loadingSize: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 14.dp
        ButtonSize.EXTRA_SMALL -> 12.dp
    }

    val textStyle = when (size) {
        ButtonSize.LARGE -> Theme.typography.bodyLarge
        ButtonSize.MEDIUM -> Theme.typography.bodyMedium
        ButtonSize.SMALL -> Theme.typography.bodySmall
        ButtonSize.EXTRA_SMALL -> Theme.typography.bodyExtraSmall
    }
    val iconSize = textStyle.fontSize.value.dp
    val resolvedIconGap = if (iconTextSpacing != Dp.Unspecified) iconTextSpacing else when (size) {
        ButtonSize.LARGE -> ControlGeometry.buttonGapLarge
        ButtonSize.MEDIUM -> ControlGeometry.buttonGapMedium
        ButtonSize.SMALL -> ControlGeometry.buttonGapSmall
        ButtonSize.EXTRA_SMALL -> ControlGeometry.buttonGapExtraSmall
    }

    val sizedShape = when (size) {
        ButtonSize.LARGE -> Theme.shapes.controlLarge
        ButtonSize.MEDIUM -> Theme.shapes.lg
        ButtonSize.SMALL -> Theme.shapes.md
        ButtonSize.EXTRA_SMALL -> Theme.shapes.sm
    }

    // Shape configuration
    val buttonShape: Shape = when (shape) {
        ButtonShape.RECTANGLE -> Theme.shapes.full
        ButtonShape.ROUND -> RoundedCornerShape(height / 2)
        ButtonShape.SQUARE -> sizedShape
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
    val legacyColors = getButtonColors(
        theme = theme,
        type = type,
        disabled = false,
        colors = colors
    )

    val visual = resolveButtonVisual(
        stateColors, false, false, isEnabled,
        outlined = type == ButtonType.OUTLINE,
        textOnly = type == ButtonType.TEXT,
    )
    val neutral = theme == ButtonTheme.DEFAULT || theme == ButtonTheme.LIGHT
    val containerColor = if (neutral) visual.background else legacyColors.first
    val contentColor = if (neutral) visual.foreground else legacyColors.second
    val borderColor = if (neutral) visual.border else legacyColors.third
    val highlightColor = when {
        type != ButtonType.FILL -> stateColors.backgroundHover.copy(alpha =
            stateColors.backgroundHover.alpha * FeedbackDefaults.transparentHighlightOpacity)
        neutral -> if (pressed) stateColors.backgroundPressed else stateColors.backgroundHover
        else -> buttonHighlightColor(containerColor, contentColor, false)
    }

    val resolvedIcon: (@Composable () -> Unit)? = when {
        iconWidget != null -> iconWidget
        icon != null -> {
            {
                FoundationIcon(
                    name = icon,
                    size = iconSize,
                    tint = contentColor,
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
        .onSizeChanged { measuredWidth = it.width / density }
        .graphicsLayer {
            scaleX = if (isEnabled) pressScale else 1f
            scaleY = if (isEnabled) pressScale else 1f
            alpha = if (disabled) FeedbackDefaults.disabledOpacity else 1f
        }
        .drawWithContent {
            drawContent()
            if (showFocusRing) {
                val width = BorderWidth.thick.toPx()
                val drawSize = this.size
                // Draw outside the layout box: focus never changes button geometry.
                translate(-width / 2, -width / 2) {
                    drawOutline(
                        buttonShape.createOutline(Size(drawSize.width + width, drawSize.height + width), layoutDirection, this),
                        color = if (neutral) stateColors.focusRing else colors.ring,
                        style = Stroke(width),
                    )
                }
            }
        }
        .clip(buttonShape)
        .then(
            when (type) {
                ButtonType.FILL -> Modifier.background(containerColor)
                ButtonType.OUTLINE -> Modifier
                    .background(Color.Transparent)
                    .border(BorderWidth.thin, borderColor, buttonShape)
                ButtonType.TEXT -> Modifier.background(Color.Transparent)
            }
        )
        .drawWithContent {
            // The surface overlay is behind the label and icons, not a tint on them.
            if (isEnabled && highlight > 0f) drawRect(highlightColor.copy(alpha = highlightColor.alpha * highlight))
            drawContent()
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = isEnabled,
            role = Role.Button,
        ) { onClick() }
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
                LoadingIndicator(
                    size = loadingSize,
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
                    Spacer(modifier = Modifier.width(resolvedIconGap))
                }
            }

            // Text
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    style = textStyle,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Trailing icon
            if (!loading && resolvedIcon != null && iconPosition == ButtonIconPosition.RIGHT) {
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(resolvedIconGap))
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
    }
}
