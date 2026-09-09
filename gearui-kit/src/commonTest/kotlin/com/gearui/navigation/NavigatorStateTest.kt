package com.gearui.navigation

import kotlinx.coroutines.isActive
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
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
        assertEquals(NavOptions.Default, s.current.options, "an Article declares no options")

        s.push(TestRoute.Guarded)
        assertEquals(
            TestRoute.Guarded.options,
            s.current.options,
            "the entry exposes the route's own options, not a default",
        )
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

        // Returns false, like Deny. A programmatic caller still cannot tell
        // "refused" from "a confirmation is showing" from the return alone —
        // pendingPop is how it tells them apart.
        assertFalse(s.pop(), "Pending reports false, the same as Deny")
        assertEquals(depth, s.entriesForTest.size, "Pending must not pop by itself")
        assertEquals(TestRoute.Dirty, s.current.route)
        assertEquals(s.current.key, s.pendingPop?.key, "the entry that asked is recorded")

        assertTrue(s.confirmPendingPop())
        assertEquals(depth - 1, s.entriesForTest.size)
        assertNull(s.pendingPop)
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

    @Test
    fun cancellingAPendingPopUnfreezesTheStack() {
        val s = state()
        s.push(TestRoute.Dirty)
        s.pop()
        assertNotNull(s.pendingPop)

        // "Keep editing". Before there was a cancel at all, the only way out was
        // forcePop — so choosing to stay locked navigation for good.
        s.cancelPendingPop()

        assertNull(s.pendingPop)
        assertTrue(s.canPop, "the stack is usable again")
        s.push(TestRoute.Detail)
        assertEquals(TestRoute.Detail, s.current.route, "push works after a cancel")
        assertTrue(s.popTo(TestRoute.Home), "popTo works after a cancel")
    }

    @Test
    fun navigatorKeepsOwningBackWhileAConfirmationIsPending() {
        val s = state()
        s.push(TestRoute.Dirty)
        s.pop()

        // canPop is false — no new pop may start — but there is still a page
        // underneath, so BACK must not fall through to the host. The
        // BackHandler registers on hasBackStack for exactly this.
        assertFalse(s.canPop)
        assertTrue(s.hasBackStack)

        // A second BACK is swallowed rather than doing anything.
        assertFalse(s.pop())
        assertEquals(TestRoute.Dirty, s.current.route)
    }

    @Test
    fun aLateConfirmationDoesNotPopSomeOtherPage() {
        val s = state()
        s.push(TestRoute.Dirty)
        s.pop()
        val asked = s.pendingPop
        assertNotNull(asked)

        // The stack moves on underneath the confirmation — a reset, say.
        s.resetTo(TestRoute.Home)
        assertEquals(1, s.entriesForTest.size)

        assertFalse(s.confirmPendingPop(), "the page that asked is gone; confirm must not pop the new top")
        assertEquals(TestRoute.Home, s.current.route)
    }

    @Test
    fun resetToInterruptsAPendingConfirmationInsteadOfBeingRefusedByIt() {
        val s = state()
        s.push(TestRoute.Dirty)
        s.pop()
        assertNotNull(s.pendingPop, "the stack is frozen")

        // A forced logout must not be refusable by a dirty form. This used to
        // return early on isMidFlight and silently do nothing.
        s.resetTo(TestRoute.Home)

        assertEquals(1, s.entriesForTest.size)
        assertEquals(TestRoute.Home, s.current.route)
        assertNull(s.pendingPop)
        assertTrue(s.canPop.not(), "at the bottom again")
    }

    @Test
    fun popToPredicateMatchesOnThePayloadWhenTheNameIsNotEnough() {
        val s = state()
        s.push(TestRoute.Article(id = "a"))
        s.push(TestRoute.Article(id = "b"))
        s.push(TestRoute.Detail)

        // popTo(route) would match "a" or "b" indifferently; the predicate is
        // there for when that is not good enough.
        assertTrue(s.popTo { it is TestRoute.Article && it.id == "a" })
        assertEquals("a", (s.current.route as TestRoute.Article).id)
    }
}
