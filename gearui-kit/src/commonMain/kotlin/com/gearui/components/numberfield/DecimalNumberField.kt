package com.gearui.components.numberfield

import com.gearui.components.icon.*
import com.gearui.components.input.numberFieldKeyboard
import androidx.compose.runtime.*
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.components.input.Input
import com.gearui.foundation.field.*
import com.gearui.foundation.interaction.LocalControlLabel
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/**
 * Exact decimal overload. Supply a matching parser when formatting currency or localized
 * separators. A whole [step] with a non-negative [min] asks for the digit pad; otherwise
 * the text keyboard, as KuiklyUI has no decimal or signed pad.
 */
@Composable
fun NumberField(
    value: DecimalValue?, onValueChange: (DecimalValue?) -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, label: String? = null, description: String? = null, error: String? = null,
    variant: FieldVariant = FieldVariant.PRIMARY, placeholder: String = "",
    min: DecimalValue? = null, max: DecimalValue? = null, step: DecimalValue = DecimalValue.One,
    required: Boolean = false, format: (DecimalValue) -> String = { it.toString() },
    parser: (String) -> DecimalValue? = DecimalValue::parse,
) {
    require(step > DecimalValue.Zero && (min == null || max == null || min <= max))
    var text by remember { mutableStateOf(value?.let(format).orEmpty()) }
    var editing by remember { mutableStateOf(false) }
    var emitted by remember { mutableStateOf(value) }
    LaunchedEffect(value, editing) {
        if (!editing || value != emitted) text = value?.let(format).orEmpty()
    }
    fun commit(next: DecimalValue?) {
        val bounded = next?.coerceIn(min, max)
        emitted = bounded
        text = bounded?.let(format).orEmpty()
        onValueChange(bounded)
    }
    val base = value ?: DecimalValue.Zero
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap)) {
        if (label != null) FieldLabel(label, required = required, invalid = error != null, enabled = enabled)
        CompositionLocalProvider(LocalControlLabel provides label, LocalFieldErrorOwned provides true) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                CloseButton(onClick = { commit(base - step) }, icon = Icons.minus,
                    contentDescription = I18n.strings.common.remove,
                    enabled = enabled && (min == null || base - step >= min))
                Input(value = text, onValueChange = { next ->
                    text = next
                    val parsed = parser(next)
                    if (next.isEmpty() || parsed != null) {
                        emitted = parsed?.coerceIn(min, max)
                        onValueChange(emitted)
                    }
                }, modifier = Modifier.weight(1f), enabled = enabled, error = error, variant = variant, placeholder = placeholder,
                    keyboardType = numberFieldKeyboard(step.isWhole && min != null && min >= DecimalValue.Zero),
                    // Commit on leaving the field, not on the unfocused state reported when it first appears.
                    onFocusChanged = { focused ->
                        val left = editing && !focused
                        editing = focused
                        if (left) commit(parser(text) ?: value)
                    })
                CloseButton(onClick = { commit(base + step) }, icon = Icons.plus,
                    contentDescription = I18n.strings.common.add,
                    enabled = enabled && (max == null || base + step <= max))
            }
        }
        if (error != null) FieldErrorText(error) else if (description != null) FieldDescription(description)
    }
}
