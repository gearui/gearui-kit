package com.gearui.components.grid

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the pure column-count math behind ResponsiveGrid.
 */
class GridMathTest {

    @Test
    fun columnsFitWidthAndSpacing() {
        // n columns need n * 120 + (n - 1) * 8 pixels.
        assertEquals(3, GridMath.columnCount(containerWidthPx = 400, minColumnWidthPx = 120, spacingPx = 8))
        // 4 columns would need 4 * 120 + 3 * 8 = 504.
        assertEquals(3, GridMath.columnCount(containerWidthPx = 503, minColumnWidthPx = 120, spacingPx = 8))
        assertEquals(4, GridMath.columnCount(containerWidthPx = 504, minColumnWidthPx = 120, spacingPx = 8))
    }

    @Test
    fun tightContainersFallBackToOneColumn() {
        assertEquals(1, GridMath.columnCount(containerWidthPx = 120, minColumnWidthPx = 120, spacingPx = 8))
        assertEquals(1, GridMath.columnCount(containerWidthPx = 247, minColumnWidthPx = 120, spacingPx = 8))
        assertEquals(2, GridMath.columnCount(containerWidthPx = 248, minColumnWidthPx = 120, spacingPx = 8))
    }

    @Test
    fun zeroSpacingDividesEvenly() {
        assertEquals(3, GridMath.columnCount(containerWidthPx = 360, minColumnWidthPx = 120, spacingPx = 0))
        assertEquals(3, GridMath.columnCount(containerWidthPx = 479, minColumnWidthPx = 120, spacingPx = 0))
    }

    @Test
    fun unmeasuredAndDegenerateInputsKeepOneColumn() {
        // Before the first layout pass the container width is still 0.
        assertEquals(1, GridMath.columnCount(containerWidthPx = 0, minColumnWidthPx = 120, spacingPx = 8))
        assertEquals(1, GridMath.columnCount(containerWidthPx = -5, minColumnWidthPx = 120, spacingPx = 8))
        // A non-positive minimum must not divide by zero.
        assertEquals(1, GridMath.columnCount(containerWidthPx = 400, minColumnWidthPx = 0, spacingPx = 8))
    }
}
