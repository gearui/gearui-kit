package com.gearui.components.input

import com.tencent.kuikly.compose.ui.text.TextRange
import com.tencent.kuikly.compose.ui.text.input.TextFieldValue
import kotlin.test.*

class FormattedInputEditTest {
    @Test fun compositionKeepsDraftSelectionAndDoesNotPublish() {
        val draft = TextFieldValue("138ni", TextRange(5), TextRange(3, 5))
        val edit = formattedInputEdit(InputFormat.ChinaMobile, "138", draft)
        assertEquals(draft, edit.fieldValue)
        assertNull(edit.raw)
        val committed = formattedInputEdit(InputFormat.ChinaMobile, draft.text, TextFieldValue("138你", TextRange(4)))
        assertEquals("138", committed.raw)
        assertNull(committed.fieldValue.composition)
    }
    @Test fun countryCodePasteIsCleanedBeforeTheRawLengthLimit() {
        val edit = formattedInputEdit(InputFormat.ChinaMobile, "", TextFieldValue("+86 138 0013 8000", TextRange(16)))
        assertEquals("13800138000", edit.raw)
        assertEquals("138 0013 8000", edit.fieldValue.text)
    }
}
