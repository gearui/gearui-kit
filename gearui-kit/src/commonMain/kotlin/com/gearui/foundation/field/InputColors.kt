package com.gearui.foundation.field

import androidx.compose.runtime.Immutable
import com.gearui.theme.Colors
import com.gearui.theme.DefaultPalette
import com.tencent.kuikly.compose.ui.graphics.Color

/** Input state palette. Validation and card variants remain GearUI extensions. */
@Immutable
data class InputColors(
    val background: Color,
    val foreground: Color,
    val placeholder: Color,
    val border: Color,
    val borderHover: Color,
    val focusRing: Color,
)

internal object DefaultInputColors {
    fun from(colors: Colors) = InputColors(
        colors.surface, colors.surfaceForeground, colors.mutedForeground,
        colors.input, colors.border, colors.ring,
    )

    val Light = InputColors(
        DefaultPalette.lightSurface, DefaultPalette.lightSurfaceForeground,
        DefaultPalette.lightMutedForeground, DefaultPalette.lightInputBorder,
        DefaultPalette.lightInputHoverBorder, DefaultPalette.lightRing,
    )
    val Dark = InputColors(
        DefaultPalette.darkSurface, DefaultPalette.darkSurfaceForeground,
        DefaultPalette.darkMutedForeground, DefaultPalette.darkInputBorder,
        DefaultPalette.darkInputHoverBorder, DefaultPalette.darkRing,
    )
}

internal fun inputBorderColor(
    colors: InputColors,
    enabled: Boolean,
    focused: Boolean,
    hovered: Boolean,
    error: Color?,
): Color = error ?: if (enabled && hovered && !focused) colors.borderHover else colors.border

/** Native fields show focus for touch and keyboard alike; disabled never gains a ring. */
internal fun inputFocusColor(colors: InputColors, enabled: Boolean, focused: Boolean, error: Color?): Color? =
    if (enabled && focused) error ?: colors.focusRing else null
