package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.gearui.foundation.typography.TextStyle

/**
 * GearUI semantic type scale — the themeable typography axis.
 *
 * This is the type [Theme.typography] resolves to, and the type a brand
 * replaces when it wants different type without forking components.
 *
 * It carries [com.gearui.foundation.typography.TextStyle], the same token type
 * the `Text` primitive accepts. Previously it carried Kuikly's `TextStyle`
 * instead, which meant the themed scale and the static scale were structurally
 * incompatible — nothing in the library could read a field off it, and every
 * component reached past the theme to the static
 * [com.gearui.foundation.typography.Typography] object. Unifying the two types
 * is what makes the axis actually replaceable.
 *
 * Usage:
 * ```kotlin
 * Text(text, style = Theme.typography.bodyMedium)
 * ```
 *
 * ⚠️ Component code uses ONLY these semantic styles.
 * No `fontSize = xx.sp` and no hardcoded values.
 */
@Immutable
data class Typography(

    /* ---------- Display (oversized headings) ---------- */

    /** Display Large - marketing heading */
    val displayLarge: TextStyle,

    /** Display Medium */
    val displayMedium: TextStyle,

    /* ---------- Headline (large headings) ---------- */

    /** Headline Large */
    val headlineLarge: TextStyle,

    /** Headline Medium */
    val headlineMedium: TextStyle,

    /** Headline Small */
    val headlineSmall: TextStyle,

    /* ---------- Title (headings) ---------- */

    /** Title Extra Large */
    val titleExtraLarge: TextStyle,

    /** Title Large */
    val titleLarge: TextStyle,

    /** Title Medium */
    val titleMedium: TextStyle,

    /** Title Small */
    val titleSmall: TextStyle,

    /* ---------- Body (body copy) ---------- */

    /** Body Extra Large */
    val bodyExtraLarge: TextStyle,

    /** Body Large */
    val bodyLarge: TextStyle,

    /** Body Medium (most used) */
    val bodyMedium: TextStyle,

    /** Body Small */
    val bodySmall: TextStyle,

    /** Body Extra Small */
    val bodyExtraSmall: TextStyle,

    /* ---------- Mark (emphasis) ---------- */

    /** Mark Large - bold */
    val markLarge: TextStyle,

    /** Mark Medium - bold */
    val markMedium: TextStyle,

    /** Mark Small - bold */
    val markSmall: TextStyle,

    /** Mark Extra Small - bold */
    val markExtraSmall: TextStyle,

    /* ---------- Link ---------- */

    /** Link Large */
    val linkLarge: TextStyle,

    /** Link Medium */
    val linkMedium: TextStyle,

    /** Link Small */
    val linkSmall: TextStyle,

    /* ---------- Caption / Label ---------- */

    /** Caption - supporting note */
    val caption: TextStyle,

    /** Label - label / badge */
    val label: TextStyle,
)

/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

object Typographies {
    /**
     * The type scale GearUI uses unless a host picks another one: the reference scale
     * (HeroUI Native), 12/16, 14/20, 16/24, 18/28, with emphasis at 500.
     *
     * It is one scale on every platform on purpose. A per-platform scale made the same
     * screen a different size on iOS than on the web, so a layout checked on one could
     * not be trusted on the other, and the reference's own metrics could never be met.
     */
    val Default: Typography = TypographyProfiles.Reference

    /**
     * The reference scale, named for callers that want to be explicit. Same object as
     * [Default].
     */
    val Reference: Typography = TypographyProfiles.Reference

    /**
     * The platform scale GearUI used before: iOS metrics, 17 body over a 20 title, with
     * emphasis at 600. Pick it for an app that has to sit beside the system's own
     * controls, or to keep a shipped layout from reflowing after upgrading.
     *
     * ```kotlin
     * GearUITheme(typography = Typographies.Platform) { App() }
     * ```
     */
    val Platform: Typography = TypographyProfiles.Native

    /** The denser web scale (15 body), for the web renderer. */
    val Web: Typography = TypographyProfiles.Web
}
