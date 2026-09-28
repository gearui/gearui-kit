package com.gearui.sample.examples.switch

import androidx.compose.runtime.*
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.switch.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

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
    val colors = Theme.colors

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
        // SwitchGroup is a set of loose field rows, so it sits on the section card.
        ExampleSection(title = "SwitchGroup", description = "一组独立设置，整行可点，支持副说明") {
            var enabledKeys by remember { mutableStateOf(setOf("push")) }
            SwitchGroup(
                items = listOf(
                    SwitchGroupItem("push", "推送通知", "接收新消息提醒"),
                    SwitchGroupItem("sound", "声音", "消息到达时播放提示音"),
                    SwitchGroupItem("night", "免打扰", "22:00 - 08:00 静音", enabled = false),
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
        ExampleSection(
            title = "开关类型",
            description = "SwitchType 基础、文字、图标，以及 trackOnColor 自定义颜色",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(0, 1, 2, 3)) { index ->
                when (index) {
                    0 -> Cell(
                        title = "基础开关",
                        trailing = {
                            Switch(
                                checked = baseSwitch,
                                onCheckedChange = { baseSwitch = it }
                            )
                        }
                    )
                    1 -> Cell(
                        title = "带文字开关",
                        trailing = {
                            Switch(
                                checked = textSwitch,
                                onCheckedChange = { textSwitch = it },
                                type = SwitchType.TEXT
                            )
                        }
                    )
                    2 -> Cell(
                        title = "带图标开关",
                        trailing = {
                            Switch(
                                checked = iconSwitch,
                                onCheckedChange = { iconSwitch = it },
                                type = SwitchType.ICON
                            )
                        }
                    )
                    else -> Cell(
                        title = "自定义颜色开关",
                        trailing = {
                            Switch(
                                checked = colorSwitch,
                                onCheckedChange = { colorSwitch = it },
                                trackOnColor = colors.success
                            )
                        }
                    )
                }
            }
        }

        // Loading state
        ExampleSection(
            title = "加载状态",
            description = "SwitchType.LOADING，关闭与开启两种",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(false, true)) { checked ->
                Cell(
                    title = if (checked) "加载中（开）" else "加载中（关）",
                    trailing = {
                        Switch(
                            checked = checked,
                            onCheckedChange = {},
                            type = SwitchType.LOADING
                        )
                    }
                )
            }
        }

        // Disabled state
        ExampleSection(
            title = "禁用状态",
            description = "enabled = false，关闭与开启两种",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(false, true)) { checked ->
                Cell(
                    title = if (checked) "禁用（开）" else "禁用（关）",
                    trailing = {
                        Switch(
                            checked = checked,
                            onCheckedChange = {},
                            enabled = false
                        )
                    }
                )
            }
        }

        // Sizes
        ExampleSection(
            title = "开关尺寸",
            description = "SwitchSize 大、中、小三档",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(SwitchSize.LARGE, SwitchSize.MEDIUM, SwitchSize.SMALL)) { size ->
                when (size) {
                    SwitchSize.LARGE -> Cell(
                        title = "大尺寸",
                        trailing = {
                            Switch(
                                checked = sizeLarge,
                                onCheckedChange = { sizeLarge = it },
                                size = SwitchSize.LARGE
                            )
                        }
                    )
                    SwitchSize.MEDIUM -> Cell(
                        title = "中尺寸",
                        trailing = {
                            Switch(
                                checked = sizeMedium,
                                onCheckedChange = { sizeMedium = it },
                                size = SwitchSize.MEDIUM
                            )
                        }
                    )
                    else -> Cell(
                        title = "小尺寸",
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
    }
}
