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

/**
 * 日期数据类
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
 * 日历选择类型
 */
enum class CalendarType {
    Single,
    Multiple,
    Range
}

/**
 * 日期选中状态
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
 * Calendar - 日历组件
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
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .background(colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showTitle && title != null) {
            Text(
                text = title,
                style = Typography.TitleMedium,
                color = colors.textPrimary
            )
        }

        CalendarHeader(
            year = displayMonth.year,
            month = displayMonth.month,
            onPreviousMonth = {
                val newMonth = if (displayMonth.month == 1) {
                    CalendarDate(displayMonth.year - 1, 12, 1)
                } else {
                    CalendarDate(displayMonth.year, displayMonth.month - 1, 1)
                }
                displayMonth = newMonth
                onMonthChange?.invoke(newMonth)
            },
            onNextMonth = {
                val newMonth = if (displayMonth.month == 12) {
                    CalendarDate(displayMonth.year + 1, 1, 1)
                } else {
                    CalendarDate(displayMonth.year, displayMonth.month + 1, 1)
                }
                displayMonth = newMonth
                onMonthChange?.invoke(newMonth)
            }
        )

        CalendarWeekHeader(firstDayOfWeek = firstDayOfWeek)

        // 使用 key 强制在选中状态变化时重组
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
                .background(colors.surfaceVariant)
                .clickable { onPreviousMonth() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = Icons.chevron_left,
                size = 18.dp,
                tint = colors.textPrimary
            )
        }

        Text(
            text = "${year}年${month}月",
            style = Typography.TitleMedium,
            color = colors.textPrimary
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant)
                .clickable { onNextMonth() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = Icons.chevron_right,
                size = 18.dp,
                tint = colors.textPrimary
            )
        }
    }
}

@Composable
private fun CalendarWeekHeader(
    firstDayOfWeek: Int = 0
) {
    val colors = Theme.colors
    val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")

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
                    color = colors.textSecondary
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

    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfMonth = getFirstDayOfWeek(year, month)
    val adjustedFirstDay = (firstDayOfMonth - firstDayOfWeek + 7) % 7

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

                            val selectType = getDateSelectType(
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

                            // 使用 key 确保每个单元格在状态变化时正确重组
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

    // 根据选中状态计算样式
    val backgroundColor: Color
    val textColor: Color

    when (selectType) {
        DateSelectType.Selected -> {
            backgroundColor = colors.primary
            textColor = colors.textAnti
        }
        DateSelectType.Start -> {
            backgroundColor = colors.primary
            textColor = colors.textAnti
        }
        DateSelectType.End -> {
            backgroundColor = colors.primary
            textColor = colors.textAnti
        }
        DateSelectType.Centre -> {
            backgroundColor = colors.primary.copy(alpha = 0.1f)
            textColor = colors.primary
        }
        DateSelectType.Disabled -> {
            backgroundColor = Color.Transparent
            textColor = colors.textDisabled
        }
        DateSelectType.Empty -> {
            backgroundColor = Color.Transparent
            textColor = if (isToday) colors.primary else colors.textPrimary
        }
    }

    // 计算形状
    val shape = when (selectType) {
        DateSelectType.Selected -> CircleShape
        DateSelectType.Start -> RoundedCornerShape(topStart = cellHeight / 2, bottomStart = cellHeight / 2)
        DateSelectType.End -> RoundedCornerShape(topEnd = cellHeight / 2, bottomEnd = cellHeight / 2)
        else -> RoundedCornerShape(0.dp)
    }

    // 是否需要背景
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

private fun getDateSelectType(
    date: CalendarDate,
    type: CalendarType,
    selectedDate: CalendarDate?,
    selectedDates: List<CalendarDate>,
    rangeStart: CalendarDate?,
    rangeEnd: CalendarDate?,
    minDate: CalendarDate?,
    maxDate: CalendarDate?
): DateSelectType {
    if (minDate != null && date < minDate) return DateSelectType.Disabled
    if (maxDate != null && date > maxDate) return DateSelectType.Disabled

    return when (type) {
        CalendarType.Single -> {
            if (selectedDate != null && date == selectedDate) {
                DateSelectType.Selected
            } else {
                DateSelectType.Empty
            }
        }
        CalendarType.Multiple -> {
            if (selectedDates.contains(date)) {
                DateSelectType.Selected
            } else {
                DateSelectType.Empty
            }
        }
        CalendarType.Range -> {
            when {
                rangeStart != null && rangeEnd != null -> {
                    when {
                        date == rangeStart -> DateSelectType.Start
                        date == rangeEnd -> DateSelectType.End
                        date > rangeStart && date < rangeEnd -> DateSelectType.Centre
                        else -> DateSelectType.Empty
                    }
                }
                rangeStart != null && date == rangeStart -> DateSelectType.Start
                else -> DateSelectType.Empty
            }
        }
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

private fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val m = if (month < 3) month + 12 else month
    val y = if (month < 3) year - 1 else year
    val k = y % 100
    val j = y / 100
    val h = (1 + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 - 2 * j) % 7
    return (h + 6) % 7
}
