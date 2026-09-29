package com.gearui.components.input

import com.tencent.kuikly.compose.ui.text.input.KeyboardType

/**
 * How an [Input] groups what the user types: a mobile number as 138 0013 8000, a bank
 * card as 6222 0212 3456 7890, an ID card as 110101 19900307 123X.
 *
 * The field's `value` stays **raw** — digits (and the ID card's X) only — and the
 * grouping exists only on screen, so a caller never strips spaces before a request and
 * leading zeros survive. Characters outside the format are dropped as they are typed or
 * pasted; the caret keeps its place among the characters the user typed; deleting a
 * separator deletes the character before it.
 *
 * The grouping is done in the text itself, not with a visual transformation, because
 * KuiklyUI's native text fields do not apply one. Each regrouping is written back to the
 * native field across KuiklyUI's asynchronous bridge, so keys arriving faster than that
 * round trip (a scripted burst, not human typing) can be dropped; a paste or an autofill
 * arrives as one change and is unaffected.
 *
 * @param groups sizes of the groups, left to right; the last size repeats for longer input.
 * @param maxLength the most raw characters accepted.
 * @param accept maps a typed character at raw position `index` to the character kept,
 *   or null to drop it.
 * @param keyboardType the keyboard the field asks for.
 * @param isComplete whether a raw value is a whole, valid entry (for enabling "Next").
 * @param prepare rewrites the field's text before filtering — for example dropping a
 *   pasted country code.
 */
class InputFormat(
    val groups: List<Int>,
    val maxLength: Int,
    val separator: Char = ' ',
    val keyboardType: KeyboardType = KeyboardType.Number,
    val accept: (char: Char, index: Int) -> Char? = { c, _ -> c.takeIf { it.isDigit() } },
    val isComplete: (raw: String) -> Boolean = { it.length == maxLength },
    val prepare: (text: String) -> String = { it },
) {
    init {
        require(groups.isNotEmpty() && groups.all { it > 0 }) { "groups must be positive" }
        require(maxLength > 0) { "maxLength must be positive" }
    }

    /** Length of a full value as shown, separators included: the native field's limit. */
    val displayMaxLength: Int get() = format("0".repeat(maxLength)).length

    /** The raw value as shown, with separators. */
    fun format(raw: String): String = buildString {
        var group = 0
        var inGroup = 0
        raw.forEach { c ->
            if (inGroup == groups[group.coerceAtMost(groups.lastIndex)]) {
                append(separator)
                group++
                inGroup = 0
            }
            append(c)
            inGroup++
        }
    }

    /** Keeps only accepted characters, up to [maxLength]. */
    fun clean(text: String): String = buildString {
        text.forEach { c ->
            if (length >= maxLength || c == separator) return@forEach
            accept(c, length)?.let(::append)
        }
    }

    companion object {
        /** A mainland China mobile number: 11 digits, 3-4-4. */
        val ChinaMobile = InputFormat(
            groups = listOf(3, 4, 4),
            maxLength = 11,
            isComplete = { it.length == 11 && it.first() == '1' },
            // A number pasted from contacts often carries the country code: +86 or 0086.
            prepare = { text ->
                val digits = text.filter { it.isDigit() }
                val national = listOf("0086", "86").firstNotNullOfOrNull { code ->
                    digits.removePrefix(code).takeIf { digits.startsWith(code) && it.length == 11 && it.first() == '1' }
                }
                national ?: text
            },
        )

        /** A bank card number: up to 19 digits in groups of four. Complete from 16 digits. */
        val BankCard = InputFormat(
            groups = listOf(4),
            maxLength = 19,
            isComplete = { it.length in 16..19 },
        )

        /**
         * A resident identity card number (GB 11643-1999): 17 digits and a check
         * character, 6-8-4 (region, birth date, sequence and check). The check character
         * may be X — typed x is taken as X — and [isComplete] verifies it.
         */
        val IdCard = InputFormat(
            groups = listOf(6, 8, 4),
            maxLength = 18,
            keyboardType = KeyboardType.Text,
            accept = { c, index ->
                when {
                    c.isDigit() -> c
                    index == 17 && (c == 'x' || c == 'X') -> 'X'
                    else -> null
                }
            },
            isComplete = ::isValidIdCard,
        )
    }
}

/** An edit applied to a formatted field: the raw value and what the field shows. */
internal data class FormattedEdit(val raw: String, val display: String, val caret: Int)

/**
 * Applies the native field's new text to a formatted value.
 *
 * [previousDisplay] is what the field showed; [text] and [caret] are what it reports
 * after the edit.
 */
internal fun InputFormat.edit(previousDisplay: String, text: String, caret: Int): FormattedEdit {
    var working = text
    var at = caret.coerceIn(0, text.length)
    // Backspace over a separator: the text lost only the separator, so the raw value is
    // unchanged and the field would look stuck. Delete the raw character before it.
    val deletedSeparator = text.length == previousDisplay.length - 1 &&
        previousDisplay.getOrNull(at) == separator &&
        previousDisplay.removeRange(at, at + 1) == text
    if (deletedSeparator && at > 0) {
        working = text.removeRange(at - 1, at)
        at -= 1
    }
    val prepared = prepare(working)
    val raw = clean(prepared)
    // A rewrite (a dropped country code) invalidates the caret: put it after the value.
    val rawBeforeCaret = if (prepared == working) clean(working.substring(0, at)).length else raw.length
    val display = format(raw)
    return FormattedEdit(raw, display, displayOffset(display, rawBeforeCaret))
}

/** Offset in [display] just after the [rawCount]-th raw character. */
internal fun InputFormat.displayOffset(display: String, rawCount: Int): Int {
    if (rawCount <= 0) return 0
    var seen = 0
    display.forEachIndexed { i, c ->
        if (c != separator) {
            seen++
            if (seen == rawCount) return i + 1
        }
    }
    return display.length
}

/** GB 11643-1999 check character over the first 17 digits. */
internal fun isValidIdCard(raw: String): Boolean {
    if (raw.length != 18 || !raw.substring(0, 17).all { it.isDigit() }) return false
    val weights = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    val sum = (0 until 17).sumOf { (raw[it] - '0') * weights[it] }
    val check = "10X98765432"[sum % 11]
    return raw[17] == check
}
