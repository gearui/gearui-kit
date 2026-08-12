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
 * SideBar with icons page
 *
 * Features:
 * - sidebar items carry icons
 * - scroll linkage like the anchor usage
 */
@Composable
fun SidebarIconPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // Currently selected index
    var selectedIndex by remember { mutableStateOf(1) }

    // Scroll state of the content on the right
    val contentListState = rememberLazyListState(initialFirstVisibleItemIndex = 1)

    // Locks the scroll listener
    var scrollLock by remember { mutableStateOf(false) }

    // Icon list
    val icons = listOf("🏠", "📂", "🛒", "📋", "👤", "⚙️", "❓", "ℹ️", "🔔", "❤️",
        "⭐", "📌", "🎯", "💡", "🔧", "📊", "📈", "🗂️", "📁", "🔍")

    // Builds the data
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

    // Watches the content scrolling on the right
    LaunchedEffect(contentListState.firstVisibleItemIndex) {
        if (!scrollLock) {
            val newIndex = contentListState.firstVisibleItemIndex
            if (newIndex != selectedIndex && newIndex in items.indices) {
                selectedIndex = newIndex
            }
        }
    }

    // Sidebar item tap handling
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
            // Sidebar on the left (with icons)
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL,
                showIcon = true
            )

            // Content area on the right
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

                // Bottom blank space
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}
