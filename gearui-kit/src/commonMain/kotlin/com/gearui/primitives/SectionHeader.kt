package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * SectionHeader - 100% Theme 驱动的章节头原语
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：SectionTokens / TextColors / 硬编码颜色
 *
 * 改造要点：
 * - 移除 SectionTokens 的颜色硬编码
 * - 直接使用 Theme.colors.textPrimary/textSecondary
 * - 背景使用 colors.background
 *
 * 使用场景：
 * - 页面主标题（"设置"、"我的"）
 * - 分组标题（"基础组件"、"表单组件"）
 * - 列表分组（"最近使用"、"全部应用"）
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingText: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    height: Dp = 44.dp,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 12.dp
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(colors.background)
            .then(clickableModifier)
            .padding(
                horizontal = paddingHorizontal,
                vertical = paddingVertical
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标题区域
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )

                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = Typography.Caption,
                        color = colors.textSecondary
                    )
                }
            }

            // 右侧 trailing
            when {
                trailing != null -> {
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        trailing()
                    }
                }

                trailingText != null -> {
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = trailingText,
                            style = Typography.BodySmall,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
