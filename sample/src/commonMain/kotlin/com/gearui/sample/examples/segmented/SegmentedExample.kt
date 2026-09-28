package com.gearui.sample.examples.segmented

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.components.segmented.SegmentedControl
import com.gearui.components.segmented.IconSegmentedControl
import com.gearui.components.segmented.SegmentedOption
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/** Fixed width for the compact two-option control: the width is the demo. */
private val CompactControlWidth = 200.dp

/** Option values can be any type; an enum keeps the call site type-safe. */
private enum class SortOrder(val label: String) {
    ASC("升序"),
    DESC("降序"),
    NONE("默认"),
}

private val PERIOD_LABELS = mapOf("daily" to "每日", "weekly" to "每周", "monthly" to "每月")

private val STATUS_LABELS = mapOf(
    "all" to "全部",
    "pending" to "待处理",
    "processing" to "进行中",
    "completed" to "已完成",
    "cancelled" to "已取消",
)

private val CONTENT_LABELS = mapOf("intro" to "简介", "features" to "功能", "pricing" to "价格")

/**
 * Segmented component examples
 *
 * A segmented control, for switching between options
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
        ExampleSection(
            title = "基础用法",
            description = "labelProvider 把选项值转成显示文本"
        ) {
            var selectedOption by remember { mutableStateOf("daily") }

            SegmentedControl(
                options = PERIOD_LABELS.keys.toList(),
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                labelProvider = { PERIOD_LABELS[it] ?: it },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "当前选择:${PERIOD_LABELS[selectedOption]}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }

        ExampleSection(
            title = "固定宽度",
            description = "二选一时可给定宽度，不必撑满"
        ) {
            var selectedOption by remember { mutableStateOf("list") }

            SegmentedControl(
                options = listOf("list", "grid"),
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                labelProvider = { if (it == "list") "列表" else "网格" },
                modifier = Modifier.width(CompactControlWidth)
            )
        }

        ExampleSection(
            title = "多个选项",
            description = "五个选项平分宽度"
        ) {
            var selectedTab by remember { mutableStateOf("all") }

            SegmentedControl(
                options = STATUS_LABELS.keys.toList(),
                selectedOption = selectedTab,
                onOptionSelected = { selectedTab = it },
                labelProvider = { STATUS_LABELS[it] ?: it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "自定义类型",
            description = "选项值可以是枚举等任意类型"
        ) {
            var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

            SegmentedControl(
                options = SortOrder.entries,
                selectedOption = sortOrder,
                onOptionSelected = { sortOrder = it },
                labelProvider = { it.label },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "排序方式:${sortOrder.name}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }

        ExampleSection(
            title = "禁用状态",
            description = "enabled = false 时整个控件不可交互"
        ) {
            var selectedOption by remember { mutableStateOf("option1") }

            SegmentedControl(
                options = listOf("option1", "option2", "option3"),
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                labelProvider = { it.replace("option", "选项 ") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "带图标选项",
            description = "IconSegmentedControl 在文字前放图标"
        ) {
            var selectedView by remember { mutableStateOf("card") }

            IconSegmentedControl(
                options = listOf(
                    SegmentedOption(value = "card", label = "卡片", icon = Icons.square),
                    SegmentedOption(value = "list", label = "列表", icon = Icons.list),
                    SegmentedOption(value = "image", label = "图片", icon = Icons.image)
                ),
                selectedOption = selectedView,
                onOptionSelected = { selectedView = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "切换内容",
            description = "选中项决定下方显示的内容"
        ) {
            var activeTab by remember { mutableStateOf("intro") }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                SegmentedControl(
                    options = CONTENT_LABELS.keys.toList(),
                    selectedOption = activeTab,
                    onOptionSelected = { activeTab = it },
                    labelProvider = { CONTENT_LABELS[it] ?: it },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = when (activeTab) {
                        "intro" -> "GearUI 是一个现代化的 Compose 组件库，提供丰富的 UI 组件。"
                        "features" -> "支持主题定制、响应式布局、完整的组件体系、优秀的开发体验。"
                        else -> "完全开源免费，欢迎社区贡献。"
                    },
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )
            }
        }
    }
}
