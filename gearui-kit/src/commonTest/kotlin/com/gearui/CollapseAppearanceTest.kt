package com.gearui

import com.gearui.foundation.disclosure.disclosureDampingRatio
import com.gearui.foundation.disclosure.disclosureStiffness
import com.gearui.foundation.disclosure.disclosureFadeSpec
import com.gearui.foundation.material.TokenTransition
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.feedbackDuration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollapseAppearanceTest {
    @Test fun compositeTransitionKeepsDelayAndRespectsReducedMotion() {
        val source = TokenTransition(200, 75, FeedbackDefaults.accordionEnterEasing)
        val slow = disclosureFadeSpec(Motion(normal = 300), source)
        assertEquals(400, slow.durationMillis)
        assertEquals(150, slow.delay)
        val reduced = disclosureFadeSpec(Motion(normal = 0), source)
        assertEquals(0, reduced.durationMillis)
        assertEquals(0, reduced.delay)
    }

    @Test fun nativePaddingDoesNotBecomeGenericCardPadding() {
        assertEquals(12f, ControlGeometry.accordionPadding.value)
        assertEquals(20f, ControlGeometry.accordionSurfacePadding.value)
        assertEquals(16f, ControlGeometry.accordionVerticalPadding.value)
    }

    @Test fun springMassIsNormalizedForBothTransitions() {
        assertEquals(250f, disclosureStiffness(Motion(), false))
        assertEquals(400f, disclosureStiffness(Motion(), true))
        assertEquals(1.1067972f, disclosureDampingRatio(false), .00001f)
        assertEquals(.875f, disclosureDampingRatio(true))
    }

    @Test fun appMotionSpeedAndSuppressionAreHonored() {
        assertEquals(100f, disclosureStiffness(Motion(normal = 300), true))
        assertEquals(400, Motion(normal = 300).feedbackDuration(FeedbackDefaults.accordionFadeDuration))
        assertEquals(0, Motion(normal = 0).feedbackDuration(FeedbackDefaults.accordionFadeDuration))
    }

    @Test fun fadeInAndFadeOutHaveOppositeEasing() {
        val enter = FeedbackDefaults.accordionEnterEasing.transform(.25f)
        val exit = FeedbackDefaults.accordionExitEasing.transform(.75f)
        assertEquals(1f, enter + exit, .002f)
        assertTrue(enter > .25f)
        assertEquals(180f, FeedbackDefaults.accordionRotation)
    }
}
