package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.text.BasicText
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.ColorProducer
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.foundation.typography.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.text.TextStyle as KuiklyTextStyle

/**
 * Text - 100% Theme 驱动的文本原语
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 改造要点：
 * - 移除 TextColors 依赖
 * - 直接使用 Theme.colors
 * - 支持 primary/secondary/tertiary 语义
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,

    /** 文本样式 Token (语义化) */
    style: TextStyle = Typography.BodyMedium,

    /** 文本颜色 - 直接指定 */
    color: Color? = null,

    /** 是否次要文本（使用 textSecondary） */
    secondary: Boolean = false,

    /** 是否三级文本（使用 textTertiary） */
    tertiary: Boolean = false,

    /** 最大行数 */
    maxLines: Int = Int.MAX_VALUE,

    /** 溢出处理 */
    overflow: TextOverflow = TextOverflow.Clip,

    /** 是否自动换行 */
    softWrap: Boolean = true,

    /** 字号 - 向后兼容参数，优先级高于 style.fontSize */
    fontSize: TextUnit? = null,

    /** 字重 - 向后兼容参数，优先级高于 style.fontWeight */
    fontWeight: FontWeight? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val themeColors = Theme.colors

    // 颜色优先级：color > tertiary > secondary > primary
    val finalColor = color ?: when {
        tertiary -> themeColors.textPlaceholder
        secondary -> themeColors.textSecondary
        else -> themeColors.textPrimary
    }

    // 字号和字重支持覆盖
    val finalFontSize = fontSize ?: style.fontSize
    val finalFontWeight = fontWeight ?: style.fontWeight

    // 将 GearUI TextStyle 转换为 Kuikly TextStyle
    val kuiklyStyle = KuiklyTextStyle(
        fontSize = finalFontSize,
        lineHeight = style.lineHeight,
        fontWeight = finalFontWeight,
        color = finalColor
    )

    BasicText(
        text = text,
        modifier = modifier,
        style = kuiklyStyle,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        color = { finalColor }
    )
}
