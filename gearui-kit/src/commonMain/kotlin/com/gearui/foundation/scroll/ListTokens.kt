package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ListTokens - list design spec
 *
 * Keeps the following consistent:
 * - gap between items
 * - divider policy
 * - Content padding
 */
data class ListTokens(
    /** gap between list items */
    val itemSpacing: Dp,

    /** whether dividers are shown */
    val divider: Boolean,

    /** content area padding */
    val contentPadding: PaddingValues
) {
    companion object {
        /** ordinary list */
        val Default = ListTokens(
            itemSpacing = 8.dp,
            divider = false,
            contentPadding = PaddingValues(16.dp)
        )

        /** settings-page style (with dividers) */
        val Settings = ListTokens(
            itemSpacing = 0.dp,
            divider = true,
            contentPadding = PaddingValues(0.dp)
        )

        /** very dense */
        val Dense = Default.copy(
            itemSpacing = 4.dp
        )
    }
}
