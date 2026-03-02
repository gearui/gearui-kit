package com.gearui.foundation.tab

import com.tencent.kuikly.compose.ui.unit.*
import com.gearui.foundation.TabSpecs

/**
 * Tab 尺寸规范
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/components/tabbar/td_tab_bar.dart
 *
 * ⚠️ 注意：颜色不在这里定义，应使用 Theme.colors
 */
data class TabTokens(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconTextSpacing: Dp,
    val indicatorHeight: Dp,
    val textSizeSp: Int
)

object TabSizeTokens {

    val Small = TabTokens(
        height = 36.dp,
        horizontalPadding = 12.dp,
        iconTextSpacing = 4.dp,
        indicatorHeight = TabSpecs.indicatorHeight,
        textSizeSp = 12
    )

    val Medium = TabTokens(
        height = TabSpecs.height,
        horizontalPadding = 16.dp,
        iconTextSpacing = 6.dp,
        indicatorHeight = TabSpecs.indicatorHeight,
        textSizeSp = 14
    )

    val Large = TabTokens(
        height = 52.dp,
        horizontalPadding = 20.dp,
        iconTextSpacing = 8.dp,
        indicatorHeight = TabSpecs.indicatorHeight,
        textSizeSp = 16
    )
}
