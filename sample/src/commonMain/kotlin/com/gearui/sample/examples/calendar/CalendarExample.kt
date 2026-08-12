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
import com.gearui.foundation.layout.Spacing

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

    // Single selection state
    var singleSelectedDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showSingleCalendar by remember { mutableStateOf(false) }

    // Multiple selection state
    var multipleSelectedDates by remember { mutableStateOf<List<CalendarDate>>(emptyList()) }
    var showMultipleCalendar by remember { mutableStateOf(false) }

    // Range selection state
    var rangeStartDate by remember { mutableStateOf<CalendarDate?>(null) }
    var rangeEndDate by remember { mutableStateOf<CalendarDate?>(null) }
    var showRangeCalendar by remember { mutableStateOf(false) }

    // Calendar without a popup
    var blockCalendarDate by remember { mutableStateOf<CalendarDate?>(null) }
    var blockCalendarMonth by remember { mutableStateOf(CalendarDate(2024, 1, 1)) }

    // Use cases
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
            // ========== Component types ==========
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
                    // Single selection calendar
                    Cell(
                        title = "单个选择日历",
                        note = singleSelectedDate?.let { "${it.year}-${it.month}-${it.day}" } ?: "请选择",
                        arrow = true,
                        onClick = { showSingleCalendar = true }
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .height(1.dp)
                            .background(colors.border)
                    )

                    // Multiple selection calendar
                    Cell(
                        title = "多个选择日历",
                        note = if (multipleSelectedDates.isNotEmpty())
                            "已选${multipleSelectedDates.size}个日期"
                        else "请选择",
                        arrow = true,
                        onClick = { showMultipleCalendar = true }
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .height(1.dp)
                            .background(colors.border)
                    )

                    // Range selection calendar
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

            // ========== Without a popup ==========
            ExampleSection(
                title = "不使用 Popup",
                description = "日历直接嵌入页面展示"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    // Month switch buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg, Alignment.CenterHorizontally)
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

                    // Calendar shown inline
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

                    // Selected dates
                    if (blockCalendarDate != null) {
                        Text(
                            text = "已选择: ${blockCalendarDate!!.year}年${blockCalendarDate!!.month}月${blockCalendarDate!!.day}日",
                            style = Typography.BodyMedium,
                            color = colors.primary
                        )
                    }
                }
            }

            // ========== Use cases ==========
            ExampleSection(
                title = "应用场景",
                description = "实际使用示例"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    // Booking date selection
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

                    // Travel date selection
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

        // ========== Popup dialogs ==========

        // Single selection calendar popup
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

        // Multiple selection calendar popup
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

        // Range selection calendar popup
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

        // Booking date popup
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

        // Travel date popup
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
