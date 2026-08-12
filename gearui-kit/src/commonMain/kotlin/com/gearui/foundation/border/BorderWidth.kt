package com.gearui.foundation.border

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Stroke weight scale (Dp values).
 *
 * GearUI is border-first: hierarchy is carried by outlines rather than shadows
 * (see [com.gearui.foundation.elevation.Elevation]). Since strokes do that
 * work, they deserve a scale of their own — before this existed, Shapes,
 * Elevation and Spacing each had named steps while stroke weight was 87
 * scattered literals of `1.dp`, `2.dp` and `0.5.dp`.
 *
 * Three steps plus zero, ordered by visual weight:
 *
 *   none     = 0dp    — no outline
 *   hairline = 0.5dp  — dividers, table grid lines, the card outline
 *   thin     = 1dp    — the default: inputs, buttons, panels, cards
 *   thick    = 2dp    — emphasis rings: timeline nodes, step markers, the
 *                       dots that need to be lifted out of their background
 *
 * ⚠️ Do **not** add a focus step. A field's stroke weight must stay constant
 * across focus and error, or the content box resizes and the layout jumps; see
 * [com.gearui.foundation.field.FieldTokens].
 */
object BorderWidth {
    /** 0dp - no outline */
    val none: Dp = 0.dp

    /** 0.5dp - hairline: dividers, grid lines */
    val hairline: Dp = 0.5.dp

    /** 1dp - the default outline */
    val thin: Dp = 1.dp

    /** 2dp - emphasis ring */
    val thick: Dp = 2.dp
}
