package com.gearui.components.avatar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.pressScale
import com.gearui.foundation.primitives.Text
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.layout.ContentScale
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.clearAndSetSemantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.onClick
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Avatar — HeroUI Native's `Avatar`: a picture of someone, with their initials as the
 * fallback.
 *
 * The [fallback] is always drawn first and the picture over it, so the initials show
 * while a [url] loads and stay if it fails — no blank or broken circle. The picture is
 * cropped to fill the [shape]. [url] is loaded asynchronously (KuiklyUI's coil3: http,
 * https or `file://`); [painter] is for a picture already in hand. Choosing the source —
 * a local cache before the network, a generated picture for a user without one — is the
 * app's.
 *
 * [badgeCount] (a number) or [badgeDot] (a red dot, for a muted chat) sits over the
 * top-right corner; [online] adds a status dot at the bottom-right, ringed in the
 * surface colour. Both are drawn above the picture.
 *
 * @param fallback the initials to show, already chosen by the caller (Chinese names
 *   usually take one character, Latin names two); at most two characters are shown.
 * @param shape `CircleShape` by default; a rounded square such as `Theme.shapes.md` for
 *   apps that set avatars that way.
 * @param backgroundColor the fallback's fill; pin it when the avatar must match a
 *   picture rendered outside the theme (a QR code's centre, say).
 */
@Composable
fun Avatar(
    fallback: String,
    modifier: Modifier = Modifier,
    url: String? = null,
    painter: Painter? = null,
    size: Dp = AvatarSizeTokens.Medium.size,
    shape: Shape = CircleShape,
    backgroundColor: Color = Theme.colors.muted,
    contentColor: Color = Theme.colors.foreground,
    badgeCount: Int? = null,
    badgeDot: Boolean = false,
    online: Boolean = false,
    onlineColor: Color = Theme.colors.success,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    val name = contentDescription ?: fallback
    val source = url?.trim()?.takeIf { it.isNotEmpty() }
    // The initials give way once a picture is showing, so a picture with transparent
    // parts does not show them through; the fill stays behind it.
    var pictureShown by remember(source, painter) { mutableStateOf(source == null && painter != null) }

    Box(
        modifier = modifier
            .size(size)
            .clearAndSetSemantics {
                this.contentDescription = name
                if (onClick != null) {
                    role = Role.Button
                    onClick(label = null) { onClick(); true }
                }
            }
            .then(
                if (onClick != null) {
                    Modifier
                        .pressScale(interaction)
                        .clickable(interactionSource = interaction, indication = null, onClick = onClick)
                } else Modifier
            ),
    ) {
        Box(
            modifier = Modifier.size(size).clip(shape).background(backgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            if (!pictureShown) {
                Text(
                    text = fallback.take(2).uppercase(),
                    // Reference `.avatar__fallback-text`: xs/sm/base by size, medium weight.
                    style = fallbackStyle(size).copy(fontWeight = FontWeight.Medium),
                    color = contentColor,
                )
            }
            AvatarPicture(source = source, painter = painter, size = size, shape = shape, onShown = { pictureShown = it })
        }

        if (badgeCount != null || badgeDot) {
            Badge(
                type = if (badgeDot) BadgeType.RedPoint else BadgeType.Message,
                count = badgeCount,
                showZero = true,
                content = { Box(Modifier.size(size)) },
            )
        }
        if (online) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(ControlGeometry.avatarStatusSize)
                    .clip(CircleShape)
                    .background(onlineColor)
                    .border(BorderWidth.thick, colors.surface, CircleShape),
            )
        }
    }
}

@Composable
private fun AvatarPicture(source: String?, painter: Painter?, size: Dp, shape: Shape, onShown: (Boolean) -> Unit) {
    when {
        source != null -> {
            // Keyed by the address: a new picture gets a fresh attempt.
            var failed by remember(source) { mutableStateOf(false) }
            if (!failed) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = source,
                        onSuccess = { onShown(true) },
                        onError = {
                            failed = true
                            onShown(false)
                        },
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(size).clip(shape),
                )
            }
        }
        painter != null -> Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).clip(shape),
        )
    }
}

@Composable
private fun fallbackStyle(size: Dp) = when {
    size.value <= AvatarSizeTokens.Small.size.value -> Theme.typography.bodyExtraSmall
    size.value <= AvatarSizeTokens.Medium.size.value -> Theme.typography.bodySmall
    else -> Theme.typography.bodyMedium
}
