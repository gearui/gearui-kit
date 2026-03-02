package com.gearui.foundation.layout

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Spacing - 全局间距规范
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/theme/td_spacers.dart
 *
 * 基于 8px 网格系统:
 * - spacer4:   4px  (0.5倍)
 * - spacer8:   8px  (1倍, 基础单位)
 * - spacer12:  12px (1.5倍)
 * - spacer16:  16px (2倍)
 * - spacer24:  24px (3倍)
 * - spacer32:  32px (4倍)
 * - spacer40:  40px (5倍)
 * - spacer48:  48px (6倍)
 *
 * 使用原则:
 * - ✅ 强制使用 Spacing.md / Spacing.lg
 * - ❌ 禁止 Spacer(12.dp) / padding(8.dp)
 */
object Spacing {
    /** 4dp - 最小间距 (xs) */
    val xs: Dp = 4.dp

    /** 8dp - 基础间距 (sm) */
    val sm: Dp = 8.dp

    /** 12dp - 紧凑间距 (md) - 最常用 */
    val md: Dp = 12.dp

    /** 16dp - 标准间距 (lg) */
    val lg: Dp = 16.dp

    /** 24dp - 中等间距 (xl) */
    val xl: Dp = 24.dp

    /** 32dp - 较大间距 (xxl) */
    val xxl: Dp = 32.dp

    /** 40dp - 大间距 */
    val xxxl: Dp = 40.dp

    /** 48dp - 超大间距 */
    val huge: Dp = 48.dp

    /** 64dp - 特大间距 */
    val massive: Dp = 64.dp
}
