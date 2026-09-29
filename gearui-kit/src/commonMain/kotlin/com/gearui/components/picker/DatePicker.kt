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
    format: String = "YYYY-MM-DD"
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
        visible = showPicker,
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
    format: String = "HH:mm"
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
        visible = showPicker,
        value = value,
        onConfirm = { onValueChange(it); showPicker = false },
        onDismiss = { showPicker = false },
    )
}

/** Years the date wheel offers. */
private val YearRange = 1900..2100

/**
 * Year, month and day wheels in the shared picker sheet. The day column follows the
 * month and year (28–31 days); the selection is local to one opening, so Cancel leaves
 * the value untouched.
 */
@Composable
private fun DateWheelSheet(
    visible: Boolean,
    value: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = I18n.strings.dateTime
    val initial = remember(value, visible) {
        val parts = value.split("-").map { it.toIntOrNull() }
        val today = CalendarDate.today()
        Triple(
            (parts.getOrNull(0) ?: today.year).coerceIn(YearRange),
            (parts.getOrNull(1) ?: today.month).coerceIn(1, 12),
            parts.getOrNull(2) ?: today.day,
        )
    }
    var year by remember(initial) { mutableStateOf(initial.first) }
    var month by remember(initial) { mutableStateOf(initial.second) }
    var day by remember(initial) { mutableStateOf(initial.third) }
    val days = CalendarMath.daysInMonth(year, month)
    val years = remember { YearRange.toList() }

    PickerSheet(
        visible = visible,
        title = strings.selectDateTitle,
        onCancel = onDismiss,
        onConfirm = {
            onConfirm("${year.pad(4)}-${month.pad(2)}-${day.coerceIn(1, days).pad(2)}")
        },
        onDismiss = onDismiss,
    ) {
        PickerWheels(columnCount = 3) { column ->
            when (column) {
                0 -> WheelPickerColumn(
                    items = remember(strings) { years.map { "$it${strings.yearSuffix}" } },
                    initialIndex = years.indexOf(year),
                    onSelectedChange = { year = years[it] },
                    modifier = Modifier.weight(1f),
                )
                1 -> WheelPickerColumn(
                    items = remember(strings) { (1..12).map { "$it${strings.monthSuffix}" } },
                    initialIndex = month - 1,
                    onSelectedChange = { month = it + 1 },
                    modifier = Modifier.weight(1f),
                )
                // Rebuilt when the month length changes, keeping the day in range.
                else -> key(days) {
                    WheelPickerColumn(
                        items = (1..days).map { "$it${strings.daySuffix}" },
                        initialIndex = day.coerceIn(1, days) - 1,
                        onSelectedChange = { day = it + 1 },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/** Hour and minute wheels in the shared picker sheet. */
@Composable
private fun TimeWheelSheet(
    visible: Boolean,
    value: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = I18n.strings.dateTime
    val initial = remember(value, visible) {
        val parts = value.split(":").map { it.toIntOrNull() }
        (parts.getOrNull(0) ?: 0).coerceIn(0, 23) to (parts.getOrNull(1) ?: 0).coerceIn(0, 59)
    }
    var hour by remember(initial) { mutableStateOf(initial.first) }
    var minute by remember(initial) { mutableStateOf(initial.second) }

    PickerSheet(
        visible = visible,
        title = strings.selectTimeTitle,
        onCancel = onDismiss,
        onConfirm = { onConfirm("${hour.pad(2)}:${minute.pad(2)}") },
        onDismiss = onDismiss,
    ) {
        PickerWheels(columnCount = 2) { column ->
            if (column == 0) {
                WheelPickerColumn(
                    items = remember(strings) { (0..23).map { "${it.pad(2)}${strings.hourSuffix}" } },
                    initialIndex = hour,
                    onSelectedChange = { hour = it },
                    modifier = Modifier.weight(1f),
                )
            } else {
                WheelPickerColumn(
                    items = remember(strings) { (0..59).map { "${it.pad(2)}${strings.minuteSuffix}" } },
                    initialIndex = minute,
                    onSelectedChange = { minute = it },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun Int.pad(width: Int): String = toString().padStart(width, '0')

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
    label: String? = null
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
                enabled = enabled,
                variant = variant,
                modifier = Modifier.weight(1f)
            )

            TimePickerInput(
                value = timeValue,
                onValueChange = onTimeChange,
                enabled = enabled,
                variant = variant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
