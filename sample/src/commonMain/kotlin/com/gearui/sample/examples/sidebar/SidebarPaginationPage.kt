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
 * SideBar 切页用法页面
 *
 * 特点：
 * - 点击侧边栏项，直接切换到对应页面内容
 * - 不需要滚动联动
 */
@Composable
fun SidebarPaginationPage(onBack: () -> Unit) {
    val colors = Theme.colors

    // 当前选中的索引
    var selectedIndex by remember { mutableStateOf(1) }

    // 生成数据
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
            // 左侧侧边栏
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                style = SidebarStyle.NORMAL
            )

            // 右侧内容区 - 切页内容
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
