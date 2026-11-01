package com.gearui.components.calendar

import com.gearui.components.closebutton.CloseButton
import com.gearui.foundation.sheet.SheetOverlay
import com.gearui.foundation.sheet.SheetHeader
import com.gearui.foundation.sheet.SheetContainer
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.overlay.OverlayDefaults
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * CalendarPopup - calendar in a popup layer
 *
 * Presents the calendar picker as a popup
 *
 * Shown as an iOS sheet through the overlay system (the app root provides it).
 *
 * Example:
 * ```kotlin
 * var showCalendar by remember { mutableStateOf(false) }
 * var selectedDate by remember { mutableStateOf<CalendarDate?>(null) }
 *
 * if (showCalendar) {
 *     CalendarPopup(
 *         visible = true,
 *         onDismiss = { showCalendar = false },
 *         onConfirm = { date ->
 *             selectedDate = date
 *             showCalendar = false
 *         }
 *     )
 * }
 * ```
 */
@Composable
fun CalendarPopup(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = I18n.strings.dateTime.selectDateTitle,
    type: CalendarType = CalendarType.Single,
    // Single selection
    initialDate: CalendarDate? = null,
    onConfirm: ((CalendarDate) -> Unit)? = null,
    // Multiple selection
    initialDates: List<CalendarDate> = emptyList(),
    onConfirmMultiple: ((List<CalendarDate>) -> Unit)? = null,
    // Range
    initialRangeStart: CalendarDate? = null,
    initialRangeEnd: CalendarDate? = null,
    onConfirmRange: ((CalendarDate?, CalendarDate?) -> Unit)? = null,
    // Shared configuration
    minDate: CalendarDate? = null,
    maxDate: CalendarDate? = null,
    autoClose: Boolean = true,
    confirmText: String = I18n.strings.common.confirm,
    showConfirmButton: Boolean = true,
    /** Show lunar dates under the days; see [Calendar]. */
    lunar: Boolean = false
) {
    val colors = Theme.colors

    // Internal state, fresh for each opening.
    var selectedDate by remember(initialDate, visible) { mutableStateOf(initialDate) }
    var selectedDates by remember(initialDates, visible) { mutableStateOf(initialDates) }
    var rangeStart by remember(initialRangeStart, visible) { mutableStateOf(initialRangeStart) }
    var rangeEnd by remember(initialRangeEnd, visible) { mutableStateOf(initialRangeEnd) }

    // An iOS sheet like every other picker: it rises over the scrim, closes on Back, the
    // close button, a tap outside or a drag on its title bar.
    SheetOverlay(visible, onDismiss, closeOnClickOutside = autoClose) {
        SheetContainer(
            onDismiss = onDismiss,
            background = colors.surface,
            dragWholeSheet = false,
            modifier = modifier,
            header = { SheetHeader(title = title, trailing = { CloseButton(onClick = onDismiss) }) },
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.lg)) {
                // The calendar itself
                Calendar(
                    type = type,
                    lunar = lunar,
                    selectedDate = selectedDate,
                    onDateSelect = { date ->
                        selectedDate = date
                        if (autoClose && !showConfirmButton) {
                            onConfirm?.invoke(date)
                            onDismiss()
                        }
                    },
                    selectedDates = selectedDates,
                    onDatesChange = { dates ->
                        selectedDates = dates
                    },
                    rangeStart = rangeStart,
                    rangeEnd = rangeEnd,
                    onRangeSelect = { start, end ->
                        rangeStart = start
                        rangeEnd = end
                    },
                    minDate = minDate,
                    maxDate = maxDate,
                    showTitle = false
                )

                // Confirm button
                if (showConfirmButton) {
                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Button(
                        text = confirmText,
                        theme = ButtonTheme.PRIMARY,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            when (type) {
                                CalendarType.Single -> {
                                    selectedDate?.let { onConfirm?.invoke(it) }
                                }
                                CalendarType.Multiple -> {
                                    onConfirmMultiple?.invoke(selectedDates)
                                }
                                CalendarType.Range -> {
                                    onConfirmRange?.invoke(rangeStart, rangeEnd)
                                }
                            }
                            if (autoClose) onDismiss()
                        }
                    )
                }
            }
        }
    }
}
