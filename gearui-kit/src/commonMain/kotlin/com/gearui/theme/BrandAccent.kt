package com.gearui.theme

import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
import kotlin.math.pow

/**
 * Replace the brand accent independently of surface colors, typography and shapes.
 * Apply to the light/dark spec selected by the host. GearUI's `accent` color is
 * a secondary surface role; primary/ring represent the brand interaction color.
 * Explicit component palettes are retained, with their focus rings updated.
 * Opaque colors avoid contrast depending on unknown content behind a control.
 */
fun ThemeSpec.withBrandAccent(color: Color, foreground: Color? = null): ThemeSpec {
    require(color.alpha == 1f) { "Brand accent must be opaque" }
    // The focus ring must be seen (WCAG 1.4.11, 3:1): a pale brand such as yellow
    // is darkened towards the foreground for the ring only.
    val ring = readableTextForm(color, listOf(colors.background, colors.surface), colors.foreground, minimum = 3.0)
    return copy(
        colors = colors.copy(
            primary = color,
            primaryForeground = foreground ?: brandAccentForeground(color),
            // The soft pair is derived from the accent, so it has to follow it. A data
            // class copy keeps whatever the base theme set, which left a purple brand
            // with the built-in blue's chips and banners.
            primarySoft = color.copy(alpha = SoftColorMix.FILL_ALPHA),
            // The text form of the brand colour (links, selected labels, chips) has to
            // read at 4.5:1 whatever the brand is: the reference mix, then further
            // towards the foreground until it does on every ground and on its soft fill.
            primarySoftForeground = readableTextForm(
                lerp(color, colors.foreground, 1f - SoftColorMix.ACCENT),
                grounds = listOf(
                    colors.background, colors.surface, colors.muted,
                    lerp(colors.surface, color, SoftColorMix.FILL_ALPHA),
                ),
                ink = colors.foreground,
            ),
            ring = ring,
        ),
        buttonColors = buttonColors?.copy(focusRing = ring),
        inputColors = inputColors?.copy(focusRing = ring),
    )
}

/** Pick the higher-contrast black/white foreground using linear sRGB luminance. */
internal fun brandAccentForeground(color: Color): Color {
    val luminance = relativeLuminance(color)
    val whiteContrast = 1.05 / (luminance + .05)
    val blackContrast = (luminance + .05) / .05
    return if (whiteContrast >= blackContrast) Color.White else Color.Black
}

/** WCAG relative luminance of an opaque colour. */
internal fun relativeLuminance(color: Color): Double {
    fun linear(v: Float): Double = if (v <= .04045f) v / 12.92 else ((v + .055) / 1.055).pow(2.4)
    return .2126 * linear(color.red) + .7152 * linear(color.green) + .0722 * linear(color.blue)
}

/** WCAG contrast ratio of two opaque colours, 1 to 21. */
internal fun contrastRatio(a: Color, b: Color): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    return (maxOf(la, lb) + .05) / (minOf(la, lb) + .05)
}

/** [color] moved towards [ink] in small steps until it contrasts at [minimum] with every ground. */
internal fun readableTextForm(color: Color, grounds: List<Color>, ink: Color, minimum: Double = 5.0): Color {
    var step = 0
    while (step <= 50) {
        val candidate = lerp(color, ink, step / 50f)
        if (grounds.all { contrastRatio(candidate, it) >= minimum }) return candidate
        step++
    }
    return ink
}
