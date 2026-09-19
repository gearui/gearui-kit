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

    /**
     * Reference fields have no border (`--field-border: transparent`) and separate
     * from the page with `--shadow-field` in light; in dark the field fill itself
     * contrasts with the background. Both halves must hold, or a default field on
     * a white surface has neither a border nor a shadow.
     */
    @Test fun defaultFieldsUseReferenceShadowInsteadOfBorder() {
        assertEquals(0f, DefaultInputColors.Light.border.alpha)
        assertEquals(0f, DefaultInputColors.Dark.border.alpha)
        kotlin.test.assertTrue(com.gearui.foundation.material.MaterialDefaults.lightField.any { it.color.alpha > 0f })
        kotlin.test.assertTrue(Themes.Dark.colors.background != DefaultInputColors.Dark.background)
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
