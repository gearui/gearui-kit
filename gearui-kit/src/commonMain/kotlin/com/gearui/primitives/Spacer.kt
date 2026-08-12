package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.gearui.foundation.layout.Spacing

/**
 * Spacer - unified spacing primitive
 *
 * ✅ Purpose: replaces every hardcoded Spacer(Modifier.height(xx.dp))
 * ✅ Rule: only the semantic values defined in Spacing may be used
 *
 * Use cases:
 * - gaps between components in a vertical layout
 * - gaps between elements in a horizontal layout
 * - separation between sections
 */

/**
 * Vertical gap (the common case)
 *
 * Example:
 * ```
 * Column {
 *     Button("Button 1")
 *     VerticalSpacer(Spacing.md)
 *     Button("Button 2")
 * }
 * ```
 */
@Composable
fun VerticalSpacer(height: Dp = Spacing.md) {
    Box(modifier = Modifier.height(height))
}

/**
 * Horizontal gap
 *
 * Example:
 * ```
 * Row {
 *     Button("OK")
 *     HorizontalSpacer(Spacing.sm)
 *     Button("Cancel")
 * }
 * ```
 */
@Composable
fun HorizontalSpacer(width: Dp = Spacing.md) {
    Box(modifier = Modifier.width(width))
}

/**
 * Convenience aliases for the common gaps
 */
@Composable
fun SpacerSmall() = VerticalSpacer(Spacing.sm)

@Composable
fun SpacerMedium() = VerticalSpacer(Spacing.md)

@Composable
fun SpacerLarge() = VerticalSpacer(Spacing.lg)

@Composable
fun SpacerExtraLarge() = VerticalSpacer(Spacing.xl)
