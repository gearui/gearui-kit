package com.gearui.components.numberfield

import com.gearui.components.icon.*
import com.gearui.components.icon.IconSource
import com.gearui.components.input.numberFieldKeyboard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldVariant
import com.gearui.components.input.Input
import com.gearui.components.input.InputSize
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.style.TextAlign

/**
 * NumberField — HeroUI v3 `NumberField` for mobile: a number the user can both type
 * and step, for quantities, prices and durations.
 *
 * Stepper is the compact control for a small count; NumberField is a full field with
 * a label, description and error, and it accepts decimals and negatives.
 *
 * The typed text is kept as typed while editing ("1.", "-" and an empty field are all
 * legal midway), and only committed to [onValueChange] when it parses. Leaving the
 * field rewrites the text from the committed value, so an abandoned edit cannot leave
 * something unparsable on screen.
 *
 * [step], [min] and [max] bound the buttons and the committed value. A whole [step]
 * with a non-negative [min] asks for the digit pad; otherwise the text keyboard, as
 * KuiklyUI has no decimal or signed pad.
 */
@Composable
fun NumberField(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    description: String? = null,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    placeholder: String = "",
    min: Double = Double.NEGATIVE_INFINITY,
    max: Double = Double.POSITIVE_INFINITY,
    step: Double = 1.0,
    required: Boolean = false,
    format: (Double) -> String = { formatNumberFieldValue(it) },
) {
    require(min <= max && step.isFinite() && step > 0 && (value == null || value.isFinite()))
    var text by remember { mutableStateOf(value?.let(format).orEmpty()) }
    var editing by remember { mutableStateOf(false) }
    var emitted by remember { mutableStateOf(value) }
    LaunchedEffect(value, editing) {
        if (!editing || value != emitted) text = value?.let(format).orEmpty()
    }
    val canDecrease = enabled && (value ?: 0.0) - step >= min - EPSILON
    val canIncrease = enabled && (value ?: 0.0) + step <= max + EPSILON

    fun commit(next: Double?) {
        val clamped = next?.coerceIn(min, max)
        emitted = clamped
        text = clamped?.let(format) ?: ""
        onValueChange(clamped)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap),
    ) {
        if (label != null) FieldLabel(text = label, required = required, invalid = error != null)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.alertGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepButton(Icons.minus, canDecrease) { commit(((value ?: 0.0) - step)) }
            Input(
                value = text,
                onValueChange = { typed ->
                    val cleaned = sanitizeNumberInput(typed)
                    text = cleaned
                    val parsed = cleaned.toDoubleOrNull()
                    when {
                        cleaned.isEmpty() -> { emitted = null; onValueChange(null) }
                        parsed != null && parsed.isFinite() -> { emitted = parsed.coerceIn(min, max); onValueChange(emitted) }
                        // Half-typed input such as "-" or "1." stays on screen and commits nothing.
                        else -> Unit
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                variant = variant,
                placeholder = placeholder,
                size = InputSize.MEDIUM,
                keyboardType = numberFieldKeyboard(step % 1.0 == 0.0 && min >= 0.0),
                textAlign = TextAlign.Center,
                // Commit on leaving the field, not on the unfocused state reported when it first appears.
                onFocusChanged = { focused ->
                    val left = editing && !focused
                    editing = focused
                    if (left) commit(text.toDoubleOrNull()?.takeIf { it.isFinite() } ?: value)
                },
            )
            StepButton(Icons.plus, canIncrease) { commit(((value ?: 0.0) + step)) }
        }
        when {
            error != null -> FieldErrorText(error)
            description != null -> FieldDescription(description)
        }
    }
}

@Composable
private fun StepButton(icon: IconSource, enabled: Boolean, onClick: () -> Unit) {
    // A CloseButton is the icon-only tertiary button of the reference; only the glyph differs.
    CloseButton(onClick = onClick, enabled = enabled, icon = icon,
        contentDescription = if (icon == Icons.minus) I18n.strings.common.remove else I18n.strings.common.add)
}

private const val EPSILON = 1e-9

/** Digits, one decimal point and a leading minus; anything else is dropped as it is typed. */
internal fun sanitizeNumberInput(raw: String): String {
    val negative = raw.startsWith("-")
    var seenDot = false
    val body = buildString {
        for (ch in raw) {
            when {
                ch in '0'..'9' -> append(ch)
                (ch == '.' || ch == ',') && !seenDot -> {
                    seenDot = true
                    append('.')
                }
            }
        }
    }
    return if (negative) "-$body" else body
}

/** Whole numbers print without a trailing ".0"; the rest keep what the user typed. */
internal fun formatNumberFieldValue(value: Double): String {
    val rounded = value.toLong()
    return if (value == rounded.toDouble()) rounded.toString() else value.toString()
}
