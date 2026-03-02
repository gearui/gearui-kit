package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * ListItem - 100% Theme 驱动的语义化列表项组件
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：TextColors / 硬编码颜色
 *
 * 改造要点：
 * - 移除 TextColors 依赖
 * - 直接使用 Theme.colors.textPrimary/textSecondary
 *
 * 👉 这是业务 90% 会使用的组件
 *
 * 本质：Cell 的语义化封装
 *
 * 职责：
 * - 提供 title/subtitle/value 三段式语义 API
 * - 自动处理文字样式（无需手写 Text）
 * - 自动处理颜色（primary/secondary）
 * - 自动处理箭头（有 onClick 就显示）
 *
 * Example:
 * ```
 * ListItem(
 *     title = "账号与安全",
 *     onClick = { navigateToSecurity() }
 * )
 *
 * ListItem(
 *     title = "消息通知",
 *     subtitle = "接收新消息通知",
 *     value = "已开启"
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
    // ⭐ Framework Rule #1: 第一行永远是这个
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
                color = colors.textPrimary
            )
        },
        subtitle = subtitle?.let {
            {
                Text(
                    text = it,
                    style = Typography.Caption,
                    color = colors.textSecondary
                )
            }
        },
        trailing = trailing ?: value?.let {
            {
                Text(
                    text = it,
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }
    )
}
