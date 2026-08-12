package com.gearui.components.checkbox

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Checkbox - fully Theme-driven checkbox
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - checked / unchecked state
 * - disabled state
 * - 2 sizes
 * - follows the theme colour automatically
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indeterminate: Boolean = false,
    size: CheckboxSize = CheckboxSize.MEDIUM
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Size parameters
    val boxSize = when (size) {
        CheckboxSize.LARGE -> 24.dp
        CheckboxSize.MEDIUM -> 20.dp
        CheckboxSize.SMALL -> 16.dp
    }
    val checkSize = boxSize * 0.6f

    // Whether it renders as checked (indeterminate counts)
    val isActive = checked || indeterminate

    // ⭐ Colour mapping: Theme semantics -> Checkbox visuals
    val backgroundColor = when {
        !enabled && isActive -> colors.muted
        !enabled -> colors.surface
        isActive -> colors.primary
        else -> colors.surface
    }

    val borderColor = when {
        !enabled -> colors.mutedForeground
        isActive -> colors.primary
        else -> colors.border
    }

    val checkColor = when {
        !enabled -> colors.mutedForeground
        else -> colors.primaryForeground
    }

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(shapes.sm)
            .background(backgroundColor)
            .border(BorderWidth.thin, borderColor, shapes.sm)
            .then(
                if (enabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Indeterminate marker, or the ✓ tick
        when {
            indeterminate -> {
                Icon(
                    name = Icons.remove,
                    size = checkSize,
                    tint = checkColor
                )
            }
            checked -> {
                Icon(
                    name = Icons.check,
                    size = checkSize,
                    tint = checkColor
                )
            }
        }
    }
}

/**
 * Checkbox size
 */
enum class CheckboxSize {
    LARGE,
    MEDIUM,
    SMALL
}

/**
 * CheckboxWithLabel - checkbox with a label
 */
@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: CheckboxSize = CheckboxSize.MEDIUM
) {
    val colors = Theme.colors
    val typography = Theme.typography

    Row(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            )
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
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
 * CheckboxGroup - group of checkboxes
 */
@Composable
fun CheckboxGroup(
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            CheckboxWithLabel(
                checked = option in selectedOptions,
                onCheckedChange = { isChecked ->
                    val newSelection = if (isChecked) {
                        selectedOptions + option
                    } else {
                        selectedOptions - option
                    }
                    onSelectionChange(newSelection)
                },
                label = option,
            )
        }
    }
}
