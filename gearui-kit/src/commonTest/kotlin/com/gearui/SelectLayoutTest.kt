package com.gearui

import com.gearui.components.select.*
import kotlin.test.*
import com.gearui.overlay.computeOffset
import com.gearui.overlay.OverlayOptions
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp

class SelectLayoutTest {
    @Test fun paddedPopoverPrefersBelowWhenContentFits() {
        val result = selectPanelLayout(2, 0, 48f, 400f, 448f, 844f, 59f, 34f, 8f, 12f)
        assertEquals(456f, result.top)
        assertEquals(120f, result.height)
    }
    @Test fun paddedPopoverKeepsLateSelectionFullyVisible() {
        val result = selectPanelLayout(30, 27, 48f, 100f, 148f, 844f, 59f, 34f, 8f, 12f)
        assertTrue(result.firstRow <= 27)
        assertTrue((27 - result.firstRow + 1) * 48 + 24 <= result.height)
        assertTrue(result.top + result.height <= 802f)
    }
    @Test fun paddedEmptyStateHasOneRowAndBothInsets() {
        val result = selectPanelLayout(0, -1, 48f, 300f, 348f, 844f, 59f, 34f, 8f, 12f)
        assertEquals(72f, result.height)
    }
    @Test fun groupsRenderOncePerConsecutiveRun() {
        val rows = selectRows(listOf(
            SelectOption(1, "Apple", group = "Fruits"), SelectOption(2, "Pear", group = "Fruits"),
            SelectOption(3, "Carrot", group = "Vegetables"),
        ))
        assertEquals(listOf("Fruits", null, null, "Vegetables", null), rows.map { it.heading })
        assertEquals(2, rows.indexOfFirst { it.option?.value == 2 })
    }
    @Test fun ungroupedAndEmptyOptionsAddNoPhantomHeaders() {
        assertTrue(selectRows(emptyList<SelectOption<Int>>()).isEmpty())
        assertEquals(1, selectRows(listOf(SelectOption(1, "A", group = " "))).size)
    }
    private fun layout(count: Int = 23, selected: Int = 1, top: Float = 300f) =
        selectPanelLayout(count, selected, 44f, top, top + 44, 844f, 59f, 34f, 8f)

    @Test fun opensBelowTheTrigger() {
        val result = layout()
        assertEquals(352f, result.top)
        assertEquals(0, result.firstRow)
        assertEquals(802f, result.top + result.height)
    }
    @Test fun hostPreservesResolvedPanelOriginAtNativeDensity() {
        val panel = layout()
        val scale = 3
        val origin = panel.top * scale
        val offset = computeOffset(
            Rect(100f, origin, 760f, origin),
            IntSize(660, (panel.height * scale).toInt()),
            IntSize(1206, 844 * scale),
            OverlayOptions(offsetY = 0.dp, autoFlip = false),
            Density(scale.toFloat()),
        )
        assertEquals(origin.toInt(), offset.y)
        assertTrue(offset.y + panel.height * scale <= (844 - 34 - 8) * scale)
    }
    @Test fun lateSelectionScrollsIntoTheVisibleViewport() {
        val result = layout(selected = 21)
        assertTrue(result.firstRow > 0)
        assertTrue(21 >= result.firstRow)
        assertTrue((21 - result.firstRow + 1) * 44 <= result.height)
        assertTrue(result.top >= 67f)
        assertTrue(result.top + result.height <= 802f)
    }
    @Test fun flipsAboveNearBottom() {
        val result = layout(top = 740f)
        assertTrue(result.top + result.height < 740f)
        assertTrue(result.top >= 67f)
    }
    @Test fun shortListsDoNotStretch() {
        assertEquals(88f, layout(count = 2, selected = 0).height)
    }
    @Test fun crampedViewportNeverProducesNegativeHeight() {
        val result = selectPanelLayout(10, 5, 44f, 80f, 124f, 100f, 59f, 50f, 8f)
        assertTrue(result.height >= 0f)
    }
    @Test fun emptyListReservesOneEmptyStateRow() {
        assertEquals(44f, layout(count = 0, selected = -1).height)
    }
}
