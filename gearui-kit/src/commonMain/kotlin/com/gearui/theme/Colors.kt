package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
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
 * core model. See `docs/DESIGN_SYSTEM.md` §3.
 *
 * Pre-1.0 legacy bridge properties were removed in Batch 13A; see
 * `docs/DESIGN_SYSTEM.md` §9 for the current API-change rules.
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

    /**
     * Selected segment of a segmented control (reference `--segment`): white on the
     * light gray track, a lifted gray in dark. Defaults to [surface].
     */
    val segment: Color = surface,

    /**
     * The hairline *inside* a surface: between the rows of a list, a card's internal
     * rules (reference `--color-separator-secondary`).
     *
     * [separator] is the strong line — the sheet grabber, a divider that separates
     * whole sections, a link's underline. Using it between list rows draws a grid over
     * the card: on white it lands near #AAA, where the platform's own lists sit near
     * #D8D8D8, which is what this role is.
     */
    val separatorSecondary: Color = lerp(surface, surfaceForeground, 0.15f),

    /**
     * Soft status fills and their foregrounds (reference `--color-*-soft` and
     * `--color-*-soft-foreground`): a tinted chip or banner that still reads as text
     * rather than as a solid badge. A soft fill is the status colour at 15%; a soft
     * foreground is the status colour mixed toward [foreground], enough to stay legible
     * on a light fill without turning grey.
     *
     * The built-in themes take the mixed values straight from the tokens. The defaults
     * here derive them with the same ratios, so a brand theme that only sets [primary]
     * still gets a matching soft pair instead of a colour that belongs to another brand.
     */
    val primarySoft: Color = primary.copy(alpha = SoftColorMix.FILL_ALPHA),
    val primarySoftForeground: Color = lerp(primary, foreground, 1f - SoftColorMix.ACCENT),
    val successSoft: Color = success.copy(alpha = SoftColorMix.FILL_ALPHA),
    val successSoftForeground: Color = lerp(success, foreground, 1f - SoftColorMix.SUCCESS),
    val warningSoft: Color = warning.copy(alpha = SoftColorMix.FILL_ALPHA),
    val warningSoftForeground: Color = lerp(warning, foreground, 1f - SoftColorMix.WARNING),
    val destructiveSoft: Color = destructive.copy(alpha = SoftColorMix.FILL_ALPHA),
    val destructiveSoftForeground: Color = lerp(destructive, foreground, 1f - SoftColorMix.DANGER),
)

/**
 * The reference `color-mix` ratios behind the soft roles (HeroUI Native `theme.css`),
 * kept in one place so a derived theme mixes the way the tokens were generated. The
 * numbers are how much of the *status* colour survives the mix; the token pipeline
 * mixes in OKLab, these derivations in sRGB, which differs only slightly at these ratios.
 */
internal object SoftColorMix {
    const val FILL_ALPHA = 0.15f
    const val ACCENT = 0.80f
    const val DANGER = 0.80f
    const val WARNING = 0.65f
    const val SUCCESS = 0.70f
}

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
            separatorSecondary = DefaultPalette.lightSeparatorSecondary,
            segment = DefaultPalette.lightSegment,
            primarySoft = DefaultPalette.lightAccentSoft,
            primarySoftForeground = DefaultPalette.lightAccentSoftForeground,
            successSoft = DefaultPalette.lightSuccessSoft,
            successSoftForeground = DefaultPalette.lightSuccessSoftForeground,
            warningSoft = DefaultPalette.lightWarningSoft,
            warningSoftForeground = DefaultPalette.lightWarningSoftForeground,
            destructiveSoft = DefaultPalette.lightDangerSoft,
            destructiveSoftForeground = DefaultPalette.lightDangerSoftForeground,

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
            separatorSecondary = DefaultPalette.darkSeparatorSecondary,
            segment = DefaultPalette.darkSegment,
            primarySoft = DefaultPalette.darkAccentSoft,
            primarySoftForeground = DefaultPalette.darkAccentSoftForeground,
            successSoft = DefaultPalette.darkSuccessSoft,
            successSoftForeground = DefaultPalette.darkSuccessSoftForeground,
            warningSoft = DefaultPalette.darkWarningSoft,
            warningSoftForeground = DefaultPalette.darkWarningSoftForeground,
            destructiveSoft = DefaultPalette.darkDangerSoft,
            destructiveSoftForeground = DefaultPalette.darkDangerSoftForeground,

            input = DefaultPalette.darkInputBorder,
            ring = DefaultPalette.darkRing,
        )
    )
}

/** Grouped pages follow the selected theme rather than a fixed iOS palette. */
val Colors.groupedBackground: Color
    get() = background
