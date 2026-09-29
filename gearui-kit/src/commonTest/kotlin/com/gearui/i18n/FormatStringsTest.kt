package com.gearui.i18n

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatStringsTest {
    private val zh = StringsZhHans.format
    private val en = StringsEnUs.format

    @Test
    fun chineseCountsUseWanAndYi() {
        assertEquals("999", zh.compactNumber(999))
        assertEquals("9999", zh.compactNumber(9_999))
        assertEquals("1万", zh.compactNumber(10_000))
        assertEquals("1.2万", zh.compactNumber(12_345))
        assertEquals("9999.9万", zh.compactNumber(99_999_999))
        assertEquals("1亿", zh.compactNumber(100_000_000))
        assertEquals("3.5亿", zh.compactNumber(356_000_000))
        assertEquals("-1.2万", zh.compactNumber(-12_345))
    }

    @Test
    fun roundsDownSoACountIsNeverOverstated() {
        assertEquals("1.9万", zh.compactNumber(19_999))
    }

    @Test
    fun englishCountsUseKmb() {
        assertEquals("12.3K", en.compactNumber(12_345))
        assertEquals("1M", en.compactNumber(1_000_000))
        assertEquals("2.5B", en.compactNumber(2_500_000_000))
    }

    @Test
    fun theWeekStartsOnMondayInMainlandChina() {
        assertEquals(1, zh.firstDayOfWeek)
        assertEquals(0, en.firstDayOfWeek)
        assertEquals(0, StringsZhHant.format.firstDayOfWeek)
    }

    // A fixed UTC+8 wall clock, so the rules are tested without the device's zone.
    private fun beijing(millis: Long): LocalMoment {
        val local = millis + 8 * 3_600_000L
        val days = local.floorDiv(86_400_000L)
        val minuteOfDay = local.mod(86_400_000L) / 60_000L
        val (y, m, d) = civil(days)
        return LocalMoment(y, m, d, (minuteOfDay / 60).toInt(), (minuteOfDay % 60).toInt())
    }

    private fun civil(z0: Long): Triple<Int, Int, Int> {
        val z = z0 + 719468
        val era = z.floorDiv(146097L)
        val doe = z - era * 146097
        val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
        val m = (if (mp < 10) mp + 3 else mp - 9).toInt()
        val y = (yoe + era * 400 + if (m <= 2) 1 else 0).toInt()
        return Triple(y, m, d)
    }

    // 2026-09-29 15:00 Beijing
    private val now = (epochDay(2026, 9, 29) * 24 + 15 - 8) * 3_600_000L

    private fun ago(minutes: Long) = zh.relativeTime(now - minutes * 60_000L, now, ::beijing)

    @Test
    fun relativeTimeFollowsChineseFeedConventions() {
        assertEquals("刚刚", ago(0))
        assertEquals("刚刚", zh.relativeTime(now + 5_000, now, ::beijing))
        assertEquals("5分钟前", ago(5))
        assertEquals("59分钟前", ago(59))
        assertEquals("13:30", ago(90))
        assertEquals("昨天 23:30", ago(15 * 60 + 30))
        assertEquals("9月27日", ago(2 * 24 * 60))
        assertEquals("2025年9月29日", ago(365L * 24 * 60))
    }

    @Test
    fun epochDayMatchesKnownDates() {
        assertEquals(0L, epochDay(1970, 1, 1))
        assertEquals(20_454L, epochDay(2026, 1, 1))
    }
}
