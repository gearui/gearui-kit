package com.gearui.foundation.border

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * BorderWidth - 描边宽度规范（Dp 值）
 *
 * GearUI 是 border-first 的设计语言：层次主要靠描边而不是阴影表达（见
 * [com.gearui.foundation.elevation.Elevation]）。既然描边承担了这个角色，它就
 * 该有自己的标度——在这之前 `Shapes` / `Elevation` / `Spacing` 三条轴都有命名档位，
 * 唯独描边宽度是散落的字面量：87 处 `1.dp` / `2.dp` / `0.5.dp`。
 *
 * 三档，按视觉重量排序：
 *
 *   none     = 0dp    — 不描边
 *   hairline = 0.5dp  — 分隔线、表格网格线、Card 的发丝级描边
 *   thin     = 1dp    — 默认描边：输入框、按钮、面板、卡片
 *   thick    = 2dp    — 强调环：Timeline 节点、Steps 步骤点这类需要从背景里挖出来的圆点
 *
 * ⚠️ 焦点态**不要**加档位。输入框的描边宽度必须跨 focus / error 保持恒定，
 * 否则内容盒尺寸变化会导致布局跳动；详见
 * [com.gearui.foundation.field.FieldTokens]。
 */
object BorderWidth {
    /** 0dp - 不描边 */
    val none: Dp = 0.dp

    /** 0.5dp - 发丝线：分隔线、网格线 */
    val hairline: Dp = 0.5.dp

    /** 1dp - 默认描边 */
    val thin: Dp = 1.dp

    /** 2dp - 强调环 */
    val thick: Dp = 2.dp
}
