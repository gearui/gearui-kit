package com.gearui.components.cell

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.TextStyle
import com.gearui.foundation.typography.Typography
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Cell - list cell
 *
 * For list rows; supports a title, a description, a chevron and more
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
    trailing: (@Composable () -> Unit)? = null
) {
    val colors = Theme.colors
    val tokens = if (compact) CellDefaults.Compact else CellDefaults.Default

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = tokens.minHeight)
            .background(colors.surface)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = tokens.paddingHorizontal,
                vertical = tokens.paddingVertical
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon. In a two-line cell the icon aligns with the TITLE line (box height = title line
        // height, centred inside) — not the whole row, which would leave it hovering between title and
        if (leading != null) {
            if (description != null) {
                Box(
                    modifier = Modifier.align(Alignment.Top).height(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    leading()
                }
            } else {
                leading()
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Middle content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                // Matches a UIKit table row: body 17pt Regular. BodyLarge(16) with full-black foreground reads
                // heavier than the system Settings app; 17 Regular is what iOS users see as the default row title.
                style = CellTextStyles.Title,
                color = titleColor ?: if (enabled) colors.foreground else colors.mutedForeground
            )

            if (description != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = description,
                    // UIKit footnote 13pt。
                    style = CellTextStyles.Footnote,
                    color = colors.mutedForeground
                )
            }
        }

        // Trailing description text
        if (note != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = note,
                // UIKit puts the trailing value at the same size as the title (17), separated only by the secondary colour.
                style = CellTextStyles.Title,
                color = colors.mutedForeground
            )
        }

        // Trailing custom content
        if (trailing != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailing()
        }

        // Chevron
        if (arrow) {
            Spacer(modifier = Modifier.width(Spacing.sm))
            Icon(
                name = Icons.chevron_right,
                size = IconSizes.Default.md,
                tint = colors.mutedForeground
            )
        }
    }
}

/**
 * Text metrics for Cell, matching a UIKit inset-grouped table:
 * row title / trailing value = body 17pt Regular; description = footnote 13pt.
 * (The kit's Typography walks a multiple-of-4 scale with no 17; pinned to UIKit here, not snapped to the scale.)
 */
private object CellTextStyles {
    val Title = TextStyle(17.sp, 24.sp, FontWeight.Normal)
    val Footnote = TextStyle(13.sp, 18.sp, FontWeight.Normal)
}
