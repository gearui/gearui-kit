package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.loading.Loading
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SideBar 延迟加载页面
 *
 * 特点：
 * - 初始显示加载状态
 * - 3秒后加载数据并显示侧边栏
 */
@Composable
fun SidebarLoadingPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // 加载状态
    var isLoading by remember { mutableStateOf(true) }

    // 当前选中的索引
    var selectedIndex by remember { mutableStateOf(1) }

    // 右侧内容滚动状态
    val contentListState = rememberLazyListState()

    // 锁定滚动监听
    var scrollLock by remember { mutableStateOf(false) }

    // 数据
    var items by remember { mutableStateOf<List<SidebarItemData>>(emptyList()) }

    // 模拟延迟加载
    LaunchedEffect(Unit) {
        delay(3000) // 延迟3秒
        items = (0..19).map { index ->
            SidebarItemData(
                index = index,
                label = "选项 $index",
                value = index,
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null
            )
        }
        isLoading = false

        // 初始滚动到选中项
        contentListState.scrollToItem(selectedIndex)
    }

    // 监听右侧内容滚动
    LaunchedEffect(contentListState.firstVisibleItemIndex, isLoading) {
        if (!scrollLock && !isLoading && items.isNotEmpty()) {
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
        title = "SideBar 延迟加载",
        onBack = onBack
    ) {
        if (isLoading) {
            // 加载中状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
                ) {
                    Loading()
                    Text(
                        text = "加载中...",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            // 加载完成，显示侧边栏
            Row(modifier = Modifier.fillMaxSize()) {
                // 左侧侧边栏
                Sidebar(
                    items = items,
                    selectedIndex = selectedIndex,
                    onItemSelected = ::onItemSelected,
                    style = SidebarStyle.NORMAL
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
}
