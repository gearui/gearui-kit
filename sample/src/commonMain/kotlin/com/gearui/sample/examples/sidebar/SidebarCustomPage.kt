package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.theme.Theme

/**
 * SideBar custom style page
 *
 * Features:
 * - custom selected and unselected background colours
 * - custom selected and unselected text colours
 * - a custom text colour on one individual item
 */
@Composable
fun SidebarCustomPage(onBack: () -> Unit) {
    val colors = Theme.colors

    // Currently selected index
    var selectedIndex by remember { mutableStateOf(1) }

    // Custom colours
    val customSelectedBgColor = Color(0xFF0066FF)    // 蓝色背景
    val customUnSelectedBgColor = Color(0xFFFFEB3B)  // 黄色背景
    val customSelectedTextColor = Color(0xFFFF0000)   // 红色文字
    val customUnSelectedTextColor = colors.primary    // 品牌色文字

    // Builds the data
    val items = remember {
        (0..99).map { index ->
            SidebarItemData(
                index = index,
                label = "选项 $index",
                value = index,
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null,
                // Item 1 gets its own green text colour
                textColor = if (index == 1) Color(0xFF00FF00) else null
            )
        }
    }

    SidebarSubPageLayout(
        title = "SideBar 自定义样式",
        onBack = onBack
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar on the left (custom colours)
            Sidebar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                style = SidebarStyle.NORMAL,
                selectedBgColor = customSelectedBgColor,
                unSelectedBgColor = customUnSelectedBgColor,
                selectedTextColor = customSelectedTextColor,
                unSelectedTextColor = customUnSelectedTextColor
            )

            // Content area on the right - custom background colour
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(customSelectedBgColor)
            ) {
                PageGridContent(index = selectedIndex)
            }
        }
    }
}
