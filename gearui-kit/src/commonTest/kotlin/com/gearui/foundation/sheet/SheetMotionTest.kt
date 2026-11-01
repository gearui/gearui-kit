package com.gearui.foundation.sheet

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SheetMotionTest {
    @Test
    fun theRubberBandGivesLessTheFurtherItGoes() {
        assertEquals(0f, rubberBand(0f, 400f))
        val first = rubberBand(50f, 400f)
        val second = rubberBand(100f, 400f) - first
        assertTrue(first in 1f..50f)
        assertTrue(second < first)
    }

    @Test
    fun theRubberBandNeverReachesTheDimension() {
        assertTrue(rubberBand(100_000f, 400f) < 400f)
    }

    @Test
    fun noDimensionNoStretch() {
        assertEquals(0f, rubberBand(80f, 0f))
    }
}
