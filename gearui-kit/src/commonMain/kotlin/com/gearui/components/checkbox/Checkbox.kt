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

/**
 * Checkbox - 100% Theme 驱动的复选框组件
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
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indeterminate: Boolean = false,
    size: CheckboxSize = CheckboxSize.MEDIUM
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 尺寸参数
    val boxSize = when (size) {
        CheckboxSize.LARGE -> 24.dp
        CheckboxSize.MEDIUM -> 20.dp
        CheckboxSize.SMALL -> 16.dp
    }
    val checkSize = boxSize * 0.6f

    // 是否显示为选中状态（包括半选）
    val isActive = checked || indeterminate

    // ⭐ 颜色映射：Theme 语义 → Checkbox 视觉
    val backgroundColor = when {
        !enabled && isActive -> colors.disabledContainer
        !enabled -> colors.surface
        isActive -> colors.primary
        else -> colors.surface
    }

    val borderColor = when {
        !enabled -> colors.disabled
        isActive -> colors.primary
        else -> colors.border
    }

    val checkColor = when {
        !enabled -> colors.disabled
        else -> colors.onPrimary
    }

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(shapes.small)
            .background(backgroundColor)
            .border(1.5.dp, borderColor, shapes.small)
            .then(
                if (enabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // 半选标记 - 或 选中标记 ✓
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
 * Checkbox 尺寸枚举
 */
enum class CheckboxSize {
    LARGE,
    MEDIUM,
    SMALL
}

/**
 * CheckboxWithLabel - 带标签的复选框
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            size = size
        )

        Text(
            text = label,
            color = if (enabled) colors.textPrimary else colors.textDisabled,
            style = Typography.BodyLarge
        )
    }
}

/**
 * CheckboxGroup - 复选框组
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
