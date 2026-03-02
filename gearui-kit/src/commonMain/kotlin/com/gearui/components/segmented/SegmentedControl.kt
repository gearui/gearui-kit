package com.gearui.components.segmented

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * SegmentedControl - 100% Theme 驱动的分段控制器
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 多选项分段切换
 * - 选中态高亮
 * - 禁用状态
 * - 自适应宽度
 */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelProvider: (T) -> String = { it.toString() }
) {
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) colors.surfaceVariant else colors.surface
                    )
                    .clickable(enabled = enabled) {
                        onOptionSelected(option)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelProvider(option),
                    style = Typography.BodyMedium,
                    color = when {
                        !enabled -> colors.textDisabled
                        isSelected -> colors.textPrimary
                        else -> colors.textSecondary
                    }
                )
            }
        }
    }
}

/**
 * IconSegmentedControl - 带图标的分段控制器
 */
@Composable
fun <T> IconSegmentedControl(
    options: List<SegmentedOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.value == selectedOption

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) colors.surfaceVariant else colors.surface
                    )
                    .clickable(enabled = enabled) {
                        onOptionSelected(option.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (option.icon != null) {
                        option.icon.invoke()
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = option.label,
                        style = Typography.BodySmall,
                        color = when {
                            !enabled -> colors.textDisabled
                            isSelected -> colors.textPrimary
                            else -> colors.textSecondary
                        }
                    )
                }
            }
        }
    }
}

/**
 * SegmentedOption - 分段选项数据类
 */
data class SegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: (@Composable () -> Unit)? = null
)
