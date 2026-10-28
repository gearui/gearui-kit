package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.gearui.foundation.control.ControlGeometry

/**
 * GearUI semantic shape roles.
 *
 * Current mapping (see `docs/DESIGN_SYSTEM.md` §4):
 *
 *   none = 0       — square, no rounding (banner, sectioned full-bleed)
 *   sm   = 8.dp    — tags, chips, dense controls
 *   md   = 12.dp   — compact controls
 *   lg   = 14.dp   — regular controls and cards
 *   controlLarge = 16.dp — large buttons and fields
 *   xl   = 24.dp   — sheets, large cards, prominent surfaces
 *   full = 9999.dp — capsule buttons, fully rounded segmented controls
 *   sheet = 32.dp top corners — edge-anchored overlays (bottom sheet, action sheet)
 *
 * Use `CircleShape` directly (e.g. `Modifier.clip(CircleShape)`) for
 * round avatars / badges — there is no dedicated `circle` token.
 *
 * Pre-1.0 legacy bridge properties were removed in Batch 13B; see
 * `docs/DESIGN_SYSTEM.md` §9 for the current API-change rules.
 */
@Immutable
data class Shapes(
    val none: Shape,
    val sm: Shape,
    val md: Shape,
    val lg: Shape,
    val xl: Shape,
    val full: Shape,
    /** Large controls, distinct from regular controls and overlay surfaces. */
    val controlLarge: Shape = lg,
    /**
     * Edge-anchored overlays — bottom sheet, action sheet, the calendar sheet. Only the
     * corners facing the content are rounded. A square-corner theme sets this square too.
     */
    val sheet: Shape = RoundedCornerShape(topStart = ControlGeometry.radiusSheet, topEnd = ControlGeometry.radiusSheet),
)

/* ---------------------------------------------------------------------- */
/* Default shape set                                                       */
/* ---------------------------------------------------------------------- */

object ShapesDefault {
    val Default = Shapes(
        none = RoundedCornerShape(ControlGeometry.radiusNone),
        sm = RoundedCornerShape(ControlGeometry.radiusSmall),
        md = RoundedCornerShape(ControlGeometry.radiusMedium),
        lg = RoundedCornerShape(ControlGeometry.radiusDefault),
        xl = RoundedCornerShape(ControlGeometry.radiusOverlay),
        full = RoundedCornerShape(ControlGeometry.radiusFull),
        controlLarge = RoundedCornerShape(ControlGeometry.radiusLarge),
    )
}
