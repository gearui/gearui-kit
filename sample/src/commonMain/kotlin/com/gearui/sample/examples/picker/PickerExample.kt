package com.gearui.sample.examples.picker

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.cell.Cell
import com.gearui.components.picker.Picker
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Picker 组件示例
 *
 * 用于一组预设数据中的选择
 */
@Composable
fun PickerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 基础选择器数据
    val cityData = listOf("广州市", "韶关市", "深圳市", "珠海市", "汕头市")

    // 多列选择器数据（年份+季节）
    val yearData = (2020..2026).map { "${it}年" }
    val seasonData = listOf("春", "夏", "秋", "冬")

    // 联动选择器数据（省-市-区）
    val areaData = mapOf(
        "广东省" to mapOf(
            "深圳市" to listOf("南山区", "宝安区", "罗湖区", "福田区"),
            "广州市" to listOf("天河区", "越秀区", "白云区", "花都区"),
            "佛山市" to listOf("顺德区", "南海区", "禅城区")
        ),
        "浙江省" to mapOf(
            "杭州市" to listOf("西湖区", "余杭区", "萧山区"),
            "宁波市" to listOf("江东区", "北仑区", "奉化市"),
            "温州市" to listOf("鹿城区", "瑞安市", "乐清市")
        ),
        "江苏省" to mapOf(
            "南京市" to listOf("玄武区", "秦淮区", "建邺区", "鼓楼区"),
            "苏州市" to listOf("姑苏区", "虎丘区", "吴中区"),
            "无锡市" to listOf("梁溪区", "锡山区", "惠山区")
        )
    )

    // 各选择器状态
    var showCityPicker by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("") }

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("") }

    var showAreaPicker by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf("") }

    var showAreaWithTitlePicker by remember { mutableStateOf(false) }
    var selectedAreaWithTitle by remember { mutableStateOf("") }

    var showAreaNoTitlePicker by remember { mutableStateOf(false) }
    var selectedAreaNoTitle by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 基础选择器 - 地区
        ExampleSection(
            title = "基础选择器",
            description = "单列数据选择"
        ) {
            Cell(
                title = "选择地区",
                note = selectedCity.ifEmpty { "请选择" },
                arrow = true,
                onClick = { showCityPicker = true }
            )
        }

        // 基础选择器 - 时间（多列）
        ExampleSection(
            title = "基础选择器 - 时间",
            description = "多列独立数据选择"
        ) {
            Cell(
                title = "选择时间",
                note = selectedTime.ifEmpty { "请选择" },
                arrow = true,
                onClick = { showTimePicker = true }
            )
        }

        // 联动选择器
        ExampleSection(
            title = "联动选择器",
            description = "多列数据联动选择"
        ) {
            Cell(
                title = "选择地区",
                note = selectedArea.ifEmpty { "请选择" },
                arrow = true,
                onClick = { showAreaPicker = true }
            )
        }

        // ==================== 组件样式 ====================

        // 带标题选择器
        ExampleSection(
            title = "带标题选择器",
            description = "显示标题的选择器"
        ) {
            Cell(
                title = "选择地区",
                note = selectedAreaWithTitle.ifEmpty { "请选择" },
                arrow = true,
                onClick = { showAreaWithTitlePicker = true }
            )
        }

        // 无标题选择器
        ExampleSection(
            title = "无标题选择器",
            description = "不显示标题的选择器"
        ) {
            Cell(
                title = "选择地区",
                note = selectedAreaNoTitle.ifEmpty { "请选择" },
                arrow = true,
                onClick = { showAreaNoTitlePicker = true }
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Picker 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Picker.Single: 单列选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. Picker.Multi: 多列独立选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. Picker.Linked: 多列联动选择器",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 从底部弹出，支持滚动选择",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持自定义标题、取消、确定文案",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }

    // ==================== Picker 弹窗 ====================

    // 基础选择器 - 地区
    Picker.Single(
        visible = showCityPicker,
        title = "选择地区",
        data = cityData,
        selectedIndex = cityData.indexOf(selectedCity).coerceAtLeast(0),
        onConfirm = { index, value ->
            selectedCity = value
            showCityPicker = false
        },
        onCancel = { showCityPicker = false },
        onDismiss = { showCityPicker = false }
    )

    // 基础选择器 - 时间（多列）
    Picker.Multi(
        visible = showTimePicker,
        title = "选择时间",
        data = listOf(yearData, seasonData),
        selectedIndexes = listOf(
            yearData.indexOfFirst { selectedTime.contains(it) }.coerceAtLeast(0),
            seasonData.indexOfFirst { selectedTime.contains(it) }.coerceAtLeast(0)
        ),
        onConfirm = { indexes ->
            val year = yearData.getOrElse(indexes.getOrElse(0) { 0 }) { "" }
            val season = seasonData.getOrElse(indexes.getOrElse(1) { 0 }) { "" }
            selectedTime = "$year $season"
            showTimePicker = false
        },
        onCancel = { showTimePicker = false },
        onDismiss = { showTimePicker = false }
    )

    // 联动选择器
    Picker.Linked(
        visible = showAreaPicker,
        title = "选择地区",
        data = areaData,
        columnNum = 3,
        initialData = listOf("浙江省", "杭州市", "西湖区"),
        onConfirm = { selected ->
            selectedArea = selected.joinToString(" ")
            showAreaPicker = false
        },
        onCancel = { showAreaPicker = false },
        onDismiss = { showAreaPicker = false }
    )

    // 带标题选择器
    Picker.Single(
        visible = showAreaWithTitlePicker,
        title = "带标题选择器",
        data = cityData,
        selectedIndex = cityData.indexOf(selectedAreaWithTitle).coerceAtLeast(0),
        onConfirm = { index, value ->
            selectedAreaWithTitle = value
            showAreaWithTitlePicker = false
        },
        onCancel = { showAreaWithTitlePicker = false },
        onDismiss = { showAreaWithTitlePicker = false }
    )

    // 无标题选择器
    Picker.Single(
        visible = showAreaNoTitlePicker,
        title = null,
        data = cityData,
        selectedIndex = cityData.indexOf(selectedAreaNoTitle).coerceAtLeast(0),
        onConfirm = { index, value ->
            selectedAreaNoTitle = value
            showAreaNoTitlePicker = false
        },
        onCancel = { showAreaNoTitlePicker = false },
        onDismiss = { showAreaNoTitlePicker = false }
    )
}
