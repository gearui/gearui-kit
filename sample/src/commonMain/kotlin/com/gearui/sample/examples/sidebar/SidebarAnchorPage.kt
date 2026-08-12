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
import com.gearui.foundation.layout.Spacing
import kotlinx.coroutines.launch

/**
 * SideBar anchor usage page
 *
 * Features:
 * - the sidebar on the left is anchored to the content on the right
 * - tapping a sidebar item scrolls the content to the matching position
 * - scrolling the content selects the matching sidebar item
 */
@Composable
fun SidebarAnchorPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // Currently selected index
    var selectedIndex by remember { mutableStateOf(1) }

    // Scroll state of the content on the right
    val contentListState = rememberLazyListState(initialFirstVisibleItemIndex = 1)

    // Locks the scroll listener (so tapping to select does not trigger it)
    var scrollLock by remember { mutableStateOf(false) }

    // Builds the data
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

    // Watches the content scrolling and updates the selected item
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
        title = "SideBar 锚点用法",
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
                        // Simulate a data update
                    },
                    size = ButtonSize.MEDIUM,
                    block = true
                )
            }
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar on the left
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = ::onItemSelected,
                style = SidebarStyle.NORMAL
            )

            // Content area on the right - anchor content
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

                // Bottom blank space, so the last item can scroll to the top
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}
