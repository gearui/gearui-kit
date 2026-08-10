package com.gearui.foundation.typography

import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * IconTokens - 图标尺寸标度
 *
 * 五档全部来自实际用法，不是拍脑袋定的：统计组件层 34 处图标尺寸，
 * 18dp 用了 10 次、16dp 8 次、12dp 5 次、24dp 与 14dp 各 2 次。
 *
 *   xs = 12dp  — 控件内的微型标记（清除按钮、开关内图标、选中勾）
 *   sm = 14dp  — 密集列表里的次级图标
 *   md = 16dp  — 输入类控件的尾部指示（下拉箭头、日历、时钟）
 *   lg = 18dp  — 与正文并排的默认图标
 *   xl = 24dp  — 导航栏、标签栏等独立图标
 *
 * 旧标度是 `small/medium/large = 14/18/24`，缺了实际用得最多的 16dp，
 * 于是 field 家族只能各写字面量——标准和用法脱节时，被绕过的是标准。
 *
 * 插画级图标另开 [IconSizes.Display]：空状态、结果页那种脱离文字流的图形，
 * 尺寸区间（28-40dp）和行内图标不重叠，混进一条标度只会让档位失真。
 */
data class IconTokens(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
)

object IconSizes {
    val Default = IconTokens(
        xs = 12.dp,
        sm = 14.dp,
        md = 16.dp,
        lg = 18.dp,
        xl = 24.dp,
    )

    /**
     * 插画级图标：空状态、结果页、图片查看器的大控件。
     *
     * 它们不与文字并排，属于版面元素而非行内图标，所以不共用 [Default] 的档位。
     */
    object Display {
        val sm: Dp = 28.dp
        val md: Dp = 36.dp
        val lg: Dp = 40.dp
    }
}
