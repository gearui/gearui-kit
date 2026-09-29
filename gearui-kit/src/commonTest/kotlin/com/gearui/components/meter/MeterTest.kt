package com.gearui.components.meter

import kotlin.test.*

class MeterTest {
    @Test fun rangesClampAndHandleLargeFiniteEndpoints() {
        assertEquals(.5f, meterFraction(0f, -Float.MAX_VALUE, Float.MAX_VALUE))
        assertEquals(0f, meterFraction(-10f, 0f, 100f))
        assertEquals(1f, meterFraction(200f, 0f, 100f))
    }
    @Test fun invalidRangesAreRejected() {
        assertFailsWith<IllegalArgumentException> { meterFraction(1f, 2f, 2f) }
        assertFailsWith<IllegalArgumentException> { meterFraction(Float.NaN, 0f, 100f) }
    }
}
