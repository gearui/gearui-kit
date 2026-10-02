package com.gearui.components

import com.gearui.components.icon.*
import com.gearui.components.alert.AlertStatus
import com.gearui.components.alert.alertStatusIcon
import com.gearui.components.icon.Icons
import com.gearui.components.inputotp.activeOtpSlot
import com.gearui.components.inputotp.sanitizeOtp
import com.gearui.components.scrollshadow.ScrollShadowVisibility
import com.gearui.components.scrollshadow.scrollShadowEdges
import com.gearui.components.tag.TagGroupSelectionMode
import com.gearui.components.tag.nextTagSelection
import com.gearui.foundation.control.ControlGeometry
import kotlin.test.Test
import kotlin.test.assertEquals

/** Selection, sanitising and edge rules for the batch 2 components. */
class Batch2LogicTest {

    @Test
    fun otpKeepsOnlyWhatFits() {
        assertEquals("123456", sanitizeOtp("123456789", 6, numeric = true))
        // A pasted code often arrives with spaces or a dash.
        assertEquals("123456", sanitizeOtp("123 456", 6, numeric = true))
        assertEquals("1234", sanitizeOtp("a1b2c3d4", 4, numeric = true))
        assertEquals("ab12", sanitizeOtp(" ab 12 ", 4, numeric = false))
    }

    @Test
    fun otpActiveSlotFollowsTheCursorAndStopsAtTheEnd() {
        assertEquals(0, activeOtpSlot(0, 6))
        assertEquals(3, activeOtpSlot(3, 6))
        assertEquals(5, activeOtpSlot(6, 6))
    }

    @Test
    fun singleSelectionClearsWhenTappedAgain() {
        assertEquals(setOf("b"), nextTagSelection(setOf("a"), "b", TagGroupSelectionMode.SINGLE))
        assertEquals(emptySet(), nextTagSelection(setOf("a"), "a", TagGroupSelectionMode.SINGLE))
        assertEquals(setOf("a", "b"), nextTagSelection(setOf("a"), "b", TagGroupSelectionMode.MULTIPLE))
        assertEquals(setOf("b"), nextTagSelection(setOf("a", "b"), "a", TagGroupSelectionMode.MULTIPLE))
        assertEquals(setOf("a"), nextTagSelection(setOf("a"), "b", TagGroupSelectionMode.NONE))
    }

    @Test
    fun autoShadowsFollowScrollCapability() {
        assertEquals(false to true, scrollShadowEdges(ScrollShadowVisibility.AUTO, false, true))
        assertEquals(true to false, scrollShadowEdges(ScrollShadowVisibility.AUTO, true, false))
        // Content that fits needs no fade at all.
        assertEquals(false to false, scrollShadowEdges(ScrollShadowVisibility.AUTO, false, false))
        assertEquals(true to true, scrollShadowEdges(ScrollShadowVisibility.BOTH, false, false))
        assertEquals(false to false, scrollShadowEdges(ScrollShadowVisibility.NONE, true, true))
    }

    @Test
    fun alertIconsAndGeometryMatchReference() {
        assertEquals(Icons.check, alertStatusIcon(AlertStatus.SUCCESS))
        assertEquals(Icons.warning, alertStatusIcon(AlertStatus.WARNING))
        assertEquals(Icons.info, alertStatusIcon(AlertStatus.DEFAULT))
        assertEquals(12f, ControlGeometry.alertPadding.value)
        assertEquals(18f, ControlGeometry.alertIcon.value)
        assertEquals(44f, ControlGeometry.otpSlotWidth.value)
        assertEquals(48f, ControlGeometry.otpSlotHeight.value)
        assertEquals(50f, ControlGeometry.scrollShadowSize.value)
    }
}
