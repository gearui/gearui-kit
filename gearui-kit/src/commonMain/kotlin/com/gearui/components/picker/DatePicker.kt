package com.gearui.components.picker

import com.gearui.components.calendar.CalendarMath
import com.gearui.components.calendar.CalendarDate
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.shadowed
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.theme.Theme
import com.gearui.theme.LocalInputColors
import com.gearui.i18n.I18n
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.components.icon.Icons
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.field.FieldErrorText

/**
 * DatePicker - fully Theme-driven date picker
 */
@Composable
fun DatePickerInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.dateTime.datePlaceholder,
    label: String? = null,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    format: String = "YYYY-MM-DD",
    constraints: DatePickerConstraints = DatePickerConstraints.Default,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap)
            )
        }

        // Input trigger
        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .then(fieldTriggerModifier(enabled, error, variant) { showPicker = true })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    style = Theme.typography.bodyMedium,
                    color = if (value.isNotEmpty()) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                Icon(
                    name = Icons.calendar_blank,
                    size = FieldDefaults.trailingIconSize,
                    tint = colors.mutedForeground
                )
            }
        }

        FieldErrorText(error)
    }

    DateWheelSheet(
        visible = showPicker && enabled,
        constraints = constraints,
        format = format,
        value = value,
        onConfirm = { onValueChange(it); showPicker = false },
        onDismiss = { showPicker = false },
    )
}

/**
 * TimePicker - time picker
 */
@Composable
fun TimePickerInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = I18n.strings.dateTime.timePlaceholder,
    label: String? = null,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    format: String = "HH:mm",
    constraints: TimePickerConstraints = TimePickerConstraints.Default,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = if (enabled) colors.foreground else colors.mutedForeground,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap)
            )
        }

        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .then(fieldTriggerModifier(enabled, error, variant) { showPicker = true })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    style = Theme.typography.bodyMedium,
                    color = if (value.isNotEmpty()) LocalInputColors.current.foreground else LocalInputColors.current.placeholder
                )

                Icon(
                    name = Icons.clock,
                    size = FieldDefaults.trailingIconSize,
                    tint = colors.mutedForeground
                )
            }
        }

        FieldErrorText(error)
    }

    TimeWheelSheet(
        visible = showPicker && enabled,
        constraints = constraints,
        format = format,
        value = value,
        onConfirm = { onValueChange(it); showPicker = false },
        onDismiss = { showPicker = false },
    )
}

