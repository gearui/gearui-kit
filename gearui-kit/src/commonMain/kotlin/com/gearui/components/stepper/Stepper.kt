package com.gearui.components.stepper

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.motion.FeedbackDefaults
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth

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
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val shapes = Theme.shapes

    val canDecrease = enabled && value > min
    val canIncrease = enabled && value < max

    val height = when (size) {
        StepperSize.SMALL -> 24.dp
        StepperSize.MEDIUM -> 32.dp
        StepperSize.LARGE -> 40.dp
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
                .background(colors.surface)
                .clickable(enabled = canDecrease) {
                    onValueChange((value - step).coerceAtLeast(min))
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
            Text(
                text = value.toString(),
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

        // Increment button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .graphicsLayer { alpha = if (enabled && !canIncrease) FeedbackDefaults.disabledOpacity else 1f }
                .background(colors.surface)
                .clickable(enabled = canIncrease) {
                    onValueChange((value + step).coerceAtMost(max))
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
    stepperWidth: Dp = 180.dp,
    labelGap: Dp = 12.dp
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
            min = min,
            max = max,
            step = step,
            size = size,
            modifier = Modifier.width(stepperWidth)
        )
    }
}
