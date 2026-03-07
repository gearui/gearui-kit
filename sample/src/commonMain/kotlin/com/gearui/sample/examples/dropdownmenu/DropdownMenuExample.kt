package com.gearui.sample.examples.dropdownmenu

import androidx.compose.runtime.*
import com.gearui.components.select.MultiSelect
import com.gearui.components.select.Select
import com.gearui.components.select.SelectOption
import com.gearui.components.select.SelectPanelMode
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

@Composable
fun DropdownMenuExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var productType by remember { mutableStateOf("all") }
    var sortType by remember { mutableStateOf("default") }
    var multiValues by remember { mutableStateOf(setOf("1", "2")) }
    var groupValues by remember { mutableStateOf(setOf("a1", "b2")) }

    val productOptions = listOf(
        SelectOption("all", "全部产品"),
        SelectOption("new", "最新产品"),
        SelectOption("hot", "最火产品")
    )
    val sortOptions = listOf(
        SelectOption("default", "默认排序"),
        SelectOption("price", "价格从高到低")
    )
    val multiOptions = listOf(
        SelectOption("1", "选项1"),
        SelectOption("2", "选项2"),
        SelectOption("3", "选项3"),
        SelectOption("4", "选项4"),
        SelectOption("5", "选项5"),
        SelectOption("6", "选项6"),
        SelectOption("7", "选项7"),
        SelectOption("8", "选项8"),
        SelectOption("9", "禁用选项", disabled = true)
    )
    val groupOptions = listOf(
        SelectOption("a1", "类型-选项1"),
        SelectOption("a2", "类型-选项2"),
        SelectOption("a3", "类型-选项3"),
        SelectOption("b1", "角色-选项1"),
        SelectOption("b2", "角色-选项2"),
        SelectOption("b3", "角色-选项3"),
        SelectOption("c1", "能力-选项1"),
        SelectOption("c2", "能力-选项2"),
        SelectOption("c3", "能力-选项3")
    )

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "组件类型", description = "单选下拉菜单、分栏下拉菜单、向上展开（由 overlay 自动翻转）") {
            Select(
                value = productType,
                options = productOptions,
                onValueChange = { productType = it },
                label = "产品筛选"
            )
            Select(
                value = sortType,
                options = sortOptions,
                onValueChange = { sortType = it },
                label = "排序方式",
                panelMode = SelectPanelMode.ITEM_ALIGNED
            )
            MultiSelect(
                values = multiValues,
                options = multiOptions,
                onValuesChange = { multiValues = it },
                label = "分栏多选（模拟）"
            )
        }

        ExampleSection(title = "组件状态", description = "禁用状态、分组菜单（按前缀分组展示）") {
            Select(
                value = "disabled",
                options = listOf(SelectOption("disabled", "禁用菜单")),
                onValueChange = {},
                label = "禁用菜单",
                enabled = false
            )
            MultiSelect(
                values = groupValues,
                options = groupOptions,
                onValuesChange = { groupValues = it },
                label = "分组菜单"
            )
        }
    }
}
