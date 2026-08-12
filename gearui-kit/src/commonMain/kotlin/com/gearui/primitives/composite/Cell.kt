package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import androidx.compose.runtime.remember
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.foundation.layout.size
import com.gearui.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.interaction.createMutableInteractionSource
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Surface
import com.gearui.foundation.primitives.SurfaceColorTokens
import com.gearui.foundation.primitives.SurfaceTokens
import com.gearui.foundation.list.CellDefaults
import com.gearui.primitives.*
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth

/**
 * Cell - fully Theme-driven core interaction unit of the List family
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: CellTokens.background or hardcoded colours
 *
 * Rework notes:
 * - the hardcoded colours in CellTokens are gone
 * - Theme.colors.background is used directly
 *
 * Responsibilities:
 * - the leading / middle / trailing layout template
 * - consistent interaction states (pressed / disabled)
 * - a consistent minimum height
 */
@Composable
internal fun Cell(
    modifier: Modifier = Modifier,
    minHeight: Dp = 52.dp,
    paddingHorizontal: Dp = CellDefaults.Default.paddingHorizontal,
    paddingVertical: Dp = 12.dp,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },

    leading: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showArrow: Boolean = false,
    showDivider: Boolean = true
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    val surfaceTokens = SurfaceTokens(
        height = minHeight,
        radius = 0.dp,  // Cell 通常不需要圆角
        borderWidth = BorderWidth.none,
        padding = PaddingValues(0.dp)  // Cell 自己控制 padding
    )

    // ⭐ Uses Theme.colors - Cell takes the surface background
    val surfaceColors = SurfaceColorTokens(
        background = colors.surface,
        disabledBackground = colors.surface,
        pressedBackground = colors.muted,
        border = colors.surface
    )

    Column {
        Surface(
            enabled = enabled,
            tokens = surfaceTokens,
            colors = surfaceColors,
            interactionSource = interactionSource,
            onClick = onClick
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(
                        horizontal = paddingHorizontal,
                        vertical = paddingVertical
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading content
                leading?.let {
                    it()
                    HorizontalSpacer(Spacing.md)
                }

                // Middle content (title + subtitle)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    title()

                    subtitle?.let {
                        VerticalSpacer(Spacing.xs)
                        it()
                    }
                }

                // Trailing content
                if (trailing != null) {
                    HorizontalSpacer(Spacing.sm)
                    trailing()
                }

                // Chevron indicator (showArrow)
                if (showArrow) {
                    HorizontalSpacer(Spacing.sm)
                    // TODO: use Icon + Icons.ChevronRight
                    Box(modifier = Modifier.size(16.dp))
                }
            }
        }

        // Divider
        if (showDivider) {
            DividerInset()
        }
    }
}
