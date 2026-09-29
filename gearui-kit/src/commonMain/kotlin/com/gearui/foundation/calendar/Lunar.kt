package com.gearui.foundation.calendar

/**
 * A date in the Chinese lunisolar calendar.
 *
 * [month] is 1..12; when [isLeapMonth] is true the date lies in the intercalary
 * month that follows the regular month of the same number. [year] is the lunar
 * year, which starts on Spring Festival rather than January 1.
 */
data class LunarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val isLeapMonth: Boolean,
)

/**
 * Traditional festivals keyed on the lunar calendar. Display names live in the
 * i18n packs; this enum is the stable identifier.
 */
enum class LunarFestival {
    SpringFestival,
    Lantern,
    DragonBoat,
    Qixi,
    Ghost,
    MidAutumn,
    DoubleNinth,
    Laba,
    LittleNewYear,
    NewYearsEve,
}

/**
 * Offline Chinese lunar calendar for Gregorian 1900-01-31 .. 2100-12-31.
 *
 * Everything is table driven and pure integer math, so results are identical
 * on every platform and need no time zone or locale support. Only indices and
 * enums are exposed; all display strings (month names, solar term names,
 * stems, branches, zodiac animals) belong to the i18n packs.
 *
 * Both tables were cross-checked day by day against an astronomical reference
 * computed for China Standard Time (UTC+8).
 */
object Lunar {

    private const val FIRST_YEAR = 1900
    private const val LAST_YEAR = 2100

