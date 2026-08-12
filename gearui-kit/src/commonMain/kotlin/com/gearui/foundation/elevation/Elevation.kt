package com.gearui.foundation.elevation

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Shadow depth scale (Dp values).
 *
 * GearUI's visual language is border-first: **things sitting flat on the page
 * do not cast shadows**. Cards, cells, lists and inputs separate themselves
 * with `colors.border`, not with elevation. A shadow says "this floats above
 * the page", so only overlay-like components may use one.
 *
 * Four steps, ordered by distance from the page:
 *
 *   none     = 0dp  — flat (the default); also the disabled state of controls
 *   raised   = 4dp  — controls attached to content (slider thumb, back-to-top)
 *                     and light overlays anchored to their trigger (Popover,
 *                     ContextMenu, Snackbar, NavigationMenu)
 *   floating = 6dp  — panels detached from their trigger: Select / Cascader /
 *                     TreeSelect dropdowns, Notification
 *   modal    = 8dp  — layers that take focus: Dialog, Tour cards
 *
 * The order has to be self-consistent: modal always above floating, floating
 * always above raised. It was not before this scale existed — Dialog sat at
 * 6dp while a TreeSelect dropdown sat at 8dp, so the modal rendered *below*
 * the dropdown.
 *
 * ⚠️ Do not use `Spacing.*` as a shadow depth. Both are Dp and their values
 * collide (`Spacing.xs` is also 4dp), but they are different semantic axes;
 * retuning the spacing scale would drag every shadow along with it.
 */
object Elevation {
    /** 0dp - flat (default / disabled) */
    val none: Dp = 0.dp

    /** 4dp - attached controls and light overlays */
    val raised: Dp = 4.dp

    /** 6dp - floating panels */
    val floating: Dp = 6.dp

    /** 8dp - modal layers */
    val modal: Dp = 8.dp
}
