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
import com.gearui.theme.Theme
import com.gearui.foundation.tag.TagSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Tag - fully Theme-driven tag
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: TagColorTokens, Color(0x...) or hardcoded colours
 *
 * Supports:
 * - 5 themes: PRIMARY, SUCCESS, WARNING, DANGER, DEFAULT
 * - 3 variants: DARK (filled), LIGHT (tinted), OUTLINE
 * - 3 sizes: LARGE, MEDIUM, SMALL
 * - closable, clickable, icon support
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
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors
    val shapes = Theme.shapes

    val interactionSource = remember { createMutableInteractionSource() }
    if (disabled) interactionSource.updateState(InteractionState.Disabled)

    // Size tokens
    val tokens = when (size) {
        TagSize.LARGE -> TagSizeTokens.Large
        TagSize.MEDIUM -> TagSizeTokens.Medium
        TagSize.SMALL -> TagSizeTokens.Small
    }

    // Map token borderRadius to Theme.shapes
    val shape = when (size) {
        TagSize.LARGE -> shapes.md   // Radius.Default = 6dp
        TagSize.MEDIUM -> shapes.sm    // Radius.Small = 3dp
        TagSize.SMALL -> shapes.sm     // Radius.Small = 3dp
    }

    // ⭐ Colour mapping: Theme semantics -> Tag visuals
    // Semantic colour from the theme
    val (themeColor, themeLightColor) = when (theme) {
        TagTheme.PRIMARY -> colors.primary to colors.muted
        TagTheme.SUCCESS -> colors.success to colors.success.copy(alpha = 0.12f)
        TagTheme.WARNING -> colors.warning to colors.warning.copy(alpha = 0.12f)
        TagTheme.DANGER -> colors.destructive to colors.destructive.copy(alpha = 0.12f)
        TagTheme.DEFAULT -> colors.mutedForeground to colors.muted
    }

    // Text colour on the DARK variant (a solid coloured fill): the matching foreground for the theme, adapting to light/dark
    val themeForeground = when (theme) {
        TagTheme.PRIMARY -> colors.primaryForeground
        TagTheme.SUCCESS -> colors.successForeground
        TagTheme.WARNING -> colors.warningForeground
        TagTheme.DANGER -> colors.destructiveForeground
        TagTheme.DEFAULT -> colors.primaryForeground
    }

    // Background and text colour follow the variant
    val (backgroundColor, textColor, borderColor) = when (variant) {
        TagVariant.DARK -> Triple(
            themeColor,           // 深色背景
            themeForeground,      // 实底上的文字（明暗自适应）
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

    // Disabled colours
    val finalBackgroundColor = if (!interactionSource.currentState.isInteractive) {
        colors.muted
    } else {
        backgroundColor
    }
    val finalTextColor = if (!interactionSource.currentState.isInteractive) {
        colors.mutedForeground
    } else {
        textColor
    }

    Box(
        modifier = modifier
            .height(tokens.height)
            .clip(shape)
            .background(finalBackgroundColor)
            .then(
                if (variant == TagVariant.OUTLINE && interactionSource.currentState.isInteractive) {
                    Modifier.border(BorderWidth.thin, borderColor, shape)
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = tokens.paddingHorizontal),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Icon
            if (icon != null) {
                Box(modifier = Modifier.size(tokens.iconBoxSize)) { icon() }
                Spacer(modifier = Modifier.width(Spacing.xs))
            }

            // Text
            Text(
                text = text,
                color = finalTextColor
            )

            // Close button
            if (closable && onClose != null) {
                Spacer(modifier = Modifier.width(Spacing.xs))
                Box(
                    modifier = Modifier
                        .size(tokens.iconBoxSize)
                        .clickable(
                            onClick = onClose
                        )
                ) {
                    Icon(
                        name = Icons.close,
                        size = tokens.iconSize,
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
