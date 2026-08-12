package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing

/**
 * SectionHeader - fully Theme-driven section header primitive
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: SectionTokens, TextColors or hardcoded colours
 *
 * Rework notes:
 * - the hardcoded colours in SectionTokens are gone
 * - Theme.colors.foreground / mutedForeground are used directly
 * - the background uses colors.background
 *
 * Use cases:
 * - page titles ("Settings", "Me")
 * - group titles ("Basic components", "Form components")
 * - list groups ("Recently used", "All apps")
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingText: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    height: Dp = 44.dp,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 12.dp
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(colors.background)
            .then(clickableModifier)
            .padding(
                horizontal = paddingHorizontal,
                vertical = paddingVertical
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading title area
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = Typography.BodyMedium,
                    color = colors.foreground
                )

                if (subtitle != null) {
                    Spacer(Modifier.height(BorderWidth.thick))
                    Text(
                        text = subtitle,
                        style = Typography.Caption,
                        color = colors.mutedForeground
                    )
                }
            }

            // Trailing area
            when {
                trailing != null -> {
                    Box(modifier = Modifier.padding(start = Spacing.sm)) {
                        trailing()
                    }
                }

                trailingText != null -> {
                    Box(modifier = Modifier.padding(start = Spacing.sm)) {
                        Text(
                            text = trailingText,
                            style = Typography.BodySmall,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
