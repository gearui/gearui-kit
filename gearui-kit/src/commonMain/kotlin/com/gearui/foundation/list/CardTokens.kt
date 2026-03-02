package com.gearui.foundation.list

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.CardSpecs

/**
 * CardTokens - Card 组件尺寸规范
 *
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors.surface
 *
 * 使用场景：
 * - 信息聚合展示
 * - 表单分组
 * - 内容模块
 */
data class CardTokens(
    val cornerRadius: Dp,
    val padding: Dp,
    val borderWidth: Dp,
    val elevation: Float
)

object CardDefaults {
    /**
     */
    val Default = CardTokens(
        cornerRadius = CardSpecs.radius,
        padding = CardSpecs.padding,
        borderWidth = CardSpecs.borderWidth,
        elevation = 2f
    )

    /**
     * 扁平 Card（无阴影）
     * 适用于已有背景色分层的场景
     */
    val Flat = Default.copy(
        elevation = 0f
    )

    /**
     * 紧凑 Card
     * 减少内边距，提高信息密度
     */
    val Compact = Default.copy(
        padding = 12.dp
    )
}
