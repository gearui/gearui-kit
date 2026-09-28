package com.gearui.sample.examples.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.layout.Spacing
import com.gearui.components.cell.Cell
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
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

/** Viewport heights for the pager demos: the pages need a bounded height to swipe. */
private val SwipePagerHeight = 280.dp
private val NestedListPagerHeight = 360.dp

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
            title = "等分标签",
            description = "标签数越多，单个标签可容纳的字数越少"
        ) {
            TabsDemoRow(listOf("选项", "选项"))
            TabsDemoRow(listOf("选项", "选项", "上限六个字"))
            TabsDemoRow(listOf("选项", "选项", "选项", "上限四字"))
            TabsDemoRow(listOf("选项", "选项", "选项", "选项", "上限三"))
        }

        ExampleSection(
            title = "可滚动",
            description = "isScrollable 让超出宽度的标签横向滚动"
        ) {
            TabsDemoRow(
                labels = List(8) { "选项${it + 1}" },
                isScrollable = true
            )
        }

        ExampleSection(
            title = "带图标",
            description = "Tab 的 icon 显示在文字前"
        ) {
            TabsDemoItemsRow(
                items = listOf(
                    Tab("icon-1", "首页", icon = Icons.house),
                    Tab("icon-2", "通知", icon = Icons.bell),
                    Tab("icon-3", "我的", icon = Icons.user)
                )
            )
        }

        ExampleSection(
            title = "徽标",
            description = "badge 显示数字，超过 99 显示 99+；dot 显示红点；下划线样式为角标，胶囊与卡片样式放在文字右侧"
        ) {
            val badgeItems = listOf(
                Tab("badge-1", "消息", badge = 8),
                Tab("badge-2", "通知", badge = 120),
                Tab("badge-3", "动态", dot = true),
                Tab("badge-4", "我的")
            )
            TabsDemoItemsRow(items = badgeItems)
            TabsDemoItemsRow(items = badgeItems, outlineType = TabsOutlineType.CAPSULE, showDivider = false)
            TabsDemoItemsRow(
                items = badgeItems + List(4) { Tab("badge-more-$it", "选项${it + 5}") },
                isScrollable = true,
                outlineType = TabsOutlineType.CARD,
                showDivider = false
            )
        }

        ExampleSection(
            title = "切换内容",
            description = "选中的标签决定下方显示的内容"
        ) {
            var contentSelected by remember { mutableStateOf("tab-0") }
            Tabs(
                items = listOf(
                    Tab("tab-0", "选项 1"),
                    Tab("tab-1", "选项 2"),
                    Tab("tab-2", "选项 3")
                ),
                selectedId = contentSelected,
                onSelect = { contentSelected = it }
            )
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

        ExampleSection(
            title = "左右滑动切换",
            description = "TabPager 与 Tabs 双向联动，滑动页面或点标签都能切换"
        ) {
            val swipeTabs = listOf(
                Tab("swipe-0", "好友"),
                Tab("swipe-1", "群组"),
                Tab("swipe-2", "第三页")
            )
            var swipeSelected by remember { mutableStateOf(0) }
            // Each page gets a distinct fill so the swipe is visible.
            val pageColors = listOf(colors.primary to colors.primaryForeground, colors.muted to colors.foreground, colors.foreground to colors.background)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwipePagerHeight)
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
                            .background(pageColors[page].first),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "页面 ${page + 1}",
                            style = Theme.typography.bodyMedium,
                            color = pageColors[page].second
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "内嵌竖向列表",
            description = "第一页 30 项可竖向滚动，第二页 2 项不滚动，横滑不受影响"
        ) {
            val listTabs = listOf(
                Tab("list-0", "好友"),
                Tab("list-1", "群组")
            )
            var listSelected by remember { mutableStateOf(0) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NestedListPagerHeight)
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
                            Cell(title = (if (page == 0) "好友 " else "群组 ") + (i + 1))
                        }
                    }
                }
            }
        }

        ExampleSection(
            title = "组件状态",
            description = "选中、默认、禁用"
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
            title = "尺寸与外观",
            description = "TabsSize 三档尺寸，TabsOutlineType 胶囊与卡片外观"
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
