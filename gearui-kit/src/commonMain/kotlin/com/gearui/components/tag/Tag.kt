package com.gearui.components.tag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.foundation.interaction.*
import com.gearui.foundation.tokens.TagTokens
import com.gearui.theme.Theme

/**
 * Tag - 100% Theme 驱动的标签组件
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：TagColorTokens / Color(0x...) / 硬编码颜色
 *
 * 支持：
 * - 5种主题：PRIMARY, SUCCESS, WARNING, DANGER, DEFAULT
 * - 3种变体：DARK (填充), LIGHT (浅色), OUTLINE (描边)
 * - 3种尺寸：LARGE, MEDIUM, SMALL
 * - 可关闭、可点击、图标支持
 */
@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    theme: TagTheme = TagTheme.DEFAULT,
    size: TagSize = TagSize.MEDIUM,
    variant: TagVariant = TagVariant.LIGHT,
    closable: Boolean = false,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors
    val shapes = Theme.shapes

    val interactionSource = remember { createMutableInteractionSource() }
    if (disabled) interactionSource.updateState(InteractionState.Disabled)

    // 尺寸 Tokens
    val tokens = when (size) {
        TagSize.LARGE -> TagTokens.Large
        TagSize.MEDIUM -> TagTokens.Medium
        TagSize.SMALL -> TagTokens.Small
    }

    // Map token borderRadius to Theme.shapes
    val shape = when (size) {
        TagSize.LARGE -> shapes.default   // Radius.Default = 6dp
        TagSize.MEDIUM -> shapes.small    // Radius.Small = 3dp
        TagSize.SMALL -> shapes.small     // Radius.Small = 3dp
    }

    // ⭐ 颜色映射：Theme 语义 → Tag 视觉
    // 根据 theme 获取语义颜色
    val (themeColor, themeLightColor) = when (theme) {
        TagTheme.PRIMARY -> colors.primary to colors.primaryLight
        TagTheme.SUCCESS -> colors.success to colors.successLight
        TagTheme.WARNING -> colors.warning to colors.warningLight
        TagTheme.DANGER -> colors.danger to colors.dangerLight
        TagTheme.DEFAULT -> colors.textSecondary to colors.surfaceVariant
    }

    // 根据 variant 决定背景色和文字色
    val (backgroundColor, textColor, borderColor) = when (variant) {
        TagVariant.DARK -> Triple(
            themeColor,           // 深色背景
            colors.onPrimary,     // 白色文字
            Color.Transparent
        )

        TagVariant.LIGHT -> Triple(
            themeLightColor,      // 浅色背景
            themeColor,           // 主题色文字
            Color.Transparent
        )

        TagVariant.OUTLINE -> Triple(
            Color.Transparent,    // 透明背景
            themeColor,           // 主题色文字
            themeColor            // 主题色边框
        )
    }

    // 禁用态颜色
    val finalBackgroundColor = if (!interactionSource.currentState.isInteractive) {
        colors.disabledContainer
    } else {
        backgroundColor
    }
    val finalTextColor = if (!interactionSource.currentState.isInteractive) {
        colors.disabled
    } else {
        textColor
    }

    Box(
        modifier = modifier
            .height(tokens.height.dp)
            .clip(shape)
            .background(finalBackgroundColor)
            .then(
                if (variant == TagVariant.OUTLINE && interactionSource.currentState.isInteractive) {
                    Modifier.border(1.dp, borderColor, shape)
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = tokens.paddingHorizontal.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            // 图标
            if (icon != null) {
                Box(modifier = Modifier.size((tokens.fontSize + 2).dp)) { icon() }
                Spacer(modifier = Modifier.width(4.dp))
            }

            // 文字
            Text(
                text = text,
                color = finalTextColor
            )

            // 关闭按钮
            if (closable && onClose != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size((tokens.fontSize + 2).dp)
                        .clickable(
                            onClick = onClose
                        )
                ) {
                    Icon(
                        name = Icons.close,
                        size = (tokens.fontSize).dp,
                        tint = finalTextColor
                    )
                }
            }
        }
    }
}

enum class TagTheme { PRIMARY, SUCCESS, WARNING, DANGER, DEFAULT }
enum class TagSize { LARGE, MEDIUM, SMALL }
enum class TagVariant { DARK, LIGHT, OUTLINE }
