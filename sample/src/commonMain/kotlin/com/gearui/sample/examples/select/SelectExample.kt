package com.gearui.sample.examples.select

import androidx.compose.runtime.*
import com.gearui.components.select.Select
import com.gearui.components.select.SelectOption
import com.gearui.components.select.MultiSelect
import com.gearui.foundation.field.FieldVariant
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/**
 * Select component examples
 */
@Composable
fun SelectExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedFruit by remember { mutableStateOf<String?>(null) }
    var selectedHobbies by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requiredCity by remember { mutableStateOf<String?>(null) }

    // City options
    val cityOptions = listOf(
        SelectOption("beijing", "北京"),
        SelectOption("shanghai", "上海"),
        SelectOption("guangzhou", "广州"),
        SelectOption("shenzhen", "深圳"),
        SelectOption("hangzhou", "杭州"),
        SelectOption("chengdu", "成都")
    )

    // Fruit options (with a disabled one)
    val fruitOptions = listOf(
        SelectOption("apple", "苹果"),
        SelectOption("banana", "香蕉"),
        SelectOption("orange", "橙子", disabled = true),
        SelectOption("grape", "葡萄"),
        SelectOption("watermelon", "西瓜")
    )

    // Hobby options
    val hobbyOptions = listOf(
        SelectOption("reading", "阅读"),
        SelectOption("music", "音乐"),
        SelectOption("sports", "运动"),
        SelectOption("travel", "旅行"),
        SelectOption("cooking", "烹饪"),
        SelectOption("photography", "摄影")
    )

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Basic single select
        ExampleSection(
            title = "基础单选",
            description = "点击展开选项面板，选中后收起"
        ) {
            Select(
                value = selectedCity,
                options = cityOptions,
                onValueChange = { selectedCity = it },
                placeholder = "请选择城市",
                variant = FieldVariant.SECONDARY,
            )
        }

        // With a label
        ExampleSection(
            title = "带标签",
            description = "label 字段标签；「橙子」为 disabled 选项"
        ) {
            Select(
                value = selectedFruit,
                options = fruitOptions,
                onValueChange = { selectedFruit = it },
                label = "选择水果",
                placeholder = "请选择",
                variant = FieldVariant.SECONDARY,
            )
        }

        // Multi-select mode
        ExampleSection(
            title = "多选模式",
            description = "MultiSelect 可勾选多项"
        ) {
            MultiSelect(
                values = selectedHobbies,
                options = hobbyOptions,
                onValuesChange = { selectedHobbies = it },
                label = "兴趣爱好",
                placeholder = "请选择兴趣爱好",
                variant = FieldVariant.SECONDARY,
            )
        }

        // Disabled state
        ExampleSection(
            title = "禁用状态",
            description = "enabled = false"
        ) {
            Select(
                value = "beijing",
                options = cityOptions,
                onValueChange = {},
                enabled = false,
                label = "禁用选择器",
                variant = FieldVariant.SECONDARY,
            )
        }

        // Error state
        ExampleSection(
            title = "错误状态",
            description = "未选择时 error 显示错误提示"
        ) {
            Select(
                value = requiredCity,
                options = cityOptions,
                onValueChange = { requiredCity = it },
                label = "必填项",
                placeholder = "请选择城市",
                error = if (requiredCity == null) "该字段为必填项" else null,
                variant = FieldVariant.SECONDARY,
            )
        }
    }
}
