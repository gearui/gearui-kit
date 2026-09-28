package com.gearui.sample.examples.datepicker

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.picker.DatePickerInput
import com.gearui.foundation.layout.Spacing
import com.gearui.components.picker.TimePickerInput
import com.gearui.components.picker.DateTimePickerInput
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/**
 * DatePicker component examples
 *
 * Date and time pickers: date, time, and both together
 */
@Composable
fun DatePickerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Date picker
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "日期选择器",
            description = "选择年月日"
        ) {
            var dateValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                DatePickerInput(
                    value = dateValue,
                    onValueChange = { dateValue = it },
                    placeholder = "请选择日期",
                    label = "出生日期"
                )

                Text(
                    text = "已选择: ${dateValue.ifEmpty { "未选择" }}",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Time picker
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "时间选择器",
            description = "选择时分"
        ) {
            var timeValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                TimePickerInput(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    placeholder = "请选择时间",
                    label = "开始时间"
                )

                Text(
                    text = "已选择: ${timeValue.ifEmpty { "未选择" }}",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Default values
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "默认值",
            description = "value 传入初始日期或时间"
        ) {
            var dateValue by remember { mutableStateOf("2024-01-15") }
            var timeValue by remember { mutableStateOf("09:30") }

            DatePickerInput(
                value = dateValue,
                onValueChange = { dateValue = it },
                placeholder = "请选择日期",
                label = "活动日期"
            )
            TimePickerInput(
                value = timeValue,
                onValueChange = { timeValue = it },
                placeholder = "请选择时间",
                label = "会议时间"
            )
        }

        // Date and time picker
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "日期时间选择器",
            description = "同时选择日期和时间"
        ) {
            var dateValue by remember { mutableStateOf("") }
            var timeValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                DateTimePickerInput(
                    dateValue = dateValue,
                    timeValue = timeValue,
                    onDateChange = { dateValue = it },
                    onTimeChange = { timeValue = it },
                    label = "预约时间"
                )

                Text(
                    text = "已选择: ${if (dateValue.isNotEmpty() || timeValue.isNotEmpty()) "$dateValue $timeValue" else "未选择"}",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Disabled state
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "禁用状态",
            description = "不可交互的选择器"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                DatePickerInput(
                    value = "2024-06-01",
                    onValueChange = {},
                    placeholder = "请选择日期",
                    label = "锁定日期",
                    enabled = false
                )

                TimePickerInput(
                    value = "14:00",
                    onValueChange = {},
                    placeholder = "请选择时间",
                    label = "锁定时间",
                    enabled = false
                )
            }
        }

        // Without a label
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "无标签样式",
            description = "不显示标签的选择器"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                var dateValue by remember { mutableStateOf("") }
                var timeValue by remember { mutableStateOf("") }

                DatePickerInput(
                    value = dateValue,
                    onValueChange = { dateValue = it },
                    placeholder = "选择日期",
                    modifier = Modifier.weight(1f)
                )

                TimePickerInput(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    placeholder = "选择时间",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
