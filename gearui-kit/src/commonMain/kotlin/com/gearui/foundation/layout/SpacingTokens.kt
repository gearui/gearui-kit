package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Spacing - global spacing scale
 *
 * Reference: internal spacing specification
 *
 * Built on an 8px grid:
 * - spacer4:   4px  (0.5x)
 * - spacer8:   8px  (1x, the base unit)
 * - spacer12:  12px (1.5x)
 * - spacer16:  16px (2x)
 * - spacer24:  24px (3x)
 * - spacer32:  32px (4x)
 * - spacer40:  40px (5x)
 * - spacer48:  48px (6x)
 *
 * Rules:
 * - ✅ always use Spacing.md / Spacing.lg
 * - ❌ never write Spacer(12.dp) / padding(8.dp)
 */
object Spacing {
    /** 0dp - no gap. Named for symmetry with Shapes.none and Elevation.none. */
    val none: Dp = 0.dp

    /** 4dp - smallest gap (xs) */
    val xs: Dp = 4.dp

    /** 8dp - base gap (sm) */
    val sm: Dp = 8.dp

    /** 12dp - compact gap (md) - most used */
    val md: Dp = 12.dp

    /** 16dp - standard gap (lg) */
    val lg: Dp = 16.dp

    /** 24dp - medium gap (xl) */
    val xl: Dp = 24.dp

    /** 32dp - large gap (xxl) */
    val xxl: Dp = 32.dp

    /** 40dp - larger gap */
    val xxxl: Dp = 40.dp

    /** 48dp - extra large gap */
    val huge: Dp = 48.dp

    /** 64dp - largest gap */
    val massive: Dp = 64.dp
}
