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
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
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
    val typography = Theme.typography
    val shapes = Theme.shapes

    val canDecrease = enabled && value > min
    val canIncrease = enabled && value < max

    val height = when (size) {
        StepperSize.SMALL -> 24.dp
        StepperSize.MEDIUM -> 32.dp
        StepperSize.LARGE -> 40.dp
    }

    val textStyle = when (size) {
        StepperSize.SMALL -> Typography.BodySmall
        StepperSize.MEDIUM -> Typography.BodyMedium
        StepperSize.LARGE -> Typography.BodyLarge
    }

    Row(
        modifier = modifier
            .height(height)
            .clip(shapes.md)
            .border(BorderWidth.thin, if (enabled) colors.border else colors.mutedForeground, shapes.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrement button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .background(if (canDecrease) colors.surface else colors.muted)
                .clickable(enabled = canDecrease) {
                    onValueChange((value - step).coerceAtLeast(min))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = textStyle,
                color = if (canDecrease) colors.foreground else colors.mutedForeground
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(BorderWidth.thin)
                .background(if (enabled) colors.border else colors.mutedForeground)
        )

        // Value display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (enabled && !disableInput) colors.surface else colors.muted),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = textStyle,
                color = if (enabled) colors.foreground else colors.mutedForeground
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(BorderWidth.thin)
                .background(if (enabled) colors.border else colors.mutedForeground)
        )

        // Increment button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .background(if (canIncrease) colors.surface else colors.muted)
                .clickable(enabled = canIncrease) {
                    onValueChange((value + step).coerceAtMost(max))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = textStyle,
                color = if (canIncrease) colors.foreground else colors.mutedForeground
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
            style = Typography.BodyMedium,
            color = if (enabled) colors.foreground else colors.mutedForeground,
            modifier = Modifier.weight(1f)
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
