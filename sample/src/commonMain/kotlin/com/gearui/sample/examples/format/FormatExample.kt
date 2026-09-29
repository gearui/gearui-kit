package com.gearui.sample.examples.format

import androidx.compose.runtime.Composable
import com.tencent.kuikly.core.datetime.DateTime
import androidx.compose.runtime.remember
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.foundation.calendar.Lunar
import com.gearui.i18n.I18n
import com.gearui.i18n.calendarLabel
import com.gearui.i18n.compactNumber
import com.gearui.i18n.fullDate
import com.gearui.i18n.relativeTime
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private const val Minute = 60_000L
private const val Day = 24 * 60 * Minute

/**
 * The conventions a language pack carries beyond words: how counts are shortened, how
 * a time is said, which day a week starts on, and the lunar calendar's names.
 * Switch the sample's language to see the same values in English or Traditional Chinese.
 */
@Composable
fun FormatExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val format = I18n.strings.format
    val lunar = I18n.strings.lunar
    val now = remember { DateTime.currentTimestamp() }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "数字缩写",
            description = "format.compactNumber：中文用万、亿，英文用 K、M、B，向下取整不虚报",
            surface = SectionSurface.Plain,
        ) {
            CellGroup(items = listOf(999L, 12_345L, 876_543L, 100_000_000L, 356_780_000L)) { value ->
                Cell(title = value.toString(), note = format.compactNumber(value))
            }
        }

        ExampleSection(
            title = "相对时间",
            description = "format.relativeTime：刚刚、N分钟前、今天时刻、昨天、本年月日、往年完整日期",
            surface = SectionSurface.Plain,
        ) {
            val samples = listOf(
                "30 秒前" to now - Minute / 2,
                "5 分钟前" to now - 5 * Minute,
                "3 小时前" to now - 180 * Minute,
                "1 天前" to now - Day,
                "10 天前" to now - 10 * Day,
                "400 天前" to now - 400 * Day,
            )
            CellGroup(items = samples) { (label, time) ->
                Cell(title = label, note = format.relativeTime(time, now))
            }
        }

        ExampleSection(
            title = "农历",
            description = "离线算法，1900–2100 年；节日优先，其次节气，初一显示月份",
            surface = SectionSurface.Plain,
        ) {
            val today = remember { CalendarDate.today() }
            val days = listOf(
                today,
                CalendarDate(2026, 2, 17),
                CalendarDate(2026, 9, 25),
                CalendarDate(2026, 6, 21),
                CalendarDate(2025, 1, 28),
            )
            CellGroup(items = days) { date ->
                val lunarDate = Lunar.fromSolar(date.year, date.month, date.day)
                Cell(
                    title = "${date.year}-${date.month}-${date.day}",
                    description = lunarDate?.let { lunar.fullDate(it) },
                    note = lunar.calendarLabel(date.year, date.month, date.day),
                )
            }
        }

        ExampleSection(
            title = "一周起始",
            description = "format.firstDayOfWeek：简体中文按 GB/T 7408 从周一开始，繁体中文与英文从周日开始",
            surface = SectionSurface.Plain,
        ) {
            val weekdays = I18n.strings.dateTime.weekdaysShort
            val first = format.firstDayOfWeek
            CellGroup(items = listOf(0)) {
                Cell(title = "本语言的一周", note = (weekdays.drop(first) + weekdays.take(first)).joinToString(" "))
            }
        }
    }
}
