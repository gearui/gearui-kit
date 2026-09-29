package com.gearui.components.avatar

import androidx.compose.runtime.Composable
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp

/** One member of an [AvatarGroup]: initials, and a picture by [url] or [painter]. */
data class AvatarGroupItem(
    val fallback: String,
    val url: String? = null,
    val painter: Painter? = null,
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
    require(size > BorderWidth.thick * 2 && overlap >= com.gearui.foundation.layout.Spacing.none && overlap < size)
    val colors = Theme.colors
    val shown = items.take(max.coerceAtLeast(0))
    val remaining = items.size - shown.size
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(-overlap), verticalAlignment = Alignment.CenterVertically) {
        shown.forEach { item ->
            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(BorderWidth.thick, ringColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Avatar(
                    fallback = item.fallback,
                    url = item.url,
                    painter = item.painter,
                    size = size - BorderWidth.thick * 2,
                    onClick = onClick,
                )
            }
        }
        if (remaining > 0) {
            Box(
                Modifier
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
