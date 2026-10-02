package com.gearui.components.picker

import com.gearui.components.icon.*
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

                Icon(Icons.calendarBlank,
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

                Icon(Icons.clock,
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

/**
 * The date wheels. Each column lists only what the constraints admit for the columns to
 * its left, so no month can lead to an empty day column; turning a column keeps the
 * other parts as close as possible (29 February moves to the 28th in a common year).
 * Nothing is computed while the sheet is closed.
 */
@Composable
internal fun DateWheelSheet(
    visible: Boolean, value: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit,
    constraints: DatePickerConstraints = DatePickerConstraints.Default, format: String = "YYYY-MM-DD",
) {
    val strings = I18n.strings.dateTime
    val columns = remember(constraints) { DateColumns(constraints) }
    var selected by remember(visible, value, constraints, format) {
        mutableStateOf(if (visible) initialPickerDate(columns, value, format) else null)
    }
    PickerSheet(visible, strings.selectDateTitle, onDismiss, {
        selected?.let { onConfirm(pickerDateText(it, constraints.precision, format)) }
    }, onDismiss, confirmEnabled = selected != null) {
        val date = selected
        if (date == null) {
            Text(text = I18n.strings.common.noData, style = Theme.typography.bodyMedium,
                color = Theme.colors.mutedForeground, modifier = Modifier.padding(Spacing.lg))
        } else {
            PickerWheels(constraints.precision.ordinal + 1) { column ->
                val entries = when (column) {
                    0 -> columns.years
                    1 -> columns.months(date.year)
                    else -> columns.days(date.year, date.month)
                }
                val chosen = when (column) { 0 -> date.year; 1 -> date.month; else -> date.day }
                val suffix = when (column) { 0 -> strings.yearSuffix; 1 -> strings.monthSuffix; else -> strings.daySuffix }
                key(column, entries) {
                    WheelPickerColumn(entries.map { it.toString() + suffix }, entries.indexOf(chosen).coerceAtLeast(0), { index ->
                        val n = entries[index]
                        selected = when (column) {
                            0 -> columns.nearest(n, date.month, date.day)
                            1 -> columns.nearest(date.year, n, date.day)
                            else -> CalendarDate(date.year, date.month, n)
                        }
                    }, Modifier.weight(1f))
                }
            }
        }
    }
}

/** The stored value if it can be read and picked, else the nearest admissible date, else today's. */
internal fun initialPickerDate(columns: DateColumns, value: String, format: String): CalendarDate? {
    parsePickerDate(value, format)?.let { (y, m, d) -> return columns.nearest(y, m, d) }
    val today = CalendarDate.today()
    return columns.nearest(today.year, today.month, today.day)
}

/** The time wheels, built like [DateWheelSheet]. */
@Composable
internal fun TimeWheelSheet(
    visible: Boolean, value: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit,
    constraints: TimePickerConstraints = TimePickerConstraints.Default, format: String = "HH:mm",
) {
    val strings = I18n.strings.dateTime
    val columns = remember(constraints) { TimeColumns(constraints) }
    var selected by remember(visible, value, constraints, format) {
        mutableStateOf(if (visible) initialPickerTime(columns, value, format) else null)
    }
    PickerSheet(visible, strings.selectTimeTitle, onDismiss, {
        selected?.let { onConfirm(pickerFormattedTime(it, constraints.precision, format)) }
    }, onDismiss, confirmEnabled = selected != null) {
        val time = selected
        if (time == null) {
            Text(text = I18n.strings.common.noData, style = Theme.typography.bodyMedium,
                color = Theme.colors.mutedForeground, modifier = Modifier.padding(Spacing.lg))
        } else {
            PickerWheels(constraints.precision.ordinal + 1) { column ->
                val entries = when (column) {
                    0 -> columns.hours
                    1 -> columns.minutes(time.hour)
                    else -> columns.seconds(time.hour, time.minute)
                }
                val chosen = when (column) { 0 -> time.hour; 1 -> time.minute; else -> time.second }
                val suffix = when (column) { 0 -> strings.hourSuffix; 1 -> strings.minuteSuffix; else -> strings.secondSuffix }
                key(column, entries) {
                    WheelPickerColumn(entries.map { it.toString().padStart(2, '0') + suffix }, entries.indexOf(chosen).coerceAtLeast(0), { index ->
                        val n = entries[index]
                        selected = when (column) {
                            0 -> columns.nearest(n, time.minute, time.second)
                            1 -> columns.nearest(time.hour, n, time.second)
                            else -> PickerTime(time.hour, time.minute, n)
                        }
                    }, Modifier.weight(1f))
                }
            }
        }
    }
}

/** The stored value if it can be read, moved to the nearest slot; else the first slot. */
internal fun initialPickerTime(columns: TimeColumns, value: String, format: String): PickerTime? {
    parsePickerTime(value, format)?.let { (h, m, s) -> return columns.nearest(h, m, s) }
    return columns.nearest(0, 0, 0)
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
