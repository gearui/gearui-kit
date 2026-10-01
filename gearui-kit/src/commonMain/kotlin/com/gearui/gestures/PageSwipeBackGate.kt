package com.gearui.gestures

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.composed
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned

/**
 * A page's answer to the app-level right-swipe-back gesture.
 *
 * The Navigator owns the left-to-right swipe that pops a route, but a page can
 * itself be horizontally swipeable (a [com.gearui.components.tabs.TabPager] that
 * is not on its first page). Both cannot consume the same drag. This gate is the
 * single arbitration point: the page publishes whether it can still move
 * backwards, and the Navigator's swipe-back stands down while it can, so the
 * page pages first and the router only takes over once the page reports it is
 * already at its leftmost (or has no horizontal paging at all).
 *
 * The predicate is read at gesture-down time, so it must answer from live page
 * state (e.g. `pagerState.currentPage > 0`), not a captured snapshot.
 */
class PageSwipeBackGate {
    /** true = the page owns the right-swipe (it has a previous page to go back to). */
    var canSwipeBack: () -> Boolean = { false }

    private class DragRegion(var bounds: Rect, var active: () -> Boolean)
    private val regions = mutableMapOf<Any, DragRegion>()

    internal fun claim(token: Any, bounds: Rect, active: () -> Boolean) {
        val region = regions[token]
        if (region == null) regions[token] = DragRegion(bounds, active)
        else { region.bounds = bounds; region.active = active }
    }

    internal fun release(token: Any) { regions.remove(token) }

    /** Whether a touch going down at [point] (root coordinates) lands on a control that drags horizontally. */
    fun ownsDragAt(point: Offset): Boolean = regions.values.any { it.active() && it.bounds.contains(point) }
}

/**
 * Marks a control that the finger drags sideways (a slider, a colour plane, a swipe
 * cell, a carousel). A drag that starts on it belongs to the control: the Navigator's
 * full-screen swipe-back does not take it, even when the page could otherwise go back.
 *
 * [active] is read when the finger goes down; a scroller passes "can scroll backwards",
 * so on its first item a right-swipe still goes back.
 */
fun Modifier.ownsHorizontalDrag(active: () -> Boolean = { true }): Modifier = composed {
    val gate = LocalPageSwipeBackGate.current
    if (gate == null) {
        this
    } else {
        val token = remember { Any() }
        val current by rememberUpdatedState(active)
        DisposableEffect(gate) { onDispose { gate.release(token) } }
        this.onGloballyPositioned { gate.claim(token, it.boundsInRoot()) { current() } }
    }
}

/**
 * Provided by the Navigator to each navigation layer. A horizontally swipeable
 * page writes its [PageSwipeBackGate.canSwipeBack] into the gate of its own
 * layer; the Navigator consults only the foreground layer's gate.
 *
 * Null outside a Navigator, so the same page composes fine standalone and the
 * swipe-back simply has no page to defer to.
 */
val LocalPageSwipeBackGate = compositionLocalOf<PageSwipeBackGate?> { null }
