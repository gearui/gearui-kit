package com.gearui.foundation.layout

import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.typography.TextStyle

/**
 * SectionHeader Tokens
 *
 * 章节头视觉规范 Token（iOS/Material/Ant Design 通用模式）
 */
data class SectionTokens(
    val height: Dp,
    val paddingHorizontal: Dp,
    val paddingVertical: Dp,
    val backgroundColor: Color,
    val textStyle: TextStyle,
    val textColor: Color,
    val trailingSpacing: Dp  // text 与 trailing 的间距
)

object Sections {
    /**
     * 标准章节头（最常用）
     * 例: "基础组件"、"表单组件"
     */
    val Default = SectionTokens(
        height = 32.dp,
        paddingHorizontal = 16.dp,
        paddingVertical = 8.dp,
        backgroundColor = Color(0xFFF5F5F5),
        textStyle = Typography.Caption,
        textColor = Color(0xFF999999),
        trailingSpacing = 8.dp
    )

    /**
     * 大标题章节头（页面主标题）
     * 例: "设置"、"我的"
     */
    val Large = SectionTokens(
        height = 44.dp,
        paddingHorizontal = 16.dp,
        paddingVertical = 12.dp,
        backgroundColor = Color.White,
        textStyle = Typography.TitleMedium,
        textColor = Color(0xFF000000),
        trailingSpacing = 12.dp
    )

    /**
     * 紧凑章节头（List 内分组）
     * 例: "最近使用"、"全部应用"
     */
    val Compact = SectionTokens(
        height = 28.dp,
        paddingHorizontal = 16.dp,
        paddingVertical = 6.dp,
        backgroundColor = Color.Transparent,
        textStyle = Typography.Caption,
        textColor = Color(0xFF999999),
        trailingSpacing = 8.dp
    )
}
