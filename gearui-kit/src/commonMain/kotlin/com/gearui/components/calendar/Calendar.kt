package com.gearui.components.calendar

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Date data class
 */
data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<CalendarDate> {

    override fun compareTo(other: CalendarDate): Int {
        return when {
            year != other.year -> year - other.year
            month != other.month -> month - other.month
            else -> day - other.day
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CalendarDate) return false
        return year == other.year && month == other.month && day == other.day
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        return result
    }

    companion object {
        fun today(): CalendarDate {
            return CalendarDate(2024, 1, 15)
        }
    }
}

/**
 * Calendar selection mode
 */
enum class CalendarType {
    Single,
    Multiple,
    Range
}

/**
 * Date selection state
 */
enum class DateSelectType {
    Selected,
    Disabled,
    Start,
    Centre,
    End,
    Empty
}

/**
 * Calendar - calendar component
 */
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    type: CalendarType = CalendarType.Single,
    selectedDate: CalendarDate? = null,
    onDateSelect: ((CalendarDate) -> Unit)? = null,
    selectedDates: List<CalendarDate> = emptyList(),
    onDatesChange: ((List<CalendarDate>) -> Unit)? = null,
    rangeStart: CalendarDate? = null,
    rangeEnd: CalendarDate? = null,
    onRangeSelect: ((CalendarDate?, CalendarDate?) -> Unit)? = null,
    currentMonth: CalendarDate = CalendarDate.today(),
    onMonthChange: ((CalendarDate) -> Unit)? = null,
    minDate: CalendarDate? = null,
    maxDate: CalendarDate? = null,
    firstDayOfWeek: Int = 0,
    title: String? = null,
    showTitle: Boolean = true,
    cellHeight: Dp = 44.dp
) {
    val colors = Theme.colors

    var displayMonth by remember { mutableStateOf(currentMonth) }
    var rangeSelectionState by remember { mutableStateOf(0) }

    LaunchedEffect(currentMonth) {
        displayMonth = currentMonth
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.lg)
            .border(BorderWidth.thin, colors.border, Theme.shapes.lg)
            .background(colors.surface)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        if (showTitle && title != null) {
            Text(
                text = title,
                style = Typography.TitleMedium,
                color = colors.foreground
            )
        }

        CalendarHeader(
            year = displayMonth.year,
            month = displayMonth.month,
            onPreviousMonth = {
                val newMonth = CalendarMath.previousMonth(displayMonth)
                displayMonth = newMonth
                onMonthChange?.invoke(newMonth)
            },
            onNextMonth = {
                val newMonth = CalendarMath.nextMonth(displayMonth)
                displayMonth = newMonth
                onMonthChange?.invoke(newMonth)
            }
        )

        CalendarWeekHeader(firstDayOfWeek = firstDayOfWeek)

        // key forces recomposition when the selection changes
        key(selectedDate, selectedDates.hashCode(), rangeStart, rangeEnd) {
            CalendarGrid(
                year = displayMonth.year,
                month = displayMonth.month,
                type = type,
                selectedDate = selectedDate,
                selectedDates = selectedDates,
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                minDate = minDate,
                maxDate = maxDate,
                firstDayOfWeek = firstDayOfWeek,
                cellHeight = cellHeight,
                onCellClick = { date ->
                    when (type) {
                        CalendarType.Single -> {
                            onDateSelect?.invoke(date)
                        }
                        CalendarType.Multiple -> {
                            val newList = if (selectedDates.contains(date)) {
                                selectedDates.filter { it != date }
                            } else {
                                selectedDates + date
                            }
                            onDatesChange?.invoke(newList)
                        }
                        CalendarType.Range -> {
                            if (rangeSelectionState == 0) {
                                onRangeSelect?.invoke(date, null)
                                rangeSelectionState = 1
                            } else {
                                if (rangeStart != null && date >= rangeStart) {
                                    onRangeSelect?.invoke(rangeStart, date)
                                } else {
                                    onRangeSelect?.invoke(date, null)
                                }
                                rangeSelectionState = 0
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.muted)
                .clickable { onPreviousMonth() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = Icons.chevron_left,
                size = IconSizes.Default.lg,
                tint = colors.foreground
            )
        }

        Text(
            text = I18n.strings.dateTime.calendarYearMonthFormat
                .formatArgs("year" to year, "month" to month),
            style = Typography.TitleMedium,
            color = colors.foreground
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.muted)
                .clickable { onNextMonth() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = Icons.chevron_right,
                size = IconSizes.Default.lg,
                tint = colors.foreground
            )
        }
    }
}

@Composable
private fun CalendarWeekHeader(
    firstDayOfWeek: Int = 0
) {
    val colors = Theme.colors
    val weekdays = I18n.strings.dateTime.weekdaysShort

    val orderedWeekdays = if (firstDayOfWeek == 0) {
        weekdays
    } else {
        weekdays.drop(firstDayOfWeek) + weekdays.take(firstDayOfWeek)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        orderedWeekdays.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    type: CalendarType,
    selectedDate: CalendarDate?,
    selectedDates: List<CalendarDate>,
    rangeStart: CalendarDate?,
    rangeEnd: CalendarDate?,
    minDate: CalendarDate?,
    maxDate: CalendarDate?,
    firstDayOfWeek: Int,
    cellHeight: Dp,
    onCellClick: (CalendarDate) -> Unit
) {
    val colors = Theme.colors
    val today = CalendarDate.today()

    val daysInMonth = CalendarMath.daysInMonth(year, month)
    val adjustedFirstDay = CalendarMath.leadingBlanks(year, month, firstDayOfWeek)

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        var dayCounter = 1 - adjustedFirstDay

        repeat(6) { week ->
            if (dayCounter <= daysInMonth) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(7) { _ ->
                        if (dayCounter < 1 || dayCounter > daysInMonth) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(cellHeight)
                            )
                        } else {
                            val currentDay = dayCounter
                            val currentDate = CalendarDate(year, month, currentDay)

                            val selectType = CalendarMath.selectType(
                                date = currentDate,
                                type = type,
                                selectedDate = selectedDate,
                                selectedDates = selectedDates,
                                rangeStart = rangeStart,
                                rangeEnd = rangeEnd,
                                minDate = minDate,
                                maxDate = maxDate
                            )

                            val isToday = currentDate == today
                            val isDisabled = selectType == DateSelectType.Disabled

                            // key makes sure each cell recomposes correctly when the state changes
                            key(currentDate, selectType) {
                                CalendarCell(
                                    day = currentDay,
                                    selectType = selectType,
                                    isToday = isToday,
                                    cellHeight = cellHeight,
                                    modifier = Modifier.weight(1f),
                                    onClick = if (isDisabled) null else {{ onCellClick(currentDate) }}
                                )
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    day: Int,
    selectType: DateSelectType,
    isToday: Boolean,
    cellHeight: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    val colors = Theme.colors

    // Style derived from the selection state
    val backgroundColor: Color
    val textColor: Color

    when (selectType) {
        DateSelectType.Selected -> {
            backgroundColor = colors.primary
            textColor = colors.primaryForeground
        }
        DateSelectType.Start -> {
            backgroundColor = colors.primary
            textColor = colors.primaryForeground
        }
        DateSelectType.End -> {
            backgroundColor = colors.primary
            textColor = colors.primaryForeground
        }
        DateSelectType.Centre -> {
            backgroundColor = colors.primary.copy(alpha = 0.1f)
            textColor = colors.primary
        }
        DateSelectType.Disabled -> {
            backgroundColor = Color.Transparent
            textColor = colors.mutedForeground
        }
        DateSelectType.Empty -> {
            backgroundColor = Color.Transparent
            textColor = if (isToday) colors.primary else colors.foreground
        }
    }

    // Shape
    val shape = when (selectType) {
        DateSelectType.Selected -> CircleShape
        DateSelectType.Start -> RoundedCornerShape(topStart = cellHeight / 2, bottomStart = cellHeight / 2)
        DateSelectType.End -> RoundedCornerShape(topEnd = cellHeight / 2, bottomEnd = cellHeight / 2)
        else -> Theme.shapes.none
    }

    // Whether a background is needed
    val needBackground = selectType == DateSelectType.Selected ||
            selectType == DateSelectType.Start ||
            selectType == DateSelectType.End ||
            selectType == DateSelectType.Centre

    Box(
        modifier = modifier
            .height(cellHeight)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(shape)
            .background(if (needBackground) backgroundColor else Color.Transparent)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = Typography.BodyMedium,
            color = textColor
        )
    }
}
