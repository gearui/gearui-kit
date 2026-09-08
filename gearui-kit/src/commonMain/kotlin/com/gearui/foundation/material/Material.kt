package com.gearui.foundation.material

import androidx.compose.runtime.Immutable

/**
 * Frosted-glass materials.
 *
 * A material is a translucent surface that blurs whatever sits behind it. It is
 * the one place GearUI borrows a *physical* effect from iOS rather than a
 * layout convention, and it is deliberately not Liquid Glass: there is no
 * refraction, no specular edge and no lensing, only a Gaussian blur with a
 * surface tint over it. See DESIGN_SYSTEM_SPEC §0.1 and §12.
 *
 * Materials carry no colour. The tint is always `Theme.colors.surface`, so a
 * material follows the brand and dark mode without holding a second copy of
 * either. What a material owns is how much it blurs and how strongly it tints.
 */
@Immutable
data class Material(
    /**
     * Gaussian radius in points.
     *
     * KuiklyUI clamps this at 12.5 (`BlurAttr.blurRadius`), so 12.5 is the
     * strongest blur reachable on any platform, not an arbitrary ceiling.
     */
    val blurRadius: Float,
    /**
     * Alpha of the surface tint painted over the blur.
     *
     * Only applied when the blur is actually running. Without it a tint is just
     * a translucent wash over arbitrary content, which is why it does not
     * survive into the fallback — see [com.gearui.foundation.material.MaterialSurface].
     */
    val tintAlpha: Float,
)

/**
 * The material steps, named after the surface each one is for rather than after
 * a thickness. Three, because GearUI has three kinds of surface that float over
 * content; a five-step `ultraThin…thick` scale would be taste rather than
 * measured need.
 */
object Materials {
    /** Bars pinned over scrolling content: NavBar, BottomNavBar. Thin, so the content underneath stays legible as it moves. */
    val Chrome = Material(blurRadius = 12.5f, tintAlpha = 0.72f)

    /** Modal surfaces that own the screen: ActionSheet, BottomSheet. Heavier, because what is behind them is dismissed context. */
    val Sheet = Material(blurRadius = 12.5f, tintAlpha = 0.82f)

    /** Transient surfaces anchored to a trigger: Popover, Dropdown, Tooltip. */
    val Popover = Material(blurRadius = 10f, tintAlpha = 0.78f)
}
