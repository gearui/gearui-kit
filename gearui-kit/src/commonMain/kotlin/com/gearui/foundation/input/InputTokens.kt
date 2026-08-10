package com.gearui.foundation.input

import com.gearui.foundation.layout.Spacing
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Input size tokens.
 *
 * Replaces the Float-based `foundation.tokens.InputTokens`, which carried its
 * values as raw `Float` and pulled radius/type from the pre-1.0 root token
 * pool. Dimensions are `Dp` here so call sites stop sprinkling `.dp`
 * conversions, and horizontal padding reads from [Spacing] rather than
 * restating 16/12/8.
 *
 * The old data class also declared fontSize, lineHeight, borderRadius,
 * borderWidth and focusBorderWidth. No component ever read them — Input
 * resolves type from `Theme.typography` and shape from `Theme.shapes` — so
 * they are dropped rather than ported; carrying unread fields is what let the
 * two radius scales disagree unnoticed.
 *
 * Colors are NOT defined here — components read from `Theme.colors`.
 */
data class InputTokens(
    val height: Dp,
    val paddingHorizontal: Dp,
)

object InputSizeTokens {

    val Large = InputTokens(
        height = 48.dp,
        paddingHorizontal = Spacing.lg,
    )

    val Medium = InputTokens(
        height = 40.dp,
        paddingHorizontal = Spacing.md,
    )

    val Small = InputTokens(
        height = 32.dp,
        paddingHorizontal = Spacing.sm,
    )
}
