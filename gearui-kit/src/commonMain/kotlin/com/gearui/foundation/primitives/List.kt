package com.gearui.foundation.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.scroll.*
import com.gearui.components.cellgroup.separatorBeforeRow
import com.gearui.foundation.list.CellDefaults
import com.gearui.primitives.Divider
import com.gearui.overlay.OverlayManager
import kotlinx.coroutines.delay

/**
 * ListScope — the DSL of [List].
 *
 * [List] is **the scrolling container**, not a row style: it lays rows out lazily and
 * says nothing about how they look. Put [com.gearui.components.cell.Cell]s in it, the
 * same rows a [com.gearui.components.cellgroup.CellGroup] holds; the group is the card
 * around a short, fixed set of rows, this is the list that scrolls.
 *
 * DSL API
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
    private val divider: Boolean,
) : ListScope {

    /**
     * Rows emitted so far in this run. Separators go before every row but the first,
     * the same rule CellGroup applies, so a list and a group of the same rows are
     * ruled the same way — and a section starts a fresh run, because its header is
     * already the break.
     */
    private var emitted = 0

    override fun item(
        key: Any?,
        content: @Composable () -> Unit
    ) {
        val index = emitted++
        lazy.item(key) {
            if (divider && separatorBeforeRow(index)) Divider(insetStart = CellDefaults.Default.paddingHorizontal)
            content()
        }
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        val first = emitted
        emitted += count
        lazy.items(
            count = count,
            key = key
        ) { index ->
            if (divider && separatorBeforeRow(first + index)) Divider(insetStart = CellDefaults.Default.paddingHorizontal)
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

        // A fresh run: the header separates this section from the one above it, and the
        // first row under a header must not carry a line.
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
 * - consistent spacing, and separators when the tokens ask for them
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
