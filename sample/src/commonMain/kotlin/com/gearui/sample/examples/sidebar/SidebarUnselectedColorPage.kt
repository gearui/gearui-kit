package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.theme.Theme
import com.gearui.Spacing
import kotlinx.coroutines.launch

/**
 * SideBar 未选中颜色自定义页面
 *
 * 特点：
 * - 自定义未选中项的文字颜色（红色）
 * - 带图标的侧边栏
 * - 支持动态更新 children
 */
@Composable
fun SidebarUnselectedColorPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // 当前选中的索引
    var selectedIndex by remember { mutableStateOf(1) }

    // 右侧内容滚动状态
    val contentListState = rememberLazyListState(initialFirstVisibleItemIndex = 1)

    // 锁定滚动监听
    var scrollLock by remember { mutableStateOf(false) }

    // 图标列表
    val icons = listOf("🏠", "📂", "🛒", "📋", "👤", "⚙️", "❓", "ℹ️", "🔔", "❤️",
        "⭐", "📌", "🎯", "💡", "🔧", "📊", "📈", "🗂️", "📁", "🔍")

    // 数据版本（用于触发更新）
    var dataVersion by remember { mutableStateOf(0) }

    // 生成数据
    val items = remember(dataVersion) {
        val prefix = if (dataVersion == 0) "选项" else "变更"
        (0..19).map { index ->
            SidebarItemData(
                index = index,
                label = "$prefix$index",
                value = index,
                icon = icons[index % icons.size],
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null
            )
        }
    }

    // 自定义未选中文字颜色
    val customUnSelectedTextColor = Color(0xFFFF0000) // 红色

    // 监听右侧内容滚动
    LaunchedEffect(contentListState.firstVisibleItemIndex) {
        if (!scrollLock) {
            val newIndex = contentListState.firstVisibleItemIndex
            if (newIndex != selectedIndex && newIndex in items.indices) {
                selectedIndex = newIndex
            }
        }
    }

    // 点击侧边栏项的处理
    fun onItemSelected(index: Int) {
        if (selectedIndex == index) return
        selectedIndex = index

        coroutineScope.launch {
            scrollLock = true
            contentListState.animateScrollToItem(index)
            scrollLock = false
        }
    }

    SidebarSubPageLayout(
        title = "SideBar 自定义未选中颜色",
        onBack = onBack,
        topContent = {
            // 顶部测试按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(Spacing.spacer16.dp)
            ) {
                Button(
                    text = "更新 children",
                    onClick = {
                        dataVersion++
                    },
                    size = ButtonSize.MEDIUM,
                    block = true
                )
            }
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧侧边栏（带图标，自定义未选中颜色）
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL,
                showIcon = true,
                unSelectedTextColor = customUnSelectedTextColor
            )

            // 右侧内容区
            LazyColumn(
                state = contentListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.surface)
            ) {
                items(items.size) { index ->
                    ContentSection(index = index)
                }

                // 底部留白
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}
