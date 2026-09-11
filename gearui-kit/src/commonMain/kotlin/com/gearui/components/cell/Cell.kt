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
    trailing: (@Composable () -> Unit)? = null,
    /**
     * 自绘标题，给出时**取代** [title] 的渲染（[title] 仍然要传，作为无障碍与排障时的纯文本）。
     *
     * 存在的理由：标题里需要富文本的场景不止一个——搜索结果要把命中的字标色。
     * 没有这个插槽，调用方只能绕开 Cell 自己拼一行，于是同一个列表里两种行高、
     * 两种分割线，改一处样式要改两处。
     */
    titleContent: (@Composable () -> Unit)? = null,
    /** 同上，用于副标题（搜索命中在备注/账号名时，副标题要显示并高亮那一段）。 */
    descriptionContent: (@Composable () -> Unit)? = null,
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
                // heightIn, not height: 24dp is the *title line* an icon should centre on, but a
                // leading slot taller than that (a 40dp avatar in a name + username row) must keep
                // its own height. A fixed height squashed it to 40x24 — a visibly stretched avatar.
                Box(
                    modifier = Modifier.align(Alignment.Top).heightIn(min = 24.dp),
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
            if (titleContent != null) {
                titleContent()
            } else {
                Text(
                    text = title,
                    // Matches a UIKit table row: body 17pt Regular. BodyLarge(16) with full-black foreground reads
                    // heavier than the system Settings app; 17 Regular is what iOS users see as the default row title.
                    style = CellTextStyles.Title,
                    color = titleColor ?: if (enabled) colors.foreground else colors.mutedForeground
                )
            }

            if (descriptionContent != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                descriptionContent()
            } else if (description != null) {
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
                name = Icons.caret_right,
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
