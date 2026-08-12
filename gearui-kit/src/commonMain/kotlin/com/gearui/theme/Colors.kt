package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * GearUI semantic color model (24 fields, business-neutral).
 *
 * Roles are grouped into:
 *  - Surfaces: background / surface / card / popover / muted (+ their foregrounds)
 *  - Brand:    primary / secondary / accent (+ their foregrounds)
 *  - Feedback: destructive (+ foreground) / success / warning / info
 *  - Controls: border / input / ring
 *
 * Component state colors (hover / pressed / focused / disabled / invalid /
 * selected / loading) live in component-specific `XxxTokens`, NOT in this
 * core model. See `docs/TOKEN_FREEZE_DECISIONS.md` Decision 1.
 *
 * Pre-1.0 legacy bridge properties were removed in Batch 13A; see
 * `docs/MIGRATION_1_0.md` for the old → new field mapping.
 */
@Immutable
data class Colors(
    // ---- Surfaces ----
    val background: Color,
    val foreground: Color,
    val surface: Color,
    val surfaceForeground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val muted: Color,
    val mutedForeground: Color,

    // ---- Brand ----
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val accent: Color,
    val accentForeground: Color,

    // ---- Feedback ----
    val destructive: Color,
    val destructiveForeground: Color,
    val success: Color,
    val successForeground: Color,
    val warning: Color,
    val warningForeground: Color,
    val info: Color,
    val infoForeground: Color,

    // ---- Controls ----
    val border: Color,
    val input: Color,
    val ring: Color,
)

/* ---------------------------------------------------------------------- */
/* Theme spec                                                              */
/* ---------------------------------------------------------------------- */

@Immutable
data class ThemeSpec(
    val colors: Colors
)

/* ---------------------------------------------------------------------- */
/* Built-in Light / Dark themes                                            */
/* ---------------------------------------------------------------------- */

object Themes {

    val Light = ThemeSpec(
        colors = Colors(
            background = Color(0xFFFFFFFF),
            foreground = Color(0xFF09090B),
            surface = Color(0xFFFFFFFF),
            surfaceForeground = Color(0xFF09090B),
            card = Color(0xFFFFFFFF),
            cardForeground = Color(0xFF09090B),
            popover = Color(0xFFFFFFFF),
            popoverForeground = Color(0xFF09090B),
            muted = Color(0xFFF4F4F5),
            mutedForeground = Color(0xFF52525B),

            primary = Color(0xFF18181B),
            primaryForeground = Color(0xFFFFFFFF),
            secondary = Color(0xFFF4F4F5),
            secondaryForeground = Color(0xFF18181B),
            accent = Color(0xFFF4F4F5),
            accentForeground = Color(0xFF18181B),

            destructive = Color(0xFFDC2626),
            destructiveForeground = Color(0xFFFFFFFF),
            success = Color(0xFF16A34A),
            successForeground = Color(0xFFFFFFFF),
            warning = Color(0xFFF59E0B),
            warningForeground = Color(0xFF09090B),
            info = Color(0xFF2563EB),
            infoForeground = Color(0xFFFFFFFF),

            border = Color(0xFFE4E4E7),
            input = Color(0xFFE4E4E7),
            ring = Color(0xFF18181B),
        )
    )

    val Dark = ThemeSpec(
        colors = Colors(
            // Neutral grey ramp: truly neutral (R=G=B), matching iOS, with the old zinc blue cast removed
            background = Color(0xFF0A0A0A),
            foreground = Color(0xFFFAFAFA),
            surface = Color(0xFF121212),
            surfaceForeground = Color(0xFFFAFAFA),
            card = Color(0xFF121212),
            cardForeground = Color(0xFFFAFAFA),
            popover = Color(0xFF1C1C1C),
            popoverForeground = Color(0xFFFAFAFA),
            muted = Color(0xFF1C1C1C),
            mutedForeground = Color(0xFFA1A1A1),

            primary = Color(0xFFFAFAFA),
            primaryForeground = Color(0xFF0A0A0A),
            secondary = Color(0xFF272727),
            secondaryForeground = Color(0xFFFAFAFA),
            accent = Color(0xFF272727),
            accentForeground = Color(0xFFFAFAFA),

            destructive = Color(0xFFF87171),
            destructiveForeground = Color(0xFFFFFFFF),
            success = Color(0xFF22C55E),
            successForeground = Color(0xFF0A0A0A),
            warning = Color(0xFFF59E0B),
            warningForeground = Color(0xFF0A0A0A),
            info = Color(0xFF60A5FA),
            infoForeground = Color(0xFF0A0A0A),

            border = Color(0xFF333333),
            input = Color(0xFF333333),
            ring = Color(0xFFFAFAFA),
        )
    )
}
