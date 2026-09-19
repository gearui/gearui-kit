package com.gearui.components.inputgroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.LocalFieldEmbedded
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.RowScope
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/**
 * InputGroup — a field with attached blocks, for the constant part of a value: the
 * country code before a phone number, the unit after an amount, a protocol before a
 * host, a "Send code" action at the end.
 *
 * The group draws the field frame once; the [com.gearui.components.input.Input] inside
 * drops its own surface and keeps only its height, so one rounded outline holds
 * everything and the corners cannot disagree. Separate the parts with
 * [InputGroupDivider] where the addon needs to read as its own block.
 *
 * ```kotlin
 * InputGroup {
 *     InputGroupAddon(text = "+86")
 *     InputGroupDivider()
 *     Input(phone, { phone = it }, modifier = Modifier.weight(1f), placeholder = "Phone number")
 * }
 * ```
 *
 * The editable part needs `Modifier.weight(1f)`: an addon is as wide as its content and
 * the field takes the rest.
 */
@Composable
fun InputGroup(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = FieldDefaults.shape,
    content: @Composable RowScope.() -> Unit,
) {
    val inputColors = LocalInputColors.current
    FieldSurface(modifier = modifier.fillMaxWidth(), shape = shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldSizeTokens.Medium.height)
                .clip(shape)
                .background(inputColors.background)
                .alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalFieldEmbedded provides true) {
                content()
            }
        }
    }
}

/**
 * A block attached to an [InputGroup]: a label such as "+86", an icon, or a small
 * action. It sits on the neutral fill so it reads as part of the control rather than as
 * text the user could edit. [onClick] makes it a target with the shared press feedback.
 */
@Composable
fun InputGroupAddon(
    text: String? = null,
    modifier: Modifier = Modifier,
    icon: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = Theme.colors
    val body: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .background(colors.muted)
                .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.buttonGapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(name = icon, size = FieldDefaults.trailingIconSize, tint = colors.mutedForeground)
            }
            if (content != null) {
                content()
            } else if (text != null) {
                Text(
                    text = text,
                    style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.mutedForeground,
                    maxLines = 1,
                )
            }
        }
    }
    if (onClick != null) {
        PressableFeedback(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            enabled = enabled,
        ) { body() }
    } else {
        Box(modifier = modifier.fillMaxHeight().alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)) { body() }
    }
}

/** A hairline between an addon and the field. */
@Composable
fun InputGroupDivider() {
    Box(
        Modifier
            .width(BorderWidth.thin)
            .fillMaxHeight()
            .background(Theme.colors.separator)
    )
}
