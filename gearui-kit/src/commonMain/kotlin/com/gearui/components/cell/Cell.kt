package com.gearui.components.cell

import com.gearui.foundation.interaction.rememberActivationTracker
import com.gearui.foundation.interaction.activationTarget
import com.gearui.foundation.interaction.LocalControlLabel
import androidx.compose.runtime.CompositionLocalProvider
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.list.LocalRowInteractionSource
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.theme.Theme
import com.gearui.foundation.typography.IconSizes

/**
 * Cell — one row: a title, an optional description, an optional value or trailing
 * slot, and a chevron when the row leads somewhere.
 *
 * This is the only row implementation in the library, so every list looks and presses
 * the same. Its neighbours are containers, not rival rows:
 * - [com.gearui.components.cellgroup.CellGroup] is **a card of rows** — the group
 *   title, the surface, the radius and the separators between rows.
 * - [com.gearui.foundation.primitives.List] is **the scrolling container** — a lazy
 *   list with `item` / `items` / `section`, whose rows are Cells.
 * - [com.gearui.primitives.composite.ListItem] is this row under a title/subtitle/value
 *   name, with the chevron decided for you.
 *
 * A tappable Cell answers the press; a display-only one does not, because nothing
 * happens.
 */
@Composable
fun Cell(
    title: String,
    modifier: Modifier = Modifier,
    note: String? = null,
    description: String? = null,
    arrow: Boolean = false,
    enabled: Boolean = true,
    /** Title colour override (destructive red for dangerous rows, say); null = the regular foreground. */
    titleColor: Color? = null,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    /**
     * Replaces the title rendering for rich text such as search highlighting.
     * Keep [title] as the plain-text accessibility/diagnostic counterpart.
     * This slot preserves shared row geometry instead of requiring a second row implementation.
     */
    titleContent: (@Composable () -> Unit)? = null,
    /** Rich subtitle counterpart, including highlighted account names or notes. */
    descriptionContent: (@Composable () -> Unit)? = null,
) {
    val colors = Theme.colors
    val tokens = if (compact) CellDefaults.Compact else CellDefaults.Default
    val interactive = onClick != null && enabled
    // A group hands the row its source so it can watch the press and cover the
    // separators either side; standalone, the row owns one.
    val interaction = LocalRowInteractionSource.current ?: remember { MutableInteractionSource() }
    val activation = rememberActivationTracker()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = tokens.minHeight)
            .graphicsLayer { alpha = if (enabled) 1f else tokens.disabledAlpha }
            .then(
                // A row that does something has to answer the finger. Cell was a bare
                // clickable: it ran the action with nothing on screen acknowledging the
                // touch, which reads as a dead row on a slow screen or a slow handler.
                if (interactive) {
                    Modifier
                        // No scale: a row spans its card, and the press fills it edge to
                        // edge the way the platform's own lists do. The corners come from
                        // the card's own clip, so the first and last rows round with it
                        // and the rows between them stay square.
                        .rowPressFeedback(interaction = interaction, shape = RectangleShape, scale = false, base = colors.surface)
                        .activationTarget(activation)
                        .clickable(interactionSource = interaction, indication = null) { activation.mark(); onClick!!() }
                } else {
                    Modifier.background(colors.surface)
                }
            )
            // Vertical padding belongs to the leading and text blocks, not to the
            // row. A trailing accessory — a switch with its 44 touch target, say —
            // is centred in the row and may overlap the padding, as it does on
            // iOS: padding the whole row made every switch row 44 + 28 = 72 tall
            // beside 52-tall rows in the same card.
            .padding(horizontal = tokens.paddingHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Native ListGroup centers the prefix against the complete content block.
        // Do not constrain its height: a caller may supply an avatar instead of an icon.
        if (leading != null) {
            Box(modifier = Modifier.padding(vertical = tokens.paddingVertical)) {
                CompositionLocalProvider(LocalControlLabel provides title) { leading() }
            }
            Spacer(modifier = Modifier.width(ControlGeometry.listItemGap))
        }

        // Middle content
        Column(modifier = Modifier.weight(1f).padding(vertical = tokens.paddingVertical)) {
            if (titleContent != null) {
                titleContent()
            } else {
                Text(
                    text = title,
                    // Reference `.list-group-item__title`: base size, medium weight.
                    style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = titleColor ?: colors.foreground
                )
            }

            if (descriptionContent != null) {
                descriptionContent()
            } else if (description != null) {
                Text(
                    text = description,
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Trailing description text
        if (note != null) {
            Spacer(modifier = Modifier.width(ControlGeometry.listItemGap))
            Text(
                text = note,
                style = Theme.typography.bodyMedium,
                color = colors.mutedForeground
            )
        }

        // Trailing custom content
        if (trailing != null) {
            Spacer(modifier = Modifier.width(ControlGeometry.listItemGap))
            CompositionLocalProvider(LocalControlLabel provides title) { trailing() }
        }

        // Chevron
        if (arrow) {
            Spacer(modifier = Modifier.width(ControlGeometry.listItemGap))
            Icon(
                name = Icons.caret_right,
                size = IconSizes.Default.md,
                tint = colors.mutedForeground
            )
        }
    }
}
