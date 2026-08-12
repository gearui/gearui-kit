package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * CellTokens - Cell sizing spec
 *
 * Cell = the core interaction unit of the List family
 *
 * Reference: internal component spec cell/td_cell_style.dart
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
        minHeight = 52.dp,
        paddingHorizontal = 16.dp,
        paddingVertical = 12.dp,
        disabledAlpha = 0.5f,
        showDivider = true
    )

    /**
     * Compact cell (for dense information)
     * 44dp tall = the iOS Compact mode
     */
    val Compact = Default.copy(
        minHeight = 44.dp,
        paddingVertical = 8.dp
    )
}
