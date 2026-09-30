package com.gearui.components.picker

import com.gearui.components.calendar.CalendarDate
import com.gearui.components.calendar.CalendarMath
import kotlin.math.abs

enum class DatePickerPrecision { YEAR, MONTH, DAY }
enum class TimePickerPrecision { HOUR, MINUTE, SECOND }

/**
 * Inclusive Gregorian bounds, the unit a date is picked to, and a predicate on complete
 * dates (weekends only, no holidays, …).
 *
 * Constraints compare by value, so building them inline in a composable does not reset
 * a picker the user is turning. [filter] compares by identity: give a function reference
 * or a lambda the compose compiler can remember (one that captures only stable values);
 * a new filter rebuilds the wheels.
 *
 * With [precision] above DAY, a year or month is offered when any of its dates passes.
 */
data class DatePickerConstraints(
    val min: CalendarDate = CalendarDate(1900, 1, 1),
    val max: CalendarDate = CalendarDate(2100, 12, 31),
    val precision: DatePickerPrecision = DatePickerPrecision.DAY,
    val filter: (CalendarDate) -> Boolean = AllDates,
) {
    init {
        require(validPickerDate(min) && validPickerDate(max) && min <= max)
    }

    /** Whether [date] itself may be picked. */
    fun allows(date: CalendarDate): Boolean = validPickerDate(date) && date >= min && date <= max && filter(date)

    companion object {
        private val AllDates: (CalendarDate) -> Boolean = { true }
        val Default = DatePickerConstraints()
    }
}

internal fun validPickerDate(date: CalendarDate): Boolean = date.year in 1..9999 && date.month in 1..12 &&
    date.day in 1..CalendarMath.daysInMonth(date.year, date.month)

/**
 * The wheels of a date picker, worked out column by column and only as they are shown:
 * the year column stops at the first admissible day of each year, and months and days
 * are computed for the year and month in view. The default 1900–2100 range costs a few
 * hundred checks, not the 73,000 of listing every date. Results are cached.
 */
internal class DateColumns(private val constraints: DatePickerConstraints) {
    private val monthCache = HashMap<Int, List<Int>>()
    private val dayCache = HashMap<Int, List<Int>>()

    val years: List<Int> by lazy {
        (constraints.min.year..constraints.max.year).filter { year -> monthRange(year).any { firstDay(year, it) != null } }
    }

    fun months(year: Int): List<Int> = monthCache.getOrPut(year) {
        monthRange(year).filter { month -> firstDay(year, month) != null }
    }

    private fun monthRange(year: Int): IntRange {
        val first = if (year == constraints.min.year) constraints.min.month else 1
        val last = if (year == constraints.max.year) constraints.max.month else 12
        return first..last
    }

    fun days(year: Int, month: Int): List<Int> = dayCache.getOrPut(year * 100 + month) {
        dayRange(year, month).filter { constraints.allows(CalendarDate(year, month, it)) }
    }

    /** The date [date] stands for at the precision, or null when it cannot be picked. */
    fun slotOf(date: CalendarDate): CalendarDate? = when (constraints.precision) {
        DatePickerPrecision.YEAR -> months(date.year).firstOrNull()?.let { m -> CalendarDate(date.year, m, days(date.year, m).first()) }
        DatePickerPrecision.MONTH -> firstDay(date.year, date.month)?.let { CalendarDate(date.year, date.month, it) }
        DatePickerPrecision.DAY -> date.takeIf(constraints::allows)
    }

    /** The admissible date closest to [year]-[month]-[day], column by column; null when none is. */
    fun nearest(year: Int, month: Int, day: Int): CalendarDate? {
        val y = years.closestTo(year) ?: return null
        val m = months(y).closestTo(month) ?: return null
        val d = days(y, m).closestTo(day) ?: return null
        return CalendarDate(y, m, d)
    }

    private fun dayRange(year: Int, month: Int): IntRange {
        val first = if (year == constraints.min.year && month == constraints.min.month) constraints.min.day else 1
        val last = if (year == constraints.max.year && month == constraints.max.month) constraints.max.day
            else CalendarMath.daysInMonth(year, month)
        return first..last
    }

    // Stops at the first admissible day: a month is shown if any of its days passes.
    private fun firstDay(year: Int, month: Int): Int? =
        dayCache[year * 100 + month]?.firstOrNull()
            ?: dayRange(year, month).firstOrNull { constraints.allows(CalendarDate(year, month, it)) }
}

/** A civil time without a timezone; no conversion of the app's time or date is implied. */
data class PickerTime(val hour: Int, val minute: Int = 0, val second: Int = 0) : Comparable<PickerTime> {
    init { require(hour in 0..23 && minute in 0..59 && second in 0..59) }
    override fun compareTo(other: PickerTime): Int = seconds.compareTo(other.seconds)
    internal val seconds: Int get() = hour * 3600 + minute * 60 + second
}

/**
 * Inclusive bounds, precision and steps for a time picker. A slot is offered only when
 * its start lies within the bounds, so a picked time never falls outside them: with
 * minute precision and `min = 09:10:30`, the first slot is 09:11.
 *
 * Compares by value like [DatePickerConstraints]; the same note on [filter] applies.
 */
