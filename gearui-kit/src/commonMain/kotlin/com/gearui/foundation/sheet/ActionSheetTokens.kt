package com.gearui.foundation.sheet

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ActionSheet geometry.
 *
 * These are sizes, not gaps, which is the whole reason the object exists. The
 * grid tile, the red-dot diameter and the dot's offset used to be taken from
 * the `Spacing` scale because it happened to hold the right numbers — an 8dp
 * dot written as `Spacing.sm`, a 48dp tile as `Spacing.huge`. Spacing describes
 * density and is a candidate theme axis; a status dot is not denser or airier,
 * it is 8dp across. Borrowing across the two means a brand that loosens spacing
 * silently inflates the dots.
 *
 * Geometry stays static by design. See DESIGN_SYSTEM_SPEC §6.1.
 */
data class ActionSheetTokens(
    /** Square tile behind a grid item's glyph. */
    val gridIconTile: Dp,
    /** Diameter of the unread red dot. */
    val redDot: Dp,
    /** How far the dot overhangs the tile's top-right corner. */
    val redDotOverhang: Dp,
)

object ActionSheetDefaults {
    val Default = ActionSheetTokens(
        gridIconTile = 48.dp,
        redDot = 8.dp,
        redDotOverhang = 2.dp,
    )
}
