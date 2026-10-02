package com.gearui.foundation.interaction

import com.gearui.components.icon.IconSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.primitives.Icon
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.background
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
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.semantics.semantics

/**
 * An icon-only button inside a component: a calendar's month arrows, a carousel's
 * arrows. It carries the three things a hand-built `Box(...).clickable { }` kept
 * leaving out — a name a screen reader can read, the button role, and a press
 * response — so components use this instead of assembling their own.
 */
@Composable
internal fun IconActionButton(
    icon: IconSource,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp,
    iconSize: Dp,
    background: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    // Drawn at [size], a hit region of at least 44 around it (see hitTarget).
    Box(
        modifier = modifier
            .hitTarget()
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .pressScale(interaction, enabled)
                .clip(shape)
                .background(background),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, size = iconSize, tint = tint)
        }
    }
}
