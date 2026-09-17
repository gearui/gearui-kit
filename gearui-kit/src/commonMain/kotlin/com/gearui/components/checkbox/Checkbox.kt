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
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.control.selectionTimingEasing
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.feedbackDuration
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.interaction.*
import com.tencent.kuikly.compose.foundation.selection.triStateToggleable
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.state.ToggleableState

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
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(modifier.sizeIn(minWidth = ControlGeometry.selectionTouchTarget, minHeight = ControlGeometry.selectionTouchTarget)
        .triStateToggleable(
            state = if (indeterminate) ToggleableState.Indeterminate else if (checked) ToggleableState.On else ToggleableState.Off,
            enabled = enabled, role = Role.Checkbox, interactionSource = interaction, indication = null,
            onClick = { onCheckedChange(!checked) },
        ), contentAlignment = Alignment.Center) {
        CheckboxMark(checked, indeterminate, enabled, pressed, size)
    }
}

@Composable
private fun CheckboxMark(checked: Boolean, indeterminate: Boolean, enabled: Boolean, pressed: Boolean, size: CheckboxSize) {
    val colors = Theme.colors
    val shape = Theme.shapes.sm
    val motion = Theme.motion
    val boxSize = when (size) {
        CheckboxSize.LARGE -> ControlGeometry.selectionLarge
        CheckboxSize.MEDIUM -> ControlGeometry.selectionMedium
        CheckboxSize.SMALL -> ControlGeometry.selectionSmall
    }
    val active = checked || indeterminate
    val reveal by animateFloatAsState(if (active) 1f else 0f,
        tween(motion.feedbackDuration(FeedbackDefaults.selectionRevealDuration), easing = selectionTimingEasing))
    val scale by animateFloatAsState(if (pressed && enabled && motion.normal > 0) FeedbackDefaults.selectionPressScale else 1f,
        tween(motion.feedbackDuration(FeedbackDefaults.selectionPressDuration), easing = selectionTimingEasing))
    val travel = with(LocalDensity.current) { ControlGeometry.checkboxIndicatorTravel.toPx() }
    Box(Modifier.size(boxSize).graphicsLayer {
        alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity
        scaleX = scale; scaleY = scale
    }.clip(shape).background(colors.surface)
        // Until field shadows are portable, keep an unselected mark visible on a surface.
        .border(BorderWidth.thin, if (active) colors.primary else colors.border, shape), contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().graphicsLayer {
                alpha = reveal
                scaleX = FeedbackDefaults.selectionRevealScale + (1f - FeedbackDefaults.selectionRevealScale) * reveal
                scaleY = scaleX
                translationX = -travel * (1f - reveal)
            }.background(colors.primary), contentAlignment = Alignment.Center) {
            Icon(if (indeterminate) Icons.minus else Icons.check, size = boxSize * (2f / 3f), tint = colors.primaryForeground)
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
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(modifier.heightIn(min = ControlGeometry.selectionTouchTarget)
        .triStateToggleable(state = if (checked) ToggleableState.On else ToggleableState.Off,
            enabled = enabled, role = Role.Checkbox, interactionSource = interaction, indication = null,
            onClick = { onCheckedChange(!checked) })
        .padding(vertical = Spacing.sm), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        CheckboxMark(checked, false, enabled, pressed, size)
        Text(label, color = if (enabled) colors.foreground else colors.mutedForeground, style = Theme.typography.bodyLarge)
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
                enabled = enabled,
            )
        }
    }
}
