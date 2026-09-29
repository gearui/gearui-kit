package com.gearui.components.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputFormatTest {
    private val mobile = InputFormat.ChinaMobile

    @Test
    fun groupsAMobileNumberThreeFourFour() {
        assertEquals("138 0013 8000", mobile.format("13800138000"))
        assertEquals("138 0", mobile.format("1380"))
    }

    @Test
    fun typingAtTheEndKeepsTheCaretAtTheEnd() {
        val edit = mobile.edit("138", "1380", 4)
        assertEquals("1380", edit.raw)
        assertEquals("138 0", edit.display)
        assertEquals(5, edit.caret)
    }

    @Test
    fun pastingSpacesAndDashesKeepsOnlyDigits() {
        val edit = mobile.edit("", "138-0013 8000", 13)
        assertEquals("13800138000", edit.raw)
        assertEquals(13, edit.caret)
    }

    @Test
    fun pastingWithACountryCodeDropsIt() {
        assertEquals("13800138000", mobile.edit("", "+86 138 0013 8000", 17).raw)
        assertEquals("13800138000", mobile.edit("", "0086-13800138000", 16).raw)
    }

    @Test
    fun backspaceOnASeparatorDeletesTheDigitBeforeIt() {
        // "138 0013" with the caret after the space; backspace removes the space.
        val edit = mobile.edit("138 0013", "1380013", 3)
        assertEquals("130013", edit.raw)
        assertEquals("130 013", edit.display)
        assertEquals(2, edit.caret)
    }

    @Test
    fun insertingInTheMiddleKeepsTheCaretAfterTheInsertedDigit() {
        // "138 0013" -> type 9 after "138 " (display offset 4) -> native text "138 90013", caret 5
        val edit = mobile.edit("138 0013", "138 90013", 5)
        assertEquals("13890013", edit.raw)
        assertEquals("138 9001 3", edit.display)
        assertEquals(5, edit.caret)
    }

    @Test
    fun stopsAtTheMaximumLength() {
        assertEquals("13800138000", mobile.clean("138001380001234"))
    }

    @Test
    fun bankCardGroupsOfFourUpToNineteenDigits() {
        assertEquals("6222 0212 3456 7890 123", InputFormat.BankCard.format("6222021234567890123"))
        assertTrue(InputFormat.BankCard.isComplete("6222021234567890"))
        assertFalse(InputFormat.BankCard.isComplete("622202123456789"))
    }

    @Test
    fun idCardTakesXOnlyAsTheCheckCharacter() {
        val id = InputFormat.IdCard
        assertEquals("11010519491231002X", id.clean("11010519491231002x"))
        assertEquals("1101", id.clean("11x01"))
        assertEquals("110105 19491231 002X", id.format("11010519491231002X"))
    }

    @Test
    fun idCardChecksumFollowsGb11643() {
        assertTrue(isValidIdCard("11010519491231002X"))
        assertFalse(isValidIdCard("110105194912310021"))
        assertFalse(isValidIdCard("11010519491231002"))
    }

    @Test
    fun keepsLeadingZeros() {
        assertEquals("0012 3456", InputFormat.BankCard.format("00123456"))
    }
}
