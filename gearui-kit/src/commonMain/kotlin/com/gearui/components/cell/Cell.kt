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
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Cell - 列表单元格组件
 *
 * 用于列表展示，支持标题、描述、箭头等
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
        // 左侧图标
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(10.dp))
        }

        // 中间内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.BodyLarge,
                color = if (enabled) colors.textPrimary else colors.textDisabled
            )

            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 右侧说明文字
        if (note != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = note,
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
        }

        // 右侧自定义内容
        if (trailing != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailing()
        }

        // 箭头
        if (arrow) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                name = Icons.chevron_right,
                size = 16.dp,
                tint = colors.textPlaceholder
            )
        }
    }
}
