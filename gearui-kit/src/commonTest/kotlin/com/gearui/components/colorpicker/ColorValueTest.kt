package com.gearui.components.colorpicker

import kotlin.test.*

class ColorValueTest {
    @Test fun primaryColorsAndHueWrap() {
        assertEquals("#FF0000", ColorValue(0f, 1f, 1f).toHex())
        assertEquals("#FF0000", ColorValue(360f, 1f, 1f).toHex())
        assertEquals("#00FF00", ColorValue(120f, 1f, 1f).toHex())
        assertEquals("#0000FF", ColorValue(240f, 1f, 1f).toHex())
    }
    @Test fun hexRoundTripsIncludingTransparentPixels() {
        for (hex in listOf("#ABCDEF", "#00000000", "#FA208080", "#FFFFFF")) {
            assertEquals(hex, ColorValue.parseHex(hex)!!.toHex(hex.length == 9))
        }
        assertEquals("#AABBCCDD", ColorValue.parseHex("#abcd")!!.toHex())
        assertEquals("#AABBCC", ColorValue.parseHex("abc")!!.toHex())
        assertNull(ColorValue.parseHex("#gg0000"))
        assertNull(ColorValue.parseHex("#12345"))
    }
    @Test fun planeEdgesClampWithoutChangingHueOrAlpha() {
        val original = ColorValue(200f, .5f, .5f, .4f)
        assertEquals(original.copy(saturation = 0f, brightness = 1f), areaColor(original, -1f, -1f))
        assertEquals(original.copy(saturation = 1f, brightness = 0f), areaColor(original, 2f, 2f))
        assertFailsWith<IllegalArgumentException> { ColorValue(Float.NaN, .5f, .5f) }
    }
}
