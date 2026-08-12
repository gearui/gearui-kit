package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier

/**
 * TabRow - Tab navigation container
 *
 * Responsibilities:
 * - lays Tab components out horizontally
 * - provides one navigation container
 * - manages the Tab layout
 *
 * Design rules:
 * - declarative API
 * - used together with TabState
 * - any number of Tabs
 *
 * Example:
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
