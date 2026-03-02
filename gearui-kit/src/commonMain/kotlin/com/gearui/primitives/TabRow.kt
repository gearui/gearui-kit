package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier

/**
 * TabRow - Tab 导航容器
 *
 * 职责：
 * - 横向排列 Tab 组件
 * - 提供统一的导航容器
 * - 管理 Tab 布局
 *
 * 设计原则：
 * - 声明式 API
 * - 与 TabState 配合使用
 * - 支持任意数量的 Tab
 *
 * 使用示例：
 * ```
 * val state = rememberTabState()
 * TabRow(state) { s ->
 *     Tab("Chats", s.selectedIndex == 0, { s.select(0) })
 *     Tab("Contacts", s.selectedIndex == 1, { s.select(1) })
 * }
 * ```
 */
@Composable
fun TabRow(
    state: TabState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(TabState) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        content(state)
    }
}
