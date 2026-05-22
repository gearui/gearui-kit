package com.gearui.theme

import androidx.compose.runtime.Immutable
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
 * Pre-1.0 legacy bridge properties were removed in Batch 13B; see
 * `docs/MIGRATION_1_0.md` for the old → new field mapping.
 */
@Immutable
data class Shapes(
    val none: Shape,
    val sm: Shape,
    val md: Shape,
    val lg: Shape,
    val xl: Shape,
    val full: Shape,
)

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
