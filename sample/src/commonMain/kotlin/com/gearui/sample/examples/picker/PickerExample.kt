package com.gearui.sample.examples.picker

import androidx.compose.runtime.*
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.picker.Picker
import com.gearui.components.picker.PickerOption
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private class PickerRow(val title: String, val value: String, val onClick: () -> Unit)

/**
 * Picker component examples
 *
 * Selects from a preset set of values
 */
@Composable
fun PickerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var stableOpen by remember { mutableStateOf(false) }
    var stableValues by remember { mutableStateOf(listOf("zj", "hz", "xh")) }
    val stableOptions = listOf(
        PickerOption("zj", "浙江省", listOf(PickerOption("hz", "杭州市", listOf(PickerOption("xh", "西湖区"))))),
        PickerOption("fj", "福建省", listOf(PickerOption("fz", "福州市", listOf(PickerOption("xh2", "西湖区")))))
    )
    Picker.Linked(stableOpen, options = stableOptions, selectedValues = stableValues, title = "按稳定值选择",
        onConfirm = { stableValues = it.map { node -> node.value }; stableOpen = false },
        onCancel = { stableOpen = false }, onDismiss = { stableOpen = false })
    // Basic picker data
    val cityData = listOf("广州市", "韶关市", "深圳市", "珠海市", "汕头市")

    // Multi-column picker data (year + season)
    val yearData = (2020..2026).map { "${it}年" }
    val seasonData = listOf("春", "夏", "秋", "冬")

    // Linked picker data (province - city - district). Hong Kong has no level below it,
    // so its path is complete at the first column.
    fun districts(vararg names: String) = names.map { PickerOption(it, it) }
    val areaData = listOf(
        PickerOption("gd", "广东省", listOf(
            PickerOption("sz", "深圳市", districts("南山区", "宝安区", "罗湖区", "福田区")),
            PickerOption("gz", "广州市", districts("天河区", "越秀区", "白云区", "花都区")),
            PickerOption("fs", "佛山市", districts("顺德区", "南海区", "禅城区")),
        )),
        PickerOption("zj", "浙江省", listOf(
            PickerOption("hz", "杭州市", districts("西湖区", "余杭区", "萧山区")),
            PickerOption("nb", "宁波市", districts("海曙区", "北仑区", "奉化区")),
        )),
        PickerOption("hk", "香港特别行政区"),
    )
    var areaValues by remember { mutableStateOf(listOf("zj", "hz", "西湖区")) }

    // State of each picker
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
        ExampleSection("稳定值与同名标签", "两省都有西湖区，按 value 区分；返回值是 value，不是显示文字", surface = SectionSurface.Plain) {
            val stableLabels = stableValues.runningFold(stableOptions as List<PickerOption>?) { level, value ->
                level?.firstOrNull { it.value == value }?.children
            }.zip(stableValues).mapNotNull { (level, value) -> level?.firstOrNull { it.value == value }?.label }
            CellGroup(items = listOf(Unit)) {
                Cell(
                    title = stableLabels.joinToString(" / "),
                    description = "value：" + stableValues.joinToString(" / "),
                    arrow = true,
                    onClick = { stableOpen = true },
                )
            }
        }
        // Each row opens a picker from the bottom sheet; the note shows the result.
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "选择器类型",
            description = "Picker.Single 单列、Picker.Multi 多列独立、Picker.Linked 多列联动"
        ) {
            CellGroup(
                items = listOf(
                    PickerRow("单列 · 地区", selectedCity) { showCityPicker = true },
                    PickerRow("多列 · 时间", selectedTime) { showTimePicker = true },
                    PickerRow("联动 · 省市区", selectedArea) { showAreaPicker = true },
                )
            ) { row ->
                Cell(
                    title = row.title,
                    note = row.value.ifEmpty { "请选择" },
                    arrow = true,
                    onClick = row.onClick
                )
            }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "标题",
            description = "title 为 null 时面板不显示标题"
        ) {
            CellGroup(
                items = listOf(
                    PickerRow("带标题", selectedAreaWithTitle) { showAreaWithTitlePicker = true },
                    PickerRow("无标题", selectedAreaNoTitle) { showAreaNoTitlePicker = true },
                )
            ) { row ->
                Cell(
                    title = row.title,
                    note = row.value.ifEmpty { "请选择" },
                    arrow = true,
                    onClick = row.onClick
                )
            }
        }
    }

    // ==================== Picker dialogs ====================

    // Basic picker - region
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

    // Basic picker - time (multiple columns)
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

    // Linked picker
    Picker.Linked(
        visible = showAreaPicker,
        title = "选择地区",
        options = areaData,
        columnNum = 3,
        selectedValues = areaValues,
        onConfirm = { selected ->
            areaValues = selected.map { it.value }
            selectedArea = selected.joinToString(" ") { it.label }
            showAreaPicker = false
        },
        onCancel = { showAreaPicker = false },
        onDismiss = { showAreaPicker = false }
    )

    // Picker with a title
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

    // Picker without a title
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
