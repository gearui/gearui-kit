package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.gearui.foundation.typography.TextStyle
import com.gearui.foundation.typography.Typography as TypographyScale

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

    /** Display Large - 64sp/72sp - marketing heading */
    val displayLarge: TextStyle,

    /** Display Medium - 48sp/56sp */
    val displayMedium: TextStyle,

    /* ---------- Headline (large headings) ---------- */

    /** Headline Large - 36sp/44sp */
    val headlineLarge: TextStyle,

    /** Headline Medium - 28sp/36sp */
    val headlineMedium: TextStyle,

    /** Headline Small - 24sp/32sp */
    val headlineSmall: TextStyle,

    /* ---------- Title (headings) ---------- */

    /** Title Extra Large - 20sp/28sp */
    val titleExtraLarge: TextStyle,

    /** Title Large - 18sp/26sp */
    val titleLarge: TextStyle,

    /** Title Medium - 16sp/24sp */
    val titleMedium: TextStyle,

    /** Title Small - 14sp/22sp */
    val titleSmall: TextStyle,

    /* ---------- Body (body copy) ---------- */

    /** Body Extra Large - 18sp/26sp */
    val bodyExtraLarge: TextStyle,

    /** Body Large - 16sp/24sp */
    val bodyLarge: TextStyle,

    /** Body Medium - 14sp/22sp (most used) */
    val bodyMedium: TextStyle,

    /** Body Small - 12sp/20sp */
    val bodySmall: TextStyle,

    /** Body Extra Small - 10sp/16sp */
    val bodyExtraSmall: TextStyle,

    /* ---------- Mark (emphasis) ---------- */

    /** Mark Large - 16sp/24sp - bold */
    val markLarge: TextStyle,

    /** Mark Medium - 14sp/22sp - bold */
    val markMedium: TextStyle,

    /** Mark Small - 12sp/20sp - bold */
    val markSmall: TextStyle,

    /** Mark Extra Small - 10sp/16sp - bold */
    val markExtraSmall: TextStyle,

    /* ---------- Link ---------- */

    /** Link Large - 16sp/24sp */
    val linkLarge: TextStyle,

    /** Link Medium - 14sp/22sp */
    val linkMedium: TextStyle,

    /** Link Small - 12sp/20sp */
    val linkSmall: TextStyle,

    /* ---------- Caption / Label ---------- */

    /** Caption - 12sp/18sp - supporting note */
    val caption: TextStyle,

    /** Label - 10sp/16sp - label / badge */
    val label: TextStyle,
)

/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

object Typographies {

    /**
     * Default Typography.
     *
     * Delegates to [com.gearui.foundation.typography.Typography], which stays the
     * single place the sizes, line heights and weights are written down. The
     * numbers used to be duplicated here, and the two copies were free to drift.
     */
    val Default = Typography(
        displayLarge = TypographyScale.DisplayLarge,
        displayMedium = TypographyScale.DisplayMedium,

        headlineLarge = TypographyScale.HeadlineLarge,
        headlineMedium = TypographyScale.HeadlineMedium,
        headlineSmall = TypographyScale.HeadlineSmall,

        titleExtraLarge = TypographyScale.TitleExtraLarge,
        titleLarge = TypographyScale.TitleLarge,
        titleMedium = TypographyScale.TitleMedium,
        titleSmall = TypographyScale.TitleSmall,

        bodyExtraLarge = TypographyScale.BodyExtraLarge,
        bodyLarge = TypographyScale.BodyLarge,
        bodyMedium = TypographyScale.BodyMedium,
        bodySmall = TypographyScale.BodySmall,
        bodyExtraSmall = TypographyScale.BodyExtraSmall,

        markLarge = TypographyScale.MarkLarge,
        markMedium = TypographyScale.MarkMedium,
        markSmall = TypographyScale.MarkSmall,
        markExtraSmall = TypographyScale.MarkExtraSmall,

        linkLarge = TypographyScale.LinkLarge,
        linkMedium = TypographyScale.LinkMedium,
        linkSmall = TypographyScale.LinkSmall,

        caption = TypographyScale.Caption,
        label = TypographyScale.Label,
    )
}
