package com.gearui.primitives

import androidx.compose.runtime.*

/**
 * TabState - navigation state
 *
 * Responsibilities:
 * - holds the selected Tab index
 * - offers the selection operation
 *
 * Design rules:
 * - keeps selectedIndex out of product code
 * - selection state lives in one place
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