/** Complete admissible dates determine every wheel, so no month can lead to an empty day column. */
@Composable
internal fun DateWheelSheet(
    visible: Boolean, value: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit,
    constraints: DatePickerConstraints = DatePickerConstraints.Default, format: String = "YYYY-MM-DD",
) {
    val strings = I18n.strings.dateTime
    val allowed = remember(constraints) { constraints.dates() }
    val initial = allowed.firstOrNull { pickerDateText(it, constraints.precision, format) == value }
        ?: allowed.firstOrNull { it >= CalendarDate.today() } ?: allowed.lastOrNull()
    var selected by remember(visible, value, constraints, format) { mutableStateOf(initial) }
    val columns = constraints.precision.ordinal + 1
    PickerSheet(visible, strings.selectDateTitle, onDismiss, {
        selected?.let { onConfirm(pickerDateText(it, constraints.precision, format)) }
    }, onDismiss, confirmEnabled = selected != null) {
        if (selected == null) {
            Text(text = I18n.strings.common.noData, style = Theme.typography.bodyMedium,
                color = Theme.colors.mutedForeground, modifier = Modifier.padding(Spacing.lg))
        } else {
            val date = selected!!
            val years = allowed.map { it.year }.distinct()
            val months = allowed.filter { it.year == date.year }.map { it.month }.distinct()
            val days = allowed.filter { it.year == date.year && it.month == date.month }.map { it.day }.distinct()
            PickerWheels(columns) { column ->
                val entries = when (column) { 0 -> years; 1 -> months; else -> days }
                val chosen = when (column) { 0 -> date.year; 1 -> date.month; else -> date.day }
                val suffix = when (column) { 0 -> strings.yearSuffix; 1 -> strings.monthSuffix; else -> strings.daySuffix }
                key(column, entries) {
                    WheelPickerColumn(entries.map { it.toString() + suffix }, entries.indexOf(chosen).coerceAtLeast(0), { index ->
                        val n = entries[index]
                        val candidates = allowed.filter { d -> when (column) {
                            0 -> d.year == n
                            1 -> d.year == date.year && d.month == n
                            else -> d.year == date.year && d.month == date.month && d.day == n
                        } }
                        selected = candidates.minByOrNull { d -> kotlin.math.abs(d.month - date.month) * 31 + kotlin.math.abs(d.day - date.day) }
                    }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun TimeWheelSheet(
    visible: Boolean, value: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit,
    constraints: TimePickerConstraints = TimePickerConstraints.Default, format: String = "HH:mm",
) {
    val strings = I18n.strings.dateTime
    val allowed = remember(constraints) { constraints.times() }
    var selected by remember(visible, value, constraints) {
        mutableStateOf(allowed.firstOrNull { pickerFormattedTime(it, constraints.precision, format) == value } ?: allowed.firstOrNull())
    }
    PickerSheet(visible, strings.selectTimeTitle, onDismiss, {
        selected?.let {
            val text = pickerFormattedTime(it, constraints.precision, format)
            onConfirm(text)
        }
    }, onDismiss, confirmEnabled = selected != null) {
        if (selected == null) {
            Text(text = I18n.strings.common.noData, style = Theme.typography.bodyMedium,
                color = Theme.colors.mutedForeground, modifier = Modifier.padding(Spacing.lg))
        } else {
            val time = selected!!
            val hours = allowed.map { it.hour }.distinct()
            val minutes = allowed.filter { it.hour == time.hour }.map { it.minute }.distinct()
            val seconds = allowed.filter { it.hour == time.hour && it.minute == time.minute }.map { it.second }.distinct()
            PickerWheels(constraints.precision.ordinal + 1) { column ->
                val entries = when (column) { 0 -> hours; 1 -> minutes; else -> seconds }
                val chosen = when (column) { 0 -> time.hour; 1 -> time.minute; else -> time.second }
                val suffix = when (column) { 0 -> strings.hourSuffix; 1 -> strings.minuteSuffix; else -> strings.secondSuffix }
                key(column, entries) {
                    WheelPickerColumn(entries.map { it.toString().padStart(2, '0') + suffix }, entries.indexOf(chosen).coerceAtLeast(0), { index ->
                        val n = entries[index]
                        selected = allowed.filter { t -> when (column) {
                            0 -> t.hour == n
                            1 -> t.hour == time.hour && t.minute == n
                            else -> t.hour == time.hour && t.minute == time.minute && t.second == n
                        } }.minByOrNull { t -> kotlin.math.abs(t.minute - time.minute) * 60 + kotlin.math.abs(t.second - time.second) }
                    }, Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * DateTimePicker - combined date and time picker
 */
@Composable
fun DateTimePickerInput(
    dateValue: String,
    timeValue: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    label: String? = null,
    dateConstraints: DatePickerConstraints = DatePickerConstraints.Default,
    timeConstraints: TimePickerConstraints = TimePickerConstraints.Default,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = if (enabled) Theme.colors.foreground else Theme.colors.mutedForeground,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            DatePickerInput(
                value = dateValue,
                onValueChange = onDateChange,
                constraints = dateConstraints,
                enabled = enabled,
                variant = variant,
                modifier = Modifier.weight(1f)
            )

            TimePickerInput(
                value = timeValue,
                onValueChange = onTimeChange,
                constraints = timeConstraints,
                enabled = enabled,
                variant = variant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
