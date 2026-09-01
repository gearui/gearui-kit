package com.gearui.components.grid

/**
 * Pure math behind [ResponsiveGrid].
 *
 * Compose-free so the column-count computation can be unit tested directly.
 */
internal object GridMath {

    /**
     * How many columns of at least [minColumnWidthPx] fit into [containerWidthPx]
     * with [spacingPx] gaps between them.
     *
     * n columns consume n * minWidth + (n - 1) * spacing, so
     * n = floor((width + spacing) / (minWidth + spacing)), never below 1.
     * Unmeasured containers (width <= 0) and non-positive minimums fall back
     * to a single column instead of dividing by zero.
     */
    fun columnCount(containerWidthPx: Int, minColumnWidthPx: Int, spacingPx: Int): Int {
        if (containerWidthPx <= 0 || minColumnWidthPx <= 0) return 1
        return maxOf(1, (containerWidthPx + spacingPx) / (minColumnWidthPx + spacingPx))
    }
}
