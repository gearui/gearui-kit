package com.gearui.navigation

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private sealed interface PushRoute : NavRoute {
    data object Home : PushRoute { override val routeName = "home" }
    data object Chat : PushRoute { override val routeName = "chat" }
    data object Profile : PushRoute { override val routeName = "profile" }
}

/**
 * The enter animation of a push, held at its first frame: the scope is attached but
 * never advanced, so what is asserted is the state the page is composed in.
 */
class NavigatorPushTransitionTest {
    private fun attached(): NavigatorState<PushRoute> {
        val s = NavigatorState<PushRoute>(PushRoute.Home)
        s.attach(saveable = {}, onEntryRemovedRef = {}, animScope = TestScope(StandardTestDispatcher()))
        return s
    }

    @Test
    fun aPushedPageArrivesFromOffScreenOverThePreviousOne() {
        val s = attached()
        s.push(PushRoute.Chat)
        assertEquals(PushRoute.Chat, s.movingEntry?.route, "the new page is the moving layer")
        assertEquals(1f, s.transitionFraction, "first frame off-screen right, not covering then jumping")
        assertEquals(listOf(NavLayerRole.Below, NavLayerRole.Moving), s.visibleLayers().map { it.role })
        assertEquals(PushRoute.Home, s.visibleLayers().first().entry.route)
        assertTrue(s.isTransitioning)
        assertFalse(s.canPop, "no swipe or pop starts against a page still arriving")
    }

    @Test
    fun aSecondPushDuringTheEnterIsNotDropped() {
        val s = attached()
        s.push(PushRoute.Chat)
        s.push(PushRoute.Profile)
        assertEquals(listOf(PushRoute.Home, PushRoute.Chat, PushRoute.Profile), s.entriesForTest.map { it.route })
        assertEquals(PushRoute.Profile, s.movingEntry?.route)
        assertEquals(1f, s.transitionFraction)
    }

    @Test
    fun backDuringTheEnterLeavesInsteadOfBeingIgnored() {
        val s = attached()
        s.push(PushRoute.Chat)
        assertTrue(s.pop(), "the arriving page settles and then leaves")
        assertEquals(PushRoute.Chat, s.movingEntry?.route, "now as the leaving layer")
    }

    @Test
    fun replaceDuringTheEnterIsNotRefused() {
        val s = attached()
        s.push(PushRoute.Chat)
        s.replace(PushRoute.Profile)
        assertEquals(listOf(PushRoute.Home, PushRoute.Profile), s.entriesForTest.map { it.route })
    }

    @Test
    fun withoutAnAnimationScopeAPushIsImmediate() {
        val s = NavigatorState<PushRoute>(PushRoute.Home)
        s.push(PushRoute.Chat)
        assertEquals(null, s.movingEntry)
        assertTrue(s.canPop)
    }
}
