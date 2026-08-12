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
import com.gearui.foundation.layout.Spacing
import kotlinx.coroutines.launch

/**
 * SideBar unselected colour customisation page
 *
 * Features:
 * - custom text colour for unselected items (red)
 * - sidebar with icons
 * - children can be updated at runtime
 */
@Composable
fun SidebarUnselectedColorPage(onBack: () -> Unit) {
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

    // Data version (used to trigger updates)
    var dataVersion by remember { mutableStateOf(0) }

    // Builds the data
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

    // Custom unselected text colour
    val customUnSelectedTextColor = Color(0xFFFF0000) // 红色

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
        title = "SideBar 自定义未选中颜色",
        onBack = onBack,
        topContent = {
            // Test buttons at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(Spacing.lg)
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
            // Sidebar on the left (with icons and a custom unselected colour)
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL,
                showIcon = true,
                unSelectedTextColor = customUnSelectedTextColor
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
