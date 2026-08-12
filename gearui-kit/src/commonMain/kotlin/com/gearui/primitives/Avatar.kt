package com.gearui.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.theme.Theme

/**
 * Avatar - fully Theme-driven identity container
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: hardcoded colours
 *
 * Rework notes:
 * - Theme.colors.muted is used as the background
 * - Theme.colors.mutedForeground is used for the content
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,

    image: Painter? = null,
    text: String? = null,
    icon: Painter? = null,

    size: Dp = AvatarSizeTokens.Medium.size,
    radius: Dp = AvatarSizeTokens.Medium.radius,  // 圆形

    // Downstream code (PrivChatAvatar and friends) can pin the fallback colours so the light/dark
    // theme does not disagree visually with a bitmap (such as the avatar at the centre of a QR code).
    backgroundColor: Color? = null,
    contentColor: Color? = null,

    badgeCount: Int? = null,
    badgeDot: Boolean = false,
    badgeVisible: Boolean = true,

    onClick: (() -> Unit)? = null
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors
    val resolvedBackground = backgroundColor ?: colors.muted
    val resolvedContent = contentColor ?: colors.mutedForeground

    val contentComposable: @Composable () -> Unit = {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(radius))
                .background(resolvedBackground)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                image != null -> {
                    Icon(
                        painter = image,
                        size = size
                    )
                }

                text != null -> {
                    Text(
                        text = text.take(2).uppercase(),
                        style = Typography.BodyMedium.copy(fontSize = (size.value * 0.4).sp),
                        color = resolvedContent
                    )
                }

                icon != null -> {
                    Icon(
                        painter = icon,
                        size = size * 0.6f
                    )
                }
            }
        }
    }

    // Badge overlay
    if (badgeVisible && (badgeCount != null || badgeDot)) {
        Badge(
            type = if (badgeDot) BadgeType.RedPoint else BadgeType.Message,
            count = badgeCount,
            showZero = badgeVisible,
            content = contentComposable
        )
    } else {
        contentComposable()
    }
}
