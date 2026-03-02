package com.gearui.primitives

import androidx.compose.runtime.*

/**
 * TabState - 导航状态管理
 *
 * 职责：
 * - 管理当前选中的 Tab 索引
 * - 提供选中操作
 *
 * 设计原则：
 * - 避免在业务代码中到处写 selectedIndex
 * - 集中管理选中状态
 */
class TabState(
    initialIndex: Int = 0
) {
    var selectedIndex by mutableStateOf(initialIndex)
        private set

    fun select(index: Int) {
        selectedIndex = index
    }
}

@Composable
fun rememberTabState(
    initialIndex: Int = 0
) = remember { TabState(initialIndex) }
