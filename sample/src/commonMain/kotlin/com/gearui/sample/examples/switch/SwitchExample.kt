package com.gearui.sample.examples.switch

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.cell.Cell
import com.gearui.components.switch.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Switch component examples
 *
 * Turns a feature on and off.
 */
@Composable
fun SwitchExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Component type state
    var baseSwitch by remember { mutableStateOf(false) }
    var textSwitch by remember { mutableStateOf(true) }
    var iconSwitch by remember { mutableStateOf(true) }
    var colorSwitch by remember { mutableStateOf(true) }

    // Component style state
    var sizeLarge by remember { mutableStateOf(true) }
    var sizeMedium by remember { mutableStateOf(true) }
    var sizeSmall by remember { mutableStateOf(true) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(title = "SwitchGroup", description = "一组独立设置，整行可点，支持副说明", useCardContainer = false) {
            var enabledKeys by remember { mutableStateOf(setOf("push")) }
            com.gearui.components.switch.SwitchGroup(
                items = listOf(
                    com.gearui.components.switch.SwitchGroupItem("push", "推送通知", "接收新消息提醒"),
                    com.gearui.components.switch.SwitchGroupItem("sound", "声音", "消息到达时播放提示音"),
                    com.gearui.components.switch.SwitchGroupItem("night", "免打扰", "22:00 - 08:00 静音", enabled = false),
                ),
                checkedKeys = enabledKeys,
                onCheckedChange = { key, checked ->
                    enabledKeys = if (checked) enabledKeys + key else enabledKeys - key
                },
                label = "通知",
                description = "控制这台设备上的提醒方式",
            )
        }

        // Component types
        ExampleSection(title = "组件类型") {
            // Basic switch
            Cell(
                title = "基础开关",
                trailing = {
                    Switch(
                        checked = baseSwitch,
                        onCheckedChange = { baseSwitch = it }
                    )
                }
            )

            // Switch with text
            Cell(
                title = "带文字开关",
                trailing = {
                    Switch(
                        checked = textSwitch,
                        onCheckedChange = { textSwitch = it },
                        type = SwitchType.TEXT
                    )
                }
            )

            // Switch with icons
            Cell(
                title = "带图标开关",
                trailing = {
                    Switch(
                        checked = iconSwitch,
                        onCheckedChange = { iconSwitch = it },
                        type = SwitchType.ICON
                    )
                }
            )

            // Switch with custom colours
            Cell(
                title = "自定义颜色开关",
                trailing = {
                    Switch(
                        checked = colorSwitch,
                        onCheckedChange = { colorSwitch = it },
                        trackOnColor = Color(0xFF00A870)  // 绿色
                    )
                }
            )
        }

        // Component states
        ExampleSection(title = "组件状态") {
            // Loading state - off
            Cell(
                title = "加载状态",
                trailing = {
                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        type = SwitchType.LOADING
                    )
                }
            )

            // Loading state - on
            Cell(
                title = "加载状态",
                trailing = {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        type = SwitchType.LOADING
                    )
                }
            )

            // Disabled state - off
            Cell(
                title = "禁用状态",
                trailing = {
                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = false
                    )
                }
            )

            // Disabled state - on
            Cell(
                title = "禁用状态",
                trailing = {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false
                    )
                }
            )
        }

        // Component styles
        ExampleSection(title = "组件样式") {
            // Large track, 28 logical pixels high.
            Cell(
                title = "大尺寸28",
                trailing = {
                    Switch(
                        checked = sizeLarge,
                        onCheckedChange = { sizeLarge = it },
                        size = SwitchSize.LARGE
                    )
                }
            )

            // Default track, 24 logical pixels high.
            Cell(
                title = "中尺寸24",
                trailing = {
                    Switch(
                        checked = sizeMedium,
                        onCheckedChange = { sizeMedium = it },
                        size = SwitchSize.MEDIUM
                    )
                }
            )

            // Compact track, 20 logical pixels high.
            Cell(
                title = "小尺寸20",
                trailing = {
                    Switch(
                        checked = sizeSmall,
                        onCheckedChange = { sizeSmall = it },
                        size = SwitchSize.SMALL
                    )
                }
            )
        }
    }
}
