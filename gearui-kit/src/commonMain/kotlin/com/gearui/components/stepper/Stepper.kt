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

/**
 * Stepper - 100% Theme 驱动的步进器
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 数字增减控制
 * - 最小/最大值限制
 * - 步长控制
 * - 禁用状态
 * - 3种尺寸
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
    // ⭐ Framework Rule #1: 第一行永远是这三个
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
            .clip(shapes.default)
            .border(1.dp, if (enabled) colors.border else colors.disabled, shapes.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 减少按钮
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .background(if (canDecrease) colors.surface else colors.disabledContainer)
                .clickable(enabled = canDecrease) {
                    onValueChange((value - step).coerceAtLeast(min))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = textStyle,
                color = if (canDecrease) colors.textPrimary else colors.textDisabled
            )
        }

        // 分割线
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(if (enabled) colors.border else colors.disabled)
        )

        // 数值显示
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (enabled && !disableInput) colors.surface else colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = textStyle,
                color = if (enabled) colors.textPrimary else colors.textDisabled
            )
        }

        // 分割线
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(if (enabled) colors.border else colors.disabled)
        )

        // 增加按钮
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(height)
                .background(if (canIncrease) colors.surface else colors.disabledContainer)
                .clickable(enabled = canIncrease) {
                    onValueChange((value + step).coerceAtMost(max))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = textStyle,
                color = if (canIncrease) colors.textPrimary else colors.textDisabled
            )
        }
    }
}

/**
 * StepperSize - 步进器尺寸
 */
enum class StepperSize {
    /** 小尺寸 - 24dp */
    SMALL,

    /** 中尺寸 - 32dp */
    MEDIUM,

    /** 大尺寸 - 40dp */
    LARGE
}

/**
 * StepperWithLabel - 带标签的步进器
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
            color = if (enabled) colors.textPrimary else colors.textDisabled,
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
