package com.gearui.components.stepper

import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.control.ControlGeometry
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.motion.rowPressFeedback
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.foundation.motion.FeedbackDefaults
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.primitives.BasicTextField
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.Role
import com.gearui.i18n.I18n

/**
 * Stepper - fully Theme-driven stepper
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - increment / decrement controls
 * - minimum and maximum bounds
 * - step size
 * - disabled state
 * - 3 sizes
 */
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    min: Int = 0,
    max: Int = 100,
    step: Int = 1,
    size: StepperSize = StepperSize.MEDIUM,
    disableInput: Boolean = false
) {
    require(min <= max && step > 0)
    val strings = I18n.strings.common
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val shapes = Theme.shapes

    val decrementInteraction = remember { MutableInteractionSource() }
    val incrementInteraction = remember { MutableInteractionSource() }
    val canDecrease = enabled && value > min
    val canIncrease = enabled && value < max

    val height = when (size) {
        StepperSize.SMALL -> ControlGeometry.stepperSizeSmall
        StepperSize.MEDIUM -> ControlGeometry.stepperSizeMedium
        StepperSize.LARGE -> ControlGeometry.stepperSizeLarge
    }

    val textStyle = when (size) {
        StepperSize.SMALL -> Theme.typography.bodySmall
        StepperSize.MEDIUM -> Theme.typography.bodyMedium
        StepperSize.LARGE -> Theme.typography.bodyLarge
    }

    Row(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity }
            .height(height)
            .clip(shapes.md)
            .border(BorderWidth.thin, colors.border, shapes.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrement button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .graphicsLayer { alpha = if (enabled && !canDecrease) FeedbackDefaults.disabledOpacity else 1f }
                .rowPressFeedback(interaction = decrementInteraction, shape = RectangleShape, enabled = canDecrease, scale = false, base = colors.surface)
                .semantics { contentDescription = strings.remove }
                .clickable(enabled = canDecrease, role = Role.Button, interactionSource = decrementInteraction, indication = null) {
                    onValueChange(stepperValue(value, -step.toLong(), min, max))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = textStyle,
                color = colors.foreground
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(BorderWidth.thin)
                .background(colors.border)
        )

        // Value display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            var draft by remember(value) { mutableStateOf(value.toString()) }
            if (disableInput) Text(
                text = value.toString(),
                style = textStyle,
                color = colors.foreground
            ) else BasicTextField(
                value = draft,
                onValueChange = { next ->
                    if (next.isEmpty() || next == "-" || next.toIntOrNull() != null) {
                        draft = next
                        next.toIntOrNull()?.takeIf { it in min..max }?.let(onValueChange)
                    }
                },
                enabled = enabled,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().onFocusChanged { if (!it.isFocused) {
                    val number = draft.toIntOrNull()?.coerceIn(min, max) ?: value
                    draft = number.toString()
                    if (number != value) onValueChange(number)
                } },
                textStyle = TextStyle(fontSize = textStyle.fontSize, fontWeight = textStyle.fontWeight, textAlign = TextAlign.Center),
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(BorderWidth.thin)
                .background(colors.border)
        )

        // Increment button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .graphicsLayer { alpha = if (enabled && !canIncrease) FeedbackDefaults.disabledOpacity else 1f }
                .rowPressFeedback(interaction = incrementInteraction, shape = RectangleShape, enabled = canIncrease, scale = false, base = colors.surface)
                .semantics { contentDescription = strings.add }
                .clickable(enabled = canIncrease, role = Role.Button, interactionSource = incrementInteraction, indication = null) {
                    onValueChange(stepperValue(value, step.toLong(), min, max))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = textStyle,
                color = colors.foreground
            )
        }
    }
}

/**
 * StepperSize - stepper size
 */
enum class StepperSize {
    /** small - 24dp */
    SMALL,

    /** medium - 32dp */
    MEDIUM,

    /** large - 40dp */
    LARGE
}

/**
 * StepperWithLabel - stepper with a label
 */
@Composable
fun StepperWithLabel(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    min: Int = 0,
    max: Int = 100,
    step: Int = 1,
    size: StepperSize = StepperSize.MEDIUM,
    stepperWidth: Dp = ControlGeometry.stepperFieldWidth,
    labelGap: Dp = Spacing.md
) {
    val colors = Theme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Theme.typography.bodyMedium,
            color = colors.foreground,
            modifier = Modifier.weight(1f).graphicsLayer {
                alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity
            }
        )

        Spacer(modifier = Modifier.width(labelGap))

        Stepper(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            min = min,
            max = max,
            step = step,
            size = size,
            modifier = Modifier.width(stepperWidth)
        )
    }
}
