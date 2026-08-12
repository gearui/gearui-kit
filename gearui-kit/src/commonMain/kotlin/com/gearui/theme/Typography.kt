package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * GearUI Framework semantic type scale
 *
 * Reference: internal type specification
 *
 * ⚠️ Rule:
 * Component code uses ONLY these semantic text styles.
 * No `fontSize = xx.sp` and no hardcoded values.
 *
 * Usage:
 * val typography = Theme.typography
 * Text(text, style = Typography.BodyMedium)
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
     * Default Typography
     *
     * Sizes, line heights and weights fully aligned
     */
    val Default = Typography(

        // Display (oversized headings)
        displayLarge = TextStyle(
            fontSize = 64.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.SemiBold
        ),
        displayMedium = TextStyle(
            fontSize = 48.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Headline (large headings)
        headlineLarge = TextStyle(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.SemiBold
        ),
        headlineMedium = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold
        ),
        headlineSmall = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Title (headings)
        titleExtraLarge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),

        // Body (body copy)
        bodyExtraLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyExtraSmall = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal
        ),

        // Mark (emphasis)
        markLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markSmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markExtraSmall = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Link
        linkLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        linkMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),
        linkSmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),

        // Caption / Label
        caption = TextStyle(
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal
        ),
        label = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        ),
    )
}
