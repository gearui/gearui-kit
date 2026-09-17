package com.gearui.foundation.button

import androidx.compose.runtime.Immutable
import com.gearui.theme.Colors
import com.gearui.theme.DefaultPalette
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.gearui.foundation.motion.FeedbackDefaults

/** Replaceable state colors for the neutral button; not global semantic colors. */
@Immutable
data class ButtonColors(
    val background: Color,
    val backgroundHover: Color,
    val backgroundPressed: Color,
    val foreground: Color,
    val foregroundHover: Color,
    val border: Color,
    val borderHover: Color,
    val borderPressed: Color,
    val focusRing: Color,
)

internal object DefaultButtonColors {
    val Light = ButtonColors(
        DefaultPalette.lightButtonBackground, buttonHighlightColor(DefaultPalette.lightButtonBackground, DefaultPalette.lightButtonContent, true),
        buttonHighlightColor(DefaultPalette.lightButtonBackground, DefaultPalette.lightButtonContent, true), DefaultPalette.lightButtonContent,
        DefaultPalette.lightButtonHoverContent, DefaultPalette.lightButtonBorder,
        DefaultPalette.lightButtonHoverBorder, DefaultPalette.lightButtonPressedBorder,
        DefaultPalette.lightRing,
    )
    val Dark = ButtonColors(
        DefaultPalette.darkButtonBackground, buttonHighlightColor(DefaultPalette.darkButtonBackground, DefaultPalette.darkButtonContent, true),
        buttonHighlightColor(DefaultPalette.darkButtonBackground, DefaultPalette.darkButtonContent, true), DefaultPalette.darkButtonContent,
        DefaultPalette.darkButtonHoverContent, DefaultPalette.darkButtonBorder,
        DefaultPalette.darkButtonHoverBorder, DefaultPalette.darkButtonPressedBorder,
        DefaultPalette.darkRing,
    )

    // Custom brands without an explicit state palette stay within their colors.
    fun from(colors: Colors) = ButtonColors(
        colors.secondary, buttonHighlightColor(colors.secondary, colors.secondaryForeground, true),
        buttonHighlightColor(colors.secondary, colors.secondaryForeground, true),
        colors.secondaryForeground, colors.secondaryForeground,
        colors.border, colors.input, colors.input, colors.ring,
    )
}

internal data class ButtonVisual(val background: Color, val foreground: Color, val border: Color)

/** Kuikly lerp interpolates in OKLab; preserve theme-provided color relationships. */
internal fun buttonHighlightColor(background: Color, foreground: Color, neutral: Boolean): Color {
    val weight = if (neutral) FeedbackDefaults.neutralMix else FeedbackDefaults.accentMix
    val alpha = background.alpha * (1f - weight) + foreground.alpha * weight
    if (alpha <= 0f) return Color.Transparent
    // CSS color-mix premultiplies the interpolation coordinates by alpha.
    return lerp(background, foreground, foreground.alpha * weight / alpha).copy(alpha = alpha)
}

/** Reanimated withTiming's default highlight curve is in-out quadratic. */
internal fun buttonHighlightProgress(fraction: Float): Float =
    if (fraction < 0.5f) 2f * fraction * fraction else 1f - 2f * (1f - fraction) * (1f - fraction)

/** Width is logical, not device pixels. Invalid measurements never invert a layer. */
internal fun buttonPressScale(width: Float, pressed: Boolean, enabled: Boolean, duration: Int): Float {
    if (!pressed || !enabled || duration <= 0 || !width.isFinite() || width <= 0f) return 1f
    return (1f - (1f - com.gearui.foundation.motion.FeedbackDefaults.pressScale) *
        com.gearui.foundation.motion.FeedbackDefaults.referenceWidth / width).coerceIn(0f, 1f)
}

/** The light variant is a surface step, not an inverted foreground workaround. */
internal fun lightButtonColors(colors: ButtonColors): ButtonColors = colors.copy(
    background = colors.backgroundHover,
    backgroundHover = colors.background,
)

/** Press wins over hover; a disabled/loading button ignores transient interactions. */
internal fun resolveButtonVisual(
    colors: ButtonColors,
    pressed: Boolean,
    hovered: Boolean,
    enabled: Boolean,
    outlined: Boolean,
    textOnly: Boolean,
): ButtonVisual {
    val isPressed = enabled && pressed
    val isHovered = enabled && hovered && !isPressed
    return ButtonVisual(
        background = when {
            outlined || textOnly -> Color.Transparent
            isPressed -> colors.backgroundPressed
            isHovered -> colors.backgroundHover
            else -> colors.background
        },
        foreground = if (isHovered) colors.foregroundHover else colors.foreground,
        border = when {
            textOnly -> Color.Transparent
            outlined && isPressed -> colors.borderPressed
            isPressed || isHovered -> colors.borderHover
            outlined -> colors.border
            else -> Color.Transparent
        },
    )
}
