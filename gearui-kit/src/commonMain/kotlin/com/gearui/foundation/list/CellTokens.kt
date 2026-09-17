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
        paddingHorizontal = ControlGeometry.listPadding,
        paddingVertical = ControlGeometry.listPadding,
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
