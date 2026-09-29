package com.gearui.components.refresh

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoadMoreTest {
    @Test
    fun loadsOnlyWhenIdleAndTheFooterIsOnScreen() {
        assertTrue(shouldLoadMore(LoadMoreStatus.Idle, lastVisibleIndex = 30, totalItems = 31))
        assertFalse(shouldLoadMore(LoadMoreStatus.Idle, lastVisibleIndex = 29, totalItems = 31))
    }

    @Test
    fun neverRequestsTwiceWhileAPageIsLoading() {
        assertFalse(shouldLoadMore(LoadMoreStatus.Loading, lastVisibleIndex = 30, totalItems = 31))
    }

    @Test
    fun aFailureWaitsForTheUser() {
        assertFalse(shouldLoadMore(LoadMoreStatus.Failed, lastVisibleIndex = 30, totalItems = 31))
    }

    @Test
    fun noMoreMeansNoMore() {
        assertFalse(shouldLoadMore(LoadMoreStatus.NoMore, lastVisibleIndex = 30, totalItems = 31))
    }

    @Test
    fun anAppendedPageThatPushesTheFooterOffScreenDoesNotChain() {
        // After a page lands, the list has 46 items but the last visible is still row 30.
        assertFalse(shouldLoadMore(LoadMoreStatus.Idle, lastVisibleIndex = 30, totalItems = 46))
    }

    @Test
    fun anEmptyLayoutIsNotAVisibleFooter() {
        assertFalse(footerVisible(lastVisibleIndex = null, totalItems = 0))
    }
}
