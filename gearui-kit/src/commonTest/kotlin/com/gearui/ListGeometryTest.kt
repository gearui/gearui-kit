package com.gearui

import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.motion.FeedbackDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class ListGeometryTest {
    // List rhythm follows iOS 26 (measured): text starts 20 in, and one 24 line centred in
    // the 52 row leaves 14 above and below. The item gap stays HeroUI's.
    @Test fun defaultRowsFollowThePlatformRhythm() {
        assertEquals(20f, CellDefaults.Default.paddingHorizontal.value)
        assertEquals(14f, CellDefaults.Default.paddingVertical.value)
        assertEquals(52f, CellDefaults.Default.minHeight.value)
        assertEquals(
            CellDefaults.Default.minHeight.value,
            CellDefaults.Default.paddingVertical.value * 2 + 24f,
            "a single 24 line fills the minimum row exactly",
        )
        assertEquals(12f, ControlGeometry.listItemGap.value)
    }

    @Test fun compactRemainsAnExplicitDenseVariant() {
        assertEquals(44f, CellDefaults.Compact.minHeight.value)
        assertEquals(8f, CellDefaults.Compact.paddingVertical.value)
        assertEquals(CellDefaults.Default.paddingHorizontal, CellDefaults.Compact.paddingHorizontal)
    }

    @Test fun disabledOpacityUsesTheSharedPolicy() {
        assertEquals(FeedbackDefaults.disabledOpacity, CellDefaults.Default.disabledAlpha)
        assertEquals(CellDefaults.Default.disabledAlpha, CellDefaults.Compact.disabledAlpha)
    }
}
