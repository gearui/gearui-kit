package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import kotlinx.coroutines.launch

/**
 * SideBar 带图标侧边导航页面
 *
 * 特点：
 * - 侧边栏项带有图标
 * - 与锚点用法类似的滚动联动
 */
@Composable
fun SidebarIconPage(onBack: () -> Unit) {
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

    // 生成数据
    val items = remember {
        (0..19).map { index ->
            SidebarItemData(
                index = index,
                label = "选项$index",
                value = index,
                icon = icons[index % icons.size],
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null
            )
        }
    }

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
        title = "SideBar 带图标侧边栏",
        onBack = onBack
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧侧边栏（带图标）
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL,
                showIcon = true
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
