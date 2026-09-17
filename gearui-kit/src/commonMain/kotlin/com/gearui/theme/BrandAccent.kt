package com.gearui.theme

import com.tencent.kuikly.compose.ui.graphics.Color
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
    return copy(
        colors = colors.copy(
            primary = color,
            primaryForeground = foreground ?: brandAccentForeground(color),
            ring = color,
        ),
        buttonColors = buttonColors?.copy(focusRing = color),
        inputColors = inputColors?.copy(focusRing = color),
    )
}

/** Pick the higher-contrast black/white foreground using linear sRGB luminance. */
internal fun brandAccentForeground(color: Color): Color {
    fun linear(v: Float): Double = if (v <= .04045f) v / 12.92 else ((v + .055) / 1.055).pow(2.4)
    val luminance = .2126 * linear(color.red) + .7152 * linear(color.green) + .0722 * linear(color.blue)
    val whiteContrast = 1.05 / (luminance + .05)
    val blackContrast = (luminance + .05) / .05
    return if (whiteContrast >= blackContrast) Color.White else Color.Black
}
