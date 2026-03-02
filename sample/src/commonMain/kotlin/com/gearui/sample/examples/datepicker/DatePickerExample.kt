package com.gearui.sample.examples.datepicker

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.picker.DatePickerInput
import com.gearui.components.picker.TimePickerInput
import com.gearui.components.picker.DateTimePickerInput
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * DatePicker 组件示例
 *
 * 日期时间选择器，支持日期、时间和日期时间选择
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
        // 日期选择器
        ExampleSection(
            title = "日期选择器",
            description = "选择年月日"
        ) {
            var dateValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DatePickerInput(
                    value = dateValue,
                    onValueChange = { dateValue = it },
                    placeholder = "请选择日期",
                    label = "出生日期"
                )

                Text(
                    text = "已选择: ${dateValue.ifEmpty { "未选择" }}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 带默认值
        ExampleSection(
            title = "带默认值",
            description = "设置初始日期值"
        ) {
            var dateValue by remember { mutableStateOf("2024-01-15") }

            DatePickerInput(
                value = dateValue,
                onValueChange = { dateValue = it },
                placeholder = "请选择日期",
                label = "活动日期"
            )
        }

        // 时间选择器
        ExampleSection(
            title = "时间选择器",
            description = "选择时分"
        ) {
            var timeValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TimePickerInput(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    placeholder = "请选择时间",
                    label = "开始时间"
                )

                Text(
                    text = "已选择: ${timeValue.ifEmpty { "未选择" }}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 时间带默认值
        ExampleSection(
            title = "时间默认值",
            description = "设置初始时间值"
        ) {
            var timeValue by remember { mutableStateOf("09:30") }

            TimePickerInput(
                value = timeValue,
                onValueChange = { timeValue = it },
                placeholder = "请选择时间",
                label = "会议时间"
            )
        }

        // 日期时间选择器
        ExampleSection(
            title = "日期时间选择器",
            description = "同时选择日期和时间"
        ) {
            var dateValue by remember { mutableStateOf("") }
            var timeValue by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DateTimePickerInput(
                    dateValue = dateValue,
                    timeValue = timeValue,
                    onDateChange = { dateValue = it },
                    onTimeChange = { timeValue = it },
                    label = "预约时间"
                )

                Text(
                    text = "已选择: ${if (dateValue.isNotEmpty() || timeValue.isNotEmpty()) "$dateValue $timeValue" else "未选择"}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "不可交互的选择器"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        // 无标签
        ExampleSection(
            title = "无标签样式",
            description = "不显示标签的选择器"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "DatePicker 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. DatePickerInput: 日期选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. TimePickerInput: 时间选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. DateTimePickerInput: 日期时间组合选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. label: 可选的标签文字",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. enabled: 控制是否可交互",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
