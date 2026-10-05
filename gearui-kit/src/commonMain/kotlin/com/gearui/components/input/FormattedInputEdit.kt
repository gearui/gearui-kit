package com.gearui.components.input

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Job
import kotlin.time.TimeSource
import kotlin.time.TimeMark
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration
import com.tencent.kuikly.compose.ui.text.TextRange
import com.tencent.kuikly.compose.ui.text.input.TextFieldValue

internal data class FormattedInputEdit(val fieldValue: TextFieldValue, val raw: String?)

/** Native IME composition is a draft. Filter and publish only after it commits. */
internal fun formattedInputEdit(format: InputFormat, previous: String, next: TextFieldValue): FormattedInputEdit {
    if (next.composition != null) return FormattedInputEdit(next, null)
    val edit = format.edit(previous, next.text, next.selection.end)
    return FormattedInputEdit(TextFieldValue(edit.display, TextRange(edit.caret)), edit.raw)
}

/**
 * Tells a formatted field's key bursts from typing.
 *
 * A burst is a change of text under [GAP] after the previous change. The native field
 * reports each change twice; a repeat of the same text is not a new key and does not
 * count. While [holding], the field shows what it reports; [before] is the grouped text
 * it showed when the burst began, for the edit applied when it ends.
 */
internal class FormatBurst(private val clock: TimeSource = TimeSource.Monotonic) {
    var holding by mutableStateOf(false)
    var before: String = ""
        private set
    var release: Job? = null
    private var lastText: String? = null
    private var lastChange: TimeMark? = null

    fun isBurst(reported: String): Boolean {
        if (reported == lastText) return holding
        val quick = lastChange?.let { it.elapsedNow() < GAP } ?: false
        lastText = reported
        lastChange = clock.markNow()
        return quick || holding
    }

    fun hold(shown: String) {
        if (!holding) before = shown
        holding = true
    }

    companion object {
        /** Under any human key interval, over a reader's. */
        val GAP: Duration = 80.milliseconds
    }
}
