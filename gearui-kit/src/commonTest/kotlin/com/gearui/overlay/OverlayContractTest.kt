package com.gearui.overlay

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The overlay dismissal contract (docs/BEHAVIOR_CONTRACT.md §2): which event closes
 * which overlay, and when and how often the caller hears about it.
 */
class OverlayContractTest {
    private fun controllerWith(vararg policies: OverlayDismissPolicy): Pair<OverlayController, IntArray> {
        val controller = OverlayController()
        val calls = IntArray(policies.size)
        policies.forEachIndexed { i, policy ->
            controller.show(options = OverlayOptions(dismissPolicy = policy), onDismiss = { calls[i]++ }) {}
        }
        return controller to calls
    }

    @Test
    fun onDismissFiresOnceAndAtOnce() {
        val (controller, calls) = controllerWith(OverlayDismissPolicy.Modal)
        val id = controller.items.single().id
        controller.dismiss(id)
        // Immediately — the caller's `visible` flag flips before the exit animation.
        assertEquals(1, calls[0])
        // Still mounted while it animates out, and a second dismissal is a no-op.
        assertTrue(controller.items.single().exiting.value)
        controller.dismiss(id)
        controller.dispatchEvent(OverlayEvent.BackPress)
        controller.dismissAll()
        assertEquals(1, calls[0])
        // Unmounting after the animation does not report again.
        controller.remove(id)
        assertEquals(1, calls[0])
        assertTrue(controller.items.isEmpty())
    }

    @Test
    fun eachEventClosesOnlyWhatItsPolicyAllows() {
        val (controller, calls) = controllerWith(
            OverlayDismissPolicy.Dropdown,  // 0: outside, scroll, back, anchor
            OverlayDismissPolicy.Sheet,     // 1: outside, back
            OverlayDismissPolicy.Modal,     // 2: back only
            OverlayDismissPolicy.toast(),   // 3: timeout only
        )
        controller.dispatchEvent(OverlayEvent.Scroll)
        assertEquals(listOf(1, 0, 0, 0), calls.toList())
        controller.dispatchEvent(OverlayEvent.OutsideClick)
        assertEquals(listOf(1, 1, 0, 0), calls.toList())
        controller.dispatchEvent(OverlayEvent.BackPress)
        assertEquals(listOf(1, 1, 1, 0), calls.toList())
        controller.dispatchEvent(OverlayEvent.Timeout)
        assertEquals(listOf(1, 1, 1, 1), calls.toList())
    }

    @Test
    fun aModalIgnoresOutsideTapsAndScrollUnlessAsked() {
        val (controller, calls) = controllerWith(
            OverlayDismissPolicy.Modal,
            OverlayDismissPolicy.Modal.copy(outsideClick = true),
        )
        controller.dispatchEvent(OverlayEvent.Scroll)
        controller.dispatchEvent(OverlayEvent.OutsideClick)
        assertEquals(listOf(0, 1), calls.toList())
    }

    @Test
    fun aToastSurvivesNavigationAndBack() {
        val (controller, calls) = controllerWith(OverlayDismissPolicy.toast())
        controller.dispatchEvent(OverlayEvent.RouteChange)
        controller.dispatchEvent(OverlayEvent.BackPress)
        controller.dispatchEvent(OverlayEvent.OutsideClick)
        controller.dispatchEvent(OverlayEvent.Scroll)
        assertEquals(0, calls[0])
    }

    @Test
    fun aRouteChangeClosesEveryInteractiveOverlay() {
        val (controller, calls) = controllerWith(
            OverlayDismissPolicy.Dropdown,
            OverlayDismissPolicy.Sheet,
            OverlayDismissPolicy.Modal,
        )
        controller.dispatchEvent(OverlayEvent.RouteChange)
        assertEquals(listOf(1, 1, 1), calls.toList())
    }

    @Test
    fun anAnchorLeavingClosesOnlyAnchoredPanels() {
        val (controller, calls) = controllerWith(OverlayDismissPolicy.Dropdown, OverlayDismissPolicy.Modal)
        controller.dispatchEvent(OverlayEvent.AnchorDetached)
        assertEquals(listOf(1, 0), calls.toList())
    }

    @Test
    fun aCallerCloseDoesNotReportBack() {
        // The caller set visible = false after its own confirm: no second outcome.
        val (controller, calls) = controllerWith(OverlayDismissPolicy.Modal)
        val id = controller.items.single().id
        controller.close(id)
        assertEquals(0, calls[0])
        assertTrue(controller.items.single().exiting.value)
        // Already leaving: a later event does not report either.
        controller.dispatchEvent(OverlayEvent.BackPress)
        controller.dismiss(id)
        assertEquals(0, calls[0])
    }
}
