package com.gearui.foundation.elevation

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Elevation - 阴影高度规范（Dp 值）
 *
 * GearUI 的视觉语言是 border-first：**平铺在页面里的东西不投影**。Card、Cell、
 * List、输入框都靠 `colors.border` 描边区分层次，不靠阴影。阴影只用来表达
 * "这个东西浮在页面之上"，所以只有浮层类组件可以用。
 *
 * 四档，按"离页面多远"排序：
 *
 *   none     = 0dp  — 不投影（默认）；也用于控件的 disabled 态
 *   raised   = 4dp  — 贴附在内容之上的控件：滑块拇指、回到顶部按钮、
 *                     以及跟随触发点弹出的轻浮层（Popover、ContextMenu、
 *                     Snackbar、NavigationMenu）
 *   floating = 6dp  — 脱离触发点的浮层面板：Select / Cascader / TreeSelect
 *                     的下拉面板、Notification
 *   modal    = 8dp  — 抢占焦点的模态层：Dialog、Tour 引导卡
 *
 * 层级必须自洽：modal 永远高于 floating，floating 永远高于 raised。在引入这个
 * 标度之前并非如此——Dialog 是 6dp 而 TreeSelect 下拉是 8dp，模态反而浮在下拉
 * 之下。
 *
 * ⚠️ 不要用 `Spacing.*` 当阴影高度。两者恰好都是 Dp 且数值撞车（Spacing.xs 也是
 * 4dp），但它们是不同的语义轴，间距标度调整时会把阴影一起带偏。
 */
object Elevation {
    /** 0dp - 不投影（默认 / disabled） */
    val none: Dp = 0.dp

    /** 4dp - 贴附控件与轻浮层 */
    val raised: Dp = 4.dp

    /** 6dp - 浮层面板 */
    val floating: Dp = 6.dp

    /** 8dp - 模态层 */
    val modal: Dp = 8.dp
}
