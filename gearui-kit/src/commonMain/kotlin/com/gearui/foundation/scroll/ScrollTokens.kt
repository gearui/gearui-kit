package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ScrollTokens - scrolling container design spec
 *
 * Keeps the following the same on every page:
 * - Content padding
 * - gaps between elements
 * - bounce behaviour
 * - scrollbar visibility policy
 */
data class ScrollTokens(
    /** content area padding */
    val contentPadding: PaddingValues,

    /** gap between elements */
    val spacing: Dp,

    /** whether elastic bounce is enabled */
    val bounceEnabled: Boolean,

    /** whether the scrollbar is shown */
    val showScrollbar: Boolean
) {
    companion object {
        /** default page scrolling */
        val Default = ScrollTokens(
            contentPadding = PaddingValues(16.dp),
            spacing = 12.dp,
            bounceEnabled = true,
            showScrollbar = false
        )

        /** dense layout */
        val Dense = Default.copy(
            spacing = 8.dp
        )

        /** no padding (fullscreen, for instance) */
        val None = Default.copy(
            contentPadding = PaddingValues(0.dp)
        )
    }
}
