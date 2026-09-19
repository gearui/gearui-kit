package com.gearui

import com.gearui.theme.Themes
import com.gearui.theme.withBrandAccent
import com.gearui.foundation.field.DefaultInputColors
import com.gearui.foundation.button.DefaultButtonColors
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrandAccentTest {
    @Test fun lightAndDarkKeepTheirSurfaceRoles() {
        for (base in listOf(Themes.Light, Themes.Dark)) {
            val result = base.withBrandAccent(Color.Red)
            // The soft pair is derived from the accent, so it moves with it; every other
            // role, including all surfaces, stays exactly as the base theme had it.
            val expected = base.colors.copy(
                primary = Color.Red,
                primaryForeground = Color.Black,
                primarySoft = Color.Red.copy(alpha = 0.15f),
                primarySoftForeground = lerp(Color.Red, base.colors.foreground, 0.20f),
                ring = Color.Red,
            )
            assertEquals(expected, result.colors)
        }
    }

    @Test fun contrastUsesLinearLightNotEncodedBrightness() {
        assertEquals(Color.White, Themes.Light.withBrandAccent(Color(0xFF0046BE)).colors.primaryForeground)
        assertEquals(Color.Black, Themes.Dark.withBrandAccent(Color(0xFFFFD238)).colors.primaryForeground)
    }

    @Test fun callerCanProvideForeground() {
        assertEquals(Color.White, Themes.Light.withBrandAccent(Color.Red, Color.White).colors.primaryForeground)
    }

    @Test fun explicitComponentPalettesKeepTheirOtherRoles() {
        val base = Themes.Light.copy(inputColors = DefaultInputColors.Light, buttonColors = DefaultButtonColors.Light)
        val changed = base.withBrandAccent(Color.Red)
        assertEquals(base.inputColors!!.copy(focusRing = Color.Red), changed.inputColors)
        assertEquals(base.buttonColors!!.copy(focusRing = Color.Red), changed.buttonColors)
    }

    @Test fun derivedComponentPalettesRemainDerived() {
        val base = Themes.Light.copy(inputColors = null, buttonColors = null)
        val changed = base.withBrandAccent(Color.Red)
        assertEquals(Color.Red, DefaultInputColors.from(changed.colors).focusRing)
        assertEquals(null, changed.inputColors)
    }

    @Test fun transparentBrandColorsAreRejectedExplicitly() {
        assertFailsWith<IllegalArgumentException> { Themes.Light.withBrandAccent(Color.Red.copy(alpha = .5f)) }
    }
}
