package com.gearui.sample.examples.collapse

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.collapse.Collapse
import com.gearui.components.collapse.CollapsePanel
import com.gearui.components.collapse.CollapseStyle
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Collapse component examples
 *
 * Component types:
 * - Basic collapse panel
 * - with operation instructions
 * - Accordion
 *
 * Component styles:
 * - block style
 * - card style
 */

private const val SAMPLE_CONTENT = "折叠面板内容区域可以自定义，支持任意内容。" +
        "GearUI 是一个基于 Compose Multiplatform 的跨平台 UI 组件库，" +
        "提供丰富的组件和主题支持，帮助开发者快速构建现代化的用户界面。"

@Composable
fun CollapseExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Basic collapse panel state
    var basicExpandedList by remember { mutableStateOf(listOf(false, false, false, false, false)) }

    // Block style state
    var blockExpandedList by remember { mutableStateOf(listOf(false, false, false, false, false)) }

    // Card style state
    var cardExpandedList by remember { mutableStateOf(listOf(false, false, false, false, false)) }

    // Operation instructions state
    var withOpTextExpandedList by remember { mutableStateOf(listOf(false, false, false, false, false)) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== Component types ==========

        // Basic collapse panel
        ExampleSection(
            title = "Basic 基础折叠面板",
            description = "最基础的折叠面板用法"
        ) {
            Collapse(
                style = CollapseStyle.Block,
                expansionCallback = { index, isExpanded ->
                    basicExpandedList = basicExpandedList.toMutableList().apply {
                        this[index] = !isExpanded
                    }
                },
                children = basicExpandedList.mapIndexed { index, expanded ->
                    CollapsePanel(
                        headerBuilder = { _ ->
                            Text(
                                text = "标题 $index",
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        },
                        isExpanded = expanded,
                        body = {
                            Text(
                                text = SAMPLE_CONTENT,
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    )
                }
            )
        }

        // With operation instructions
        ExampleSection(
            title = "with Operation Instructions 带操作说明",
            description = "展开图标旁显示操作说明文案"
        ) {
            Collapse(
                style = CollapseStyle.Block,
                expansionCallback = { index, isExpanded ->
                    withOpTextExpandedList = withOpTextExpandedList.toMutableList().apply {
                        this[index] = !isExpanded
                    }
                },
                children = withOpTextExpandedList.mapIndexed { index, expanded ->
                    CollapsePanel(
                        headerBuilder = { _ ->
                            Text(
                                text = "标题 $index",
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        },
                        expandIconTextBuilder = { isExpanded ->
                            if (isExpanded) "收起" else "展开"
                        },
                        isExpanded = expanded,
                        body = {
                            Text(
                                text = SAMPLE_CONTENT,
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    )
                }
            )
        }

        // Accordion mode
        ExampleSection(
            title = "Accordion 手风琴式",
            description = "同时只展开一个面板"
        ) {
            Collapse.Accordion(
                style = CollapseStyle.Block,
                children = (0 until 5).map { index ->
                    CollapsePanel(
                        value = index,
                        headerBuilder = { _ ->
                            Text(
                                text = "标题 $index",
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        },
                        body = {
                            Text(
                                text = SAMPLE_CONTENT,
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    )
                }
            )
        }

        // ========== Component styles ==========

        // Block style
        ExampleSection(
            title = "Block Style 通栏样式",
            description = "默认通栏风格，无边距"
        ) {
            Collapse(
                style = CollapseStyle.Block,
                expansionCallback = { index, isExpanded ->
                    blockExpandedList = blockExpandedList.toMutableList().apply {
                        this[index] = !isExpanded
                    }
                },
                children = blockExpandedList.mapIndexed { index, expanded ->
                    CollapsePanel(
                        headerBuilder = { _ ->
                            Text(
                                text = "标题 $index",
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        },
                        isExpanded = expanded,
                        body = {
                            Text(
                                text = SAMPLE_CONTENT,
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    )
                }
            )
        }

        // Card style
        ExampleSection(
            title = "Card Style 卡片样式",
            description = "卡片风格，有圆角和边距"
        ) {
            Collapse(
                style = CollapseStyle.Card,
                expansionCallback = { index, isExpanded ->
                    cardExpandedList = cardExpandedList.toMutableList().apply {
                        this[index] = !isExpanded
                    }
                },
                children = cardExpandedList.mapIndexed { index, expanded ->
                    CollapsePanel(
                        headerBuilder = { _ ->
                            Text(
                                text = "标题 $index",
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        },
                        isExpanded = expanded,
                        body = {
                            Text(
                                text = SAMPLE_CONTENT,
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    )
                }
            )
        }
    }
}
