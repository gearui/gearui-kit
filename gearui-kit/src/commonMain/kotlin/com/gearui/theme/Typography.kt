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

internal expect fun platformTypography(): Typography

object Typographies {
    /** Native and Web type profiles are generated from the same DTCG source. */
    val Default: Typography = platformTypography()

    /**
     * The reference's own scale (Tailwind steps: 12/16, 14/20, 16/24, 18/28), with
     * medium at 500 rather than 600.
     *
     * [Default] stays on the platform scale — 17/15/20 on iOS — because that is what a
     * phone's own controls use and what apps built on GearUI already ship; swapping it
     * would reflow every screen in every consumer at once. Pick this profile when
     * matching the reference's metrics matters more than matching the platform, and pick
     * it for the whole app rather than per screen: two scales in one product read as a
     * bug, not as a choice.
     *
     * ```kotlin
     * GearUITheme(typography = Typographies.Reference) { App() }
     * ```
     */
    val Reference: Typography = TypographyProfiles.Reference
}
