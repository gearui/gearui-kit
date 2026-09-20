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
 * A material holds no literal colour — it names the *role* it paints, so it
 * follows the brand and dark mode without holding a second copy of either.
 * What a material owns is that role, how much it blurs, and how strongly it
 * tints.
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
    /**
     * Which theme colour this material is. See [MaterialRole].
     *
     * Defaults to [MaterialRole.Overlay] because that is what a material
     * almost always is; [Materials.Chrome] is the one exception and says so.
     */
    val role: MaterialRole = MaterialRole.Overlay,
)

/**
 * Which theme colour a material paints.
 *
 * The reference draws the line by *elevation*, not by component: everything
 * that floats above the page — menu, popover, dialog, sheet, toast — paints
 * `--color-overlay`, while cards and panels that sit in the page paint
 * `--color-surface` (heroui-native v1.0.9, `menu.css` / `popover.css` /
 * `dialog.css` against `surface.css`). Two roles, and which one a surface gets
 * is decided here rather than at each call site, where half of them had
 * drifted onto `surface`.
 *
 * That the two roles happen to hold the same value in the default theme is not
 * a reason to collapse them: the reference's own worked theme in
 * `docs/theming.md` gives overlay a lighter value than surface in both light
 * and dark, which is only expressible if the roles stay apart.
 */
@Immutable
enum class MaterialRole {
    /**
     * Bars pinned over scrolling content, which read as part of the page
     * rather than as something floating over it — so they take the page's own
     * base colour. A bar painted `surface` draws a visible band across the top
     * in a dark theme, where `surface` sits one step lighter than
     * `background`; that was a fixed bug.
     */
    Chrome,

    /** Anything that floats above the page. Paints the overlay role. */
    Overlay,
}

/**
 * The material steps, named after the surface each one is for rather than after
 * a thickness. Three, because GearUI has three kinds of surface that float over
 * content; a five-step `ultraThin…thick` scale would be taste rather than
 * measured need.
 */
object Materials {
    /** Bars pinned over scrolling content: NavBar, BottomNavBar. Thin, so the content underneath stays legible as it moves. */
    val Chrome = Material(blurRadius = 12.5f, tintAlpha = 0.72f, role = MaterialRole.Chrome)

    /** Modal surfaces that own the screen: ActionSheet, BottomSheet. Heavier, because what is behind them is dismissed context. */
    val Sheet = Material(blurRadius = 12.5f, tintAlpha = 0.82f, role = MaterialRole.Overlay)

    /** Transient surfaces anchored to a trigger: Popover, Dropdown, Tooltip. */
    val Popover = Material(blurRadius = 10f, tintAlpha = 0.78f, role = MaterialRole.Overlay)
}
