package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.theme.Theme
import com.gearui.Spacing
import kotlinx.coroutines.launch

/**
 * SideBar 锚点用法页面
 *
 * 特点：
 * - 左侧侧边栏与右侧内容区锚点联动
 * - 点击侧边栏项，右侧内容滚动到对应位置
 * - 右侧内容滚动时，左侧侧边栏自动选中对应项
 */
@Composable
fun SidebarAnchorPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // 当前选中的索引
    var selectedIndex by remember { mutableStateOf(1) }

    // 右侧内容滚动状态
    val contentListState = rememberLazyListState(initialFirstVisibleItemIndex = 1)

    // 锁定滚动监听（防止点击选择时触发滚动监听）
    var scrollLock by remember { mutableStateOf(false) }

    // 生成数据
    val items = remember {
        (0..19).map { index ->
            SidebarItemData(
                index = index,
                label = "选项$index",
                value = index,
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null
            )
        }
    }

    // 监听右侧内容滚动，更新左侧选中项
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
        title = "SideBar 锚点用法",
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
                        // 模拟更新数据
                    },
                    size = ButtonSize.MEDIUM,
                    block = true
                )
            }
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧侧边栏
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL
            )

            // 右侧内容区 - 锚点内容
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

                // 底部留白，确保最后一项可以滚动到顶部
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}
