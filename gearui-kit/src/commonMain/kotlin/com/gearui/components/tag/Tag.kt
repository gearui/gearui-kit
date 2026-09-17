package com.gearui.components.tag

import androidx.compose.runtime.Composable
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
import com.tencent.kuikly.compose.ui.graphics.lerp
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.interaction.disabledAppearance
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

    // Size tokens
    val tokens = when (size) {
        TagSize.LARGE -> TagSizeTokens.Large
        TagSize.MEDIUM -> TagSizeTokens.Medium
        TagSize.SMALL -> TagSizeTokens.Small
    }

    // Map token borderRadius to Theme.shapes
    val shape = when (size) {
        TagSize.LARGE -> shapes.xl
        TagSize.MEDIUM -> shapes.controlLarge
        TagSize.SMALL -> shapes.md
    }

    // ⭐ Colour mapping: Theme semantics -> Tag visuals
    // Semantic colour from the theme
    val (themeColor, themeLightColor) = when (theme) {
        TagTheme.PRIMARY -> colors.primary to colors.primary.copy(alpha = FeedbackDefaults.tagSoftOpacity)
        TagTheme.SUCCESS -> colors.success to colors.success.copy(alpha = FeedbackDefaults.tagSoftOpacity)
        TagTheme.WARNING -> colors.warning to colors.warning.copy(alpha = FeedbackDefaults.tagSoftOpacity)
        TagTheme.DANGER -> colors.destructive to colors.destructive.copy(alpha = FeedbackDefaults.tagSoftOpacity)
        TagTheme.DEFAULT -> colors.secondary to colors.secondary
    }

    // Text colour on the DARK variant (a solid coloured fill): the matching foreground for the theme, adapting to light/dark
    val themeForeground = when (theme) {
        TagTheme.PRIMARY -> colors.primaryForeground
        TagTheme.SUCCESS -> colors.successForeground
        TagTheme.WARNING -> colors.warningForeground
        TagTheme.DANGER -> colors.destructiveForeground
        TagTheme.DEFAULT -> colors.secondaryForeground
    }

    val softForeground = when (theme) {
        TagTheme.DEFAULT -> colors.secondaryForeground
        TagTheme.SUCCESS -> lerp(themeColor, colors.foreground, FeedbackDefaults.tagSuccessForegroundMix)
        TagTheme.WARNING -> lerp(themeColor, colors.foreground, FeedbackDefaults.tagWarningForegroundMix)
        else -> lerp(themeColor, colors.foreground, FeedbackDefaults.tagAccentForegroundMix)
    }

    // Background and text colour follow the variant
    val (backgroundColor, textColor, borderColor) = when (variant) {
        TagVariant.DARK -> Triple(
            themeColor,           // Solid fill
            themeForeground,      // Paired foreground
            Color.Transparent
        )

        TagVariant.LIGHT -> Triple(
            themeLightColor,      // Soft fill
            softForeground,
            Color.Transparent
        )

        TagVariant.OUTLINE -> Triple(
            Color.Transparent,    // Transparent fill
            if (theme == TagTheme.DEFAULT) colors.foreground else themeColor,
            themeColor            // Themed border
        )
    }

    Box(
        modifier = modifier
            .disabledAppearance(disabled)
            .height(tokens.height)
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (variant == TagVariant.OUTLINE) {
                    Modifier.border(BorderWidth.thin, borderColor, shape)
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = !disabled,
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
                color = textColor,
                style = when (size) {
                    TagSize.SMALL -> Theme.typography.bodyExtraSmall
                    TagSize.MEDIUM -> Theme.typography.bodySmall
                    TagSize.LARGE -> Theme.typography.bodyMedium
                }.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )

            // Close button
            if (closable && onClose != null) {
                Spacer(modifier = Modifier.width(Spacing.xs))
                Box(
                    modifier = Modifier
                        .size(tokens.iconBoxSize)
                        .clickable(
                            enabled = !disabled,
                            onClick = onClose
                        )
                ) {
                    Icon(
                        name = Icons.x,
                        size = tokens.iconSize,
                        tint = textColor
                    )
                }
            }
        }
    }
}

enum class TagTheme { PRIMARY, SUCCESS, WARNING, DANGER, DEFAULT }
enum class TagSize { LARGE, MEDIUM, SMALL }
enum class TagVariant { DARK, LIGHT, OUTLINE }
