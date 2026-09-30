package com.gearui.components.input

import kotlin.test.Test
import kotlin.test.assertEquals

/** Inserting into a number that is already full keeps the digit at the caret and drops the last. */
class FullFormattedInsertTest {
    private val phone = InputFormat.ChinaMobile

    @Test fun insertAfterAGroupLandsAtTheCaret() {
        val e = phone.edit("138 1234 5678", "138 12349 5678", 9)
        assertEquals("13812349567", e.raw)
        assertEquals("138 1234 9567", e.display)
        assertEquals(10, e.caret)
    }

    @Test fun insertJustAfterASeparatorLandsAtTheCaret() {
        val e = phone.edit("138 1234 5678", "138 91234 5678", 5)
        assertEquals("13891234567", e.raw)
        assertEquals("138 9123 4567", e.display)
        assertEquals(5, e.caret)
    }
}
