package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Radius - 全局圆角规范（Dp 值）
 *
 * 与冻结后的 `theme.Shapes` scale 完全一致（同名同值），消除双标准：
 *
 *   none = 0
 *   sm   = 4dp
 *   md   = 6dp
 *   lg   = 8dp
 *   xl   = 12dp
 *   full = 9999dp（胶囊/完全圆角）
 *
 * `Shapes` 提供 `Shape` 实例（用于 `Modifier.clip`），`Radius` 提供对应 `Dp`
 * 值（用于需要 Dp 的场景，如组件 token）。两者数值保持同步。
 */
object Radius {
    /** 0dp - 直角 */
    val none: Dp = 0.dp

    /** 4dp - 小圆角（tag、chip、密集控件） */
    val sm: Dp = 4.dp

    /** 6dp - 默认圆角（输入框、默认表面） */
    val md: Dp = 6.dp

    /** 8dp - 大圆角（按钮、卡片，GearUI 移动端默认） */
    val lg: Dp = 8.dp

    /** 12dp - 特大圆角（sheet、大卡片、强调表面） */
    val xl: Dp = 12.dp

    /** 9999dp - 胶囊/完全圆角 */
    val full: Dp = 9999.dp

    /** 9999dp - 圆形（圆形头像/徽章），等同 [full] */
    val circle: Dp = 9999.dp
}
