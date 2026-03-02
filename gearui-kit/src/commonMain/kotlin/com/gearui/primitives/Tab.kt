package com.gearui.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.TabSpecs
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * Tab - 100% Theme 驱动的导航选择器单元
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：TabColorTokens / 硬编码颜色
 *
 * 改造要点：
 * - selected: colors.primary
 * - unselected: colors.textSecondary
 * - indicator: colors.primary
 */
@Composable
fun Tab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    icon: Painter? = null,
    badgeCount: Int? = null,
    badgeDot: Boolean = false,

    height: Dp = TabSpecs.height,
    horizontalPadding: Dp = 16.dp,
    indicatorHeight: Dp = TabSpecs.indicatorHeight
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors
    val typography = Theme.typography

    val content: @Composable () -> Unit = {
        Column(
            modifier = modifier
                .height(height)
                .background(colors.surface)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Content Area (Icon + Text)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (icon != null) {
                    Icon(
                        painter = icon,
                        size = 18.dp
                    )
                }

                Text(
                    text = text,
                    style = Typography.BodyMedium,
                    color = if (selected) colors.textPrimary else colors.textSecondary
                )
            }

            Spacer(Modifier.height(4.dp))

            // Indicator (下划线)
            if (selected) {
                Box(
                    Modifier
                        .height(indicatorHeight)
                        .fillMaxWidth()
                        .background(colors.textPrimary)
                )
            } else {
                Spacer(Modifier.height(indicatorHeight))
            }
        }
    }

    // Badge overlay
    if (badgeCount != null || badgeDot) {
        Badge(
            type = if (badgeDot) BadgeType.RedPoint else BadgeType.Message,
            count = badgeCount,
            alignment = Alignment.TopEnd,
            content = content
        )
    } else {
        content()
    }
}
