package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.theme.Theme

/**
 * SideBar 自定义样式页面
 *
 * 特点：
 * - 自定义选中/未选中背景颜色
 * - 自定义选中/未选中文字颜色
 * - 自定义单个项目的文字颜色
 */
@Composable
fun SidebarCustomPage(onBack: () -> Unit) {
    val colors = Theme.colors

    // 当前选中的索引
    var selectedIndex by remember { mutableStateOf(1) }

    // 自定义颜色
    val customSelectedBgColor = Color(0xFF0066FF)    // 蓝色背景
    val customUnSelectedBgColor = Color(0xFFFFEB3B)  // 黄色背景
    val customSelectedTextColor = Color(0xFFFF0000)   // 红色文字
    val customUnSelectedTextColor = colors.primary    // 品牌色文字

    // 生成数据
    val items = remember {
        (0..99).map { index ->
            SidebarItemData(
                index = index,
                label = "选项 $index",
                value = index,
                showDot = index == 1,
                badgeCount = if (index == 2) "8" else null,
                // 单独设置第1项的文字颜色为绿色
                textColor = if (index == 1) Color(0xFF00FF00) else null
            )
        }
    }

    SidebarSubPageLayout(
        title = "SideBar 自定义样式",
        onBack = onBack
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧侧边栏（自定义颜色）
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

            // 右侧内容区 - 自定义背景色
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
