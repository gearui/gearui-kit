package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * GearUI Framework 语义排版系统
 *
 * 参考: 内部字体规范
 *
 * ⚠️ 规则：
 * 组件层 ONLY 使用这些语义文本样式
 * 禁止出现 fontSize = xx.sp / 硬编码值
 *
 * 使用方式：
 * val typography = Theme.typography
 * Text(text, style = Typography.BodyMedium)
 */
@Immutable
data class GearTypography(

    /* ---------- Display (超大标题) ---------- */

    /** Display Large - 64sp/72sp - 营销大标题 */
    val displayLarge: TextStyle,

    /** Display Medium - 48sp/56sp */
    val displayMedium: TextStyle,

    /* ---------- Headline (大标题) ---------- */

    /** Headline Large - 36sp/44sp */
    val headlineLarge: TextStyle,

    /** Headline Medium - 28sp/36sp */
    val headlineMedium: TextStyle,

    /** Headline Small - 24sp/32sp */
    val headlineSmall: TextStyle,

    /* ---------- Title (标题) ---------- */

    /** Title Extra Large - 20sp/28sp */
    val titleExtraLarge: TextStyle,

    /** Title Large - 18sp/26sp */
    val titleLarge: TextStyle,

    /** Title Medium - 16sp/24sp */
    val titleMedium: TextStyle,

    /** Title Small - 14sp/22sp */
    val titleSmall: TextStyle,

    /* ---------- Body (正文) ---------- */

    /** Body Extra Large - 18sp/26sp */
    val bodyExtraLarge: TextStyle,

    /** Body Large - 16sp/24sp */
    val bodyLarge: TextStyle,

    /** Body Medium - 14sp/22sp (最常用) */
    val bodyMedium: TextStyle,

    /** Body Small - 12sp/20sp */
    val bodySmall: TextStyle,

    /** Body Extra Small - 10sp/16sp */
    val bodyExtraSmall: TextStyle,

    /* ---------- Mark (强调) ---------- */

    /** Mark Large - 16sp/24sp - 加粗 */
    val markLarge: TextStyle,

    /** Mark Medium - 14sp/22sp - 加粗 */
    val markMedium: TextStyle,

    /** Mark Small - 12sp/20sp - 加粗 */
    val markSmall: TextStyle,

    /** Mark Extra Small - 10sp/16sp - 加粗 */
    val markExtraSmall: TextStyle,

    /* ---------- Link (链接) ---------- */

    /** Link Large - 16sp/24sp */
    val linkLarge: TextStyle,

    /** Link Medium - 14sp/22sp */
    val linkMedium: TextStyle,

    /** Link Small - 12sp/20sp */
    val linkSmall: TextStyle,

    /* ---------- Caption / Label ---------- */

    /** Caption - 12sp/18sp - 辅助说明 */
    val caption: TextStyle,

    /** Label - 10sp/16sp - 标签/徽章 */
    val label: TextStyle,
)

/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

object GearTypographies {

    /**
     * Default Typography - 默认排版
     *
     * 字号/行高/字重完全对齐
     */
    val Default = GearTypography(

        // Display (超大标题)
        displayLarge = TextStyle(
            fontSize = 64.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.SemiBold
        ),
        displayMedium = TextStyle(
            fontSize = 48.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Headline (大标题)
        headlineLarge = TextStyle(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.SemiBold
        ),
        headlineMedium = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold
        ),
        headlineSmall = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Title (标题)
        titleExtraLarge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),

        // Body (正文)
        bodyExtraLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyExtraSmall = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal
        ),

        // Mark (强调)
        markLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markSmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold
        ),
        markExtraSmall = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold
        ),

        // Link (链接)
        linkLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        linkMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        ),
        linkSmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),

        // Caption / Label
        caption = TextStyle(
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal
        ),
        label = TextStyle(
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        ),
    )
}
