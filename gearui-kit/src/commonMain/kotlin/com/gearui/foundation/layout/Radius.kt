package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Radius - global corner radius scale (Dp values)
 *
 * Exactly matches the frozen `theme.Shapes` scale (same names, same values), so there is only one standard:
 *
 *   none = 0
 *   sm   = 4dp
 *   md   = 6dp
 *   lg   = 8dp
 *   xl   = 12dp
 *   full = 9999dp (pill / fully rounded)
 *
 * `Shapes` supplies `Shape` instances (for `Modifier.clip`); `Radius` supplies the matching `Dp`
 * values (for places that need a Dp, such as component tokens). The numbers stay in sync.
 */
object Radius {
    /** 0dp - square corners */
    val none: Dp = 0.dp

    /** 4dp - small radius (tags, chips, dense controls) */
    val sm: Dp = 4.dp

    /** 6dp - default radius (inputs, default surfaces) */
    val md: Dp = 6.dp

    /** 8dp - large radius (buttons, cards; the GearUI mobile default) */
    val lg: Dp = 8.dp

    /** 12dp - extra large radius (sheets, large cards, emphasised surfaces) */
    val xl: Dp = 12.dp

    /** 9999dp - pill / fully rounded */
    val full: Dp = 9999.dp

    /** 9999dp - circular (round avatars and badges); identical to [full] */
    val circle: Dp = 9999.dp
}
