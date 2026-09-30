package com.gearui.overlay

import com.gearui.components.select.dropdownPlacement
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DropdownAnchorTest {
    private val density = Density(1f)

    private fun place(trigger: Rect, viewport: Int = 844) =
        dropdownPlacement(trigger, 6, -1, density, viewport, 0.dp, 0.dp)

    @Test
    fun panelFollowsTheTriggerAndItsWidth() {
        val narrow = assertNotNull(place(Rect(16f, 100f, 374f, 148f)))
        val wide = assertNotNull(place(Rect(16f, 100f, 1264f, 148f)))
        assertEquals(358f, narrow.anchorWidth)
        assertEquals(1248f, wide.anchorWidth)
        val moved = assertNotNull(place(Rect(16f, 300f, 374f, 348f)))
        assertTrue(moved.anchor.top > narrow.anchor.top, "a trigger lower on the page puts the panel lower")
        assertEquals(16f, moved.anchor.left)
    }

    @Test
    fun panelOpensAboveATriggerNearTheBottom() {
        val placed = assertNotNull(place(Rect(16f, 760f, 374f, 808f)))
        assertTrue(placed.anchor.top + placed.layout.height <= 760f)
    }

    @Test
    fun noPanelForATriggerOffScreenOrWithoutRoom() {
        assertNull(place(Rect(16f, -80f, 374f, -32f)), "scrolled above the screen")
        assertNull(place(Rect(16f, 900f, 374f, 948f)), "scrolled below the screen")
        assertNull(place(Rect(16f, 100f, 374f, 148f), viewport = 0), "no viewport yet")
    }

    @Test
    fun updateAnchorMovesTheSameOverlay() {
        val controller = OverlayController()
        val id = controller.show(anchorBounds = Rect(0f, 10f, 100f, 10f), passThroughBounds = Rect(0f, 0f, 100f, 10f)) {}
        val before = controller.items.single()
        controller.updateAnchor(id, Rect(0f, 50f, 300f, 50f), Rect(0f, 40f, 300f, 50f))
        val after = controller.items.single()
        assertSame(before, after, "moving must not replace the item, or its content restarts")
        assertEquals(Rect(0f, 50f, 300f, 50f), after.anchorBounds)
        assertEquals(Rect(0f, 40f, 300f, 50f), after.passThrough.value)
    }
}
