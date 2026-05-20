package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * GearUI semantic shape scale (six steps).
 *
 * Scale (see `docs/TOKEN_FREEZE_DECISIONS.md` Decision 2):
 *
 *   none = 0       — square, no rounding (banner, sectioned full-bleed)
 *   sm   = 4.dp    — tags, chips, dense controls
 *   md   = 6.dp    — inputs, default surface rounding
 *   lg   = 8.dp    — buttons, cards (GearUI mobile default)
 *   xl   = 12.dp   — sheets, large cards, prominent surfaces
 *   full = 9999.dp — capsule buttons, fully rounded segmented controls
 *
 * Use `CircleShape` directly (e.g. `Modifier.clip(CircleShape)`) for
 * round avatars / badges — there is no dedicated `circle` token.
 *
 * Legacy properties (`small / default / large / extraLarge / round /
 * circle`) keep compiling but emit deprecation warnings and will be
 * removed before 1.0 RC. Note that legacy values were 3 / 9.dp; the new
 * `sm` and `lg` are 4 / 8.dp respectively — visual difference is
 * negligible on screen.
 */
@Immutable
data class Shapes(
    val none: Shape,
    val sm: Shape,
    val md: Shape,
    val lg: Shape,
    val xl: Shape,
    val full: Shape,
) {
    // ------------------------------------------------------------------
    // Legacy bridge properties (pre-1.0). Will be removed before 1.0 RC.
    // ------------------------------------------------------------------

    @Deprecated("Use sm", ReplaceWith("sm"))
    val small: Shape get() = sm

    @Deprecated("Use md", ReplaceWith("md"))
    val default: Shape get() = md

    @Deprecated("Use lg", ReplaceWith("lg"))
    val large: Shape get() = lg

    @Deprecated("Use xl", ReplaceWith("xl"))
    val extraLarge: Shape get() = xl

    @Deprecated("Use full", ReplaceWith("full"))
    val round: Shape get() = full

    @Deprecated("Use CircleShape directly: Modifier.clip(CircleShape)")
    val circle: Shape get() = CircleShape
}

/* ---------------------------------------------------------------------- */
/* Default shape set                                                       */
/* ---------------------------------------------------------------------- */

object ShapesDefault {
    val Default = Shapes(
        none = RoundedCornerShape(0.dp),
        sm = RoundedCornerShape(4.dp),
        md = RoundedCornerShape(6.dp),
        lg = RoundedCornerShape(8.dp),
        xl = RoundedCornerShape(12.dp),
        full = RoundedCornerShape(9999.dp),
    )
}
