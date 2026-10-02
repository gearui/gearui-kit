package com.gearui.sample.examples.togglebutton

import com.gearui.components.icon.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonGroup
import com.gearui.components.button.ButtonGroupDivider
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.components.togglebutton.ToggleButton
import com.gearui.components.togglebutton.ToggleButtonGroup
import com.gearui.components.togglebutton.ToggleButtonItem
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier

@Composable
fun ToggleButtonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var bold by remember { mutableStateOf(true) }
    var range by remember { mutableStateOf(setOf("week")) }
    var filters by remember { mutableStateOf(setOf("unread")) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "ToggleButton",
            description = "选中为实心，未选中为描边；状态留在按钮上"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                ToggleButton(bold, { bold = it }, text = "收藏", icon = Icons.heart)
                ToggleButton(false, {}, text = "禁用", enabled = false)
            }
        }

        ExampleSection(
            title = "单选组",
            description = "同一时间只有一个选中，再次点击可取消"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                ToggleButtonGroup(
                    items = listOf(
                        ToggleButtonItem("day", "日"),
                        ToggleButtonItem("week", "周"),
                        ToggleButtonItem("month", "月"),
                    ),
                    selectedKeys = range,
                    onSelectionChange = { range = it },
                    size = ButtonSize.SMALL,
                )
                Text("当前:${range.firstOrNull() ?: "未选择"}", style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
            }
        }

        ExampleSection(
            title = "多选组",
            description = "multiple = true，用作筛选条"
        ) {
            ToggleButtonGroup(
                items = listOf(
                    ToggleButtonItem("unread", "未读"),
                    ToggleButtonItem("star", "星标"),
                    ToggleButtonItem("file", "带附件"),
                ),
                selectedKeys = filters,
                onSelectionChange = { filters = it },
                multiple = true,
                size = ButtonSize.SMALL,
            )
        }

        ExampleSection(
            title = "ButtonGroup",
            description = "相关动作拼成一个控件，中间用发丝线分隔"
        ) {
            ButtonGroup(modifier = Modifier.fillMaxWidth()) {
                Button(text = "拷贝", type = ButtonType.TEXT, modifier = Modifier.weight(1f), onClick = { Toast.show("拷贝") })
                ButtonGroupDivider()
                Button(text = "分享", type = ButtonType.TEXT, modifier = Modifier.weight(1f), onClick = { Toast.show("分享") })
                ButtonGroupDivider()
                Button(text = "删除", type = ButtonType.TEXT, modifier = Modifier.weight(1f), onClick = { Toast.show("删除") })
            }
        }
    }
}
