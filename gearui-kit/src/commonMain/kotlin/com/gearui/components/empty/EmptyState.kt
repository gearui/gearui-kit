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
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

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
    onAction: (() -> Unit)? = null,
    customAction: (@Composable () -> Unit)? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 图标/图片
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.height(Spacing.lg))
        } else {
            // 默认空状态图标
            Icon(
                name = Icons.image,
                size = 28.dp,
                tint = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // 主要消息
        Text(
            text = message,
            style = Typography.TitleMedium,
            color = colors.foreground
        )

        // 描述文字
        if (description != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = description,
                style = Typography.BodyMedium,
                color = colors.mutedForeground
            )
        }

        // 操作区域（优先自定义）
        if (customAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            customAction()
        } else if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            Box(
                modifier = Modifier
                    .clip(shapes.sm)
                    .background(colors.primary)
                    .clickable(onClick = onAction)
                    .padding(horizontal = Spacing.xl, vertical = 10.dp)
            ) {
                Text(
                    text = actionText,
                    style = Typography.BodyMedium,
                    color = colors.primaryForeground
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
    onAction: (() -> Unit)? = null,
    customAction: (@Composable () -> Unit)? = null
) {
    val s = I18n.strings
    val (message, description, iconName) = when (type) {
        EmptyStateType.NO_DATA -> Triple(s.common.noData, s.feedback.emptyNoDataDescription, Icons.image)
        EmptyStateType.NO_SEARCH_RESULT -> Triple(s.common.noSearchResult, s.feedback.emptyNoSearchResultDescription, Icons.search)
        EmptyStateType.NO_NETWORK -> Triple(s.feedback.emptyNoNetworkTitle, s.feedback.emptyNoNetworkDescription, Icons.warning)
        EmptyStateType.ERROR -> Triple(s.common.loadFailed, s.feedback.emptyErrorDescription, Icons.error)
        EmptyStateType.NO_PERMISSION -> Triple(s.feedback.emptyNoPermissionTitle, s.feedback.emptyNoPermissionDescription, Icons.no_photography)
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
                tint = colors.mutedForeground
            )
        },
        actionText = actionText,
        onAction = onAction,
        customAction = customAction,
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
