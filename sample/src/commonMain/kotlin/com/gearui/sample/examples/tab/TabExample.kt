package com.gearui.sample.examples.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.Spacing
import com.gearui.components.icon.Icons
import com.gearui.components.tabs.Tabs
import com.gearui.components.tabs.Tab
import com.gearui.components.tabs.TabsOutlineType
import com.gearui.components.tabs.TabsSize
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun TabsExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "组件类型",
            description = "用于内容分类后的展示切换。"
        ) {
            TabsDemoRow(listOf("选项", "选项"))
            TabsDemoRow(listOf("选项", "选项", "上限六个字"))
            TabsDemoRow(listOf("选项", "选项", "选项", "上限四字"))
            TabsDemoRow(listOf("选项", "选项", "选项", "选项", "上限三"))
            TabsDemoRow(
                labels = listOf(
                    "选项", "选项", "选项", "选项", "选项", "选项", "选项", "选项"
                ),
                isScrollable = true
            )
            TabsDemoItemsRow(
                items = listOf(
                    Tab("icon-1", "选项1", icon = Icons.home),
                    Tab("icon-2", "选项2", icon = Icons.menu),
                    Tab("icon-3", "选项3", icon = Icons.person)
                )
            )
            TabsDemoItemsRow(
                items = listOf(
                    Tab("badge-1", "选项"),
                    Tab("badge-2", "选项(8)", icon = Icons.notifications),
                    Tab("badge-3", "选项", icon = Icons.info)
                )
            )

            var contentSelected by remember { mutableStateOf("tab-0") }
            val contentTabs = listOf(
                Tab("tab-0", "选项"),
                Tab("tab-1", "选项"),
                Tab("tab-2", "选项")
            )
            Tabs(
                items = contentTabs,
                selectedId = contentSelected,
                onSelect = { contentSelected = it }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (contentSelected) {
                        "tab-0" -> "内容区 1"
                        "tab-1" -> "内容区 2"
                        else -> "内容区 3"
                    },
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
            }
        }

        ExampleSection(
            title = "组件状态",
            description = "选中、默认、禁用状态。"
        ) {
            var selected by remember { mutableStateOf("selected") }
            Tabs(
                items = listOf(
                    Tab("selected", "选中"),
                    Tab("default", "默认"),
                    Tab("disabled", "禁用", disabled = true)
                ),
                selectedId = selected,
                onSelect = { selected = it }
            )
        }

        ExampleSection(
            title = "组件样式",
            description = "尺寸与外观变体。"
        ) {
            TabsDemoRow(
                labels = listOf("小尺寸", "选项2", "选项3", "选项4"),
                size = TabsSize.SMALL
            )
            TabsDemoRow(
                labels = listOf("大尺寸", "选项2", "选项3", "选项4"),
                size = TabsSize.LARGE
            )
            TabsDemoRow(
                labels = listOf("选项1", "选项2", "选项3", "选项4"),
                outlineType = TabsOutlineType.CAPSULE,
                showIndicator = false,
                showDivider = false
            )
            TabsDemoRow(
                labels = listOf("选项1", "选项2", "选项3", "选项4"),
                outlineType = TabsOutlineType.CARD,
                showIndicator = false,
                showDivider = false
            )
        }
    }
}

@Composable
private fun TabsDemoRow(
    labels: List<String>,
    isScrollable: Boolean = false,
    showIndicator: Boolean = true,
    showDivider: Boolean = true,
    size: TabsSize = TabsSize.MEDIUM,
    outlineType: TabsOutlineType = TabsOutlineType.UNDERLINE
) {
    val items = labels.mapIndexed { index, label ->
        Tab("item-$index", label)
    }
    var selected by remember { mutableStateOf(items.firstOrNull()?.id) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
    ) {
        Tabs(
            items = items,
            selectedId = selected,
            onSelect = { selected = it },
            isScrollable = isScrollable,
            showIndicator = showIndicator,
            showDivider = showDivider,
            size = size,
            outlineType = outlineType
        )
    }
}

@Composable
private fun TabsDemoItemsRow(
    items: List<Tab>,
    isScrollable: Boolean = false,
    showIndicator: Boolean = true,
    showDivider: Boolean = true,
    size: TabsSize = TabsSize.MEDIUM,
    outlineType: TabsOutlineType = TabsOutlineType.UNDERLINE
) {
    var selected by remember { mutableStateOf(items.firstOrNull()?.id) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
    ) {
        Tabs(
            items = items,
            selectedId = selected,
            onSelect = { selected = it },
            isScrollable = isScrollable,
            showIndicator = showIndicator,
            showDivider = showDivider,
            size = size,
            outlineType = outlineType
        )
    }
}
