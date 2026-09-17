package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.border.BorderWidth

/**
 * CardTokens - Card sizing spec
 *
 *
 * ⚠️ Note: colours are not defined here; use Theme.colors.surface
 *
 * Use cases:
 * - aggregated information
 * - form groups
 * - content modules
 */
data class CardTokens(
    val cornerRadius: Dp,
    val padding: Dp,
    val borderWidth: Dp,
    val elevation: Float
)

object CardDefaults {
    /**
     */
    val Default = CardTokens(
        cornerRadius = ControlGeometry.radiusOverlay,
        padding = ControlGeometry.cardPadding,
        // Explicit fallback until layered Native surface shadows are supported.
        borderWidth = BorderWidth.hairline,
        elevation = 0f
    )

}
