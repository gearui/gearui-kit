package com.gearui.components.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.gearui.gestures.LocalPageSwipeBackGate
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.pager.HorizontalPager
import com.tencent.kuikly.compose.foundation.pager.rememberPagerState
import com.tencent.kuikly.compose.ui.Modifier

/**
 * The pages a [Tabs] bar selects between, swipeable side to side.
 *
 * Tabs on their own only change a selection; the content still appears and disappears
 * in place, so a tab bar over a page reads as a filter rather than as two pages the
 * finger can move between. This carries the pages, and the selection is shared: tapping
 * a tab scrolls here, and a swipe reports the page it lands on so the bar follows.
 *
 * Controlled on purpose. The selected index lives with whoever owns the tab bar — the
 * two cannot each keep their own copy and stay in step.
 *
 * @param count how many pages.
 * @param selectedIndex which page is showing; changing it scrolls there.
 * @param onSelectedIndexChange called with the page a swipe settled on, never for a
 *   scroll this component started itself.
 * @param userScrollEnabled false pins the pages, leaving the bar as the only way across
 *   — for content that owns horizontal dragging itself, such as a map or a carousel.
 * @param beyondViewportPageCount how many pages either side stay composed while off
 *   screen. Pages here own real content — lists, avatars — and Kuikly builds their
 *   native view tree on the main thread the moment a page first becomes visible, so
 *   with the default 0 every backward drag pays that cost mid-gesture as a visible
 *   hitch. Keeping the neighbours alive moves the cost to page entry, off the finger.
 * @param content one page.
 */
@Composable
fun TabPager(
    count: Int,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    beyondViewportPageCount: Int = 1,
    content: @Composable (index: Int) -> Unit,
) {
    if (count <= 0) return

    val state = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, count - 1),
        pageCount = { count },
    )

    // Page-first swipe-back arbitration. While this pager is not on its first
    // page it can still move backwards, so it owns the right-swipe and the
    // app-level swipe-back stands down; on the first page it reports false and
    // the router takes the gesture. Read live at gesture-down, not snapshotted.
    val swipeBackGate = LocalPageSwipeBackGate.current
    LaunchedEffect(swipeBackGate) {
        swipeBackGate?.canSwipeBack = { state.currentPage > 0 }
    }

    // The bar moved: bring the pages with it.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex in 0 until count && state.currentPage != selectedIndex) {
            state.animateScrollToPage(selectedIndex)
        }
    }

    // The finger moved: tell the bar. `currentPage` turns over once the drag passes
    // the halfway point, so the bar follows the page the swipe has committed to.
    LaunchedEffect(state.currentPage) {
        if (state.currentPage != selectedIndex) onSelectedIndexChange(state.currentPage)
    }

    // The caller decides how much room this gets — in a Column that means weight(1f),
    // not fillMaxSize, which would claim the parent's whole height rather than what is
    // left under the bar.
    HorizontalPager(
        state = state,
        modifier = modifier.fillMaxWidth(),
        userScrollEnabled = userScrollEnabled,
        beyondViewportPageCount = beyondViewportPageCount,
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) { content(page) }
    }
}
