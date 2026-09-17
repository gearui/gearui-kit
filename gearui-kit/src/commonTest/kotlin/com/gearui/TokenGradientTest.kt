package com.gearui

import com.gearui.foundation.material.MaterialDefaults
import com.gearui.foundation.material.TokenGradientStop
import com.gearui.foundation.material.tintedMask
import com.gearui.foundation.material.verticalBrush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.LinearGradient
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TokenGradientTest {
    @Test
    fun preservesStopsAndColorsIncludingHardEdges() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue)
        val stops = listOf(0f, 0.5f, 0.5f)
        val brush = assertIs<LinearGradient>(stops.zip(colors) { p, c -> TokenGradientStop(p, c) }.verticalBrush())
        assertEquals(colors, brush.colors)
        assertEquals(stops, brush.stops)
    }

    @Test
    fun oneStopRendersSolid() {
        val brush = assertIs<SolidColor>(listOf(TokenGradientStop(0.3f, Color.Red)).verticalBrush())
        assertEquals(Color.Red, brush.value)
    }

    @Test
    fun invalidRenderingInputsFailExplicitly() {
        assertFailsWith<IllegalArgumentException> { emptyList<TokenGradientStop>().verticalBrush() }
        for (position in listOf(Float.NaN, -0.1f, 1.1f)) {
            assertFailsWith<IllegalArgumentException> { listOf(TokenGradientStop(position, Color.Red)).verticalBrush() }
        }
        assertFailsWith<IllegalArgumentException> {
            listOf(TokenGradientStop(0.8f, Color.Red), TokenGradientStop(0.2f, Color.Blue)).verticalBrush()
        }
    }

    @Test
    fun maskUsesCurrentSurfaceWithoutMutatingTokens() {
        val top = MaterialDefaults.pickerTopMask
        val light = top.tintedMask(Color.White)
        val dark = top.tintedMask(Color.Black)
        assertEquals(Color.White, light.first().color)
        assertEquals(Color.Black, dark.first().color)
        assertEquals(0f, dark.last().color.alpha)
        assertEquals(1f, top.first().color.alpha)
        assertEquals(listOf(0f, 1f), top.map { it.position })
    }

    @Test
    fun maskMultipliesSurfaceAlphaAndBottomReversesFade() {
        val surface = Color.White.copy(alpha = 0.4f)
        val mask = MaterialDefaults.pickerBottomMask.tintedMask(surface)
        assertEquals(0f, mask.first().color.alpha)
        assertTrue(kotlin.math.abs(0.4f - mask.last().color.alpha) < 0.01f)
    }
}
