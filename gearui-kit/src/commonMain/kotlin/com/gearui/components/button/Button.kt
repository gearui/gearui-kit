package com.gearui.components.button

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.CircularProgressIndicator
import com.gearui.foundation.primitives.Icon as FoundationIcon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * Button - 100% Theme 驱动
 *
 * 支持：
 * - 4种类型：填充(fill)、描边(outline)、文字(text)、幽灵(ghost)
 * - 4种主题：primary、danger、warning、success、default、light
 * - 4种尺寸：large、medium、small、extraSmall
 * - 5种形状：rectangle、round、square、circle、filled
 * - 图标支持：左侧/右侧图标
 * - 状态：loading、disabled
 * - 通栏模式：block
 */
@Composable
fun Button(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: ButtonTheme = ButtonTheme.PRIMARY,
    type: ButtonType = ButtonType.FILL,
    size: ButtonSize = ButtonSize.MEDIUM,
    shape: ButtonShape = ButtonShape.RECTANGLE,
    disabled: Boolean = false,
    loading: Boolean = false,
    block: Boolean = false,
    icon: String? = null,
    iconWidget: (@Composable () -> Unit)? = null,
    iconPosition: ButtonIconPosition = ButtonIconPosition.LEFT,
    iconTextSpacing: Dp = 8.dp
) {
    val colors = Theme.colors

    // 尺寸配置
    val height: Dp = when (size) {
        ButtonSize.LARGE -> 48.dp
        ButtonSize.MEDIUM -> 40.dp
        ButtonSize.SMALL -> 32.dp
        ButtonSize.EXTRA_SMALL -> 28.dp
    }

    val paddingH: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 12.dp
        ButtonSize.EXTRA_SMALL -> 8.dp
    }

    val loadingSize: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 14.dp
        ButtonSize.EXTRA_SMALL -> 12.dp
    }

    val iconSize: Dp = when (size) {
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 18.dp
        ButtonSize.SMALL -> 16.dp
        ButtonSize.EXTRA_SMALL -> 14.dp
    }

    // 形状配置
    val buttonShape: Shape = when (shape) {
        ButtonShape.RECTANGLE -> RoundedCornerShape(8.dp)
        ButtonShape.ROUND -> RoundedCornerShape(height / 2)
        ButtonShape.SQUARE -> RoundedCornerShape(8.dp)
        ButtonShape.CIRCLE -> CircleShape
        ButtonShape.FILLED -> RoundedCornerShape(height / 2)
    }

    // 是否是方形/圆形（只有图标，没有文字）
    val hasIcon = icon != null || iconWidget != null
    val isIconOnly = text.isEmpty() && hasIcon
    val buttonWidth = if (isIconOnly && (shape == ButtonShape.SQUARE || shape == ButtonShape.CIRCLE)) {
        height
    } else {
        Dp.Unspecified
    }

    // 颜色配置
    val (containerColor, contentColor, borderColor) = getButtonColors(
        theme = theme,
        type = type,
        disabled = disabled,
        colors = colors
    )

    val isEnabled = !disabled && !loading

    val resolvedIcon: (@Composable () -> Unit)? = when {
        iconWidget != null -> iconWidget
        icon != null -> {
            {
                FoundationIcon(
                    name = icon,
                    size = iconSize,
                    tint = contentColor,
                    preferSvg = true
                )
            }
        }
        else -> null
    }

    // 按钮修饰符
    val buttonModifier = modifier
        .then(if (block) Modifier.fillMaxWidth() else Modifier)
        .then(if (buttonWidth != Dp.Unspecified) Modifier.width(buttonWidth) else Modifier)
        .height(height)
        .clip(buttonShape)
        .then(
            when (type) {
                ButtonType.FILL -> Modifier.background(containerColor)
                ButtonType.OUTLINE -> Modifier
                    .background(Color.Transparent)
                    .border(1.dp, borderColor, buttonShape)
                ButtonType.TEXT -> Modifier.background(Color.Transparent)
                ButtonType.GHOST -> Modifier.background(Color.Transparent)
            }
        )
        .clickable(enabled = isEnabled) { onClick() }
        .padding(horizontal = if (isIconOnly) 0.dp else paddingH)

    Box(
        modifier = buttonModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Loading
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(loadingSize),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // 左侧图标
            if (!loading && resolvedIcon != null && iconPosition == ButtonIconPosition.LEFT) {
                resolvedIcon()
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(iconTextSpacing))
                }
            }

            // 文字
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    style = when (size) {
                        ButtonSize.LARGE -> Typography.BodyLarge
                        ButtonSize.MEDIUM -> Typography.BodyMedium
                        ButtonSize.SMALL -> Typography.BodySmall
                        ButtonSize.EXTRA_SMALL -> Typography.BodySmall
                    },
                    color = contentColor
                )
            }

            // 右侧图标
            if (!loading && resolvedIcon != null && iconPosition == ButtonIconPosition.RIGHT) {
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(iconTextSpacing))
                }
                resolvedIcon()
            }
        }
    }
}

