package com.gearui.components.calendar

import java.util.Calendar

internal actual fun currentCalendarDate(): CalendarDate {
    // java.util.Calendar keeps minSdk 21 working; java.time needs API 26.
    val now = Calendar.getInstance()
    return CalendarDate(
        year = now.get(Calendar.YEAR),
        month = now.get(Calendar.MONTH) + 1,
        day = now.get(Calendar.DAY_OF_MONTH)
    )
}
