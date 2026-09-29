package com.gearui.i18n

import androidx.compose.runtime.Immutable

/** A unit a large number is shortened to: 12,000 is "1.2万" in Chinese, "12K" in English. */
@Immutable
data class CompactUnit(val size: Long, val suffix: String)

/**
 * How numbers, dates and weeks are written in a language — the conventions that differ
 * by region rather than by word.
 *
 * @property firstDayOfWeek 0 = Sunday, 1 = Monday. Mainland China starts the week on
 *   Monday (GB/T 7408); Taiwan and the US on Sunday.
 * @property compactUnits ascending; a count at or above a unit's size is written in it.
 */
@Immutable
data class FormatStrings(
    val firstDayOfWeek: Int,
    val compactUnits: List<CompactUnit>,
    val justNow: String,
    /** `{n}` minutes. */
    val minutesAgoFormat: String,
    /** `{time}` as HH:mm. */
    val yesterdayFormat: String,
    /** `{month}` and `{day}`, numbers. */
    val monthDayFormat: String,
    /** `{year}`, `{month}` and `{day}`, numbers. */
    val dateFormat: String,
)

@Immutable
data class FormatStringsPatch(
    val firstDayOfWeek: Int? = null,
    val compactUnits: List<CompactUnit>? = null,
    val justNow: String? = null,
    val minutesAgoFormat: String? = null,
    val yesterdayFormat: String? = null,
    val monthDayFormat: String? = null,
    val dateFormat: String? = null,
)

val FormatStringsPatch.isEmpty: Boolean
    get() = firstDayOfWeek == null && compactUnits == null && justNow == null &&
        minutesAgoFormat == null && yesterdayFormat == null && monthDayFormat == null &&
        dateFormat == null

fun FormatStrings.merge(patch: FormatStringsPatch?): FormatStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        firstDayOfWeek = patch.firstDayOfWeek ?: firstDayOfWeek,
        compactUnits = patch.compactUnits ?: compactUnits,
        justNow = patch.justNow ?: justNow,
        minutesAgoFormat = patch.minutesAgoFormat ?: minutesAgoFormat,
        yesterdayFormat = patch.yesterdayFormat ?: yesterdayFormat,
        monthDayFormat = patch.monthDayFormat ?: monthDayFormat,
        dateFormat = patch.dateFormat ?: dateFormat,
    )
}

/**
 * A count shortened to the language's units, one decimal, rounded **down** so a count
 * is never overstated: 12,345 → "1.2万"; 100,000,000 → "1亿"; 999 → "999".
 */
fun FormatStrings.compactNumber(value: Long): String {
    val magnitude = if (value == Long.MIN_VALUE) Long.MAX_VALUE else kotlin.math.abs(value)
    val unit = compactUnits.lastOrNull { magnitude >= it.size } ?: return value.toString()
    val tenths = magnitude / (unit.size / 10)
    val whole = tenths / 10
    val fraction = tenths % 10
    val sign = if (value < 0) "-" else ""
    return if (fraction == 0L) "$sign$whole${unit.suffix}" else "$sign$whole.$fraction${unit.suffix}"
}

/** A wall-clock moment in the device's time zone. */
@Immutable
data class LocalMoment(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int)

/**
 * When something happened, the way Chinese feeds and comment threads write it:
 * under a minute "刚刚"; under an hour "5分钟前"; earlier today "14:30"; yesterday
 * "昨天 14:30"; this year "9月28日"; before that "2025年9月28日".
 *
 * [then] and [now] are epoch milliseconds; the day boundaries are the device's.
 * A time in the future (a skewed clock) reads as just now.
 */
fun FormatStrings.relativeTime(then: Long, now: Long = currentEpochMillis()): String =
    relativeTime(then, now, ::localMomentOf)

internal fun FormatStrings.relativeTime(then: Long, now: Long, local: (Long) -> LocalMoment): String {
    val elapsed = now - then
    if (elapsed < MinuteMillis) return justNow
    if (elapsed < HourMillis) return minutesAgoFormat.formatArgs("n" to elapsed / MinuteMillis)
    val a = local(then)
    val b = local(now)
    val time = "${a.hour.toString().padStart(2, '0')}:${a.minute.toString().padStart(2, '0')}"
    val dayGap = epochDay(b.year, b.month, b.day) - epochDay(a.year, a.month, a.day)
    return when {
        dayGap == 0L -> time
        dayGap == 1L -> yesterdayFormat.formatArgs("time" to time)
        a.year == b.year -> monthDayFormat.formatArgs("month" to a.month, "day" to a.day)
        else -> dateFormat.formatArgs("year" to a.year, "month" to a.month, "day" to a.day)
    }
}

private const val MinuteMillis = 60_000L
private const val HourMillis = 3_600_000L

/** Days since 1970-01-01 of a Gregorian date (Hinnant's days-from-civil). */
internal fun epochDay(year: Int, month: Int, day: Int): Long {
    val y = (if (month <= 2) year - 1 else year).toLong()
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val mp = (month + 9) % 12
    val doy = (153 * mp + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097 + doe - 719468
}

/** Now, in epoch milliseconds. */
internal expect fun currentEpochMillis(): Long

/** [epochMillis] on the device's wall clock. */
internal expect fun localMomentOf(epochMillis: Long): LocalMoment
