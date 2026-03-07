package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Radius - 全局圆角规范
 *
 * 参考: 内部圆角规范
 *
 * 圆角层级:
 * - radiusSmall:       3dp  (小圆角，细节元素)
 * - radiusDefault:     6dp  (默认圆角，常用)
 * - radiusLarge:       9dp  (大圆角)
 * - radiusExtraLarge:  12dp (特大圆角)
 * - radiusRound:       9999dp (胶囊型/完全圆角)
 *
 * 使用原则:
 * - ✅ 强制使用 Radius.default / Radius.small
 * - ❌ 禁止 RoundedCornerShape(8.dp)
 */
object Radius {
    /** 3dp - 小圆角 (按钮、小组件边角) */
    val small: Dp = 3.dp

    /** 6dp - 默认圆角 (输入框、卡片、默认组件) */
    val default: Dp = 6.dp

    /** 9dp - 大圆角 (较大卡片、模态框) */
    val large: Dp = 9.dp

    /** 12dp - 特大圆角 (特殊强调组件) */
    val extraLarge: Dp = 12.dp

    /** 9999dp - 胶囊型 (胶囊按钮、完全圆角) */
    val round: Dp = 9999.dp

    /** 9999dp - 圆形 (圆形头像、徽章) */
    val circle: Dp = 9999.dp
}
