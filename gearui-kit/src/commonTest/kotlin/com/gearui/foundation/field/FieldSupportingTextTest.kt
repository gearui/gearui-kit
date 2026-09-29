package com.gearui.foundation.field

import kotlin.test.*

class FieldSupportingTextTest {
    @Test fun parentAndChildDoNotBothPrintTheSameError() {
        assertNull(fieldSupportingText("Error", "Helper", true))
        assertEquals("Error", fieldSupportingText("Error", "Helper", false))
        assertEquals("Helper", fieldSupportingText(null, "Helper", true))
    }
}
