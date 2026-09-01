package com.gearui.components.calendar

import java.util.GregorianCalendar

internal actual fun currentCalendarDate(): CalendarDate {
    // Calendar.getInstance() can return a locale-specific calendar
    // (e.g. Buddhist for th_TH); the kit's grid math is strictly Gregorian.
    val now = GregorianCalendar()
    return CalendarDate(
        year = now.get(GregorianCalendar.YEAR),
        month = now.get(GregorianCalendar.MONTH) + 1,
        day = now.get(GregorianCalendar.DAY_OF_MONTH)
    )
}
