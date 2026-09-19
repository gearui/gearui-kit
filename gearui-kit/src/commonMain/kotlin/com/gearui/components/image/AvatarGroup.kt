package com.gearui.components.image

import androidx.compose.runtime.Composable
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp

/** One member of an [AvatarGroup]: a picture, or initials when there is none. */
data class AvatarGroupItem(
    val painter: Painter? = null,
    val fallbackText: String = "",
)

/**
 * AvatarGroup — HeroUI v3 `AvatarGroup` for mobile: overlapping avatars that say who
 * is in a conversation or a team, with "+N" for the rest.
 *
 * Each avatar carries a ring in the surrounding colour so the overlap stays readable;
 * pass [ringColor] when the group sits on a card rather than the page.
 *
 * [max] caps how many faces are drawn. The remainder becomes one counter chip, which
 * is the point of the component: a row of twenty faces is unreadable and slow.
 */
@Composable
fun AvatarGroup(
    items: List<AvatarGroupItem>,
    modifier: Modifier = Modifier,
    size: Dp = ControlGeometry.avatarGroupSize,
    max: Int = 4,
    overlap: Dp = size / 3,
    ringColor: com.tencent.kuikly.compose.ui.graphics.Color = Theme.colors.background,
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors
    val shown = items.take(max.coerceAtLeast(0))
    val remaining = items.size - shown.size
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        shown.forEachIndexed { index, item ->
            Box(
                Modifier
                    .offset(x = -overlap * index)
                    .size(size)
                    .clip(CircleShape)
                    .border(BorderWidth.thick, ringColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Avatar(
                    painter = item.painter,
                    size = size - BorderWidth.thick * 2,
                    fallbackText = item.fallbackText,
                    onClick = onClick,
                )
            }
        }
        if (remaining > 0) {
            Box(
                Modifier
                    .offset(x = -overlap * shown.size)
                    .size(size)
                    .clip(CircleShape)
                    .border(BorderWidth.thick, ringColor, CircleShape)
                    .background(colors.muted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$remaining",
                    style = Theme.typography.bodyExtraSmall.copy(fontWeight = FontWeight.Medium),
                    color = colors.mutedForeground,
                )
            }
        }
    }
}
