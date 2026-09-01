package com.gearui.components.calendar

/**
 * Pure calendar math behind the Calendar component.
 *
 * Compose-free so month grids, leap years and selection states can be unit
 * tested directly. Weekday numbering follows the i18n pack: 0 = Sunday.
 */
internal object CalendarMath {

    /** Gregorian leap year rule. */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    /** Number of days in a month (month is 1..12). */
    fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    /** Weekday of the 1st of the month: 0 = Sunday .. 6 = Saturday (Zeller's congruence). */
    fun firstWeekdayOfMonth(year: Int, month: Int): Int {
        val m = if (month < 3) month + 12 else month
        val y = if (month < 3) year - 1 else year
        val k = y % 100
        val j = y / 100
        val h = (1 + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 - 2 * j) % 7
        return (h + 6) % 7
    }

    /** Empty cells before day 1 when the week starts on [firstDayOfWeek]. */
    fun leadingBlanks(year: Int, month: Int, firstDayOfWeek: Int): Int {
        return (firstWeekdayOfMonth(year, month) - firstDayOfWeek + 7) % 7
    }

    /** The first day of the previous month. */
    fun previousMonth(month: CalendarDate): CalendarDate {
        return if (month.month == 1) {
            CalendarDate(month.year - 1, 12, 1)
        } else {
            CalendarDate(month.year, month.month - 1, 1)
        }
    }

    /** The first day of the next month. */
    fun nextMonth(month: CalendarDate): CalendarDate {
        return if (month.month == 12) {
            CalendarDate(month.year + 1, 1, 1)
        } else {
            CalendarDate(month.year, month.month + 1, 1)
        }
    }

    /** Selection state of one cell, honouring min/max bounds. */
    fun selectType(
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
}
