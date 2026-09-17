package com.gearui

import com.gearui.foundation.material.*
import com.gearui.foundation.typography.resolveFontFamily
import com.gearui.foundation.typography.nativeGenericFont
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.text.font.FontListFontFamily
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.font.KuiklyFont
import kotlin.test.*

class SurfaceDecorationTest {
    @Test fun sharpShadowIsSingleBand() {
        assertEquals(listOf(ShadowBand(0f, .4f)), shadowBands(0f, .4f))
        assertTrue(shadowBands(12f, 0f).isEmpty())
    }
    @Test fun blurredCoverageIsBoundedAndPreservesSourceAlpha() {
        for (blur in listOf(.1f, 1f, 8f, 100f)) {
            val bands = shadowBands(blur, .6f)
            assertTrue(bands.size <= 49)
            assertTrue(bands.zipWithNext().all { (a, b) -> a.extent >= b.extent })
            var alpha = 0f
            bands.forEach { assertTrue(it.alpha in 0f..1f); alpha += (1f - alpha) * it.alpha }
            assertEquals(.6f, alpha, 1f / 255f)
        }
    }
    @Test fun lowOpacityShadowsSurviveEightBitBridge() {
        val bands = shadowBands(28f, .03f)
        assertTrue(bands.any { it.alpha >= 1f / 255f })
        var alpha = 0f
        bands.forEach { alpha += (1f - alpha) * it.alpha }
        assertEquals(.03f, alpha, 1f / 255f)
    }
    @Test fun invalidBlurFails() {
        for (value in listOf(-1f, Float.NaN, Float.POSITIVE_INFINITY)) assertFailsWith<IllegalArgumentException> { shadowBands(value, .5f) }
    }
    @Test fun oddDashPatternRepeatsRatherThanDroppingLastEntry() {
        assertEquals(listOf(5f, 3f, 1f, 5f, 3f, 1f), normaliseDashArray(listOf(5f, 3f, 1f)))
        assertEquals(emptyList(), normaliseDashArray(listOf(0f, 0f)))
        assertFailsWith<IllegalArgumentException> { normaliseDashArray(listOf(-1f, 2f)) }
    }
    @Test fun dashPhaseContinuesAcrossCornerSegments() {
        val segments = dashSegments(listOf(Offset(0f, 0f), Offset(3f, 0f), Offset(3f, 7f)), listOf(5f, 3f))
        assertEquals(3, segments.size)
        assertEquals(Offset(3f, 2f), segments[1].end)
        assertFalse(segments[1].startsDash)
        assertTrue(segments[1].endsDash)
        assertEquals(Offset(3f, 5f), segments[2].start)
    }
    @Test fun zeroLengthDashProducesCapWithoutLooping() {
        val segments = dashSegments(listOf(Offset.Zero, Offset(10f, 0f)), listOf(0f, 3f))
        assertEquals(4, segments.size)
        assertTrue(segments.all { it.start == it.end })
    }
    @Test fun pathologicalDashDensityFailsExplicitly() {
        assertFailsWith<IllegalArgumentException> { dashSegments(listOf(Offset.Zero, Offset(100f, 0f)), listOf(.00001f, .00001f)) }
    }
    @Test fun fontRegistrySelectsFirstAvailableFamily() {
        val family = resolveFontFamily(listOf("missing", "Brand", "system-ui"), FontWeight.Bold, mapOf("Brand" to "Brand-Bold")) as FontListFontFamily
        assertEquals("Brand-Bold", (family.fonts.single() as KuiklyFont).fontName)
        assertEquals(FontWeight.Bold, family.fonts.single().weight)
    }
    @Test fun systemFallbackWinsAtItsDeclaredPosition() {
        val platformName = nativeGenericFont("system-ui")
        for (names in listOf(listOf("system-ui", "Brand"), listOf("missing"))) {
            val family = resolveFontFamily(names, FontWeight.Normal, mapOf("Brand" to "Installed"))
            if (platformName == null) assertEquals(FontFamily.Default, family)
            else {
                val font = (family as FontListFontFamily).fonts.single() as KuiklyFont
                assertEquals(platformName, font.fontName)
                assertEquals(FontWeight.Normal, font.weight)
            }
        }
    }
}