data class TimePickerConstraints(
    val min: PickerTime = PickerTime(0),
    val max: PickerTime = PickerTime(23, 59, 59),
    val precision: TimePickerPrecision = TimePickerPrecision.MINUTE,
    val minuteStep: Int = 1,
    val secondStep: Int = 1,
    val filter: (PickerTime) -> Boolean = AllTimes,
) {
    init { require(min <= max && minuteStep in 1..59 && secondStep in 1..59) }

    /** Whether [time] is one of the picker's slots and passes the bounds and filter. */
    fun allows(time: PickerTime): Boolean =
        time.minute in minuteSlots && time.second in secondSlots && time >= min && time <= max && filter(time)

    internal val minuteSlots: List<Int> get() =
        if (precision == TimePickerPrecision.HOUR) listOf(0) else (0..59 step minuteStep).toList()
    internal val secondSlots: List<Int> get() =
        if (precision == TimePickerPrecision.SECOND) (0..59 step secondStep).toList() else listOf(0)

    companion object {
        private val AllTimes: (PickerTime) -> Boolean = { true }
        val Default = TimePickerConstraints()
    }
}

/** The wheels of a time picker, column by column like [DateColumns]. */
internal class TimeColumns(private val constraints: TimePickerConstraints) {
    private val minuteSlots = constraints.minuteSlots
    private val secondSlots = constraints.secondSlots
    private val minuteCache = HashMap<Int, List<Int>>()
    private val secondCache = HashMap<Int, List<Int>>()

    val hours: List<Int> by lazy { (0..23).filter { h -> minuteSlots.any { m -> firstSecond(h, m) != null } } }

    fun minutes(hour: Int): List<Int> = minuteCache.getOrPut(hour) { minuteSlots.filter { firstSecond(hour, it) != null } }

    fun seconds(hour: Int, minute: Int): List<Int> = secondCache.getOrPut(hour * 60 + minute) {
        secondSlots.filter { constraints.allows(PickerTime(hour, minute, it)) }
    }

    fun nearest(hour: Int, minute: Int, second: Int): PickerTime? {
        val h = hours.closestTo(hour) ?: return null
        val m = minutes(h).closestTo(minute) ?: return null
        val s = seconds(h, m).closestTo(second) ?: return null
        return PickerTime(h, m, s)
    }

    private fun firstSecond(hour: Int, minute: Int): Int? =
        secondCache[hour * 60 + minute]?.firstOrNull()
            ?: secondSlots.firstOrNull { constraints.allows(PickerTime(hour, minute, it)) }
}

private fun List<Int>.closestTo(target: Int): Int? = minByOrNull { abs(it - target) }

/**
 * Formats a picked date. `YYYY`, `MM` and `DD` are replaced; with the default format the
 * precision drops the parts it does not pick ("2026", "2026-09"). A custom format should
 * contain only the parts its precision picks.
 */
internal fun pickerDateText(date: CalendarDate, precision: DatePickerPrecision, format: String): String {
    val pattern = if (format == "YYYY-MM-DD") when (precision) {
        DatePickerPrecision.YEAR -> "YYYY"
        DatePickerPrecision.MONTH -> "YYYY-MM"
        DatePickerPrecision.DAY -> format
    } else format
    return pattern.replace("YYYY", date.year.toString().padStart(4, '0'))
        .replace("MM", date.month.toString().padStart(2, '0')).replace("DD", date.day.toString().padStart(2, '0'))
}

/**
 * Reads a stored date back leniently: the numbers in [text] are taken in the order the
 * format's `YYYY`, `MM` and `DD` appear, so "2024-3-5", "2024-03-05" and "2024/03/05"
 * all read as 5 March 2024. Missing parts are 1. Null when there is no year.
 */
internal fun parsePickerDate(text: String, format: String): Triple<Int, Int, Int>? {
    val numbers = Regex("\\d+").findAll(text).map { it.value.toInt() }.toList()
    val order = listOf("YYYY", "MM", "DD")
        .map { token -> token to format.indexOf(token) }
        .filter { it.second >= 0 }
        .sortedBy { it.second }
        .map { it.first }
        .ifEmpty { listOf("YYYY", "MM", "DD") }
    val parts = order.zip(numbers).toMap()
    val year = parts["YYYY"] ?: return null
    return Triple(year, parts["MM"] ?: 1, parts["DD"] ?: 1)
}

/** Like [parsePickerDate] for `HH`, `mm` and `ss`: "09:30:00" reads as 09:30 under "HH:mm". */
internal fun parsePickerTime(text: String, format: String): Triple<Int, Int, Int>? {
    val numbers = Regex("\\d+").findAll(text).map { it.value.toInt() }.toList()
    val order = listOf("HH", "mm", "ss")
        .map { token -> token to format.indexOf(token) }
        .filter { it.second >= 0 }
        .sortedBy { it.second }
        .map { it.first }
        .ifEmpty { listOf("HH", "mm", "ss") }
    val parts = order.zip(numbers).toMap()
    val hour = parts["HH"] ?: return null
    return Triple(hour, parts["mm"] ?: 0, parts["ss"] ?: 0)
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
