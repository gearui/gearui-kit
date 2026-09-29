package com.gearui.components.datefield

import androidx.compose.runtime.*
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.input.Input
import com.gearui.components.inputgroup.InputGroup
import com.gearui.components.picker.*
import com.gearui.foundation.field.*
import com.gearui.foundation.interaction.LocalControlLabel
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.tencent.kuikly.compose.ui.text.style.TextAlign

/** Segmented civil date. Display order follows the language pack; the value remains Gregorian. */
@Composable
fun DateField(value: CalendarDate?, onValueChange: (CalendarDate?) -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, label: String? = null, error: String? = null,
    constraints: DatePickerConstraints = DatePickerConstraints.Default, variant: FieldVariant = FieldVariant.PRIMARY) {
    var parts by remember { mutableStateOf(dateParts(value)) }
    var emitted by remember { mutableStateOf(value) }
    LaunchedEffect(value) { if (value != emitted) parts = dateParts(value) }
    val copy = I18n.strings
    val order = dateSegmentOrder(copy.format.dateFormat).filter { it <= constraints.precision.ordinal }
    val names = listOf(copy.dateTime.yearSuffix, copy.dateTime.monthSuffix, copy.dateTime.daySuffix)
    val allowed = remember(constraints) { constraints.dates().toSet() }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap)) {
        if (label != null) FieldLabel(label, invalid = error != null, enabled = enabled)
        InputGroup(enabled = enabled, variant = variant) {
            order.forEachIndexed { index, segment ->
                if (index > 0) Text("/", style = Theme.typography.bodyMedium, color = Theme.colors.mutedForeground)
                CompositionLocalProvider(LocalControlLabel provides listOfNotNull(label, names[segment]).joinToString(" ")) {
                    Input(parts[segment], { text ->
                        parts = parts.toMutableList().also { it[segment] = text }
                        val candidate = parseDateParts(parts, constraints.precision)
                        val resolved = candidate?.let { date -> allowed.firstOrNull { d ->
                            d.year == date.year && (constraints.precision == DatePickerPrecision.YEAR || d.month == date.month) &&
                                (constraints.precision != DatePickerPrecision.DAY || d.day == date.day)
                        } }
                        if (parts.all { it.isEmpty() } || resolved != null) { emitted = resolved; onValueChange(resolved) }
                    }, modifier = Modifier.weight(if (segment == 0) 1.5f else 1f), enabled = enabled,
                        placeholder = names[segment], keyboardType = KeyboardType.Number, maxLength = if (segment == 0) 4 else 2,
                        textAlign = TextAlign.Center, variant = variant)
                }
            }
        }
        FieldErrorText(error)
    }
}

@Composable
fun TimeField(value: PickerTime?, onValueChange: (PickerTime?) -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, label: String? = null, error: String? = null,
    constraints: TimePickerConstraints = TimePickerConstraints.Default, variant: FieldVariant = FieldVariant.PRIMARY) {
    var parts by remember { mutableStateOf(timeParts(value)) }
    var emitted by remember { mutableStateOf(value) }
    LaunchedEffect(value) { if (value != emitted) parts = timeParts(value) }
    val copy = I18n.strings.dateTime
    val names = listOf(copy.hourSuffix, copy.minuteSuffix, copy.secondSuffix)
    val allowed = remember(constraints) { constraints.times().toSet() }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap)) {
        if (label != null) FieldLabel(label, invalid = error != null, enabled = enabled)
        InputGroup(enabled = enabled, variant = variant) {
            repeat(constraints.precision.ordinal + 1) { segment ->
                if (segment > 0) Text(":", style = Theme.typography.bodyMedium, color = Theme.colors.mutedForeground)
                CompositionLocalProvider(LocalControlLabel provides listOfNotNull(label, names[segment]).joinToString(" ")) {
                    Input(parts[segment], { text ->
                        parts = parts.toMutableList().also { it[segment] = text }
                        val candidate = parseTimeParts(parts, constraints.precision)
                        if (parts.all { it.isEmpty() } || candidate in allowed) { emitted = candidate; onValueChange(candidate) }
                    }, modifier = Modifier.weight(1f), enabled = enabled, placeholder = names[segment],
                        keyboardType = KeyboardType.Number, maxLength = 2, textAlign = TextAlign.Center, variant = variant)
                }
            }
        }
        FieldErrorText(error)
    }
}

private fun dateParts(date: CalendarDate?) = listOf(date?.year, date?.month, date?.day).map { it?.toString().orEmpty() }
private fun timeParts(time: PickerTime?) = listOf(time?.hour, time?.minute, time?.second).map { it?.toString()?.padStart(2, '0').orEmpty() }
internal fun dateSegmentOrder(pattern: String): List<Int> = listOf("{year}", "{month}", "{day}")
    .mapIndexed { index, token -> index to pattern.indexOf(token).let { if (it < 0) Int.MAX_VALUE else it } }
    .sortedBy { it.second }.map { it.first }
internal fun parseDateParts(parts: List<String>, precision: DatePickerPrecision): CalendarDate? {
    val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val month = if (precision == DatePickerPrecision.YEAR) 1 else parts.getOrNull(1)?.toIntOrNull() ?: return null
    val day = if (precision != DatePickerPrecision.DAY) 1 else parts.getOrNull(2)?.toIntOrNull() ?: return null
    return CalendarDate(year, month, day).takeIf(::validPickerDate)
}
internal fun parseTimeParts(parts: List<String>, precision: TimePickerPrecision): PickerTime? {
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minute = if (precision == TimePickerPrecision.HOUR) 0 else parts.getOrNull(1)?.toIntOrNull() ?: return null
    val second = if (precision != TimePickerPrecision.SECOND) 0 else parts.getOrNull(2)?.toIntOrNull() ?: return null
    return if (hour in 0..23 && minute in 0..59 && second in 0..59) PickerTime(hour, minute, second) else null
}
