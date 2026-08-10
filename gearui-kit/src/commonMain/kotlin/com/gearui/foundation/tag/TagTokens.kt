package com.gearui.foundation.tag

import com.gearui.foundation.layout.Spacing
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Tag size tokens.
 *
 * Replaces the Float-based `foundation.tokens.TagTokens`. Beyond the Float →
 * Dp move, the icon dimensions are now stated outright. They used to be
 * derived at the call site as `(tokens.fontSize + 2).dp` for the icon box and
 * `tokens.fontSize.dp` for the glyph — arithmetic on a type token to express a
 * geometry token, which meant Tag silently depended on the legacy Typography
 * scale. Values are unchanged: 14/16, 12/14, 12/14.
 *
 * The old class also carried borderRadius, which Tag already ignored in favour
 * of `Theme.shapes`; it is not ported.
 *
 * Colors are NOT defined here — components read from `Theme.colors`.
 */
data class TagTokens(
    val height: Dp,
    val paddingHorizontal: Dp,
    /** Glyph size for a leading/trailing icon. */
    val iconSize: Dp,
    /** Box the glyph is centred in; slightly larger so icons align with text. */
    val iconBoxSize: Dp,
)

object TagSizeTokens {

    val Large = TagTokens(
        height = 32.dp,
        paddingHorizontal = Spacing.md,
        iconSize = 14.dp,
        iconBoxSize = 16.dp,
    )

    val Medium = TagTokens(
        height = 24.dp,
        paddingHorizontal = Spacing.sm,
        iconSize = 12.dp,
        iconBoxSize = 14.dp,
    )

    val Small = TagTokens(
        height = 20.dp,
        paddingHorizontal = Spacing.sm,
        iconSize = 12.dp,
        iconBoxSize = 14.dp,
    )
}
