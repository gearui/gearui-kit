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
 * Declarative API:
 * - item() - a single list item
 * - items() - a batch of list items
 * - section() - a group (header + items)
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
 * ListScopeImpl - core of the DSL implementation
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
 * List - virtualised list primitive
 *
 * Equivalent in role to:
 * - Material3: LazyColumn
 * - Flutter: ListView
 * - Ant Design: List
 *
 * Responsibilities:
 * - virtualised rendering (performance)
 * - consistent spacing / dividers
 * - section support
 * - consistent physics
 * - telling Overlays to dismiss on scroll
 *
 * Use cases:
 * - Gallery
 * - chat message lists
 * - settings lists
 * - feeds
 * - any long list
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

    // Polls for scroll offset changes
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
