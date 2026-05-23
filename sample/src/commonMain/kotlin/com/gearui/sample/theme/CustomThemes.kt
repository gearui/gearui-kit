package com.gearui.sample.theme

import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.theme.Colors
import com.gearui.theme.ThemeSpec

/**
 * Sample-only theme set.
 *
 * - DarkPurple: dark purple-tinted theme, brand purple reserved for accents.
 */
object CustomThemes {

    /**
     * DarkPurple — low-saturation deep indigo base, brand purple used for
     * accents and rings. Foregrounds stay on the cool-white side for
     * legibility.
     */
    val DarkPurple = ThemeSpec(
        colors = Colors(
            background = Color(0xFF181427),
            foreground = Color(0xF2F6F1FF),
            surface = Color(0xFF26203A),
            surfaceForeground = Color(0xF2F6F1FF),
            card = Color(0xFF2C2442),
            cardForeground = Color(0xF2F6F1FF),
            popover = Color(0xFF332A4D),
            popoverForeground = Color(0xF2F6F1FF),
            muted = Color(0xFF221B34),
            mutedForeground = Color(0xB3CDC2E4),

            primary = Color(0xFFBFA6FF),
            primaryForeground = Color(0xFFFFFFFF),
            secondary = Color(0xFF332B4D),
            secondaryForeground = Color(0xF2F6F1FF),
            accent = Color(0xFF6E48CF),
            accentForeground = Color(0xFFFFFFFF),

            destructive = Color(0xFFFF2DA8),
            destructiveForeground = Color(0xFFFFFFFF),
            success = Color(0xFF5FE0B8),
            successForeground = Color(0xFF09090B),
            warning = Color(0xFFF3C17A),
            warningForeground = Color(0xFF09090B),
            info = Color(0xFFB89CFF),
            infoForeground = Color(0xFF09090B),

            border = Color(0xFF4C406F),
            input = Color(0xFF3D325A),
            ring = Color(0xFFBFA6FF),
        )
    )
}
