package com.gearui.navigation

import kotlinx.coroutines.isActive
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The navigation stack machine, without a composition.
 *
 * S2 made Navigator generic over the route type, which touched every mutation
 * on this class, and the only evidence it still worked was one push/pop on a
 * device. That covers the happy path and nothing else — not key uniqueness, not
 * exactly-once removal, not the pop-interception branches, not the mid-flight
 * guards. Those are the parts a refactor breaks quietly.
 *
 * Transitions are deliberately not started here: `startCommitPopAnim` needs a
 * coroutine scope from composition, so a pop that reaches it is left in flight.
 * The tests either use `attach`-free paths or assert the in-flight state, which
 * is itself worth pinning.
 */
private sealed interface TestRoute : NavRoute {
    data object Home : TestRoute {
        override val routeName: String = "home"
    }

    data object Detail : TestRoute {
        override val routeName: String = "detail"
    }

    /** Carries a payload, to check that popTo matches on name rather than equality. */
    data class Article(val id: String) : TestRoute {
        override val routeName: String = "article"
    }

    data object Guarded : TestRoute {
        override val routeName: String = "guarded"
        override val options: NavOptions = NavOptions(onPopRequest = { PopDecision.Deny })
    }

    data object Dirty : TestRoute {
        override val routeName: String = "dirty"
        override val options: NavOptions = NavOptions(onPopRequest = { PopDecision.Pending })
    }
}

private fun state() = NavigatorState<TestRoute>(TestRoute.Home)

class NavigatorStateTest {

    @Test
    fun startsAtTheInitialRouteAndCannotPop() {
        val s = state()
        assertEquals(TestRoute.Home, s.current.route)
        assertNull(s.previous)
        assertFalse(s.canPop)
        assertFalse(s.pop(), "popping the bottom of the stack must fail")
    }

    @Test
    fun pushingTheSameRouteTwiceGivesDistinctKeys() {
        val s = state()
        s.push(TestRoute.Detail)
        val first = s.current.key
        s.push(TestRoute.Detail)
        val second = s.current.key

        assertNotEquals(first, second, "entry identity must not collide for a repeated route")
        assertEquals(TestRoute.Detail, s.current.route)
        assertEquals(first, s.previous?.key)
    }

    @Test
    fun entryCarriesItsRouteAndTheRoutesOptions() {
        val s = state()
        s.push(TestRoute.Article(id = "42"))

        val route = s.current.route
        assertTrue(route is TestRoute.Article)
        assertEquals("42", route.id, "the payload survives the round trip through the stack")
        assertEquals(TestRoute.Guarded.options, TestRoute.Guarded.options)
        assertEquals(NavOptions.Default, s.current.options, "an Article declares no options")
    }

    @Test
    fun replaceSwapsTheTopAndDisposesTheOldEntryOnce() {
        val s = state()
        s.push(TestRoute.Detail)
        val replaced = s.current.key
        val removed = mutableListOf<String>()
        s.attachForTest { removed.add(it.key) }

        s.replace(TestRoute.Article(id = "7"))

        assertEquals(2, s.entriesForTest.size, "replace must not grow the stack")
        assertEquals("article", s.current.route.routeName)
        assertContentEquals(listOf(replaced), removed)
    }

    @Test
    fun resetToClearsEverythingAndReportsEachEntryExactlyOnce() {
        val s = state()
        s.push(TestRoute.Detail)
        s.push(TestRoute.Article(id = "1"))
        val before = s.entriesForTest.map { it.key }
        val removed = mutableListOf<String>()
        s.attachForTest { removed.add(it.key) }

        s.resetTo(TestRoute.Home)

        assertEquals(1, s.entriesForTest.size)
        assertEquals(TestRoute.Home, s.current.route)
        assertContentEquals(before, removed, "every dropped entry is reported, in stack order, once")
    }

    @Test
    fun popToMatchesOnRouteNameNotOnEquality() {
        val s = state()
        s.push(TestRoute.Article(id = "target"))
        s.push(TestRoute.Detail)
        s.push(TestRoute.Detail)

        // A different payload, same route name — this is the case that makes
        // popTo(route) usable at all.
        assertTrue(s.popTo(TestRoute.Article(id = "ignored")))

        // Detached from a composition there is no animation scope, so the exit
        // completes synchronously instead of leaving the outgoing entry in the
        // stack as the moving layer. That path is what a logout outside
        // composition takes, and it is worth pinning on its own.
        assertEquals(2, s.entriesForTest.size)
        assertEquals("article", s.current.route.routeName)
        assertEquals("target", (s.current.route as TestRoute.Article).id, "the surviving entry keeps its own payload, not the one passed to popTo")
    }

    @Test
    fun popToReturnsFalseWhenThereIsNoSuchRouteOrItIsAlreadyOnTop() {
        val s = state()
        assertFalse(s.popTo(TestRoute.Detail), "no such entry")
        s.push(TestRoute.Detail)
        assertFalse(s.popTo(TestRoute.Detail), "already on top")
    }

    @Test
    fun onPopRequestDenyLeavesTheStackAlone() {
        val s = state()
        s.push(TestRoute.Guarded)
        val depth = s.entriesForTest.size

        assertFalse(s.pop(), "Deny reports the pop as refused")
        assertEquals(depth, s.entriesForTest.size)
        assertEquals(TestRoute.Guarded, s.current.route)
    }

    @Test
    fun onPopRequestPendingConsumesTheBackWithoutPopping() {
        val s = state()
        s.push(TestRoute.Dirty)
        val depth = s.entriesForTest.size

        // Returns false, like Deny. BACK is unaffected by that — the handler
        // being registered is what tells Kuikly the event was consumed, and it
        // ignores the return — but a programmatic caller cannot tell "refused"
        // from "a confirmation is now showing" through the public API. Pinned
        // as current behaviour; worth resolving before the API freezes.
        assertFalse(s.pop(), "Pending currently reports false, the same as Deny")
        assertEquals(depth, s.entriesForTest.size, "Pending must not pop by itself")
        assertEquals(TestRoute.Dirty, s.current.route)

        // The continuation is forcePop, which skips the interceptor.
        assertTrue(s.forcePop())
        assertEquals(depth - 1, s.entriesForTest.size)
    }

    @Test
    fun retainedStateIsPerEntryAndSurvivesUntilTheEntryLeaves() {
        val s = state()
        s.push(TestRoute.Detail)
        val key = s.current.key

        val first = s.retainedOf(key).retain("k") { mutableListOf("v") }
        val again = s.retainedOf(key).retain("k") { mutableListOf("other") }
        assertTrue(first === again, "retain creates once and returns the same instance")

        val otherEntry = s.retainedOf(s.entriesForTest.first().key).retain("k") { mutableListOf("z") }
        assertFalse(first === otherEntry, "each entry has its own store")

        assertTrue(s.retainedOf(key).coroutineScope.isActive)
    }

    @Test
    fun retainedStateIsCancelledWhenTheNavigatorLeavesComposition() {
        val s = state()
        s.push(TestRoute.Detail)
        val scope = s.retainedOf(s.current.key).coroutineScope

        s.onForgotten()

        assertFalse(scope.isActive, "a dropped Navigator must not leave coroutines running")
    }
}
