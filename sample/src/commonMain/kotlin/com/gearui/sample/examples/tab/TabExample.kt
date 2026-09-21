package com.gearui.sample.examples.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.layout.Spacing
import com.gearui.components.icon.Icons
import com.gearui.components.tabs.Tabs
import com.gearui.components.tabs.Tab
import com.gearui.components.tabs.TabPager
import com.gearui.components.tabs.TabsOutlineType
import com.gearui.components.tabs.TabsSize
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
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
                    Tab("icon-1", "选项1", icon = Icons.house),
                    Tab("icon-2", "选项2", icon = Icons.list),
                    Tab("icon-3", "选项3", icon = Icons.user)
                )
            )
            TabsDemoItemsRow(
                items = listOf(
                    Tab("badge-1", "选项"),
                    Tab("badge-2", "选项(8)", icon = Icons.bell),
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
                    .background(colors.muted),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (contentSelected) {
                        "tab-0" -> "内容区 1"
                        "tab-1" -> "内容区 2"
                        else -> "内容区 3"
                    },
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )
            }
        }

        ExampleSection(
            title = "左右滑动切换 (TabPager)",
            description = "验证 tab 与页面左右滑动双向联动：可从任意页滑到相邻页并回滑。"
        ) {
            val swipeTabs = listOf(
                Tab("swipe-0", "好友"),
                Tab("swipe-1", "群组"),
                Tab("swipe-2", "第三页")
            )
            var swipeSelected by remember { mutableStateOf(0) }
            val pageColors = listOf(colors.primary, colors.muted, colors.foreground)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Tabs(
                    items = swipeTabs,
                    selectedId = swipeTabs[swipeSelected].id,
                    onSelect = { id -> swipeSelected = swipeTabs.indexOfFirst { it.id == id } }
                )
                TabPager(
                    count = swipeTabs.size,
                    selectedIndex = swipeSelected,
                    onSelectedIndexChange = { swipeSelected = it },
                    modifier = Modifier.weight(1f)
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(pageColors[page]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "页面 ${page + 1}",
                            style = Theme.typography.bodyMedium,
                            color = colors.background
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "TabPager + 内嵌竖向列表 (复刻联系人页)",
            description = "两页：第一页竖向可滚（30 项）、第二页不可滚（2 项），复刻 ContactPage 结构。"
        ) {
            val listTabs = listOf(
                Tab("list-0", "好友"),
                Tab("list-1", "群组")
            )
            var listSelected by remember { mutableStateOf(0) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                Tabs(
                    items = listTabs,
                    selectedId = listTabs[listSelected].id,
                    onSelect = { id -> listSelected = listTabs.indexOfFirst { it.id == id } }
                )
                TabPager(
                    count = listTabs.size,
                    selectedIndex = listSelected,
                    onSelectedIndexChange = { listSelected = it },
                    modifier = Modifier.weight(1f)
                ) { page ->
                    GearLazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(if (page == 0) 30 else 2) { i ->
                            Text(
                                text = (if (page == 0) "好友 " else "群组 ") + (i + 1),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.sm),
                                style = Theme.typography.bodyMedium,
                                color = colors.foreground
                            )
                        }
                    }
                }
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
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
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
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
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
