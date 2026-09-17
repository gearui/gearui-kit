package com.gearui

import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.control.ControlGeometry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ControlGeometryTest {
    @Test
    fun iconGapsAndTextareaInsetsAreIndependentMetrics() {
        assertEquals(listOf(5f, 6f, 8f, 10f), listOf(
            ControlGeometry.buttonGapExtraSmall.value, ControlGeometry.buttonGapSmall.value,
            ControlGeometry.buttonGapMedium.value, ControlGeometry.buttonGapLarge.value))
        assertEquals(8f, ControlGeometry.textareaPaddingVertical.value)
    }

    @Test
    fun regularFieldUsesGeneratedGeometry() {
        assertEquals(48f, FieldSizeTokens.Medium.height.value)
        assertEquals(12f, FieldSizeTokens.Medium.paddingHorizontal.value)
        assertEquals(ControlGeometry.controlMedium, FieldSizeTokens.Medium.height)
    }

    @Test
    fun buttonAndFieldPaddingRemainDistinct() {
        assertEquals(16f, ControlGeometry.buttonPaddingMedium.value)
        assertTrue(ControlGeometry.buttonPaddingMedium > FieldSizeTokens.Medium.paddingHorizontal)
    }

    @Test
    fun sizesAreOrderedAndBordersDoNotChangeWithSize() {
        assertTrue(FieldSizeTokens.Small.height < FieldSizeTokens.Medium.height)
        assertTrue(FieldSizeTokens.Medium.height < FieldSizeTokens.Large.height)
        assertEquals(FieldSizeTokens.Small.borderWidth, FieldSizeTokens.Large.borderWidth)
    }
}
