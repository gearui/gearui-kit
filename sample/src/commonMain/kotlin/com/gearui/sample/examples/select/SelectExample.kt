package com.gearui.sample.examples.select

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.select.Select
import com.gearui.components.select.SelectOption
import com.gearui.components.select.MultiSelect
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Select 组件示例
 */
@Composable
fun SelectExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedFruit by remember { mutableStateOf<String?>(null) }
    var selectedHobbies by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requiredCity by remember { mutableStateOf<String?>(null) }

    // 城市选项
    val cityOptions = listOf(
        SelectOption("beijing", "北京"),
        SelectOption("shanghai", "上海"),
        SelectOption("guangzhou", "广州"),
        SelectOption("shenzhen", "深圳"),
        SelectOption("hangzhou", "杭州"),
        SelectOption("chengdu", "成都")
    )

    // 水果选项（带禁用）
    val fruitOptions = listOf(
        SelectOption("apple", "苹果 🍎"),
        SelectOption("banana", "香蕉 🍌"),
        SelectOption("orange", "橙子 🍊", disabled = true),
        SelectOption("grape", "葡萄 🍇"),
        SelectOption("watermelon", "西瓜 🍉")
    )

    // 爱好选项
    val hobbyOptions = listOf(
        SelectOption("reading", "阅读 📚"),
        SelectOption("music", "音乐 🎵"),
        SelectOption("sports", "运动 ⚽"),
        SelectOption("travel", "旅行 ✈️"),
        SelectOption("cooking", "烹饪 🍳"),
        SelectOption("photography", "摄影 📷")
    )

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础单选
        ExampleSection(
            title = "基础单选",
            description = "单选下拉选择"
        ) {
            Select(
                value = selectedCity,
                options = cityOptions,
                onValueChange = { selectedCity = it },
                placeholder = "请选择城市"
            )
        }

        // 带标签
        ExampleSection(
            title = "带标签",
            description = "显示字段标签"
        ) {
            Select(
                value = selectedFruit,
                options = fruitOptions,
                onValueChange = { selectedFruit = it },
                label = "选择水果",
                placeholder = "请选择"
            )
        }

        // 多选模式
        ExampleSection(
            title = "多选模式",
            description = "支持选择多个选项"
        ) {
            MultiSelect(
                values = selectedHobbies,
                options = hobbyOptions,
                onValuesChange = { selectedHobbies = it },
                label = "兴趣爱好",
                placeholder = "请选择兴趣爱好"
            )
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "不可交互"
        ) {
            Select(
                value = "beijing",
                options = cityOptions,
                onValueChange = {},
                enabled = false,
                label = "禁用选择器"
            )
        }

        // 错误状态
        ExampleSection(
            title = "错误状态",
            description = "显示错误提示"
        ) {
            Select(
                value = requiredCity,
                options = cityOptions,
                onValueChange = { requiredCity = it },
                label = "必填项",
                placeholder = "请选择城市",
                error = if (requiredCity == null) "该字段为必填项" else null
            )
        }
    }
}
