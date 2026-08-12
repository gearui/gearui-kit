package com.gearui.foundation.typography

import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * TextStyle - text style token
 *
 * Contains:
 * - fontSize
 * - lineHeight
 * - fontWeight
 */
data class TextStyle(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
    val fontWeight: FontWeight
)

/**
 * Typography - semantic type scale
 *
 * Reference: internal type specification
 *
 * Hierarchy:
 * - Display: oversized headings (64sp/48sp) - marketing pages
 * - Headline: large headings (36sp/28sp/24sp) - page titles
 * - Title: headings (20sp/18sp/16sp/14sp) - section titles
 * - Body: body copy (18sp/16sp/14sp/12sp/10sp) - content text
 * - Mark: emphasis (16sp/14sp/12sp/10sp) - bold body copy
 * - Link: links (16sp/14sp/12sp) - tappable text
 *
 * Rules:
 * - ✅ always use the semantic names (TitleLarge / BodyMedium)
 * - ❌ never hardcode fontSize
 */
object Typography {

    /* ---------- Display (oversized headings) ---------- */

    /** Display Large - 64sp/72sp - marketing heading */
    val DisplayLarge = TextStyle(64.sp, 72.sp, FontWeight.SemiBold)

    /** Display Medium - 48sp/56sp */
    val DisplayMedium = TextStyle(48.sp, 56.sp, FontWeight.SemiBold)

    /* ---------- Headline (large headings) ---------- */

    /** Headline Large - 36sp/44sp */
    val HeadlineLarge = TextStyle(36.sp, 44.sp, FontWeight.SemiBold)

    /** Headline Medium - 28sp/36sp */
    val HeadlineMedium = TextStyle(28.sp, 36.sp, FontWeight.SemiBold)

    /** Headline Small - 24sp/32sp */
    val HeadlineSmall = TextStyle(24.sp, 32.sp, FontWeight.SemiBold)

    /* ---------- Title (headings) ---------- */

    /** Title Extra Large - 20sp/28sp */
    val TitleExtraLarge = TextStyle(20.sp, 28.sp, FontWeight.SemiBold)

    /** Title Large - 18sp/26sp */
    val TitleLarge = TextStyle(18.sp, 26.sp, FontWeight.SemiBold)

    /** Title Medium - 16sp/24sp */
    val TitleMedium = TextStyle(16.sp, 24.sp, FontWeight.SemiBold)

    /** Title Small - 14sp/22sp */
    val TitleSmall = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /* ---------- Body (body copy) ---------- */

    /** Body Extra Large - 18sp/26sp */
    val BodyExtraLarge = TextStyle(18.sp, 26.sp, FontWeight.Normal)

    /** Body Large - 16sp/24sp */
    val BodyLarge = TextStyle(16.sp, 24.sp, FontWeight.Normal)

    /** Body Medium - 14sp/22sp (most used) */
    val BodyMedium = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /** Body Small - 12sp/20sp */
    val BodySmall = TextStyle(12.sp, 20.sp, FontWeight.Normal)

    /** Body Extra Small - 10sp/16sp */
    val BodyExtraSmall = TextStyle(10.sp, 16.sp, FontWeight.Normal)

    /* ---------- Mark (emphasis) ---------- */

    /** Mark Large - 16sp/24sp - bold */
    val MarkLarge = TextStyle(16.sp, 24.sp, FontWeight.SemiBold)

    /** Mark Medium - 14sp/22sp - bold */
    val MarkMedium = TextStyle(14.sp, 22.sp, FontWeight.SemiBold)

    /** Mark Small - 12sp/20sp - bold */
    val MarkSmall = TextStyle(12.sp, 20.sp, FontWeight.SemiBold)

    /** Mark Extra Small - 10sp/16sp - bold */
    val MarkExtraSmall = TextStyle(10.sp, 16.sp, FontWeight.SemiBold)

    /* ---------- Link ---------- */

    /** Link Large - 16sp/24sp */
    val LinkLarge = TextStyle(16.sp, 24.sp, FontWeight.Normal)

    /** Link Medium - 14sp/22sp */
    val LinkMedium = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /** Link Small - 12sp/20sp */
    val LinkSmall = TextStyle(12.sp, 20.sp, FontWeight.Normal)

    /* ---------- Caption (supporting text) ---------- */

    /** Caption - 12sp/18sp - supporting note */
    val Caption = TextStyle(12.sp, 18.sp, FontWeight.Normal)

    /* ---------- Label ---------- */

    /** Label - 10sp/16sp - label / badge */
    val Label = TextStyle(10.sp, 16.sp, FontWeight.Medium)
}
