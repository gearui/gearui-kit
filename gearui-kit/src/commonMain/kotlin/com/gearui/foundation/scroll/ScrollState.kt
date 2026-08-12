package com.gearui.foundation.scroll

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.ExperimentalFoundationApi
import com.tencent.kuikly.compose.foundation.lazy.LazyListState

/**
 * ScrollState - ScrollView state wrapper
 *
 * Purpose:
 * - isolates the underlying KuiklyUI API
 * - leaves room to swap the underlying engine later
 * - offers one scroll control interface
 *
 * Note: KuiklyUI has no ScrollState, so this is built on LazyListState
 */
@Stable
class ScrollState internal constructor(
    val raw: LazyListState
) {
    /** index of the first visible item */
    val firstVisibleItemIndex: Int get() = raw.firstVisibleItemIndex

    /** scroll offset of the first visible item */
    val firstVisibleItemScrollOffset: Int get() = raw.firstVisibleItemScrollOffset

    /** whether it is scrolling */
    val isScrollInProgress: Boolean get() = raw.isScrollInProgress

    suspend fun scrollToTop() = raw.scrollToItem(0)

    suspend fun scrollToItem(index: Int) = raw.scrollToItem(index)
}

/**
 * Creates and remembers a ScrollState
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberScrollState(): ScrollState {
    return remember { ScrollState(LazyListState()) }
}

/**
 * ListState - List state wrapper
 */
@Stable
class ListState internal constructor(
    val raw: LazyListState
) {
    /** index of the first visible item */
    val firstVisibleItemIndex: Int get() = raw.firstVisibleItemIndex

    /** scroll offset of the first visible item */
    val firstVisibleItemScrollOffset: Int get() = raw.firstVisibleItemScrollOffset

    /** whether it is scrolling */
    val isScrollInProgress: Boolean get() = raw.isScrollInProgress

    suspend fun scrollToTop() = raw.scrollToItem(0)

    suspend fun scrollToItem(index: Int) = raw.scrollToItem(index)
}

/**
 * Creates and remembers a ListState
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberListState(): ListState {
    return remember { ListState(LazyListState()) }
}
