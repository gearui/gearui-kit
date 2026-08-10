package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth

/**
 * Divider - 100% Theme 驱动的分割线原语
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 改造要点：
 * - 移除 DividerTokens 的颜色硬编码
 * - 直接使用 Theme.colors.border
 */

/**
 * 基础分割线组件
 *
 * @param thickness 线条粗细
 * @param insetStart 左侧缩进
 * @param insetEnd 右侧缩进
 */
@Composable
fun Divider(
    thickness: Dp = 0.5.dp,
    insetStart: Dp = 0.dp,
    insetEnd: Dp = 0.dp
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = insetStart, end = insetEnd)
            .height(thickness)
            .background(colors.border)
    )
}

/**
 * 便捷别名（常用分割线直接调用）
 */

/**
 * 全宽分割线（最常用）
 */
@Composable
fun DividerFull() = Divider(
    thickness = BorderWidth.hairline,
    insetStart = 0.dp,
    insetEnd = 0.dp
)

/**
 * 左缩进分割线（列表项专用）
 */
@Composable
fun DividerInset() = Divider(
    thickness = BorderWidth.hairline,
    insetStart = 16.dp,
    insetEnd = 0.dp
)

/**
 * 章节分隔（8dp 灰色色块）
 */
@Composable
fun DividerSection() {
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(colors.muted)
    )
}
