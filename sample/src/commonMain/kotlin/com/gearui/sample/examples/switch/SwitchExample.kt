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
 * Switch 组件示例
 *
 * 用于控制某个功能的开启和关闭。
 */
@Composable
fun SwitchExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // 组件类型状态
    var baseSwitch by remember { mutableStateOf(false) }
    var textSwitch by remember { mutableStateOf(true) }
    var iconSwitch by remember { mutableStateOf(true) }
    var colorSwitch by remember { mutableStateOf(true) }

    // 组件样式状态
    var sizeLarge by remember { mutableStateOf(true) }
    var sizeMedium by remember { mutableStateOf(true) }
    var sizeSmall by remember { mutableStateOf(true) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 组件类型
        ExampleSection(title = "组件类型") {
            // 基础开关
            Cell(
                title = "基础开关",
                trailing = {
                    Switch(
                        checked = baseSwitch,
                        onCheckedChange = { baseSwitch = it }
                    )
                }
            )

            // 带文字开关
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

            // 带图标开关
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

            // 自定义颜色开关
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

        // 组件状态
        ExampleSection(title = "组件状态") {
            // 加载状态 - 关闭
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

            // 加载状态 - 开启
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

            // 禁用状态 - 关闭
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

            // 禁用状态 - 开启
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

        // 组件样式
        ExampleSection(title = "组件样式") {
            // 大尺寸 32
            Cell(
                title = "大尺寸32",
                trailing = {
                    Switch(
                        checked = sizeLarge,
                        onCheckedChange = { sizeLarge = it },
                        size = SwitchSize.LARGE
                    )
                }
            )

            // 中尺寸 28
            Cell(
                title = "中尺寸28",
                trailing = {
                    Switch(
                        checked = sizeMedium,
                        onCheckedChange = { sizeMedium = it },
                        size = SwitchSize.MEDIUM
                    )
                }
            )

            // 小尺寸 24
            Cell(
                title = "小尺寸24",
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