/**
 * 获取按钮颜色
 */
@Composable
private fun getButtonColors(
    theme: ButtonTheme,
    type: ButtonType,
    disabled: Boolean,
    colors: com.gearui.theme.GearColors
): Triple<Color, Color, Color> {
    // 主题基础色
    val primaryColor: Color
    val lightColor: Color

    when (theme) {
        ButtonTheme.PRIMARY -> {
            primaryColor = colors.primary
            lightColor = colors.primaryLight
        }
        ButtonTheme.DANGER -> {
            primaryColor = colors.danger
            lightColor = colors.dangerLight
        }
        ButtonTheme.WARNING -> {
            primaryColor = colors.warning
            lightColor = colors.warningLight
        }
        ButtonTheme.SUCCESS -> {
            primaryColor = colors.success
            lightColor = colors.successLight
        }
        ButtonTheme.DEFAULT -> {
            primaryColor = colors.surfaceVariant
            lightColor = colors.surface
        }
        ButtonTheme.LIGHT -> {
            primaryColor = colors.primaryLight
            lightColor = colors.surface
        }
    }

    return when (type) {
        ButtonType.FILL -> {
            if (disabled) {
                Triple(
                    if (theme == ButtonTheme.DEFAULT) colors.disabledContainer else lightColor,
                    colors.textDisabled,
                    Color.Transparent
                )
            } else {
                Triple(
                    if (theme == ButtonTheme.LIGHT) lightColor else primaryColor,
                    if (theme == ButtonTheme.DEFAULT) colors.textPrimary
                    else if (theme == ButtonTheme.LIGHT) colors.primary
                    else colors.onPrimary,
                    Color.Transparent
                )
            }
        }
        ButtonType.OUTLINE -> {
            if (disabled) {
                Triple(
                    colors.surface,
                    colors.textDisabled,
                    colors.disabled
                )
            } else {
                Triple(
                    colors.surface,
                    if (theme == ButtonTheme.DEFAULT) colors.textPrimary else primaryColor,
                    colors.border
                )
            }
        }
        ButtonType.TEXT -> {
            if (disabled) {
                Triple(
                    Color.Transparent,
                    colors.textDisabled,
                    Color.Transparent
                )
            } else {
                Triple(
                    Color.Transparent,
                    if (theme == ButtonTheme.DEFAULT) colors.textPrimary else primaryColor,
                    Color.Transparent
                )
            }
        }
        ButtonType.GHOST -> {
            if (disabled) {
                Triple(
                    Color.Transparent,
                    colors.textDisabled,
                    Color.Transparent
                )
            } else {
                Triple(
                    Color.Transparent,
                    if (theme == ButtonTheme.DEFAULT) colors.textPrimary else primaryColor,
                    Color.Transparent
                )
            }
        }
    }
}
