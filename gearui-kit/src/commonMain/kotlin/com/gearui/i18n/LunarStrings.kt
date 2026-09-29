package com.gearui.i18n

import androidx.compose.runtime.Immutable
import com.gearui.foundation.calendar.Lunar
import com.gearui.foundation.calendar.LunarDate
import com.gearui.foundation.calendar.LunarFestival

/**
 * Names for the Chinese lunar calendar computed by [Lunar]. The algorithm only yields
 * indices; every name is here, so a pack can write them in its own script.
 *
 * @property monthNames 12, 正月 … 腊月.
 * @property dayNames 30, 初一 … 三十.
 * @property solarTerms 24, starting at 小寒, in [Lunar.solarTerm] order.
 * @property festivals one per [LunarFestival], in declaration order.
 * @property stems 10 heavenly stems; [branches] 12 earthly branches; [zodiac] 12 animals.
 */
@Immutable
data class LunarStrings(
    val monthNames: List<String>,
    val leapPrefix: String,
    val dayNames: List<String>,
    val solarTerms: List<String>,
    val festivals: List<String>,
    val stems: List<String>,
    val branches: List<String>,
    val zodiac: List<String>,
    /** `{ganzhi}`, `{zodiac}`, `{month}` and `{day}`: 丙午年（马）八月十五. */
    val fullDateFormat: String,
)

@Immutable
data class LunarStringsPatch(
    val monthNames: List<String>? = null,
    val leapPrefix: String? = null,
    val dayNames: List<String>? = null,
    val solarTerms: List<String>? = null,
    val festivals: List<String>? = null,
    val stems: List<String>? = null,
    val branches: List<String>? = null,
    val zodiac: List<String>? = null,
    val fullDateFormat: String? = null,
)

val LunarStringsPatch.isEmpty: Boolean
    get() = monthNames == null && leapPrefix == null && dayNames == null && solarTerms == null &&
        festivals == null && stems == null && branches == null && zodiac == null &&
        fullDateFormat == null

fun LunarStrings.merge(patch: LunarStringsPatch?): LunarStrings {
    if (patch == null || patch.isEmpty) return this
    return copy(
        monthNames = patch.monthNames ?: monthNames,
        leapPrefix = patch.leapPrefix ?: leapPrefix,
        dayNames = patch.dayNames ?: dayNames,
        solarTerms = patch.solarTerms ?: solarTerms,
        festivals = patch.festivals ?: festivals,
        stems = patch.stems ?: stems,
        branches = patch.branches ?: branches,
        zodiac = patch.zodiac ?: zodiac,
        fullDateFormat = patch.fullDateFormat ?: fullDateFormat,
    )
}

/** 八月 or 闰六月. */
fun LunarStrings.monthName(date: LunarDate): String =
    (if (date.isLeapMonth) leapPrefix else "") + monthNames[date.month - 1]

/** 十五. */
fun LunarStrings.dayName(date: LunarDate): String = dayNames[date.day - 1]

/** 丙午年（马）八月十五. */
fun LunarStrings.fullDate(date: LunarDate): String {
    val (stem, branch) = Lunar.ganzhiYear(date.year)
    return fullDateFormat.formatArgs(
        "ganzhi" to stems[stem] + branches[branch],
        "zodiac" to zodiac[Lunar.zodiac(date.year)],
        "month" to monthName(date),
        "day" to dayName(date),
    )
}

/**
 * What a calendar cell shows under the Gregorian day, as Chinese calendars do: a
 * festival first, then a solar term, then the month name on the 1st of a lunar month,
 * otherwise the day — 中秋, 秋分, 九月, 十六. Null outside 1900–2100.
 */
fun LunarStrings.calendarLabel(year: Int, month: Int, day: Int): String? {
    val lunar = Lunar.fromSolar(year, month, day) ?: return null
    Lunar.festival(lunar)?.let { return festivals[it.ordinal] }
    Lunar.solarTerm(year, month, day)?.let { return solarTerms[it] }
    return if (lunar.day == 1) monthName(lunar) else dayName(lunar)
}

/**
 * Whether [calendarLabel] for the day names an occasion (a festival or a solar term)
 * rather than an ordinary lunar day, so the cell can colour it.
 */
fun calendarLabelIsOccasion(year: Int, month: Int, day: Int): Boolean {
    val lunar = Lunar.fromSolar(year, month, day) ?: return false
    return Lunar.festival(lunar) != null || Lunar.solarTerm(year, month, day) != null
}
