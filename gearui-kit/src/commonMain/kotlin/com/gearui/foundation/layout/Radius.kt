package com.gearui.foundation.layout

import com.gearui.foundation.control.ControlGeometry
import com.gearui.unit.Dp

/**
 * The default corner radius scale as `Dp`, for code that needs a number rather than a
 * `Shape` — a component token, a custom `RoundedCornerShape` with mixed corners.
 *
 * Read from the same generated geometry as `ShapesDefault.Default`, so the two cannot
 * drift apart again (they did: this object kept the pre-beta5 4 / 6 / 8 / 12 after the
 * shapes moved to HeroUI Native's scale). It is the *default* scale: a theme that
 * replaces `shapes` is read through `Theme.shapes`, which is what components should use.
 */
object Radius {
    val none: Dp = ControlGeometry.radiusNone

    /** Small controls, chips, tags. */
    val sm: Dp = ControlGeometry.radiusSmall

    /** Fields. */
    val md: Dp = ControlGeometry.radiusMedium

    /** Buttons and cards. */
    val lg: Dp = ControlGeometry.radiusDefault

    /** Dialogs and overlay surfaces. */
    val xl: Dp = ControlGeometry.radiusOverlay

    /** Pill / fully rounded. */
    val full: Dp = ControlGeometry.radiusFull

    /** Circular avatars and badges; identical to [full]. */
    val circle: Dp = ControlGeometry.radiusFull
}
