package com.gearui.gestures

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * When a downward drag on a sheet counts as "dismiss".
 *
 * The gesture itself lives in a pointer loop no test can enter; this is the
 * decision it makes on release, which is the part with a rule in it.
 */
class SwipeDismissTest {

    private fun commit(drag: Float, velocity: Float = 0f) = shouldCommitDismiss(
        dragPx = drag,
        velocityPxPerSec = velocity,
        commitPx = 100f,
        minFlingPx = 20f,
        flingVelocityPxPerSec = 1000f,
    )

    @Test
    fun dragPastTheThresholdDismisses() {
        assertTrue(commit(drag = 100f))
        assertTrue(commit(drag = 240f))
    }

    @Test
    fun dragShortOfTheThresholdSpringsBack() {
        assertFalse(commit(drag = 99f), "just short is short; otherwise a nudge closes the user's sheet")
        assertFalse(commit(drag = 0f))
    }

    @Test
    fun aFlickDismissesOverAShorterDistance() {
        assertTrue(
            commit(drag = 30f, velocity = 1200f),
            "a fast flick travels less; without this the gesture feels stuck for the users who are quickest at it",
        )
    }

    @Test
    fun highVelocityWithNoTravelIsNotIntent() {
        assertFalse(
            commit(drag = 19f, velocity = 5000f),
            "a fast reading over almost no movement is jitter, not a decision",
        )
    }

    @Test
    fun aSlowDragStillCountsIfItGoesFarEnough() {
        assertTrue(commit(drag = 150f, velocity = 0f), "distance alone is enough; dragging all the way down slowly is intent too")
    }

    @Test
    fun shortTravelBelowTheFlingVelocityDoesNotDismiss() {
        assertFalse(commit(drag = 50f, velocity = 900f))
    }
}
