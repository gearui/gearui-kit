package com.gearui.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.Icons
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.shadowed
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.style.TextOverflow

/**
 * DateRangePickerInput — a field that opens the calendar and returns a date range, for
 * a filter ("last week"), a booking, a report period.
 *
 * Calendar already selects a range and CalendarPopup already presents it; what this adds
 * is the part every caller was writing by hand: a field trigger that shows the chosen
 * range, a placeholder for each end, an error line and a clear button.
 *
 * The range is the caller's state, so it survives navigation and can be preset:
 *
 * ```kotlin
 * var from by remember { mutableStateOf<CalendarDate?>(null) }
 * var to by remember { mutableStateOf<CalendarDate?>(null) }
 * DateRangePickerInput(from, to, { a, b -> from = a; to = b }, label = "Reporting period")
 * ```
 *
 * [format] controls how a date is printed; the default is ISO `YYYY-MM-DD`, which sorts
 * and parses everywhere. Pass your own for a localised format.
 */
@Composable
fun DateRangePickerInput(
    start: CalendarDate?,
    end: CalendarDate?,
    onRangeChange: (CalendarDate?, CalendarDate?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    required: Boolean = false,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    minDate: CalendarDate? = null,
    maxDate: CalendarDate? = null,
    clearable: Boolean = true,
    startPlaceholder: String = I18n.strings.dateTime.rangeStartPlaceholder,
    endPlaceholder: String = I18n.strings.dateTime.rangeEndPlaceholder,
    title: String = I18n.strings.dateTime.selectRangeTitle,
    format: (CalendarDate) -> String = ::formatCalendarDate,
) {
    val colors = Theme.colors
    val inputColors = LocalInputColors.current
    var open by remember { mutableStateOf(false) }
    val hasRange = start != null || end != null

    Column(modifier = modifier) {
        if (label != null) {
            FieldLabel(
                text = label,
                required = required,
                invalid = error != null,
                modifier = Modifier.padding(bottom = FieldDefaults.labelGap),
            )
        }

        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .then(fieldTriggerModifier(enabled, error, variant) { open = true })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap),
            ) {
                Text(
                    text = start?.let(format) ?: startPlaceholder,
                    style = Theme.typography.bodyMedium,
                    color = if (start != null) inputColors.foreground else inputColors.placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(name = Icons.arrow_right, size = FieldDefaults.trailingIconSize, tint = colors.mutedForeground)
                Text(
                    text = end?.let(format) ?: endPlaceholder,
                    style = Theme.typography.bodyMedium,
                    color = if (end != null) inputColors.foreground else inputColors.placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (clearable && hasRange && enabled) {
                    com.gearui.components.closebutton.CloseButton(
                        onClick = { onRangeChange(null, null) },
                        size = FieldDefaults.trailingIconSize + FieldDefaults.labelGap,
                        iconSize = FieldDefaults.trailingIconSize,
                    )
                } else {
                    Icon(
                        name = Icons.calendar_blank,
                        size = FieldDefaults.trailingIconSize,
                        tint = colors.mutedForeground,
                    )
                }
            }
        }

        FieldErrorText(error)
    }

    CalendarPopup(
        visible = open,
        onClose = { open = false },
        title = title,
        type = CalendarType.Range,
        initialRangeStart = start,
        initialRangeEnd = end,
        onConfirmRange = { from, to ->
            onRangeChange(from, to)
            open = false
        },
        minDate = minDate,
        maxDate = maxDate,
    )
}

/** ISO `YYYY-MM-DD`: the default because it sorts, parses and reads the same everywhere. */
fun formatCalendarDate(date: CalendarDate): String =
    "${date.year.toString().padStart(4, '0')}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}"
