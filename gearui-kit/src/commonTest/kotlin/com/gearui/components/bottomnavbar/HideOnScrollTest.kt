package com.gearui.components.bottomnavbar

import kotlin.test.Test
import kotlin.test.assertEquals

class HideOnScrollTest {
    private fun p(index: Int, offset: Int) = ScrollPosition(index, offset)

    @Test
    fun readingOnHidesTheBar() {
        assertEquals(false, barVisibility(p(2, 0), p(3, 10), visible = true, thresholdPx = 24))
        assertEquals(false, barVisibility(p(2, 0), p(2, 40), visible = true, thresholdPx = 24))
    }

    @Test
    fun scrollingBackShowsIt() {
        assertEquals(true, barVisibility(p(3, 0), p(2, 100), visible = false, thresholdPx = 24))
        assertEquals(true, barVisibility(p(2, 80), p(2, 30), visible = false, thresholdPx = 24))
    }

    @Test
    fun smallMovementsLeaveItAlone() {
        assertEquals(true, barVisibility(p(2, 10), p(2, 20), visible = true, thresholdPx = 24))
        assertEquals(false, barVisibility(p(2, 20), p(2, 10), visible = false, thresholdPx = 24))
    }

    @Test
    fun theTopAlwaysShowsIt() {
        assertEquals(true, barVisibility(p(0, 200), p(0, 5), visible = false, thresholdPx = 24))
    }
}
