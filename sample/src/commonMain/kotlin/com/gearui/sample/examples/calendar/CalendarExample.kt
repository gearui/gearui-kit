package com.gearui.sample.examples.calendar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.calendar.Calendar
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.calendar.CalendarType
import com.gearui.components.calendar.CalendarPopup
import com.gearui.components.cell.Cell
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonSize
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * Calendar 日历组件示例
 *
 * 按照日历形式展示数据或日期的容器。
 */
@Composable
fun CalendarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 单选状态
    var singleSelectedDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showSingleCalendar by remember { mutableStateOf(false) }

    // 多选状态
    var multipleSelectedDates by remember { mutableStateOf<List<CalendarDate>>(emptyList()) }
    var showMultipleCalendar by remember { mutableStateOf(false) }

    // 区间选择状态
    var rangeStartDate by remember { mutableStateOf<CalendarDate?>(null) }
    var rangeEndDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showRangeCalendar by remember { mutableStateOf(false) }

    // 不使用 Popup 的日历
    var blockCalendarDate by remember { mutableStateOf<CalendarDate?>(null) }
    var blockCalendarMonth by remember { mutableStateOf(CalendarDate(2024, 1, 1)) }

    // 应用场景
    var appointmentDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showAppointmentCalendar by remember { mutableStateOf(false) }

    var travelStartDate by remember { mutableStateOf<CalendarDate?>(null) }
    var travelEndDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showTravelCalendar by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ExamplePage(
            component = component,
            onBack = onBack
        ) {
            // ========== 组件类型 ==========
            ExampleSection(
                title = "组件类型",
                description = "不同选择模式的日历"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surface)
                ) {
                    // 单个选择日历
                    Cell(
                        title = "单个选择日历",
                        note = singleSelectedDate?.let { "${it.year}-${it.month}-${it.day}" } ?: "请选择",
                        arrow = true,
                        onClick = { showSingleCalendar = true }
                    )

                    // 分割线
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.spacer16.dp)
                            .height(1.dp)
                            .background(colors.divider)
                    )

                    // 多个选择日历
                    Cell(
                        title = "多个选择日历",
                        note = if (multipleSelectedDates.isNotEmpty())
                            "已选${multipleSelectedDates.size}个日期"
                        else "请选择",
                        arrow = true,
                        onClick = { showMultipleCalendar = true }
                    )

                    // 分割线
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.spacer16.dp)
                            .height(1.dp)
                            .background(colors.divider)
                    )

                    // 区间选择日历
                    Cell(
                        title = "区间选择日历",
                        note = if (rangeStartDate != null && rangeEndDate != null)
                            "${rangeStartDate!!.month}/${rangeStartDate!!.day} - ${rangeEndDate!!.month}/${rangeEndDate!!.day}"
                        else "请选择",
                        arrow = true,
                        onClick = { showRangeCalendar = true }
                    )
                }
            }

            // ========== 不使用 Popup ==========
            ExampleSection(
                title = "不使用 Popup",
                description = "日历直接嵌入页面展示"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
                ) {
                    // 月份切换按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp, Alignment.CenterHorizontally)
                    ) {
                        Button(
                            text = "上个月",
                            theme = ButtonTheme.PRIMARY,
                            size = ButtonSize.SMALL,
                            onClick = {
                                blockCalendarMonth = if (blockCalendarMonth.month == 1) {
                                    CalendarDate(blockCalendarMonth.year - 1, 12, 1)
                                } else {
                                    CalendarDate(blockCalendarMonth.year, blockCalendarMonth.month - 1, 1)
                                }
                            }
                        )
                        Button(
                            text = "下个月",
                            theme = ButtonTheme.PRIMARY,
                            size = ButtonSize.SMALL,
                            onClick = {
                                blockCalendarMonth = if (blockCalendarMonth.month == 12) {
                                    CalendarDate(blockCalendarMonth.year + 1, 1, 1)
                                } else {
                                    CalendarDate(blockCalendarMonth.year, blockCalendarMonth.month + 1, 1)
                                }
                            }
                        )
                    }

                    // 日历直接展示
                    Calendar(
                        type = CalendarType.Single,
                        selectedDate = blockCalendarDate,
                        onDateSelect = { date ->
                            blockCalendarDate = date
                        },
                        currentMonth = blockCalendarMonth,
                        onMonthChange = { month ->
                            blockCalendarMonth = month
                        }
                    )

                    // 显示选中日期
                    if (blockCalendarDate != null) {
                        Text(
                            text = "已选择: ${blockCalendarDate!!.year}年${blockCalendarDate!!.month}月${blockCalendarDate!!.day}日",
                            style = Typography.BodyMedium,
                            color = colors.primary
                        )
                    }
                }
            }

            // ========== 应用场景 ==========
            ExampleSection(
                title = "应用场景",
                description = "实际使用示例"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
                ) {
                    // 预约日期选择
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface)
                    ) {
                        Cell(
                            title = "预约日期",
                            note = appointmentDate?.let {
                                "${it.year}年${it.month}月${it.day}日"
                            } ?: "请选择预约日期",
                            arrow = true,
                            onClick = { showAppointmentCalendar = true }
                        )
                    }

                    // 出行日期选择
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface)
                    ) {
                        Cell(
                            title = "出行日期",
                            note = if (travelStartDate != null && travelEndDate != null)
                                "${travelStartDate!!.month}月${travelStartDate!!.day}日 - ${travelEndDate!!.month}月${travelEndDate!!.day}日"
                            else "选择入住和离店日期",
                            arrow = true,
                            onClick = { showTravelCalendar = true }
                        )
                    }
                }
            }
        }

        // ========== Popup 弹窗 ==========

        // 单选日历 Popup
        CalendarPopup(
            visible = showSingleCalendar,
            onClose = { showSingleCalendar = false },
            title = "请选择日期",
            type = CalendarType.Single,
            initialDate = singleSelectedDate,
            onConfirm = { date ->
                singleSelectedDate = date
            }
        )

        // 多选日历 Popup
        CalendarPopup(
            visible = showMultipleCalendar,
            onClose = { showMultipleCalendar = false },
            title = "请选择日期（可多选）",
            type = CalendarType.Multiple,
            initialDates = multipleSelectedDates,
            onConfirmMultiple = { dates ->
                multipleSelectedDates = dates
            }
        )

        // 区间选择日历 Popup
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

        // 预约日期 Popup
        CalendarPopup(
            visible = showAppointmentCalendar,
            onClose = { showAppointmentCalendar = false },
            title = "选择预约日期",
            type = CalendarType.Single,
            initialDate = appointmentDate,
            onConfirm = { date ->
                appointmentDate = date
            }
        )

        // 出行日期 Popup
        CalendarPopup(
            visible = showTravelCalendar,
            onClose = { showTravelCalendar = false },
            title = "选择入住和离店日期",
            type = CalendarType.Range,
            initialRangeStart = travelStartDate,
            initialRangeEnd = travelEndDate,
            onConfirmRange = { start, end ->
                travelStartDate = start
                travelEndDate = end
            }
        )
    }
}
