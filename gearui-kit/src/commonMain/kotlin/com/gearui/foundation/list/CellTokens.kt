package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults

/**
 * CellTokens - Cell sizing spec
 *
 * Cell = the core interaction unit of the List family
 *
 * Default padding follows Native ListGroup; compact sizing is a GearUI extension.
 *
 * ⚠️ Note: colours are not defined here; use Theme.colors
 */
data class CellTokens(
    val minHeight: Dp,
    val paddingHorizontal: Dp,
    val paddingVertical: Dp,
    val disabledAlpha: Float,
    val showDivider: Boolean
)

object CellDefaults {
    /**
     * Standard cell (the common case)
     */
    val Default = CellTokens(
        minHeight = ControlGeometry.listMinHeight,
        // The axes are separate on purpose: iOS starts text 20 in from the card edge
        // but centres one line in a 52 row, which is 14 above and below. A single
        // padding for both made a 56 row with text 16 in.
        paddingHorizontal = ControlGeometry.listPaddingInline,
        paddingVertical = ControlGeometry.listPaddingBlock,
        disabledAlpha = FeedbackDefaults.disabledOpacity,
        showDivider = true
    )

    /**
     * Compact cell (for dense information)
     * 44dp tall = the iOS Compact mode
     */
    val Compact = Default.copy(
        minHeight = ControlGeometry.listCompactMinHeight,
        paddingVertical = ControlGeometry.listCompactPadding
    )
}
