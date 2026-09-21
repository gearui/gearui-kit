package com.gearui.gestures

import androidx.compose.runtime.compositionLocalOf

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
