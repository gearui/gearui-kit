package com.gearui.components.closebutton

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.gearui.foundation.primitives.Icon
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * CloseButton — HeroUI Native `CloseButton`.
 *
 * An icon-only tertiary button: a 32 circle on the neutral fill with an 18 muted "x",
 * pressing with the shared [PressableFeedback]. Use it for every dismiss affordance
 * (dialogs, sheets, notifications, banners) so they look and respond the same.
 *
 * [contentDescription] is what a screen reader announces; override it when the button
 * closes something specific ("Dismiss banner") rather than the surface it sits on.
 *
 * [containerColor] and [iconColor] exist for media surfaces such as an image viewer,
 * where the neutral fill would disappear against the photo. [icon] swaps the glyph for
 * another icon-only control of the same shape, such as a number field's step buttons.
 */
@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = ControlGeometry.closeButtonSize,
    iconSize: Dp = ControlGeometry.closeButtonIcon,
    containerColor: Color = Color.Unspecified,
    iconColor: Color = Color.Unspecified,
    icon: String = Icons.x,
    contentDescription: String = I18n.strings.common.close,
) {
    val colors = Theme.colors
    PressableFeedback(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(if (containerColor.isSpecified()) containerColor else colors.muted),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                name = icon,
                size = iconSize,
                tint = if (iconColor.isSpecified()) iconColor else colors.mutedForeground,
            )
        }
    }
}

private fun Color.isSpecified(): Boolean = this != Color.Unspecified
