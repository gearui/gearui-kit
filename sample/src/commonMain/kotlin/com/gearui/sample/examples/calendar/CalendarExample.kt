package com.gearui.sample.examples.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.calendar.Calendar
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.calendar.CalendarPopup
import com.gearui.components.calendar.CalendarType
import com.gearui.components.calendar.DateRangePickerInput
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier

private fun CalendarDate.formatted(): String = "$year-$month-$day"

/**
 * Calendar component examples
 *
 * A container that presents data or dates in calendar form.
 */
@Composable
fun CalendarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var rangeFrom by remember { mutableStateOf<CalendarDate?>(null) }
    var rangeTo by remember { mutableStateOf<CalendarDate?>(null) }

    var singleSelectedDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showSingleCalendar by remember { mutableStateOf(false) }

    var multipleSelectedDates by remember { mutableStateOf<List<CalendarDate>>(emptyList()) }
    var showMultipleCalendar by remember { mutableStateOf(false) }

    var rangeStartDate by remember { mutableStateOf<CalendarDate?>(null) }
    var rangeEndDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showRangeCalendar by remember { mutableStateOf(false) }

    var inlineDate by remember { mutableStateOf<CalendarDate?>(null) }
    var inlineMonth by remember { mutableStateOf(CalendarDate(2024, 1, 1)) }

    Box(modifier = Modifier.fillMaxSize()) {
        ExamplePage(
            component = component,
            onBack = onBack
        ) {
            ExampleSection(
                title = "日期范围字段",
                description = "DateRangePickerInput 点击弹出日历，选完回填区间"
            ) {
                DateRangePickerInput(
                    start = rangeFrom,
                    end = rangeTo,
                    onRangeChange = { from, to ->
                        rangeFrom = from
                        rangeTo = to
                    },
                    label = "统计区间",
                    required = true,
                )
                Text(
                    text = if (rangeFrom != null && rangeTo != null) {
                        "已选：${rangeFrom?.formatted()} 至 ${rangeTo?.formatted()}"
                    } else {
                        "尚未选择区间"
                    },
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }

            ExampleSection(
                surface = SectionSurface.Plain,
                title = "弹出式选择",
                description = "CalendarPopup 支持单选、多选、区间三种模式"
            ) {
                CellGroup(items = listOf(0, 1, 2)) { index ->
                    when (index) {
                        0 -> Cell(
                            title = "单个选择",
                            note = singleSelectedDate?.formatted() ?: "请选择",
                            arrow = true,
                            onClick = { showSingleCalendar = true }
                        )
                        1 -> Cell(
                            title = "多个选择",
                            note = if (multipleSelectedDates.isNotEmpty()) {
                                "已选 ${multipleSelectedDates.size} 个日期"
                            } else {
                                "请选择"
                            },
                            arrow = true,
                            onClick = { showMultipleCalendar = true }
                        )
                        else -> {
                            val start = rangeStartDate
                            val end = rangeEndDate
                            Cell(
                                title = "区间选择",
                                note = if (start != null && end != null) {
                                    "${start.month}/${start.day} - ${end.month}/${end.day}"
                                } else {
                                    "请选择"
                                },
                                arrow = true,
                                onClick = { showRangeCalendar = true }
                            )
                        }
                    }
                }
            }

            ExampleSection(
                surface = SectionSurface.Plain,
                title = "内嵌日历",
                description = "Calendar 直接嵌入页面，标题栏可切换月份"
            ) {
                Calendar(
                    type = CalendarType.Single,
                    selectedDate = inlineDate,
                    onDateSelect = { date -> inlineDate = date },
                    currentMonth = inlineMonth,
                    onMonthChange = { month -> inlineMonth = month }
                )
                inlineDate?.let {
                    Text(
                        text = "已选择：${it.year}年${it.month}月${it.day}日",
                        style = Theme.typography.bodyMedium,
                        color = colors.primary
                    )
                }
            }
        }

        CalendarPopup(
            visible = showSingleCalendar,
            onClose = { showSingleCalendar = false },
            title = "请选择日期",
            type = CalendarType.Single,
            initialDate = singleSelectedDate,
            onConfirm = { date -> singleSelectedDate = date }
        )

        CalendarPopup(
            visible = showMultipleCalendar,
            onClose = { showMultipleCalendar = false },
            title = "请选择日期（可多选）",
            type = CalendarType.Multiple,
            initialDates = multipleSelectedDates,
            onConfirmMultiple = { dates -> multipleSelectedDates = dates }
        )

        CalendarPopup(
            visible = showRangeCalendar,
            onClose = { showRangeCalendar = false },
            title = "请选择日期区间",
            type = CalendarType.Range,
            initialRangeStart = rangeStartDate,
            initialRangeEnd = rangeEndDate,
            onConfirmRange = { start, end ->
                rangeStartDate = start
                rangeEndDate = end
            }
        )
    }
}
