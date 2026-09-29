package com.gearui.sample.examples.checkbox

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.gearui.foundation.layout.Spacing
import com.gearui.components.checkbox.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/**
 * Checkbox component examples
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
        // Basic checkboxes
        var checked1 by remember { mutableStateOf(false) }
        var checked2 by remember { mutableStateOf(true) }
        ExampleSection(
            title = "基础复选框",
            description = "未选中、选中与半选（indeterminate）"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                Checkbox(
                    checked = checked1,
                    onCheckedChange = { checked1 = it },
                    contentDescription = "选项 A"
                )
                Checkbox(
                    checked = checked2,
                    onCheckedChange = { checked2 = it },
                    contentDescription = "选项 B"
                )
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    indeterminate = true,
                    contentDescription = "选项 C"
                )
            }
        }

        // Checkboxes with labels
        var labelChecked1 by remember { mutableStateOf(false) }
        var labelChecked2 by remember { mutableStateOf(true) }
        ExampleSection(
            title = "带标签的复选框",
            description = "复选框配合文字标签"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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

        // Checkbox sizes
        var sizeChecked by remember { mutableStateOf(true) }
        ExampleSection(
            title = "复选框尺寸",
            description = "CheckboxSize 大、中、小三档"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.LARGE,
                    contentDescription = "大尺寸"
                )
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.MEDIUM,
                    contentDescription = "中尺寸"
                )
                Checkbox(
                    checked = sizeChecked,
                    onCheckedChange = { sizeChecked = it },
                    size = CheckboxSize.SMALL,
                    contentDescription = "小尺寸"
                )
            }
        }

        // Disabled state
        ExampleSection(
            title = "禁用状态",
            description = "enabled = false，点击无响应"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    contentDescription = "禁用选项 1"
                )
                Checkbox(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false,
                    contentDescription = "禁用选项 2"
                )
            }
        }

        // Checkbox group
        var selectedOptions by remember {
            mutableStateOf(setOf("选项1", "选项3"))
        }
        ExampleSection(
            title = "复选框组",
            description = "CheckboxGroup 管理一组多选值"
        ) {
            CheckboxGroup(
                options = listOf("选项1", "选项2", "选项3", "选项4"),
                selectedOptions = selectedOptions,
                onSelectionChange = { selectedOptions = it }
            )
        }
    }
}
