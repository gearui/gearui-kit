package com.gearui.components.calendar

import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the Gregorian pinning on Android: under a locale whose default
 * calendar is not Gregorian (th_TH resolves to the Buddhist calendar) the
 * platform clock must still produce a Gregorian date, because the kit's
 * month-grid and weekday math assume Gregorian unconditionally.
 */
class CalendarPlatformAndroidTest {

    private val originalLocale = Locale.getDefault()

    @BeforeTest
    fun useThaiLocale() {
        Locale.setDefault(Locale("th", "TH"))
    }

    @AfterTest
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun todayIsGregorianUnderBuddhistLocale() {
        val today = CalendarDate.today()
        assertTrue(today.year in 2020..2100, "expected Gregorian year, got ${today.year}")
        assertTrue(today.month in 1..12)
        assertTrue(today.day in 1..CalendarMath.daysInMonth(today.year, today.month))
    }
}
