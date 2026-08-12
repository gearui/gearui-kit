package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * ListItem - fully Theme-driven semantic list row
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: TextColors or hardcoded colours
 *
 * Rework notes:
 * - the TextColors dependency is gone
 * - Theme.colors.foreground / mutedForeground are used directly
 *
 * 👉 This is the component product code will reach for 90% of the time
 *
 * In essence: a semantic wrapper over Cell
 *
 * Responsibilities:
 * - offers the three-part title / subtitle / value semantic API
 * - applies the text styles (no hand-written Text)
 * - applies the colours (primary / secondary)
 * - shows the chevron automatically (whenever onClick is present)
 *
 * Example:
 * ```
 * ListItem(
 *     title = "Account and security",
 *     onClick = { navigateToSecurity() }
 * )
 *
 * ListItem(
 *     title = "Notifications",
 *     subtitle = "Receive new message alerts",
 *     value = "On"
 * )
 * ```
 */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    Cell(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        showArrow = onClick != null && trailing == null,  // 有点击但无自定义 trailing 时显示箭头
        leading = leading,
        title = {
            Text(
                text = title,
                style = Typography.BodyMedium,
                color = colors.foreground
            )
        },
        subtitle = subtitle?.let {
            {
                Text(
                    text = it,
                    style = Typography.Caption,
                    color = colors.mutedForeground
                )
            }
        },
        trailing = trailing ?: value?.let {
            {
                Text(
                    text = it,
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }
        }
    )
}
