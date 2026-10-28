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
    @Test fun aSelectionIsKeptWhileTheTextIsUnchanged() {
        // Shift+Left four times from the end of a full number: anchor 13, active end 9.
        val selecting = TextFieldValue("138 0013 8000", TextRange(13, 9))
        val edit = formattedInputEdit(InputFormat.ChinaMobile, "138 0013 8000", selecting)
        assertEquals(TextRange(13, 9), edit.fieldValue.selection)
        assertEquals("13800138000", edit.raw)
    }
    @Test fun typingOverASelectionReplacesIt() {
        // The native field replaces the selected "8000" with "7" and reports the result.
        val edit = formattedInputEdit(InputFormat.ChinaMobile, "138 0013 8000", TextFieldValue("138 0013 7", TextRange(10)))
        assertEquals("138 0013 7", edit.fieldValue.text)
        assertEquals("138001 37".replace(" ", ""), edit.raw)
        assertEquals(TextRange(10), edit.fieldValue.selection)
    }
}
