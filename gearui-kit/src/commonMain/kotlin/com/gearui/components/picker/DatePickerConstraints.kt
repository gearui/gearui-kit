package com.gearui.components.picker

import com.gearui.components.calendar.CalendarDate
import com.gearui.components.calendar.CalendarMath

enum class DatePickerPrecision { YEAR, MONTH, DAY }
enum class TimePickerPrecision { HOUR, MINUTE, SECOND }

/** Inclusive Gregorian bounds. The predicate applies to complete dates, before columns are derived. */
class DatePickerConstraints(
    val min: CalendarDate = CalendarDate(1900, 1, 1),
    val max: CalendarDate = CalendarDate(2100, 12, 31),
    val precision: DatePickerPrecision = DatePickerPrecision.DAY,
    val filter: (CalendarDate) -> Boolean = { true },
) {
    init {
        require(validPickerDate(min) && validPickerDate(max) && min <= max)
    }

    internal fun dates(): List<CalendarDate> = buildList {
        for (year in min.year..max.year) for (month in 1..12) {
            for (day in 1..CalendarMath.daysInMonth(year, month)) {
                val date = CalendarDate(year, month, day)
                if (date >= min && date <= max && filter(date)) add(date)
            }
        }
    }

    companion object { val Default = DatePickerConstraints() }
}

internal fun validPickerDate(date: CalendarDate): Boolean = date.year in 1..9999 && date.month in 1..12 &&
    date.day in 1..CalendarMath.daysInMonth(date.year, date.month)

/** A civil time without a timezone; no conversion of the app's time or date is implied. */
data class PickerTime(val hour: Int, val minute: Int = 0, val second: Int = 0) : Comparable<PickerTime> {
    init { require(hour in 0..23 && minute in 0..59 && second in 0..59) }
    override fun compareTo(other: PickerTime): Int = seconds.compareTo(other.seconds)
    internal val seconds: Int get() = hour * 3600 + minute * 60 + second
}

class TimePickerConstraints(
    val min: PickerTime = PickerTime(0),
    val max: PickerTime = PickerTime(23, 59, 59),
    val precision: TimePickerPrecision = TimePickerPrecision.MINUTE,
    val minuteStep: Int = 1,
    val secondStep: Int = 1,
    val filter: (PickerTime) -> Boolean = { true },
) {
    init { require(min <= max && minuteStep in 1..59 && secondStep in 1..59) }
    internal fun times(): List<PickerTime> = buildList {
        val minutes = if (precision == TimePickerPrecision.HOUR) listOf(0) else (0..59 step minuteStep).toList()
        val seconds = if (precision == TimePickerPrecision.SECOND) (0..59 step secondStep).toList() else listOf(0)
        for (hour in 0..23) for (minute in minutes) for (second in seconds) {
            val time = PickerTime(hour, minute, second)
            val bucketEnd = time.seconds + when (precision) {
                TimePickerPrecision.HOUR -> 3599
                TimePickerPrecision.MINUTE -> 59
                TimePickerPrecision.SECOND -> 0
            }
            if (bucketEnd >= min.seconds && time.seconds <= max.seconds && filter(time)) add(time)
        }
    }
    companion object { val Default = TimePickerConstraints() }
}

internal fun pickerDateText(date: CalendarDate, precision: DatePickerPrecision, format: String): String {
    val pattern = if (format == "YYYY-MM-DD") when (precision) {
        DatePickerPrecision.YEAR -> "YYYY"
        DatePickerPrecision.MONTH -> "YYYY-MM"
        DatePickerPrecision.DAY -> format
    } else format
    return pattern.replace("YYYY", date.year.toString().padStart(4, '0'))
        .replace("MM", date.month.toString().padStart(2, '0')).replace("DD", date.day.toString().padStart(2, '0'))
}

internal fun pickerTimeText(time: PickerTime, precision: TimePickerPrecision): String =
    time.hour.toString().padStart(2, '0') +
        (if (precision != TimePickerPrecision.HOUR) ":" + time.minute.toString().padStart(2, '0') else "") +
        (if (precision == TimePickerPrecision.SECOND) ":" + time.second.toString().padStart(2, '0') else "")

internal fun pickerFormattedTime(time: PickerTime, precision: TimePickerPrecision, format: String): String =
    if (format == "HH:mm") pickerTimeText(time, precision) else format
        .replace("HH", time.hour.toString().padStart(2, '0'))
        .replace("mm", time.minute.toString().padStart(2, '0'))
        .replace("ss", time.second.toString().padStart(2, '0'))
