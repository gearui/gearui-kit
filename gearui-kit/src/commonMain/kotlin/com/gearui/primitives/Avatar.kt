package com.gearui.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.AvatarSpecs
import com.gearui.theme.Theme

/**
 * Avatar - 100% Theme 驱动的身份表达容器
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：AvatarColorTokens / 硬编码颜色
 *
 * 改造要点：
 * - 使用 Theme.colors.surfaceVariant 作为背景
 * - 使用 Theme.colors.textSecondary 作为内容颜色
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,

    image: Painter? = null,
    text: String? = null,
    icon: Painter? = null,

    size: Dp = AvatarSpecs.Size.medium,
    radius: Dp = AvatarSpecs.defaultRadius,  // 圆形

    badgeCount: Int? = null,
    badgeDot: Boolean = false,
    badgeVisible: Boolean = true,

    onClick: (() -> Unit)? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    val contentComposable: @Composable () -> Unit = {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(radius))
                .background(colors.surfaceVariant)
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
                        color = colors.textSecondary
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
