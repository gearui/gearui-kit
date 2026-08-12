package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * SideBar page-switching usage page
 *
 * Features:
 * - tapping a sidebar item switches straight to that page's content
 * - no scroll linkage needed
 */
@Composable
fun SidebarPaginationPage(onBack: () -> Unit) {
    val colors = Theme.colors

    // Currently selected index
    var selectedIndex by remember { mutableStateOf(1) }

    // Builds the data
    val items = remember {
        (0..99).map { index ->
            SidebarItemData(
                index = index,
                label = "选项 $index",
                value = index,
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null
            )
        }
    }

    SidebarSubPageLayout(
        title = "SideBar 切页用法",
        onBack = onBack
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar on the left
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                style = SidebarStyle.NORMAL
            )

            // Content area on the right - the switched page
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.surface)
            ) {
                PageGridContent(index = selectedIndex)
            }
        }
    }
}
