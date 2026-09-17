package com.gearui

import com.gearui.foundation.button.DefaultButtonColors
import com.gearui.foundation.button.resolveButtonVisual
import com.gearui.foundation.button.lightButtonColors
import com.gearui.foundation.button.buttonHighlightColor
import com.gearui.theme.TypographyProfiles
import com.gearui.theme.Typographies
import com.gearui.theme.Themes
import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.math.roundToInt

class ButtonAppearanceTest {
    private val light = DefaultButtonColors.Light

    @Test fun semanticSecondaryMatchesTheDefaultControlPalette() {
        for ((theme, palette) in listOf(Themes.Light to DefaultButtonColors.Light, Themes.Dark to DefaultButtonColors.Dark)) {
            assertEquals(palette.background, theme.colors.secondary)
            assertEquals(palette.foreground, theme.colors.secondaryForeground)
        }
    }

    @Test fun lightVariantUsesTheSameStatePaletteInBothModes() {
        for (base in listOf(DefaultButtonColors.Light, DefaultButtonColors.Dark)) {
            val variant = lightButtonColors(base)
            assertEquals(base.backgroundHover, variant.background)
            assertEquals(base.foreground, variant.foreground)
            assertEquals(base.background, resolveButtonVisual(variant, false, true, true, false, false).background)
            assertEquals(base.backgroundPressed, resolveButtonVisual(variant, true, false, true, false, false).background)
        }
    }

    @Test fun normalFillHasTransparentBorder() {
        val state = resolveButtonVisual(light, false, false, true, false, false)
        assertEquals(light.background, state.background)
        assertEquals(Color.Transparent, state.border)
    }

    @Test fun pressedWinsOverHover() {
        val state = resolveButtonVisual(light, true, true, true, false, false)
        assertEquals(light.backgroundPressed, state.background)
        assertEquals(light.foreground, state.foreground)
        assertEquals(light.borderHover, state.border)
    }

    @Test fun hoverUsesIndependentContentAndBackground() {
        val dark = DefaultButtonColors.Dark
        val state = resolveButtonVisual(dark, false, true, true, false, false)
        assertEquals(dark.backgroundHover, state.background)
        assertEquals(dark.foregroundHover, state.foreground)
    }

    @Test fun disabledOrLoadingSuppressesTransientStates() {
        assertEquals(
            resolveButtonVisual(light, false, false, true, false, false),
            resolveButtonVisual(light, true, true, false, false, false),
        )
    }

    @Test fun outlinedPressDoesNotFillTheSurface() {
        val state = resolveButtonVisual(light, true, false, true, true, false)
        assertEquals(Color.Transparent, state.background)
        assertEquals(light.borderPressed, state.border)
    }

    @Test fun releaseRestoresHoveredState() {
        val state = resolveButtonVisual(light, false, true, true, false, false)
        assertEquals(light.backgroundHover, state.background)
    }

    @Test fun textExtensionNeverDrawsAContainer() {
        val state = resolveButtonVisual(light, true, true, true, false, true)
        assertEquals(Color.Transparent, state.background)
        assertEquals(Color.Transparent, state.border)
    }

    @Test fun customThemeFallbackUsesOnlyItsOwnColors() {
        val brand = Themes.Light.colors.copy(secondary = Color.Magenta, accent = Color.Cyan)
        val colors = DefaultButtonColors.from(brand)
        assertEquals(brand.secondary, colors.background)
        assertEquals(buttonHighlightColor(brand.secondary, brand.secondaryForeground, true), colors.backgroundPressed)
        assertEquals(brand.ring, colors.focusRing)
    }

    @Test fun defaultSurfaceValuesMatchTheReference() {
        // Kuikly stores sRGB colors in eight-bit channels.
        fun channel(value: Float) = (value * 255).roundToInt() / 255f
        assertEquals(channel(0.960848743f), Themes.Light.colors.background.red)
        assertEquals(Themes.Light.colors.secondary, light.background)
        assertEquals(buttonHighlightColor(light.background, light.foreground, true), light.backgroundPressed)
        assertEquals(Themes.Light.colors.primary, Themes.Light.colors.ring)
    }

    @Test fun typographyProfilesKeepPlatformDifferences() {
        assertEquals(17f, TypographyProfiles.Native.bodyMedium.fontSize.value)
        assertEquals(22f, TypographyProfiles.Native.bodyMedium.lineHeight.value)
        assertEquals(15f, TypographyProfiles.Web.bodyMedium.fontSize.value)
        assertEquals(23f, TypographyProfiles.Web.bodyMedium.lineHeight.value)
        assertEquals(Typographies.Default.bodyMedium, com.gearui.foundation.typography.Typography.BodyMedium)
    }
}
