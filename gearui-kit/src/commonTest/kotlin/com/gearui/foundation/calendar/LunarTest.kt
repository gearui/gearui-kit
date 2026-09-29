package com.gearui.foundation.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the offline lunar calendar. Expectations are published
 * calendar facts (Spring Festival dates, leap months, solar terms), plus a
 * sampled round trip over the whole supported range.
 */
class LunarTest {

    // Solar term indices used below.
    private val minorCold = 0
    private val springBegins = 2
    private val springEquinox = 5
    private val qingming = 6
    private val summerSolstice = 11
    private val winterSolstice = 23

    @Test
    fun springFestivalFallsOnKnownDates() {
        val dates = listOf(
            Triple(1900, 1, 31), Triple(1949, 1, 29), Triple(2000, 2, 5),
            Triple(2020, 1, 25), Triple(2023, 1, 22), Triple(2024, 2, 10),
            Triple(2025, 1, 29), Triple(2026, 2, 17), Triple(2027, 2, 6),
            Triple(2030, 2, 3), Triple(2050, 1, 23), Triple(2100, 2, 9),
        )
        for ((y, m, d) in dates) {
            val lunar = Lunar.fromSolar(y, m, d)
            assertEquals(LunarDate(y, 1, 1, false), lunar, "$y-$m-$d")
            assertEquals(LunarFestival.SpringFestival, Lunar.festival(lunar!!))
            assertEquals(Triple(y, m, d), Lunar.toSolar(y, 1, 1))
        }
    }

    @Test
    fun leapMonthsMatchTheAlmanac() {
        val leaps = mapOf(2017 to 6, 2020 to 4, 2023 to 2, 2025 to 6, 2028 to 5, 2033 to 11)
        for ((year, month) in leaps) {
            assertNotNull(Lunar.toSolar(year, month, 1, isLeapMonth = true), "$year leap $month")
            assertNull(Lunar.toSolar(year, month + 1, 1, isLeapMonth = true))
        }
        // 2020 leap 4th month starts on 2020-05-23.
        assertEquals(Triple(2020, 5, 23), Lunar.toSolar(2020, 4, 1, isLeapMonth = true))
        assertEquals(LunarDate(2020, 4, 1, true), Lunar.fromSolar(2020, 5, 23))
        // 2024 has no leap month.
        for (month in 1..12) assertNull(Lunar.toSolar(2024, month, 1, isLeapMonth = true))
    }

    @Test
    fun festivalsLandOnKnownDays() {
        assertEquals(LunarDate(2025, 8, 15, false), Lunar.fromSolar(2025, 10, 6))
        assertEquals(LunarFestival.MidAutumn, festivalOn(2025, 10, 6))
        assertEquals(LunarFestival.MidAutumn, festivalOn(2026, 9, 25))
        assertEquals(LunarFestival.DragonBoat, festivalOn(2024, 6, 10))
        assertEquals(LunarFestival.LittleNewYear, festivalOn(2025, 1, 22))
        assertNull(festivalOn(2025, 10, 7))
    }

    @Test
    fun newYearsEveIsTheLastDayOfMonthTwelve() {
        // Month 12 of lunar 2024 has only 29 days, so the eve is the 29th.
        assertEquals(LunarDate(2024, 12, 29, false), Lunar.fromSolar(2025, 1, 28))
        assertEquals(LunarFestival.NewYearsEve, festivalOn(2025, 1, 28))
        assertEquals(LunarFestival.NewYearsEve, festivalOn(2026, 2, 16))
        assertEquals(LunarDate(2025, 12, 29, false), Lunar.fromSolar(2026, 2, 16))
        assertNull(Lunar.festival(LunarDate(2025, 12, 28, false)))
        // Month 12 of lunar 2023 has 30 days: the eve is the 30th, not the 29th.
        assertEquals(LunarDate(2023, 12, 30, false), Lunar.fromSolar(2024, 2, 9))
        assertEquals(LunarFestival.NewYearsEve, festivalOn(2024, 2, 9))
        assertNull(festivalOn(2024, 2, 8))
    }

    @Test
    fun leapMonthsCarryNoFestivals() {
        assertNull(Lunar.festival(LunarDate(2025, 6, 15, true)))
        assertNull(Lunar.festival(LunarDate(2020, 4, 1, true)))
    }

