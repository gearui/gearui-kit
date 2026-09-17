package com.gearui

import com.gearui.foundation.list.CardDefaults
import com.gearui.primitives.composite.resolveCardShape
import com.gearui.theme.ShapesDefault
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class CardGeometryTest {
    @Test fun defaultCardUsesTheActiveThemeShape() {
        val square = RoundedCornerShape(0.dp)
        val custom = ShapesDefault.Default.copy(xl = square)
        assertSame(square, resolveCardShape(Float.NaN, custom))
    }

    @Test fun explicitRadiusRemainsALocalOverride() {
        assertEquals(RoundedCornerShape(7.dp), resolveCardShape(7f, ShapesDefault.Default))
        assertEquals(RoundedCornerShape(0.dp), resolveCardShape(0f, ShapesDefault.Default))
    }

    @Test fun negativeRadiusKeepsTheExistingClamp() {
        assertEquals(RoundedCornerShape(0.dp), resolveCardShape(-3f, ShapesDefault.Default))
    }

    @Test fun defaultGeometryKeepsTheDeclaredShadowFallback() {
        assertEquals(16.dp, CardDefaults.Default.padding)
        assertEquals(.5.dp, CardDefaults.Default.borderWidth)
        assertEquals(0f, CardDefaults.Default.elevation)
    }
}
