package com.gearui.components.input

import com.tencent.kuikly.compose.ui.text.TextRange
import com.tencent.kuikly.compose.ui.text.input.TextFieldValue

internal data class FormattedInputEdit(val fieldValue: TextFieldValue, val raw: String?)

/** Native IME composition is a draft. Filter and publish only after it commits. */
internal fun formattedInputEdit(format: InputFormat, previous: String, next: TextFieldValue): FormattedInputEdit {
    if (next.composition != null) return FormattedInputEdit(next, null)
    val edit = format.edit(previous, next.text, next.selection.end)
    return FormattedInputEdit(TextFieldValue(edit.display, TextRange(edit.caret)), edit.raw)
}