    @Test
    fun solarTermsFallOnKnownDays() {
        assertEquals(springBegins, Lunar.solarTerm(2000, 2, 4))
        assertEquals(springBegins, Lunar.solarTerm(2024, 2, 4))
        assertEquals(springBegins, Lunar.solarTerm(2025, 2, 3))
        assertEquals(springBegins, Lunar.solarTerm(2026, 2, 4))
        assertEquals(springEquinox, Lunar.solarTerm(2024, 3, 20))
        assertEquals(qingming, Lunar.solarTerm(2025, 4, 4))
        assertEquals(qingming, Lunar.solarTerm(2026, 4, 5))
        assertEquals(summerSolstice, Lunar.solarTerm(2024, 6, 21))
        assertEquals(winterSolstice, Lunar.solarTerm(2024, 12, 21))
        assertEquals(winterSolstice, Lunar.solarTerm(2025, 12, 21))
        assertEquals(minorCold, Lunar.solarTerm(2024, 1, 6))
        assertNull(Lunar.solarTerm(2024, 2, 5))
        assertNull(Lunar.solarTerm(1899, 12, 22))
    }

    @Test
    fun everyYearHasTwentyFourSolarTerms() {
        for (year in listOf(1900, 1951, 2024, 2100)) {
            var count = 0
            for (month in 1..12) for (day in 1..31) if (Lunar.solarTerm(year, month, day) != null) count++
            assertEquals(24, count, "$year")
        }
    }

    @Test
    fun ganzhiAndZodiacFollowTheSixtyYearCycle() {
        assertEquals(Pair(0, 0), Lunar.ganzhiYear(1984)) // Jia-Zi, Rat
        assertEquals(Pair(0, 4), Lunar.ganzhiYear(2024)) // Jia-Chen, Dragon
        assertEquals(Pair(2, 6), Lunar.ganzhiYear(2026)) // Bing-Wu, Horse
        assertEquals(Pair(6, 0), Lunar.ganzhiYear(1900)) // Geng-Zi
        assertEquals(0, Lunar.zodiac(1984))
        assertEquals(4, Lunar.zodiac(2024))
        assertEquals(6, Lunar.zodiac(2026))
        assertEquals(0, Lunar.zodiac(1900))
    }

    @Test
    fun rangeEdgesAreEnforced() {
        assertNull(Lunar.fromSolar(1900, 1, 30))
        assertEquals(LunarDate(1900, 1, 1, false), Lunar.fromSolar(1900, 1, 31))
        assertNotNull(Lunar.fromSolar(2100, 12, 31))
        assertNull(Lunar.fromSolar(2101, 1, 1))
        assertNull(Lunar.toSolar(1899, 12, 1))
        assertNull(Lunar.toSolar(2101, 1, 1))
        // Invalid Gregorian input: 1900 and 2100 are not leap years.
        assertNull(Lunar.fromSolar(1900, 2, 29))
        assertNull(Lunar.fromSolar(2100, 2, 29))
        assertNotNull(Lunar.fromSolar(2000, 2, 29))
        assertNull(Lunar.fromSolar(2024, 13, 1))
        assertNull(Lunar.toSolar(2024, 1, 31))
    }

    @Test
    fun sampledRoundTripCoversTheWholeRange() {
        var year = 1900
        var month = 1
        var day = 31
        var checked = 0
        while (year < 2100 || (year == 2100 && month <= 12)) {
            val lunar = assertNotNull(Lunar.fromSolar(year, month, day), "$year-$month-$day")
            assertTrue(lunar.day in 1..30)
            assertEquals(Triple(year, month, day), Lunar.toSolar(lunar.year, lunar.month, lunar.day, lunar.isLeapMonth))
            checked++
            // Step 7 days forward.
            day += 7
            val length = daysInMonth(year, month)
            if (day > length) {
                day -= length
                month++
                if (month > 12) {
                    month = 1
                    year++
                }
            }
            if (year > 2100) break
        }
        assertTrue(checked > 10_000)
    }

    private fun festivalOn(year: Int, month: Int, day: Int): LunarFestival? =
        Lunar.fromSolar(year, month, day)?.let(Lunar::festival)

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    }
}
