package com.gearui.foundation.layout

import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.typography.TextStyle

/**
 * SectionHeader Tokens
 *
 * Section header visual tokens (the pattern shared by iOS, Material and Ant Design)
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
     * Standard section header (the common case)
     * e.g. "Basic components", "Form components"
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
     * Large section header (page title)
     * e.g. "Settings", "Me"
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
     * Compact section header (a group inside a List)
     * e.g. "Recently used", "All apps"
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
