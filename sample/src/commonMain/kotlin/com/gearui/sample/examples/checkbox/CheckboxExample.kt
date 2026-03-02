package com.gearui.sample.examples.checkbox

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.checkbox.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/**
 * Checkbox 组件示例
 */
@Composable
fun CheckboxExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础复选框
        var checked1 by remember { mutableStateOf(false) }
        var checked2 by remember { mutableStateOf(true) }
        ExampleSection(
            title = "基础复选框",
            description = "选中和未选中状态"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Checkbox(
                    checked = checked1,
                    onCheckedChange = { checked1 = it }
                )
                Checkbox(
                    checked = checked2,
                    onCheckedChange = { checked2 = it }
                )
            }
        }

        // 带标签的复选框
        var labelChecked1 by remember { mutableStateOf(false) }
        var labelChecked2 by remember { mutableStateOf(true) }
        ExampleSection(
            title = "带标签的复选框",
            description = "复选框配合文字标签"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CheckboxWithLabel(
                    checked = labelChecked1,
                    onCheckedChange = { labelChecked1 = it },
                    label = "复选项 1"
                )
                CheckboxWithLabel(
                    checked = labelChecked2,
                    onCheckedChange = { labelChecked2 = it },
                    label = "复选项 2"
                )
            }
        }

        // 复选框尺寸
        var sizeChecked by remember { mutableStateOf(true) }
        ExampleSection(
            title = "复选框尺寸",
            description = "提供大、中、小三种尺寸"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = com.tencent.kuikly.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.LARGE
                )
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.MEDIUM
                )
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.SMALL
                )
            }
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "不可操作的复选框"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false
                )
                Checkbox(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false
                )
            }
        }

        // 复选框组
        var selectedOptions by remember {
            mutableStateOf(setOf("选项1", "选项3"))
        }
        ExampleSection(
            title = "复选框组",
            description = "一组相关的复选框"
        ) {
            CheckboxGroup(
                options = listOf("选项1", "选项2", "选项3", "选项4"),
                selectedOptions = selectedOptions,
                onSelectionChange = { selectedOptions = it }
            )
        }
    }
}
