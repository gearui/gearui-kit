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
 * Legacy properties (`textPrimary`, `danger`, `surfaceVariant`, ...) keep
 * compiling against the pre-1.0 35-field palette but emit deprecation
 * warnings. They will be removed before 1.0 RC.
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
    val warning: Color,
    val info: Color,

    // ---- Controls ----
    val border: Color,
    val input: Color,
    val ring: Color,
) {
    // ------------------------------------------------------------------
    // Legacy bridge properties (pre-1.0). Will be removed before 1.0 RC.
    // ------------------------------------------------------------------

    // Surfaces
    @Deprecated("Use muted", ReplaceWith("muted"))
    val surfaceVariant: Color get() = muted

    @Deprecated("Use surface or card", ReplaceWith("surface"))
    val surfaceComponent: Color get() = surface

    @Deprecated("Use popover", ReplaceWith("popover"))
    val overlay: Color get() = popover

    @Deprecated("Overlay scrim is a runtime token; not part of Colors.")
    val mask: Color get() = Color(0x6609090B)

    // Content
    @Deprecated("Use foreground", ReplaceWith("foreground"))
    val textPrimary: Color get() = foreground

    @Deprecated("Use mutedForeground", ReplaceWith("mutedForeground"))
    val textSecondary: Color get() = mutedForeground

    @Deprecated("Use mutedForeground", ReplaceWith("mutedForeground"))
    val textPlaceholder: Color get() = mutedForeground

    @Deprecated("State color moved to ComponentTokens; reads mutedForeground.")
    val textDisabled: Color get() = mutedForeground

    @Deprecated("Use primaryForeground", ReplaceWith("primaryForeground"))
    val textAnti: Color get() = primaryForeground

    @Deprecated("Use primary directly for link/brand text", ReplaceWith("primary"))
    val textBrand: Color get() = primary

    @Deprecated("Use foreground", ReplaceWith("foreground"))
    val iconPrimary: Color get() = foreground

    @Deprecated("Use mutedForeground", ReplaceWith("mutedForeground"))
    val iconSecondary: Color get() = mutedForeground

    // Brand states
    @Deprecated("Use primaryForeground", ReplaceWith("primaryForeground"))
    val onPrimary: Color get() = primaryForeground

    @Deprecated("State color moved to ComponentTokens (e.g. ButtonTokens.hoverBackground).")
    val primaryHover: Color get() = primary

    @Deprecated("State color moved to ComponentTokens (e.g. ButtonTokens.pressedBackground).")
    val primaryActive: Color get() = primary

    @Deprecated("State color moved to ComponentTokens; use muted as a fallback.")
    val primaryLight: Color get() = muted

    @Deprecated("State color moved to ComponentTokens; use mutedForeground as a fallback.")
    val primaryDisabled: Color get() = mutedForeground

    // Borders
    @Deprecated("Use border or input (input is for editable control borders)", ReplaceWith("border"))
    val stroke: Color get() = border

    @Deprecated("Use border", ReplaceWith("border"))
    val divider: Color get() = border

    // Disabled state
    @Deprecated("State color moved to ComponentTokens.<role>.disabledForeground")
    val disabled: Color get() = mutedForeground

    @Deprecated("State color moved to ComponentTokens.<role>.disabledBackground")
    val disabledContainer: Color get() = muted

    // Feedback aliases
    @Deprecated("Use destructive", ReplaceWith("destructive"))
    val danger: Color get() = destructive

    @Deprecated("Subtle feedback backgrounds moved to ComponentTokens.<feedback>.subtleBackground.")
    val successLight: Color get() = success.copy(alpha = 0.12f)

    @Deprecated("Subtle feedback backgrounds moved to ComponentTokens.<feedback>.subtleBackground.")
    val warningLight: Color get() = warning.copy(alpha = 0.12f)

    @Deprecated("Subtle feedback backgrounds moved to ComponentTokens.<feedback>.subtleBackground.")
    val dangerLight: Color get() = destructive.copy(alpha = 0.12f)

    @Deprecated("Subtle feedback backgrounds moved to ComponentTokens.<feedback>.subtleBackground.")
    val infoLight: Color get() = info.copy(alpha = 0.12f)

    // Inverse (toast / snackbar)
    @Deprecated("Inverse surfaces moved to component-specific tokens (e.g. ToastTokens.surface).")
    val inverseSurface: Color get() = foreground

    @Deprecated("Inverse surfaces moved to component-specific tokens (e.g. ToastTokens.surfaceForeground).")
    val inverseOnSurface: Color get() = background
}

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
            warning = Color(0xFFF59E0B),
            info = Color(0xFF2563EB),

            border = Color(0xFFE4E4E7),
            input = Color(0xFFE4E4E7),
            ring = Color(0xFF18181B),
        )
    )

    val Dark = ThemeSpec(
        colors = Colors(
            background = Color(0xFF09090B),
            foreground = Color(0xFFFAFAFA),
            surface = Color(0xFF111217),
            surfaceForeground = Color(0xFFFAFAFA),
            card = Color(0xFF111217),
            cardForeground = Color(0xFFFAFAFA),
            popover = Color(0xFF1A1C24),
            popoverForeground = Color(0xFFFAFAFA),
            muted = Color(0xFF1A1C24),
            mutedForeground = Color(0xFFA1A1AA),

            primary = Color(0xFFFAFAFA),
            primaryForeground = Color(0xFF09090B),
            secondary = Color(0xFF27272A),
            secondaryForeground = Color(0xFFFAFAFA),
            accent = Color(0xFF27272A),
            accentForeground = Color(0xFFFAFAFA),

            destructive = Color(0xFFF87171),
            destructiveForeground = Color(0xFFFFFFFF),
            success = Color(0xFF22C55E),
            warning = Color(0xFFF59E0B),
            info = Color(0xFF60A5FA),

            border = Color(0xFF2F3340),
            input = Color(0xFF2F3340),
            ring = Color(0xFFFAFAFA),
        )
    )
}
