package com.gearui.components.cellgroup

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Where separators go in a run of rows.
 *
 * The rule looks too small to test until you notice which half of it callers
 * kept getting wrong: not "put lines between rows", but "and none after the
 * last one". A row cannot know it is last, so every caller that placed its own
 * dividers either counted correctly by hand or drew a stray line against the
 * card's bottom edge.
 */
class CellGroupTest {

    private fun separatorsIn(count: Int) = (0 until count).count { separatorBeforeRow(it) }

    @Test
    fun theFirstRowHasNoSeparatorAboveIt() {
        assertFalse(separatorBeforeRow(0), "a line above the first row draws against the card's top edge")
    }

    @Test
    fun everyLaterRowHasOne() {
        assertTrue(separatorBeforeRow(1))
        assertTrue(separatorBeforeRow(7))
    }

    @Test
    fun nRowsGetNMinusOneSeparators() {
        assertEquals(0, separatorsIn(1), "a single row is not divided from anything")
        assertEquals(2, separatorsIn(3))
        assertEquals(9, separatorsIn(10))
    }

    @Test
    fun anEmptyGroupDrawsNothing() {
        assertEquals(0, separatorsIn(0))
    }
}
