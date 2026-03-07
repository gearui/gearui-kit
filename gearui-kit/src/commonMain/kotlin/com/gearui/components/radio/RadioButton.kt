package com.gearui.components.radio

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.material3.Text

/**
 * RadioButton - 100% Theme 驱动的单选按钮组件
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 选中/未选中状态
 * - 禁用状态
 * - 2种尺寸
 * - 自动跟随主题色
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RadioSize = RadioSize.MEDIUM
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 尺寸参数
    val outerSize = when (size) {
        RadioSize.LARGE -> 24.dp
        RadioSize.MEDIUM -> 20.dp
        RadioSize.SMALL -> 16.dp
    }
    // 选中点收敛到更小比例，统一视觉层次
    val innerSize = outerSize * 0.38f

    // ⭐ 颜色映射：Theme 语义 → Radio 视觉
    val borderColor = when {
        !enabled -> colors.disabled
        selected -> colors.primary
        else -> colors.stroke
    }

    val innerColor = if (!enabled) colors.disabled else colors.primary

    Box(
        modifier = modifier
            .size(outerSize)
            .clip(shapes.circle)
            // 未选中不做底色填充，避免深色主题出现“黑块”
            .background(Color.Transparent)
            .border(1.5.dp, borderColor, shapes.circle)
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // 选中标记 (内圆)
        if (selected) {
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(shapes.circle)
                    .background(innerColor)
            )
        }
    }
}

/**
 * Radio 尺寸枚举
 */
enum class RadioSize {
    LARGE,
    MEDIUM,
    SMALL
}

/**
 * RadioButtonWithLabel - 带标签的单选按钮
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
    val typography = Theme.typography

    Row(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            size = size
        )

        Text(
            text = label,
            color = if (enabled) colors.textPrimary else colors.textDisabled,
            style = typography.bodyLarge
        )
    }
}

/**
 * RadioGroup - 单选按钮组
 *
 * @param options 选项列表
 * @param selectedOption 当前选中的选项
 * @param onOptionSelected 选项变化回调
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
 * RadioCardGroup - 卡片式单选按钮组（横向排列）
 *
 * @param options 选项列表
 * @param selectedOption 当前选中的选项
 * @param onOptionSelected 选项变化回调
 * @param iconProvider 图标提供者（可选）
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shapes.default)
                    .background(
                        if (isSelected) colors.primary.copy(alpha = 0.1f)
                        else colors.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) colors.primary else colors.border,
                        shape = shapes.default
                    )
                    .then(
                        if (enabled) {
                            Modifier.clickable { onOptionSelected(option) }
                        } else Modifier
                    )
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 图标
                    iconProvider?.let { provider ->
                        Text(
                            text = provider(option),
                            color = if (isSelected) colors.primary else colors.textSecondary
                        )
                    }

                    // 标签
                    Text(
                        text = labelProvider(option),
                        color = if (isSelected) colors.primary
                        else if (enabled) colors.textPrimary
                        else colors.textDisabled
                    )

                    // 选中指示器
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(shapes.circle)
                                .background(colors.primary)
                        )
                    }
                }
            }
        }
    }
}
