package com.gearui.components.yearpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.picker.DatePickerConstraints
import com.gearui.components.picker.DatePickerInput
import com.gearui.components.picker.DatePickerPrecision
import com.gearui.foundation.field.FieldVariant
import com.tencent.kuikly.compose.ui.Modifier

/** Gregorian year wheel sharing date-picker bounds, field states and sheet behavior. */
@Composable
fun YearPicker(value: Int?, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier,
    min: Int = 1900, max: Int = 2100, enabled: Boolean = true, label: String? = null,
    error: String? = null, variant: FieldVariant = FieldVariant.PRIMARY) {
    val constraints = remember(min, max) { DatePickerConstraints(CalendarDate(min, 1, 1), CalendarDate(max, 12, 31), DatePickerPrecision.YEAR) }
    DatePickerInput(value?.toString().orEmpty(), { it.toIntOrNull()?.let(onValueChange) }, modifier,
        enabled = enabled, label = label, error = error, variant = variant, constraints = constraints)
}
