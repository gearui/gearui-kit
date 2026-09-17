package com.gearui

import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.motion.FeedbackDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class ListGeometryTest {
    @Test fun defaultRowsUseReferenceInsetsAndGap() {
        assertEquals(16f, CellDefaults.Default.paddingHorizontal.value)
        assertEquals(16f, CellDefaults.Default.paddingVertical.value)
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
