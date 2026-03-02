package com.gearui.foundation.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.scroll.*
import com.gearui.overlay.OverlayManager
import kotlinx.coroutines.delay

/**
 * ListScope - List DSL API
 *
 * 提供声明式 API:
 * - item() - 单个列表项
 * - items() - 批量列表项
 * - section() - 分组 (header + items)
 */
interface ListScope {
    fun item(
        key: Any? = null,
        content: @Composable () -> Unit
    )

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (index: Int) -> Unit
    )

    fun section(
        header: @Composable () -> Unit,
        content: ListScope.() -> Unit
    )
}

/**
 * ListScopeImpl - DSL 实现核心
 */
internal class ListScopeImpl(
    private val lazy: LazyListScope,
    private val divider: Boolean
) : ListScope {

    override fun item(
        key: Any?,
        content: @Composable () -> Unit
    ) {
        lazy.item(key) {
            content()
        }
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        lazy.items(
            count = count,
            key = key
        ) { index ->
            itemContent(index)
        }
    }

    override fun section(
        header: @Composable () -> Unit,
        content: ListScope.() -> Unit
    ) {
        lazy.item {
            header()
        }

        val child = ListScopeImpl(lazy, divider)
        child.content()
    }
}

/**
 * List - 虚拟化列表 Primitive
 *
 * 地位等价于:
 * - Material3: LazyColumn
 * - Flutter: ListView
 * - Ant Design: List
 *
 * 职责:
 * - 虚拟化渲染 (性能)
 * - 统一 spacing/divider
 * - Section 支持
 * - 统一 physics
 * - 滚动时通知 Overlay 关闭
 *
 * 使用场景:
 * - Gallery
 * - Chat 消息列表
 * - Settings 列表
 * - Feed 流
 * - 任何长列表
 */
@Composable
fun List(
    modifier: Modifier = Modifier,
    state: ListState = rememberListState(),
    tokens: ListTokens = ListTokens.Default,
    physics: ScrollPhysics = ScrollPhysics.Platform,
    content: ListScope.() -> Unit
) {
    var lastOffset by remember { mutableStateOf(0) }

    // 轮询监听 scroll offset 变化
    LaunchedEffect(state) {
        while (true) {
            val offset = state.firstVisibleItemIndex * 10000 + state.firstVisibleItemScrollOffset

            if (offset != lastOffset) {
                lastOffset = offset
                OverlayManager.notifyScroll()
            }

            delay(16) // 60fps
        }
    }

    LazyColumn(
        modifier = modifier.then(physics.modifier()),
        state = state.raw,
        contentPadding = tokens.contentPadding,
        verticalArrangement = Arrangement.spacedBy(tokens.itemSpacing)
    ) {
        val scope = ListScopeImpl(
            lazy = this,
            divider = tokens.divider
        )
        scope.content()
    }
}
