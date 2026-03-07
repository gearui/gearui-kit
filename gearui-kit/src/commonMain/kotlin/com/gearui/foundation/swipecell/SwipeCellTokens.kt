package com.gearui.foundation.swipecell

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.layout.Spacing

/**
 * SwipeCellTokens - SwipeCell 组件设计规范
 *
 * 参考: 内部组件规范swipe_cell/td_swipe_cell_style.dart
 *
 * 设计原则：
 * - 所有尺寸、间距使用语义化 Token
 * - 颜色通过 Theme.colors 获取，不在此定义
 * - 支持不同尺寸适配
 */
data class SwipeCellTokens(
    /** 操作按钮宽度 - 每个 flex 单位的基础宽度 */
    val actionWidth: Dp,

    /** 操作按钮最小宽度 */
    val actionMinWidth: Dp,

    /** 操作按钮水平内边距 */
    val actionPaddingHorizontal: Dp,

    /** 操作按钮垂直内边距 */
    val actionPaddingVertical: Dp,

    /** 图标与文字间距 */
    val iconSpacing: Dp,

    /** 打开阈值比例 (0-1)，滑动超过此比例自动打开 */
    val openThreshold: Float,

    /** 快速滑动速度阈值 (像素/秒) */
    val velocityThreshold: Float,

    /** 阻尼系数 - 超出边界时的阻力 */
    val dampingRatio: Float,

    /** 弹性动画阻尼比 */
    val springDampingRatio: Float,

    /** 弹性动画刚度 */
    val springStiffness: Float
)

/**
 * SwipeCell 默认 Token 配置
 */
object SwipeCellDefaults {

    /**
     * 标准配置 - 适用于大多数场景
     *
     * 操作按钮宽度 80dp，足够显示 2 个中文字符 + 内边距
     */
    val Default = SwipeCellTokens(
        actionWidth = 80.dp,
        actionMinWidth = 64.dp,
        actionPaddingHorizontal = Spacing.md,  // 12.dp
        actionPaddingVertical = Spacing.sm,    // 8.dp
        iconSpacing = Spacing.xs,              // 4.dp
        openThreshold = 0.4f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,             // DampingRatioMediumBouncy
        springStiffness = 400f                 // StiffnessMedium
    )

    /**
     * 紧凑配置 - 适用于多操作按钮场景
     *
     * 操作按钮宽度 64dp，适合仅图标或短文字
     */
    val Compact = SwipeCellTokens(
        actionWidth = 64.dp,
        actionMinWidth = 56.dp,
        actionPaddingHorizontal = Spacing.sm,  // 8.dp
        actionPaddingVertical = Spacing.xs,    // 4.dp
        iconSpacing = Spacing.xs,              // 4.dp
        openThreshold = 0.35f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,
        springStiffness = 400f
    )

    /**
     * 宽松配置 - 适用于单按钮或长文字场景
     *
     * 操作按钮宽度 96dp，足够显示 3-4 个中文字符
     */
    val Large = SwipeCellTokens(
        actionWidth = 96.dp,
        actionMinWidth = 80.dp,
        actionPaddingHorizontal = Spacing.lg,  // 16.dp
        actionPaddingVertical = Spacing.md,    // 12.dp
        iconSpacing = Spacing.sm,              // 8.dp
        openThreshold = 0.4f,
        velocityThreshold = 500f,
        dampingRatio = 0.3f,
        springDampingRatio = 0.7f,
        springStiffness = 400f
    )
}
