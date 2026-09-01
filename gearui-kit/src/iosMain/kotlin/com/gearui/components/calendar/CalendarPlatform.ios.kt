package com.gearui.components.calendar

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

internal actual fun currentCalendarDate(): CalendarDate {
    val components = NSCalendar.currentCalendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        NSDate()
    )
    return CalendarDate(
        year = components.year.toInt(),
        month = components.month.toInt(),
        day = components.day.toInt()
    )
}
