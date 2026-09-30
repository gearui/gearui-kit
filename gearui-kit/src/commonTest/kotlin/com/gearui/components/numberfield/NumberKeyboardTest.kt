package com.gearui.components.numberfield

import com.gearui.components.input.numberFieldKeyboard
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberKeyboardTest {
    @Test
    fun onlyWholeNonNegativeFieldsGetTheDigitPad() {
        // KuiklyUI maps only Number, Email and Password; its digit pad has no "." or "-".
        assertEquals(KeyboardType.Number, numberFieldKeyboard(wholeNonNegative = true))
        assertEquals(KeyboardType.Text, numberFieldKeyboard(wholeNonNegative = false))
    }

    @Test
    fun wholeDecimalsHaveNoFraction() {
        assertTrue(DecimalValue.parse("3")!!.isWhole)
        assertTrue(DecimalValue.parse("3.00")!!.isWhole)
        assertFalse(DecimalValue.parse("0.05")!!.isWhole)
        assertFalse(DecimalValue.parse("-1.5")!!.isWhole)
    }
}
