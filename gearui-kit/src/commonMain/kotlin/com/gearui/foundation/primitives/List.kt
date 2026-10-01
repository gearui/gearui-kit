package com.gearui.foundation.primitives

import com.gearui.foundation.motion.collectIsShownPressedAsState
import kotlinx.coroutines.flow.drop
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.scroll.*
import com.gearui.components.cellgroup.separatorBeforeRow
import com.gearui.components.cellgroup.separatorCoveredByPress
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.list.LocalRowInteractionSource
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.gearui.foundation.list.CellDefaults
import com.gearui.primitives.Divider
import com.gearui.overlay.OverlayManager

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
    /**
     * Which row is under a finger, shared by the whole run. A row can hide the line
     * above it on its own, but the line below it is drawn by the next row, so the two
     * have to agree — the same rule CellGroup applies, where the container sees them all.
     */
    private val pressedRow: MutableState<Int> = mutableStateOf(NO_ROW),
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
            Row(index) { content() }
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
            Row(first + index) { itemContent(index) }
        }
    }

    /**
     * One row and the separator above it. The separator is filled with the press colour
     * instead of ruled when it touches the pressed row, so the highlight is not cut in
     * two — and the row gets the source the container is watching.
     */
    @Composable
    private fun Row(index: Int, content: @Composable () -> Unit) {
        val interaction = remember { MutableInteractionSource() }
        val pressed by interaction.collectIsShownPressedAsState()
        LaunchedEffect(pressed) {
            if (pressed) pressedRow.value = index
            else if (pressedRow.value == index) pressedRow.value = NO_ROW
        }
        if (divider && separatorBeforeRow(index)) {
            val covered = separatorCoveredByPress(index) { pressedRow.value == it }
            if (covered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ControlGeometry.separatorThickness)
                        .background(Theme.colors.muted)
                )
            } else {
                Divider(insetStart = CellDefaults.Default.paddingHorizontal)
            }
        }
        CompositionLocalProvider(LocalRowInteractionSource provides interaction) {
            content()
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
        val child = ListScopeImpl(lazy, divider, pressedRow)
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
    val pressedRow = remember { mutableStateOf(NO_ROW) }

    // Anchored overlays close when the list under them scrolls. This used to poll the
    // offset every 16 ms for as long as the list was on screen — sixty wake-ups a
    // second on the main thread for a list that was not moving. The scroll position
    // is snapshot state, so observe it instead: nothing runs until it changes.
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
            .drop(1) // the position the list opened at is not a scroll
            .collect { OverlayManager.notifyScroll() }
    }

    LazyColumn(
        modifier = modifier.then(physics.modifier()),
        state = state.raw,
        contentPadding = tokens.contentPadding,
        verticalArrangement = Arrangement.spacedBy(tokens.itemSpacing)
    ) {
        val scope = ListScopeImpl(
            lazy = this,
            divider = tokens.divider,
            pressedRow = pressedRow,
        )
        scope.content()
    }
}

/** No row is pressed. */
private const val NO_ROW = -1
