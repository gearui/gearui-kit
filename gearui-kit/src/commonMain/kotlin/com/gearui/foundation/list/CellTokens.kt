package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.CellSpecs

/**
 * CellTokens - Cell 组件尺寸规范
 *
 * Cell = List 生态的核心交互单元
 *
 * 参考: 内部组件规范cell/td_cell_style.dart
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors
 */
data class CellTokens(
    val minHeight: Dp,
    val paddingHorizontal: Dp,
    val paddingVertical: Dp,
    val disabledAlpha: Float,
    val showDivider: Boolean
)

object CellDefaults {
    /**
     * 标准 Cell（最常用）
     */
    val Default = CellTokens(
        minHeight = 52.dp,
        paddingHorizontal = CellSpecs.padding,
        paddingVertical = 12.dp,
        disabledAlpha = 0.5f,
        showDivider = true
    )

    /**
     * 紧凑 Cell（信息密度高的场景）
     * 44dp 高度 = iOS Compact 模式
     */
    val Compact = Default.copy(
        minHeight = 44.dp,
        paddingVertical = 8.dp
    )
}
