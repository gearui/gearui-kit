package com.gearui.components.calendar

import kotlin.js.Date

internal actual fun currentCalendarDate(): CalendarDate {
    val now = Date()
    return CalendarDate(
        year = now.getFullYear(),
        month = now.getMonth() + 1,
        day = now.getDate()
    )
}
