package com.gearui

import com.gearui.foundation.field.DefaultInputColors
import com.gearui.foundation.field.inputBorderColor
import com.gearui.foundation.field.inputFocusColor
import com.gearui.theme.ShapesDefault
import com.gearui.theme.Themes
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Outline
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class InputAppearanceTest {
    private val colors = DefaultInputColors.Dark

    @Test fun focusRingUsesBrandAndValidationWithoutChangingLayout() {
        val custom = colors.copy(focusRing = Color.Green)
        assertEquals(Color.Green, inputFocusColor(custom, true, true, null))
        assertEquals(Color.Red, inputFocusColor(custom, true, true, Color.Red))
        assertEquals(null, inputFocusColor(custom, false, true, Color.Red))
        assertEquals(null, inputFocusColor(custom, true, false, null))
    }

    @Test fun defaultFieldsRemainVisibleWithoutLayeredShadow() {
        kotlin.test.assertTrue(DefaultInputColors.Light.border.alpha > 0f)
        kotlin.test.assertTrue(DefaultInputColors.Dark.border.alpha > 0f)
    }

    @Test fun explicitTransparentBorderIsNotOverridden() {
        val custom = DefaultInputColors.Light.copy(border = Color.Transparent)
        assertEquals(Color.Transparent, inputBorderColor(custom, true, false, false, null))
    }

    @Test fun hoverChangesBorderOnlyUntilFocus() {
        assertEquals(colors.borderHover, inputBorderColor(colors, true, false, true, null))
        assertEquals(colors.border, inputBorderColor(colors, true, true, true, null))
        assertEquals(colors.border, inputBorderColor(colors, true, false, false, null))
    }

    @Test fun disabledDoesNotRespondToHover() {
        assertEquals(colors.border, inputBorderColor(colors, false, false, true, null))
    }

    @Test fun validationExtensionWinsOverTransientStates() {
        for (focused in listOf(false, true)) for (hovered in listOf(false, true)) {
            assertEquals(Color.Red, inputBorderColor(colors, true, focused, hovered, Color.Red))
        }
    }

    @Test fun customBrandDoesNotFallBackToDefaultPalette() {
        val brand = Themes.Light.colors.copy(surface = Color.Red, input = Color.Blue)
        val palette = DefaultInputColors.from(brand)
        assertEquals(Color.Red, palette.background)
        assertEquals(Color.Blue, palette.border)
        assertEquals(brand.ring, palette.focusRing)
    }

    @Test fun builtInThemesCarryTheirInputPaletteThroughCopies() {
        assertEquals(DefaultInputColors.Light, Themes.Light.copy().inputColors)
        assertEquals(DefaultInputColors.Dark, Themes.Dark.copy().inputColors)
    }

    @Test fun controlRadiiFollowReferenceSizes() {
        val shapes = ShapesDefault.Default
        for ((shape, radius) in listOf(shapes.sm to 8f, shapes.md to 12f,
            shapes.lg to 14f, shapes.controlLarge to 16f)) {
            val outline = shape.createOutline(Size(100f, 52f), LayoutDirection.Ltr, Density(1f)) as Outline.Rounded
            assertEquals(radius, outline.roundRect.topLeftCornerRadius.x)
        }
        assertEquals(shapes.lg, shapes.copy(controlLarge = shapes.lg).controlLarge)
    }
}
