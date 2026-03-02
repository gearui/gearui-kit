package com.gearui.foundation.scroll

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.ExperimentalFoundationApi
import com.tencent.kuikly.compose.foundation.lazy.LazyListState

/**
 * ScrollState - ScrollView 状态封装
 *
 * 目的:
 * - 隔离 KuiklyUI 底层 API
 * - 未来可替换底层引擎
 * - 提供统一的滚动控制接口
 *
 * 注意: KuiklyUI 不支持 ScrollState,使用 LazyListState 实现
 */
@Stable
class ScrollState internal constructor(
    val raw: LazyListState
) {
    /** 首个可见项索引 */
    val firstVisibleItemIndex: Int get() = raw.firstVisibleItemIndex

    /** 首个可见项滚动偏移 */
    val firstVisibleItemScrollOffset: Int get() = raw.firstVisibleItemScrollOffset

    /** 是否正在滚动 */
    val isScrollInProgress: Boolean get() = raw.isScrollInProgress

    suspend fun scrollToTop() = raw.scrollToItem(0)

    suspend fun scrollToItem(index: Int) = raw.scrollToItem(index)
}

/**
 * 创建并记住 ScrollState
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberScrollState(): ScrollState {
    return remember { ScrollState(LazyListState()) }
}

/**
 * ListState - List 状态封装
 */
@Stable
class ListState internal constructor(
    val raw: LazyListState
) {
    /** 首个可见项索引 */
    val firstVisibleItemIndex: Int get() = raw.firstVisibleItemIndex

    /** 首个可见项滚动偏移 */
    val firstVisibleItemScrollOffset: Int get() = raw.firstVisibleItemScrollOffset

    /** 是否正在滚动 */
    val isScrollInProgress: Boolean get() = raw.isScrollInProgress

    suspend fun scrollToTop() = raw.scrollToItem(0)

    suspend fun scrollToItem(index: Int) = raw.scrollToItem(index)
}

/**
 * 创建并记住 ListState
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberListState(): ListState {
    return remember { ListState(LazyListState()) }
}
