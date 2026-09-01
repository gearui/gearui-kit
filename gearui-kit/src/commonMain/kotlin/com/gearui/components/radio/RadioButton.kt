package com.gearui.components.radio

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * RadioButton - fully Theme-driven radio button
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - selected / unselected state
 * - disabled state
 * - 2 sizes
 * - follows the theme colour automatically
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RadioSize = RadioSize.MEDIUM
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Size parameters
    val outerSize = when (size) {
        RadioSize.LARGE -> 24.dp
        RadioSize.MEDIUM -> 20.dp
        RadioSize.SMALL -> 16.dp
    }
    // The selected dot is kept proportionally smaller to keep the visual hierarchy consistent
    val innerSize = outerSize * 0.38f

    // ⭐ Colour mapping: Theme semantics -> Radio visuals
    val borderColor = when {
        !enabled -> colors.mutedForeground
        selected -> colors.primary
        else -> colors.border
    }

    val innerColor = if (!enabled) colors.mutedForeground else colors.primary

    Box(
        modifier = modifier
            .size(outerSize)
            .clip(CircleShape)
            // Unselected is left unfilled so dark themes do not show a "black block"
            .background(Color.Transparent)
            .border(BorderWidth.thin, borderColor, CircleShape)
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Selected marker (inner circle)
        if (selected) {
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(CircleShape)
                    .background(innerColor)
            )
        }
    }
}

/**
 * Radio size
 */
enum class RadioSize {
    LARGE,
    MEDIUM,
    SMALL
}

/**
 * RadioButtonWithLabel - radio button with a label
 */
@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RadioSize = RadioSize.MEDIUM
) {
    val colors = Theme.colors

    Row(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            size = size
        )

        Text(
            text = label,
            color = if (enabled) colors.foreground else colors.mutedForeground,
            style = Typography.BodyLarge
        )
    }
}

/**
 * RadioGroup - group of radio buttons
 *
 * @param options the options
 * @param selectedOption currently selected option
 * @param onOptionSelected selection change callback
 */
@Composable
fun <T> RadioGroup(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelProvider: (T) -> String = { it.toString() }
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            RadioButtonWithLabel(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                label = labelProvider(option),
            )
        }
    }
}

/**
 * RadioCardGroup - card-style radio group (laid out horizontally)
 *
 * @param options the options
 * @param selectedOption currently selected option
 * @param onOptionSelected selection change callback
 * @param iconProvider optional icon provider
 */
@Composable
fun <T> RadioCardGroup(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelProvider: (T) -> String = { it.toString() },
    iconProvider: ((T) -> String)? = null
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shapes.md)
                    .background(
                        if (isSelected) colors.primary.copy(alpha = 0.1f)
                        else colors.muted
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) colors.primary else colors.border,
                        shape = shapes.md
                    )
                    .then(
                        if (enabled) {
                            Modifier.clickable { onOptionSelected(option) }
                        } else Modifier
                    )
                    .padding(vertical = Spacing.lg, horizontal = Spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Icon
                    iconProvider?.let { provider ->
                        Text(
                            text = provider(option),
                            color = if (isSelected) colors.primary else colors.mutedForeground
                        )
                    }

                    // Label
                    Text(
                        text = labelProvider(option),
                        color = if (isSelected) colors.primary
                        else if (enabled) colors.foreground
                        else colors.mutedForeground
                    )

                    // Selected indicator
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                        )
                    }
                }
            }
        }
    }
}
