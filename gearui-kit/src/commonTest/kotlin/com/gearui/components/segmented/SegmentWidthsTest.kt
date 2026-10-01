package com.gearui.components.segmented

import kotlin.test.*

class SegmentWidthsTest {
    @Test fun equalWhileEveryLabelFits() {
        assertEquals(listOf(100, 100, 100), segmentWidths(listOf(60, 80, 40), 300, padding = 24, tightPadding = 8))
    }
    @Test fun byContentWhenOneLabelIsTooWide() {
        // 140 does not fit a third of 300; each keeps its content and shares the rest (80 / 3).
        assertEquals(listOf(87, 167, 46), segmentWidths(listOf(60, 140, 20), 300, padding = 24, tightPadding = 8))
    }
    @Test fun paddingGivesWayBeforeLabelsAreCut() {
        // Five labels of 42 + 24 padding (330) in 248: with 8 of padding (250) still too much,
        // so they shrink; with 28 + 24 for the first (318) the tight sum is 236 and fits.
        val widths = segmentWidths(listOf(52, 66, 66, 66, 66), 248, padding = 24, tightPadding = 8)
        assertEquals(248, widths.sum())
        assertTrue(widths.drop(1).all { it >= 42 + 8 }, "every three-character label keeps its text: $widths")
    }
    @Test fun shrinksInProportionWhenNothingElseFits() {
        val widths = segmentWidths(listOf(124, 124), 100, padding = 24, tightPadding = 8)
        assertEquals(listOf(50, 50), widths)
    }
    @Test fun alwaysFillsTheTrackExactly() {
        for (available in listOf(97, 250, 301)) {
            assertEquals(available, segmentWidths(listOf(30, 70, 55), available, 24, 8).sum())
        }
    }
}
