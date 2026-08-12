package com.gearui.components.segmented

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.border.BorderWidth

/**
 * SegmentedControl - fully Theme-driven segmented control
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - switching between several segments
 * - highlighted selection
 * - disabled state
 * - self-sizing width
 */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelProvider: (T) -> String = { it.toString() }
) {
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = modifier
            .height(36.dp)
            .clip(Theme.shapes.lg)
            .background(colors.surface)
            .border(BorderWidth.thin, colors.border, Theme.shapes.lg)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(Theme.shapes.md)
                    .background(
                        if (isSelected) colors.muted else colors.surface
                    )
                    .clickable(enabled = enabled) {
                        onOptionSelected(option)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelProvider(option),
                    style = Typography.BodyMedium,
                    color = when {
                        !enabled -> colors.mutedForeground
                        isSelected -> colors.foreground
                        else -> colors.mutedForeground
                    }
                )
            }
        }
    }
}

/**
 * IconSegmentedControl - segmented control with icons
 */
@Composable
fun <T> IconSegmentedControl(
    options: List<SegmentedOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(Theme.shapes.lg)
            .background(colors.surface)
            .border(BorderWidth.thin, colors.border, Theme.shapes.lg)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.value == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(Theme.shapes.md)
                    .background(
                        if (isSelected) colors.muted else colors.surface
                    )
                    .clickable(enabled = enabled) {
                        onOptionSelected(option.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (option.icon != null) {
                        option.icon.invoke()
                        Spacer(modifier = Modifier.height(BorderWidth.thick))
                    }

                    Text(
                        text = option.label,
                        style = Typography.BodySmall,
                        color = when {
                            !enabled -> colors.mutedForeground
                            isSelected -> colors.foreground
                            else -> colors.mutedForeground
                        }
                    )
                }
            }
        }
    }
}

/**
 * SegmentedOption - segment option data class
 */
data class SegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: (@Composable () -> Unit)? = null
)
