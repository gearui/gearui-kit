package com.gearui.components.empty

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * EmptyState - 100% Theme 驱动的空状态组件
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 空状态提示
 * - 自定义图标/图片
 * - 操作按钮
 * - 多种预设状态
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 图标/图片
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // 默认空状态图标
            Icon(
                name = Icons.image,
                size = 28.dp,
                tint = colors.textPlaceholder
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 主要消息
        Text(
            text = message,
            style = Typography.TitleMedium,
            color = colors.textPrimary
        )

        // 描述文字
        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
        }

        // 操作按钮
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .clip(shapes.small)
                    .background(colors.primary)
                    .clickable(onClick = onAction)
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = actionText,
                    style = Typography.BodyMedium,
                    color = colors.onPrimary
                )
            }
        }
    }
}

/**
 * EmptyStateType - 预设空状态类型
 */
@Composable
fun EmptyStatePreset(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    val (message, description, iconName) = when (type) {
        EmptyStateType.NO_DATA -> Triple("暂无数据", "当前没有可显示的内容", Icons.image)
        EmptyStateType.NO_SEARCH_RESULT -> Triple("无搜索结果", "换个关键词试试吧", Icons.search)
        EmptyStateType.NO_NETWORK -> Triple("网络连接失败", "请检查网络设置后重试", Icons.warning)
        EmptyStateType.ERROR -> Triple("加载失败", "出现了一些问题", Icons.error)
        EmptyStateType.NO_PERMISSION -> Triple("无权限访问", "您暂无查看此内容的权限", Icons.no_photography)
    }

    val colors = Theme.colors
    val typography = Theme.typography

    EmptyState(
        message = message,
        description = description,
        icon = {
            Icon(
                name = iconName,
                size = 28.dp,
                tint = colors.textPlaceholder
            )
        },
        actionText = actionText,
        onAction = onAction,
        modifier = modifier
    )
}

/**
 * EmptyStateType - 空状态类型
 */
enum class EmptyStateType {
    /** 无数据 */
    NO_DATA,

    /** 无搜索结果 */
    NO_SEARCH_RESULT,

    /** 无网络 */
    NO_NETWORK,

    /** 错误 */
    ERROR,

    /** 无权限 */
    NO_PERMISSION
}
