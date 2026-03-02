package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.gearui.foundation.layout.Spacing

/**
 * Spacer - 统一间距原语
 *
 * ✅ 作用：替代所有 Spacer(Modifier.height(xx.dp)) 硬编码
 * ✅ 原则：只允许使用 Spacing 中定义的语义化间距值
 *
 * 使用场景：
 * - 垂直布局组件之间的间距
 * - 水平布局元素之间的间距
 * - 章节之间的分隔
 */

/**
 * 垂直间距（最常用）
 *
 * Example:
 * ```
 * Column {
 *     Button("按钮1")
 *     VerticalSpacer(Spacing.md)
 *     Button("按钮2")
 * }
 * ```
 */
@Composable
fun VerticalSpacer(height: Dp = Spacing.md) {
    Box(modifier = Modifier.height(height))
}

/**
 * 水平间距
 *
 * Example:
 * ```
 * Row {
 *     Button("确定")
 *     HorizontalSpacer(Spacing.sm)
 *     Button("取消")
 * }
 * ```
 */
@Composable
fun HorizontalSpacer(width: Dp = Spacing.md) {
    Box(modifier = Modifier.width(width))
}

/**
 * 便捷别名（常用间距直接调用）
 */
@Composable
fun SpacerSmall() = VerticalSpacer(Spacing.sm)

@Composable
fun SpacerMedium() = VerticalSpacer(Spacing.md)

@Composable
fun SpacerLarge() = VerticalSpacer(Spacing.lg)

@Composable
fun SpacerExtraLarge() = VerticalSpacer(Spacing.xl)
