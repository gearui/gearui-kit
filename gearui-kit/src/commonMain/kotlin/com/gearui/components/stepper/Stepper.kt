package com.gearui.components.stepper

import com.gearui.components.input.numberFieldKeyboard
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
    /**
     * Lets the count be typed as well as stepped. Off by default: a stepper is for small
     * counts the buttons reach quickly, and a text field inside it is easy to hit by
     * accident. A non-negative [min] gets the digit pad; otherwise the text keyboard, as
     * KuiklyUI has no signed pad.
     */
    editable: Boolean = false
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

    // The drawn stepper stays [height]; the − and + each take taps over a 44 square
    // (HIG hit region), so the component reserves that room around the drawing.
    val hit = maxOf(height, ControlGeometry.selectionTouchTarget)
    val inset = (hit - height) / 2
    Box(modifier = modifier.height(hit)) {
    Row(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = inset)
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
                .rowPressFeedback(interaction = decrementInteraction, shape = RectangleShape, enabled = canDecrease, scale = false, base = colors.surface),
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

        // Value display: a compact readout, as wide as a few digits, so the stepper keeps
        // its control size instead of stretching into something that looks like a text field.
        Box(
            modifier = Modifier
                .width(height * 2)
                .fillMaxHeight()
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            var draft by remember(value) { mutableStateOf(value.toString()) }
            var focused by remember { mutableStateOf(false) }
            if (!editable) Text(
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
                keyboardType = numberFieldKeyboard(min >= 0),
                // Commit on leaving the field, not on the unfocused state reported when it first appears.
                modifier = Modifier.fillMaxWidth().onFocusChanged {
                    val left = focused && !it.isFocused
                    focused = it.isFocused
                    if (left) {
                        val number = draft.toIntOrNull()?.coerceIn(min, max) ?: value
                        draft = number.toString()
                        if (number != value) onValueChange(number)
                    }
                },
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
                .rowPressFeedback(interaction = incrementInteraction, shape = RectangleShape, enabled = canIncrease, scale = false, base = colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = textStyle,
                color = colors.foreground
            )
        }
    }
    // The hit regions, over the drawn − and + (sharing their interaction, so the cells
    // still show the press). A 44 square reaches past a smaller button's inner edge; when
    // the value is a text field that strip belongs to the field — a tap meant to focus it
    // changed the value — so there a region stops at its button and keeps only the height.
    val hitWidth = if (editable) inset + height else hit
    Box(
        Modifier.align(Alignment.CenterStart).width(hitWidth).height(hit)
            .semantics { contentDescription = strings.remove }
            .clickable(enabled = canDecrease, role = Role.Button, interactionSource = decrementInteraction, indication = null) {
                onValueChange(stepperValue(value, -step.toLong(), min, max))
            }
    )
    Box(
        Modifier.align(Alignment.CenterEnd).width(hitWidth).height(hit)
            .semantics { contentDescription = strings.add }
            .clickable(enabled = canIncrease, role = Role.Button, interactionSource = incrementInteraction, indication = null) {
                onValueChange(stepperValue(value, step.toLong(), min, max))
            }
    )
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
