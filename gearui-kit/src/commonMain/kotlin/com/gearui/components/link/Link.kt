package com.gearui.components.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.interaction.pressScale
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.foundation.typography.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/** Text size of a [Link] or [LinkButton], matching the Button label sizes. */
enum class LinkSize { SMALL, MEDIUM, LARGE }

/**
 * Link — HeroUI v3 `Link` for mobile.
 *
 * Inline text navigation: medium weight in the foreground colour with an underline in
 * the separator colour, so it reads as a link without shouting in the accent colour.
 * Pressing scales it with [pressScale] (no highlight — there is no surface to
 * tint). Use [LinkButton] for a link that stands alone as an action.
 *
 * [startIcon] and [endIcon] take icon names and follow the text size.
 *
 * [underline] false drops the rule for links whose context already says "tappable",
 * such as a footer row of legal links.
 */
@Composable
fun Link(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: LinkSize = LinkSize.MEDIUM,
    enabled: Boolean = true,
    underline: Boolean = true,
    color: Color = Color.Unspecified,
    startIcon: String? = null,
    endIcon: String? = null,
) {
    val colors = Theme.colors
    val underlineColor = colors.separator
    LinkLabel(
        text = text,
        onClick = onClick,
        modifier = modifier,
        underlineColor = if (underline) underlineColor else null,
        style = linkStyle(size),
        enabled = enabled,
        color = if (color != Color.Unspecified) color else colors.foreground,
        startIcon = startIcon,
        endIcon = endIcon,
    )
}

/**
 * LinkButton — HeroUI Native `LinkButton`.
 *
 * A ghost button with no padding and no highlight, for inline actions such as
 * "Forgot password?" or "Terms of Service". Same label sizes as Button; presses with
 * the width-compensated scale only. Defaults to the foreground colour like the
 * reference ghost label; pass [color] (for example `Theme.colors.primary`) for an
 * accented action.
 */
@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: LinkSize = LinkSize.MEDIUM,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
) {
    LinkLabel(
        text = text,
        onClick = onClick,
        modifier = modifier,
        style = linkStyle(size),
        enabled = enabled,
        color = if (color != Color.Unspecified) color else Theme.colors.foreground,
    )
}

@Composable
private fun linkStyle(size: LinkSize): TextStyle {
    val typography = Theme.typography
    return when (size) {
        LinkSize.SMALL -> typography.bodySmall
        LinkSize.MEDIUM -> typography.bodyMedium
        LinkSize.LARGE -> typography.bodyLarge
    }.copy(fontWeight = FontWeight.Medium)
}

@Composable
private fun LinkLabel(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    style: TextStyle,
    enabled: Boolean,
    color: Color,
    underlineColor: Color? = null,
    startIcon: String? = null,
    endIcon: String? = null,
) {
    val source = remember { MutableInteractionSource() }
    // Icons follow the text size, like the reference `Link.Icon`.
    val iconSize = style.fontSize.value.dp
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)
            .pressScale(source, enabled = enabled)
            .clickable(enabled = enabled, interactionSource = source, indication = null, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.buttonGapSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (startIcon != null) Icon(name = startIcon, size = iconSize, tint = color)
        // The underline belongs to the words, not the icons. It is a real 1dp view:
        // draw modifiers on a Kuikly Text node do not paint.
        Box {
            Text(text = text, style = style, color = color, maxLines = 1)
            if (underlineColor != null) {
                Box(Modifier.matchParentSize(), contentAlignment = Alignment.BottomStart) {
                    Box(Modifier.fillMaxWidth().height(BorderWidth.thin).background(underlineColor))
                }
            }
        }
        if (endIcon != null) Icon(name = endIcon, size = iconSize, tint = color)
    }
}
