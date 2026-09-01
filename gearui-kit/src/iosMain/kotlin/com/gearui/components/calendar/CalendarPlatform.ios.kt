package com.gearui.components.calendar

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

internal actual fun currentCalendarDate(): CalendarDate {
    // currentCalendar follows the user's locale calendar (Buddhist, Japanese, ...);
    // the kit's grid math is strictly Gregorian, so pin the identifier. A freshly
    // initialised NSCalendar uses the current time zone by default.
    val calendar = NSCalendar(NSCalendarIdentifierGregorian)
    val components = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        NSDate()
    )
    return CalendarDate(
        year = components.year.toInt(),
        month = components.month.toInt(),
        day = components.day.toInt()
    )
}
