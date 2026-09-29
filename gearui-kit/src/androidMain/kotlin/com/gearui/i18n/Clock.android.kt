package com.gearui.i18n

import java.util.GregorianCalendar

internal actual fun currentEpochMillis(): Long = System.currentTimeMillis()

internal actual fun localMomentOf(epochMillis: Long): LocalMoment {
    // Gregorian, not Calendar.getInstance(): a locale can select another calendar.
    val c = GregorianCalendar().apply { timeInMillis = epochMillis }
    return LocalMoment(
        year = c.get(GregorianCalendar.YEAR),
        month = c.get(GregorianCalendar.MONTH) + 1,
        day = c.get(GregorianCalendar.DAY_OF_MONTH),
        hour = c.get(GregorianCalendar.HOUR_OF_DAY),
        minute = c.get(GregorianCalendar.MINUTE),
    )
}
