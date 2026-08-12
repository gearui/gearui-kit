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
import com.gearui.foundation.layout.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SideBar deferred loading page
 *
 * Features:
 * - shows a loading state at first
 * - loads the data and shows the sidebar after 3 seconds
 */
@Composable
fun SidebarLoadingPage(onBack: () -> Unit) {
    val colors = Theme.colors
    val coroutineScope = rememberCoroutineScope()

    // Loading state
    var isLoading by remember { mutableStateOf(true) }

    // Currently selected index
    var selectedIndex by remember { mutableStateOf(1) }

    // Scroll state of the content on the right
    val contentListState = rememberLazyListState()

    // Locks the scroll listener
    var scrollLock by remember { mutableStateOf(false) }

    // Data
    var items by remember { mutableStateOf<List<SidebarItemData>>(emptyList()) }

    // Simulated deferred loading
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

        // Scroll to the selected item initially
        contentListState.scrollToItem(selectedIndex)
    }

    // Watches the content scrolling on the right
    LaunchedEffect(contentListState.firstVisibleItemIndex, isLoading) {
        if (!scrollLock && !isLoading && items.isNotEmpty()) {
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
        title = "SideBar 延迟加载",
        onBack = onBack
    ) {
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    Loading()
                    Text(
                        text = "加载中...",
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                }
            }
        } else {
            // Loaded: show the sidebar
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar on the left
                Sidebar(
                    items = items,
                    selectedIndex = selectedIndex,
                    onItemSelected = ::onItemSelected,
                    style = SidebarStyle.NORMAL
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
}
