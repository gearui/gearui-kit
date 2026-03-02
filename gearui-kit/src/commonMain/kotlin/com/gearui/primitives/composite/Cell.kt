package com.gearui.primitives.composite

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import androidx.compose.runtime.remember
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.foundation.layout.size
import com.gearui.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.interaction.createMutableInteractionSource
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Surface
import com.gearui.foundation.primitives.SurfaceColorTokens
import com.gearui.foundation.primitives.SurfaceTokens
import com.gearui.foundation.CellSpecs
import com.gearui.primitives.*
import com.gearui.theme.Theme

/**
 * Cell - 100% Theme 驱动的 List 生态核心交互单元
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：CellTokens.background / 硬编码颜色
 *
 * 改造要点：
 * - 移除 CellTokens 的颜色硬编码
 * - 直接使用 Theme.colors.background
 *
 * 职责：
 * - 左中右三段式布局模板
 * - 统一交互状态（pressed/disabled）
 * - 统一最小高度
 */
@Composable
fun Cell(
    modifier: Modifier = Modifier,
    minHeight: Dp = 52.dp,
    paddingHorizontal: Dp = CellSpecs.padding,
    paddingVertical: Dp = 12.dp,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },

    leading: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showArrow: Boolean = false,
    showDivider: Boolean = true
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    val surfaceTokens = SurfaceTokens(
        height = minHeight,
        radius = 0.dp,  // Cell 通常不需要圆角
        borderWidth = 0.dp,
        padding = PaddingValues(0.dp)  // Cell 自己控制 padding
    )

    // ⭐ 使用 Theme.colors - Cell 使用 surface 背景
    val surfaceColors = SurfaceColorTokens(
        background = colors.surface,
        disabledBackground = colors.surface,
        pressedBackground = colors.surfaceVariant,
        border = colors.surface
    )

    Column {
        Surface(
            enabled = enabled,
            tokens = surfaceTokens,
            colors = surfaceColors,
            interactionSource = interactionSource,
            onClick = onClick
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(
                        horizontal = paddingHorizontal,
                        vertical = paddingVertical
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧内容（Leading）
                leading?.let {
                    it()
                    HorizontalSpacer(Spacing.md)
                }

                // 中间内容（Title + Subtitle）
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    title()

                    subtitle?.let {
                        VerticalSpacer(Spacing.xs)
                        it()
                    }
                }

                // 右侧内容（Trailing）
                if (trailing != null) {
                    HorizontalSpacer(Spacing.sm)
                    trailing()
                }

                // 箭头指示器（showArrow）
                if (showArrow) {
                    HorizontalSpacer(Spacing.sm)
                    // TODO: 使用 Icon + Icons.ChevronRight
                    Box(modifier = Modifier.size(16.dp))
                }
            }
        }

        // Divider
        if (showDivider) {
            DividerInset()
        }
    }
}
