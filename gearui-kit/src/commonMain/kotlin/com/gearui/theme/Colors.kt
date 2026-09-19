package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.foundation.button.ButtonColors
import com.gearui.foundation.button.DefaultButtonColors

/**
 * GearUI semantic color model (business-neutral).
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

    /**
     * Separator lines and the sheet handle (reference `--separator`). Stronger than
     * [border], which outlines surfaces. Defaults to [border] for hand-built themes.
     */
    val separator: Color = border,
)

/* ---------------------------------------------------------------------- */
/* Theme spec                                                              */
/* ---------------------------------------------------------------------- */

@Immutable
data class ThemeSpec(
    val colors: Colors,
    /** Null derives neutral button states from this theme's semantic colors. */
    val buttonColors: ButtonColors? = null,
    /** Null derives Input states from semantic colors for custom brands. */
    val inputColors: com.gearui.foundation.field.InputColors? = null,
)

/* ---------------------------------------------------------------------- */
/* Built-in Light / Dark themes                                            */
/* ---------------------------------------------------------------------- */

object Themes {

    val Light = ThemeSpec(
        inputColors = com.gearui.foundation.field.DefaultInputColors.Light,
        buttonColors = DefaultButtonColors.Light,
        colors = Colors(
            background = DefaultPalette.lightBackground,
            foreground = DefaultPalette.lightForeground,
            surface = DefaultPalette.lightSurface,
            surfaceForeground = DefaultPalette.lightSurfaceForeground,
            card = DefaultPalette.lightSurface,
            cardForeground = DefaultPalette.lightForeground,
            popover = DefaultPalette.lightOverlay,
            popoverForeground = DefaultPalette.lightForeground,
            muted = DefaultPalette.lightMuted,
            mutedForeground = DefaultPalette.lightMutedForeground,

            primary = DefaultPalette.lightPrimary,
            primaryForeground = DefaultPalette.lightPrimaryForeground,
            secondary = DefaultPalette.lightButtonBackground,
            secondaryForeground = DefaultPalette.lightButtonContent,
            accent = DefaultPalette.lightSurface,
            accentForeground = DefaultPalette.lightSurfaceForeground,

            destructive = DefaultPalette.lightDestructive,
            destructiveForeground = DefaultPalette.lightDestructiveForeground,
            success = DefaultPalette.lightSuccess,
            successForeground = DefaultPalette.lightSuccessForeground,
            warning = DefaultPalette.lightWarning,
            warningForeground = DefaultPalette.lightWarningForeground,
            info = Color(0xFF2563EB),
            infoForeground = Color(0xFFFFFFFF),

            border = DefaultPalette.lightBorder,
            separator = DefaultPalette.lightSeparator,
            input = DefaultPalette.lightInputBorder,
            ring = DefaultPalette.lightRing,
        )
    )

    val Dark = ThemeSpec(
        inputColors = com.gearui.foundation.field.DefaultInputColors.Dark,
        buttonColors = DefaultButtonColors.Dark,
        colors = Colors(
            background = DefaultPalette.darkBackground,
            foreground = DefaultPalette.darkForeground,
            surface = DefaultPalette.darkSurface,
            surfaceForeground = DefaultPalette.darkSurfaceForeground,
            card = DefaultPalette.darkSurface,
            cardForeground = DefaultPalette.darkForeground,
            popover = DefaultPalette.darkOverlay,
            popoverForeground = DefaultPalette.darkForeground,
            muted = DefaultPalette.darkMuted,
            mutedForeground = DefaultPalette.darkMutedForeground,

            primary = DefaultPalette.darkPrimary,
            primaryForeground = DefaultPalette.darkPrimaryForeground,
            secondary = DefaultPalette.darkButtonBackground,
            secondaryForeground = DefaultPalette.darkButtonContent,
            accent = DefaultPalette.darkSurface,
            accentForeground = DefaultPalette.darkSurfaceForeground,

            destructive = DefaultPalette.darkDestructive,
            destructiveForeground = DefaultPalette.darkDestructiveForeground,
            success = DefaultPalette.darkSuccess,
            successForeground = DefaultPalette.darkSuccessForeground,
            warning = DefaultPalette.darkWarning,
            warningForeground = DefaultPalette.darkWarningForeground,
            info = Color(0xFF60A5FA),
            infoForeground = Color(0xFF0A0A0A),

            border = DefaultPalette.darkBorder,
            separator = DefaultPalette.darkSeparator,
            input = DefaultPalette.darkInputBorder,
            ring = DefaultPalette.darkRing,
        )
    )
}

/** Grouped pages follow the selected theme rather than a fixed iOS palette. */
val Colors.groupedBackground: Color
    get() = background
