package com.gearui.components.numberfield

import kotlin.test.*

class DecimalValueTest {
    private fun d(text: String) = DecimalValue.parse(text)!!
    @Test fun exactSteppingAndCancellation() {
        assertEquals(d("0.3"), d("0.1") + d("0.2"))
        assertEquals(d("0"), d("-0.1") + d("0.1"))
        assertEquals(d("-0.01"), d("0.09") - d("0.1"))
        assertEquals(d("1"), d("0.99999999999999999999") + d("0.00000000000000000001"))
    }
    @Test fun valuesBeyondDoublePrecisionRemainDistinct() {
        assertTrue(d("9007199254740993") > d("9007199254740992"))
        assertEquals("999999999999999999999999.01", (d("999999999999999999999999") + d(".01")).toString())
    }
    @Test fun canonicalizationComparisonAndBounds() {
        assertEquals("0", d("-0.000").toString())
        assertEquals(d("1"), d("0001.00"))
        assertEquals(d("1").hashCode(), d("1.00").hashCode())
        assertTrue(d("-1.01") < d("-1"))
        assertEquals(d("2"), d("3").coerceIn(d("1"), d("2")))
    }
    @Test fun unfinishedInputDoesNotCommit() {
        listOf("", "-", ".", "1.", "1e2", "NaN").forEach { assertNull(DecimalValue.parse(it)) }
        assertEquals(d("-0.5"), d("-.5"))
    }
}
