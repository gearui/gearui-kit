package com.gearui.sample.examples.segmented

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.segmented.SegmentedControl
import com.gearui.components.segmented.IconSegmentedControl
import com.gearui.components.segmented.SegmentedOption
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Segmented 组件示例
 *
 * 分段控制器，用于选项切换
 */
@Composable
fun SegmentedExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础分段控制器
        ExampleSection(
            title = "基础用法",
            description = "简单的选项切换"
        ) {
            var selectedOption by remember { mutableStateOf("daily") }

            val options = listOf("daily", "weekly", "monthly")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SegmentedControl(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    labelProvider = { option ->
                        when (option) {
                            "daily" -> "每日"
                            "weekly" -> "每周"
                            "monthly" -> "每月"
                            else -> option
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "当前选择: $selectedOption",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 两个选项
        ExampleSection(
            title = "两个选项",
            description = "简单的二选一切换"
        ) {
            var selectedOption by remember { mutableStateOf("list") }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SegmentedControl(
                    options = listOf("list", "grid"),
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    labelProvider = { if (it == "list") "列表" else "网格" },
                    modifier = Modifier.width(200.dp)
                )

                Text(
                    text = "显示模式: ${if (selectedOption == "list") "列表视图" else "网格视图"}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 多个选项
        ExampleSection(
            title = "多个选项",
            description = "支持多个选项切换"
        ) {
            var selectedTab by remember { mutableStateOf("all") }

            val tabs = listOf("all", "pending", "processing", "completed", "cancelled")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SegmentedControl(
                    options = tabs,
                    selectedOption = selectedTab,
                    onOptionSelected = { selectedTab = it },
                    labelProvider = { tab ->
                        when (tab) {
                            "all" -> "全部"
                            "pending" -> "待处理"
                            "processing" -> "进行中"
                            "completed" -> "已完成"
                            "cancelled" -> "已取消"
                            else -> tab
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "当前筛选: $selectedTab",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 自定义对象类型
        ExampleSection(
            title = "自定义对象类型",
            description = "使用自定义对象作为选项值"
        ) {
            var sortOrder by remember { mutableStateOf("none") }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SegmentedControl(
                    options = listOf("asc", "desc", "none"),
                    selectedOption = sortOrder,
                    onOptionSelected = { sortOrder = it },
                    labelProvider = { order ->
                        when (order) {
                            "asc" -> "升序"
                            "desc" -> "降序"
                            "none" -> "默认"
                            else -> order
                        }
                    },
                    modifier = Modifier.width(240.dp)
                )

                Text(
                    text = "排序方式: $sortOrder",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "整个控制器不可交互"
        ) {
            var selectedOption by remember { mutableStateOf("option1") }

            SegmentedControl(
                options = listOf("option1", "option2", "option3"),
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                labelProvider = { it.replace("option", "选项 ") },
                enabled = false,
                modifier = Modifier.width(280.dp)
            )
        }

        // 带图标的分段控制器
        ExampleSection(
            title = "带图标选项",
            description = "选项可包含图标"
        ) {
            var selectedView by remember { mutableStateOf("card") }

            val viewOptions = listOf(
                SegmentedOption(
                    value = "card",
                    label = "卡片",
                    icon = null
                ),
                SegmentedOption(
                    value = "list",
                    label = "列表",
                    icon = null
                ),
                SegmentedOption(
                    value = "table",
                    label = "表格",
                    icon = null
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IconSegmentedControl(
                    options = viewOptions,
                    selectedOption = selectedView,
                    onOptionSelected = { selectedView = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "视图模式: $selectedView",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 实际应用示例
        ExampleSection(
            title = "实际应用",
            description = "切换内容显示"
        ) {
            var activeTab by remember { mutableStateOf("intro") }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SegmentedControl(
                    options = listOf("intro", "features", "pricing"),
                    selectedOption = activeTab,
                    onOptionSelected = { activeTab = it },
                    labelProvider = { tab ->
                        when (tab) {
                            "intro" -> "简介"
                            "features" -> "功能"
                            "pricing" -> "价格"
                            else -> tab
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 根据选项显示不同内容
                when (activeTab) {
                    "intro" -> Text(
                        text = "GearUI 是一个现代化的 Compose 组件库，提供丰富的 UI 组件。",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    "features" -> Text(
                        text = "支持主题定制、响应式布局、完整的组件体系、优秀的开发体验。",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    "pricing" -> Text(
                        text = "完全开源免费，欢迎社区贡献。",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "SegmentedControl 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. options: 选项列表，支持任意类型",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. selectedOption: 当前选中的选项",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. labelProvider: 将选项值转换为显示文本",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. enabled: 控制整体是否可交互",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. IconSegmentedControl: 支持带图标的选项",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
