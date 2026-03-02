package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.DividerSpecs

/**
 * DividerTokens - 分割线尺寸 Token
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/components/divider/td_divider.dart
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors.divider
 */
data class DividerTokens(
    val thickness: Dp,
    val insetStart: Dp,
    val insetEnd: Dp
)

/**
 * Dividers - 预设分割线样式
 */
object Dividers {
    val Full = DividerTokens(
        thickness = DividerSpecs.height,
        insetStart = 0.dp,
        insetEnd = 0.dp
    )

    /** 缩进分割线 (列表项) */
    val Inset = DividerTokens(
        thickness = DividerSpecs.height,
        insetStart = 16.dp,
        insetEnd = 0.dp
    )

    /** Section 分隔块 (8dp 灰色块) */
    val Section = DividerTokens(
        thickness = 8.dp,
        insetStart = 0.dp,
        insetEnd = 0.dp
    )
}
