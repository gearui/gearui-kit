package com.gearui.i18n

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

internal actual fun currentEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual fun localMomentOf(epochMillis: Long): LocalMoment {
    // Pinned to Gregorian: the user's calendar may be Buddhist or Japanese.
    val calendar = NSCalendar(NSCalendarIdentifierGregorian)
    val c = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or NSCalendarUnitHour or NSCalendarUnitMinute,
        NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0),
    )
    return LocalMoment(c.year.toInt(), c.month.toInt(), c.day.toInt(), c.hour.toInt(), c.minute.toInt())
}
