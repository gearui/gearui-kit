package com.gearui.components

import com.gearui.components.numberfield.formatNumberFieldValue
import com.gearui.components.numberfield.sanitizeNumberInput
import com.gearui.components.togglebutton.nextToggleSelection
import com.gearui.foundation.control.ControlGeometry
import kotlin.test.Test
import kotlin.test.assertEquals

/** Input sanitising and selection rules for the batch 3 components. */
class Batch3LogicTest {

    @Test
    fun numberInputKeepsOnlyOneDecimalPointAndALeadingMinus() {
        assertEquals("12.5", sanitizeNumberInput("12.5"))
        assertEquals("12.5", sanitizeNumberInput("1a2.b5"))
        // A second point is dropped rather than rejecting the keystroke.
        assertEquals("12.55", sanitizeNumberInput("12.5.5"))
        assertEquals("-8", sanitizeNumberInput("-8"))
        assertEquals("8", sanitizeNumberInput("8-"))
        // A comma is what many keyboards offer for the decimal separator.
        assertEquals("1.5", sanitizeNumberInput("1,5"))
        assertEquals("", sanitizeNumberInput("abc"))
    }

    @Test
    fun wholeNumbersPrintWithoutTrailingZero() {
        assertEquals("3", formatNumberFieldValue(3.0))
        assertEquals("-3", formatNumberFieldValue(-3.0))
        assertEquals("3.5", formatNumberFieldValue(3.5))
    }

    @Test
    fun singleToggleGroupKeepsOneSelectionAndCanClearIt() {
        assertEquals(setOf("b"), nextToggleSelection(setOf("a"), "b", multiple = false))
        assertEquals(emptySet(), nextToggleSelection(setOf("a"), "a", multiple = false))
        assertEquals(setOf("a", "b"), nextToggleSelection(setOf("a"), "b", multiple = true))
        assertEquals(setOf("b"), nextToggleSelection(setOf("a", "b"), "a", multiple = true))
    }

    @Test
    fun pullRefreshThresholdMatchesThePlatformDefault() {
        assertEquals(80f, ControlGeometry.pullRefreshThreshold.value)
    }
}
