package com.gearui.components.cell

import androidx.compose.runtime.Composable
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
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
        // Leading icon。两行 cell 里图标与**标题行**对齐（盒高 = 标题行高，内部居中），
        // 不对整行居中——那会让图标悬在标题和描述之间，和单行 cell 的图标错位。
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
                // 对标 UIKit 表格行：body 17pt Regular。BodyLarge(16) 配全黑前景显得比
                // 系统设置更「重」，17 Regular 是 iOS 用户眼里的默认行标题。
                style = CellTextStyles.Title,
                color = if (enabled) colors.foreground else colors.mutedForeground
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
                // UIKit 右侧 value 与标题同字号（17），只用次级颜色区分。
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
 * Cell 的文字规格，对标 UIKit inset-grouped 表格：
 * 行标题/右侧值 = body 17pt Regular；描述 = footnote 13pt。
 * （kit 的 Typography 走 4 的倍数刻度，没有 17；这里按 UIKit 钉死，不迁就刻度。）
 */
private object CellTextStyles {
    val Title = TextStyle(17.sp, 24.sp, FontWeight.Normal)
    val Footnote = TextStyle(13.sp, 18.sp, FontWeight.Normal)
}