    /**
     * One entry per lunar year 1900..2100, the widely used layout:
     * bits 15..4 flag months 1..12 as long (30 days) or short (29 days),
     * bits 3..0 hold the leap month number (0 = none) and bit 16 flags the
     * leap month as long.
     */
    private val lunarInfo = intArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,  // 1900-1909
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,  // 1910-1919
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,  // 1920-1929
        0x06566, 0x0d4a0, 0x0ea50, 0x16a95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,  // 1930-1939
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,  // 1940-1949
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,  // 1950-1959
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,  // 1960-1969
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,  // 1970-1979
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,  // 1980-1989
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x05ac0, 0x0ab60, 0x096d5, 0x092e0,  // 1990-1999
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,  // 2000-2009
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,  // 2010-2019
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,  // 2020-2029
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,  // 2030-2039
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,  // 2040-2049
        0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,  // 2050-2059
        0x092e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,  // 2060-2069
        0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,  // 2070-2079
        0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,  // 2080-2089
        0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,  // 2090-2099
        0x0d520,  // 2100-2100
    )

    /**
     * Day of month of the 24 solar terms for each Gregorian year 1900..2100.
     * Term i is stored in bits 2i..2i+1 as an offset from [termBaseDay], which
     * keeps the whole table in one Long per year.
     */
    private val solarTermInfo = longArrayOf(
        0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0xAAAAAABAAA6AL, 0xAAABBABBAFAAL, 0x5AA665A65AABL,  // 1900-1904
        0x6AAAA6AA9A5AL, 0xAAAAAAAAAA6AL, 0xAAABBABBAFAAL, 0x5AA665A65AABL, 0x6AAAA6AA9A5AL,  // 1905-1909
        0xAAAAAAAAAA6AL, 0xAAABBABBAFAAL, 0x56A665A65AABL, 0x6AA6A6AA9A56L, 0xAAAAAAAA9A5AL,  // 1910-1914
        0xAAABAABAAEAAL, 0x569665A65AAAL, 0x6AA6A6A69A56L, 0x6AAAAAAA9A5AL, 0xAAABAABAAEAAL,  // 1915-1919
        0x569665A65AAAL, 0x5AA6A6A65A56L, 0x6AAAAAAA9A5AL, 0xAAABAABAAA6AL, 0x569665A65AAAL,  // 1920-1924
        0x5AA6A6A65A56L, 0x6AAAA6AA9A5AL, 0xAAABAABAAA6AL, 0x555665A65AAAL, 0x5AA665A65A56L,  // 1925-1929
        0x6AAAA6AA9A5AL, 0xAAAAAABAAA6AL, 0x555665665AAAL, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL,  // 1930-1934
        0xAAAAAAAAAA6AL, 0x555665665AAAL, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0xAAAAAAAAAA6AL,  // 1935-1939
        0x555665665AAAL, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0xAAAAAAAAAA6AL, 0x555665655AAAL,  // 1940-1944
        0x569665A65A56L, 0x6AA6A6AA9A56L, 0xAAAAAAAA9A5AL, 0x5556556559AAL, 0x569665A65A55L,  // 1945-1949
        0x6AA6A6A65A56L, 0xAAAAAAAA9A5AL, 0x5556556559AAL, 0x569665A65A55L, 0x5AA6A6A65A56L,  // 1950-1954
        0x6AAAA6AA9A5AL, 0x5556556555AAL, 0x569665A65A55L, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL,  // 1955-1959
        0x55555565556AL, 0x555665665A55L, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0x55555565556AL,  // 1960-1964
        0x555665665A55L, 0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0x55555555556AL, 0x555665665A55L,  // 1965-1969
        0x5AA665A65A56L, 0x6AAAA6AA9A5AL, 0x55555555556AL, 0x555665655A55L, 0x5AA665A65A56L,  // 1970-1974
        0x6AA6A6AA9A5AL, 0x55555555456AL, 0x555655655A55L, 0x5A9665A65A56L, 0x6AA6A6A69A56L,  // 1975-1979
        0x55555555456AL, 0x555655655A55L, 0x569665A65A56L, 0x6AA6A6A65A56L, 0x55555155455AL,  // 1980-1984
        0x555655655955L, 0x569665A65A55L, 0x5AA6A5A65A56L, 0x15555155455AL, 0x555555655555L,  // 1985-1989
        0x569665665A55L, 0x5AA665A65A56L, 0x15555155455AL, 0x555555655515L, 0x555665665A55L,  // 1990-1994
        0x5AA665A65A56L, 0x15555155455AL, 0x555555555515L, 0x555665665A55L, 0x5AA665A65A56L,  // 1995-1999
        0x15555155455AL, 0x555555555515L, 0x555665665A55L, 0x5AA665A65A56L, 0x15555155455AL,  // 2000-2004
        0x555555555515L, 0x555655655A55L, 0x5AA665A65A56L, 0x15515155455AL, 0x555555554515L,  // 2005-2009
        0x555655655A55L, 0x5A9665A65A56L, 0x15515151455AL, 0x555551554515L, 0x555655655A55L,  // 2010-2014
        0x569665A65A56L, 0x155151510556L, 0x555551554505L, 0x555655655955L, 0x569665665A55L,  // 2015-2019
        0x155110510556L, 0x155551554505L, 0x555555655555L, 0x569665665A55L, 0x055110510556L,  // 2020-2024
        0x155551554505L, 0x555555555515L, 0x555665665A55L, 0x055110510556L, 0x155551554505L,  // 2025-2029
        0x555555555515L, 0x555665665A55L, 0x055110510556L, 0x155551554505L, 0x555555555515L,  // 2030-2034
        0x555655655A55L, 0x055110510556L, 0x155551554505L, 0x555555555515L, 0x555655655A55L,  // 2035-2039
        0x055110510556L, 0x155151514505L, 0x555555554515L, 0x555655655A55L, 0x054110510556L,  // 2040-2044
        0x155151510505L, 0x555551554515L, 0x555655655A55L, 0x014110110556L, 0x155110510501L,  // 2045-2049
        0x555551554505L, 0x555555655555L, 0x014110110555L, 0x155110510501L, 0x555551554505L,  // 2050-2054
        0x555555555555L, 0x014110110555L, 0x055110510501L, 0x155551554505L, 0x555555555555L,  // 2055-2059
        0x000110110555L, 0x055110510501L, 0x155551554505L, 0x555555555515L, 0x000110110555L,  // 2060-2064
        0x055110510501L, 0x155551554505L, 0x555555555515L, 0x000100100555L, 0x055110510501L,  // 2065-2069
        0x155151514505L, 0x555555555515L, 0x000100100555L, 0x054110510501L, 0x155151514505L,  // 2070-2074
        0x555551554515L, 0x000100100555L, 0x054110510501L, 0x155150510505L, 0x555551554515L,  // 2075-2079
        0x000100100555L, 0x014110110501L, 0x155110510505L, 0x555551554505L, 0x000000100055L,  // 2080-2084
        0x014110110500L, 0x155110510501L, 0x555551554505L, 0x000000000055L, 0x014110110500L,  // 2085-2089
        0x055110510501L, 0x155551554505L, 0x000000000055L, 0x000110110500L, 0x055110510501L,  // 2090-2094
        0x155551554505L, 0x000000000015L, 0x000100110500L, 0x055110510501L, 0x155551554505L,  // 2095-2099
        0x555555555515L,  // 2100-2100
    )

    /** Earliest day of month on which each solar term can fall in range. */
    private val termBaseDay = intArrayOf(
        4, 19, 3, 18, 4, 19, 4, 19, 4, 20, 4, 20,
        6, 22, 6, 22, 6, 22, 7, 22, 6, 21, 6, 21,
    )

    /** Epoch day of lunar 1900-01-01 (Gregorian 1900-01-31). */
    private val firstEpochDay = epochDay(1900, 1, 31)

    /** Epoch day of the last supported Gregorian day. */
    private val lastEpochDay = epochDay(2100, 12, 31)

    /** Epoch day of lunar new year for every supported lunar year. */
    private val yearStart: IntArray = IntArray(LAST_YEAR - FIRST_YEAR + 1).also { starts ->
        var day = firstEpochDay
        for (i in starts.indices) {
            starts[i] = day
            day += yearDays(FIRST_YEAR + i)
        }
    }

    /**
     * Converts a Gregorian date to its lunar date, or null when the date is
     * invalid or outside 1900-01-31 .. 2100-12-31.
     */
    fun fromSolar(year: Int, month: Int, day: Int): LunarDate? {
        if (!isValidSolar(year, month, day)) return null
        val target = epochDay(year, month, day)
        if (target < firstEpochDay || target > lastEpochDay) return null

        // Lunar year starts are sorted; the target belongs to the last one not after it.
        var index = yearStart.size - 1
        while (yearStart[index] > target) index--
        val lunarYear = FIRST_YEAR + index
        var offset = target - yearStart[index]

        val leap = leapMonth(lunarYear)
        for (m in 1..12) {
            val regular = monthDays(lunarYear, m)
            if (offset < regular) return LunarDate(lunarYear, m, offset + 1, false)
            offset -= regular
            if (m == leap) {
                val intercalary = leapDays(lunarYear)
                if (offset < intercalary) return LunarDate(lunarYear, m, offset + 1, true)
                offset -= intercalary
            }
        }
        return null
    }

    /**
     * Converts a lunar date back to Gregorian (year, month, day). Returns null
     * when the lunar date does not exist (for example a leap flag on a month
     * that is not the leap month) or maps outside the supported range.
     */
    fun toSolar(year: Int, month: Int, day: Int, isLeapMonth: Boolean = false): Triple<Int, Int, Int>? {
        if (year !in FIRST_YEAR..LAST_YEAR || month !in 1..12) return null
        val leap = leapMonth(year)
        if (isLeapMonth && leap != month) return null
        val length = if (isLeapMonth) leapDays(year) else monthDays(year, month)
        if (day !in 1..length) return null

        var offset = 0
        for (m in 1 until month) {
            offset += monthDays(year, m)
            if (m == leap) offset += leapDays(year)
        }
        // The leap month follows its regular namesake.
        if (isLeapMonth) offset += monthDays(year, month)

        val target = yearStart[year - FIRST_YEAR] + offset + day - 1
        if (target > lastEpochDay) return null
        return civilFromEpochDay(target)
    }

    /**
     * Index (0..23) of the solar term falling on the given Gregorian day, or
     * null when none does. Index 0 is Minor Cold in early January; terms then
     * follow in order, two per month, ending with the Winter Solstice (23).
     * Days are reckoned in China Standard Time.
     */
    fun solarTerm(year: Int, month: Int, day: Int): Int? {
        if (year !in FIRST_YEAR..LAST_YEAR || month !in 1..12) return null
        val packed = solarTermInfo[year - FIRST_YEAR]
        val first = (month - 1) * 2
        for (term in first..first + 1) {
            val termDay = termBaseDay[term] + ((packed shr (term * 2)) and 3L).toInt()
            if (termDay == day) return term
        }
        return null
    }

    /**
     * Traditional festival on the given lunar date, or null. Leap months carry
     * no festivals. Little New Year follows the northern convention (12/23),
     * and New Year's Eve is the last day of month 12, whether it has 29 or 30 days.
     */
    fun festival(date: LunarDate): LunarFestival? {
        if (date.isLeapMonth) return null
        return when (date.month) {
            1 -> when (date.day) {
                1 -> LunarFestival.SpringFestival
                15 -> LunarFestival.Lantern
                else -> null
            }
            5 -> if (date.day == 5) LunarFestival.DragonBoat else null
            7 -> when (date.day) {
                7 -> LunarFestival.Qixi
                15 -> LunarFestival.Ghost
                else -> null
            }
            8 -> if (date.day == 15) LunarFestival.MidAutumn else null
            9 -> if (date.day == 9) LunarFestival.DoubleNinth else null
            12 -> when {
                date.day == 8 -> LunarFestival.Laba
                date.day == 23 -> LunarFestival.LittleNewYear
                date.year in FIRST_YEAR..LAST_YEAR && date.day == monthDays(date.year, 12) ->
                    LunarFestival.NewYearsEve
                else -> null
            }
            else -> null
        }
    }

    /**
     * Sexagenary (stem, branch) of a lunar year: stem 0..9, branch 0..11.
     * 1984 is the first year of a cycle, so it maps to (0, 0).
     */
    fun ganzhiYear(lunarYear: Int): Pair<Int, Int> {
        val offset = lunarYear - 1984
        return Pair(offset.mod(10), offset.mod(12))
    }

    /** Zodiac animal index 0..11 of a lunar year, 0 = Rat; equals the branch. */
    fun zodiac(lunarYear: Int): Int = (lunarYear - 1984).mod(12)

    private fun leapMonth(year: Int): Int = lunarInfo[year - FIRST_YEAR] and 0xf

    private fun leapDays(year: Int): Int {
        if (leapMonth(year) == 0) return 0
        return if ((lunarInfo[year - FIRST_YEAR] and 0x10000) != 0) 30 else 29
    }

    private fun monthDays(year: Int, month: Int): Int {
        return if ((lunarInfo[year - FIRST_YEAR] and (0x10000 shr month)) != 0) 30 else 29
    }

    private fun yearDays(year: Int): Int {
        var total = 0
        for (m in 1..12) total += monthDays(year, m)
        return total + leapDays(year)
    }

    private fun isValidSolar(year: Int, month: Int, day: Int): Boolean {
        if (month !in 1..12 || day < 1) return false
        val length = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            else -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        }
        return day <= length
    }

    /**
     * Days since 1970-01-01 in the proleptic Gregorian calendar
     * (Hinnant's days_from_civil, integer only).
     */
    private fun epochDay(year: Int, month: Int, day: Int): Int {
        val y = if (month <= 2) year - 1 else year
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val mp = (month + 9) % 12
        val doy = (153 * mp + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097 + doe - 719468
    }

    /** Inverse of [epochDay]: (year, month, day). */
    private fun civilFromEpochDay(epochDay: Int): Triple<Int, Int, Int> {
        val z = epochDay + 719468
        val era = (if (z >= 0) z else z - 146096) / 146097
        val doe = z - era * 146097
        val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val day = doy - (153 * mp + 2) / 5 + 1
        val month = if (mp < 10) mp + 3 else mp - 9
        val year = yoe + era * 400 + if (month <= 2) 1 else 0
        return Triple(year, month, day)
    }
}
