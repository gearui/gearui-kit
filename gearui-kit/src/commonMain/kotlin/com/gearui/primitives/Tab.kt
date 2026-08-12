package com.gearui.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.tab.TabSizeTokens
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Tab - fully Theme-driven navigation selector unit
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: TabColorTokens or hardcoded colours
 *
 * Rework notes:
 * - selected: colors.primary
 * - unselected: colors.mutedForeground
 * - indicator: colors.primary
 */
@Composable
fun Tab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    icon: Painter? = null,
    badgeCount: Int? = null,
    badgeDot: Boolean = false,

    height: Dp = TabSizeTokens.Medium.height,
    horizontalPadding: Dp = 16.dp,
    indicatorHeight: Dp = TabSizeTokens.Medium.indicatorHeight
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors
    val typography = Theme.typography

    val content: @Composable () -> Unit = {
        Column(
            modifier = modifier
                .height(height)
                .background(colors.surface)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Content Area (Icon + Text)
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (icon != null) {
                    Icon(
                        painter = icon,
                        size = IconSizes.Default.lg
                    )
                }

                Text(
                    text = text,
                    style = Typography.BodyMedium,
                    color = if (selected) colors.foreground else colors.mutedForeground
                )
            }

            Spacer(Modifier.height(4.dp))

            // Indicator (underline)
            if (selected) {
                Box(
                    Modifier
                        .height(indicatorHeight)
                        .fillMaxWidth()
                        .background(colors.foreground)
                )
            } else {
                Spacer(Modifier.height(indicatorHeight))
            }
        }
    }

    // Badge overlay
    if (badgeCount != null || badgeDot) {
        Badge(
            type = if (badgeDot) BadgeType.RedPoint else BadgeType.Message,
            count = badgeCount,
            alignment = Alignment.TopEnd,
            content = content
        )
    } else {
        content()
    }
}
