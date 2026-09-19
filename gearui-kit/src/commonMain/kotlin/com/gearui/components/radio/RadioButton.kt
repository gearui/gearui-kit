package com.gearui.components.radio

import com.tencent.kuikly.compose.ui.text.font.FontWeight
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
import com.gearui.foundation.interaction.disabledAppearance
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.feedbackDuration
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.selection.selectable
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.semantics.Role

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
    val interaction = remember { MutableInteractionSource() }
    Box(modifier.sizeIn(minWidth = ControlGeometry.selectionTouchTarget, minHeight = ControlGeometry.selectionTouchTarget)
        .selectable(selected, interactionSource = interaction, indication = null, enabled = enabled, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center) {
        RadioMark(selected, enabled, size)
    }
}

@Composable
private fun RadioMark(selected: Boolean, enabled: Boolean, size: RadioSize) {
    val colors = Theme.colors
    val motion = Theme.motion
    val outer = when (size) {
        RadioSize.LARGE -> ControlGeometry.selectionLarge
        RadioSize.MEDIUM -> ControlGeometry.selectionMedium
        RadioSize.SMALL -> ControlGeometry.selectionSmall
    }
    val scale by animateFloatAsState(if (selected || motion.normal <= 0) 1f else FeedbackDefaults.radioExitScale,
        tween(motion.feedbackDuration(FeedbackDefaults.radioRevealDuration), easing = FeedbackDefaults.pressEasing))
    Box(Modifier.size(outer).graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity }
        .clip(CircleShape).background(if (selected) colors.primary else colors.surface)
        // Semantic outline is the fallback for the reference's field shadow.
        .border(BorderWidth.thin, if (selected) colors.primary else colors.border, CircleShape), contentAlignment = Alignment.Center) {
        Box(Modifier.size(ControlGeometry.radioThumb * (outer / ControlGeometry.selectionMedium))
            .graphicsLayer { alpha = if (selected) 1f else 0f; scaleX = scale; scaleY = scale }
            .clip(CircleShape).background(colors.primaryForeground))
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
    val interaction = remember { MutableInteractionSource() }
    Row(modifier.heightIn(min = ControlGeometry.selectionTouchTarget)
        .selectable(selected, interactionSource = interaction, indication = null, enabled = enabled, role = Role.RadioButton, onClick = onClick)
        .padding(vertical = Spacing.sm), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        RadioMark(selected, enabled, size)
        Text(label, color = colors.foreground, style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity })
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
                enabled = enabled,
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
        modifier = modifier.fillMaxWidth().disabledAppearance(!enabled),
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
                        width = if (isSelected) BorderWidth.thick else BorderWidth.thin,
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
                        else colors.foreground
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
