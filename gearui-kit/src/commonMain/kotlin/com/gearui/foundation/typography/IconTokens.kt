package com.gearui.foundation.typography

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Icon size scale.
 *
 * All five steps come from measured usage rather than from taste: across the
 * component layer's 34 icon sizes, 18dp appeared 10 times, 16dp 8 times, 12dp
 * 5 times, and 24dp and 14dp twice each.
 *
 *   xs = 12dp  — marks inside a control (clear button, switch glyph, checkmark)
 *   sm = 14dp  — secondary icons in dense lists
 *   md = 16dp  — trailing affordance on input-like controls (chevron, calendar,
 *                clock)
 *   lg = 18dp  — the default icon sitting inline with body text
 *   xl = 24dp  — standalone icons in nav bars and tab bars
 *
 * The previous scale was `small/medium/large = 14/18/24` and had no 16dp step
 * — the very size the whole field family had settled on — so the family wrote
 * literals instead. When a scale is missing the value people need, the scale
 * is what gets bypassed.
 *
 * Illustration icons live separately in [IconSizes.Display]: empty states and
 * result pages draw figures outside the text flow, and their range (28-40dp)
 * does not overlap with inline icons. Folding them into one scale would stretch
 * it until the steps stopped meaning anything.
 */
data class IconTokens(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
)

object IconSizes {
    val Default = IconTokens(
        xs = 12.dp,
        sm = 14.dp,
        md = 16.dp,
        lg = 18.dp,
        xl = 24.dp,
    )

    /**
     * Illustration-scale icons: empty states, result pages, the large controls
     * in the image viewer.
     *
     * These do not sit beside text — they are layout elements rather than
     * inline icons, which is why they do not share [Default]'s steps.
     */
    object Display {
        val sm: Dp = 28.dp
        val md: Dp = 36.dp
        val lg: Dp = 40.dp
    }
}
