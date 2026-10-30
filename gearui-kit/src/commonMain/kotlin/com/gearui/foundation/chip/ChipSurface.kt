package com.gearui.foundation.chip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.interaction.HitRegion
import com.gearui.foundation.interaction.pressScale
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.components.icon.IconSource
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.text.style.TextOverflow

/**
 * A tappable pill — a suggested question, a quick reply: drawn compact, taking taps in a
 * region at least 44 tall around it, pressed with the kit's press scale, named by its text.
 */
@Composable
internal fun ChipSurface(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: IconSource? = null,
    enabled: Boolean = true,
) {
    val colors = Theme.colors
    val shape = Theme.shapes.full
    val interaction = remember { MutableInteractionSource() }
    HitRegion(
        modifier
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Row(
            Modifier
                .pressScale(interaction, enabled)
                .clip(shape)
                .background(colors.card)
                .border(BorderWidth.hairline, colors.border, shape)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            val tint = if (enabled) colors.foreground else colors.mutedForeground
            icon?.let { Icon(it, size = IconSizes.Default.md, tint = tint) }
            Text(label, style = Theme.typography.bodySmall, color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
