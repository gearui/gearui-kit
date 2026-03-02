package com.gearui.foundation.typography

import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * TextStyle - 文本样式 Token
 *
 * 包含:
 * - fontSize: 字号
 * - lineHeight: 行高
 * - fontWeight: 字重
 */
data class TextStyle(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
    val fontWeight: FontWeight
)

/**
 * Typography - 语义化排版系统
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/theme/td_fonts.dart
 *
 * 层级体系:
 * - Display: 超大标题 (64sp/48sp) - 营销页面
 * - Headline: 大标题 (36sp/28sp/24sp) - 页面标题
 * - Title: 标题 (20sp/18sp/16sp/14sp) - 区块标题
 * - Body: 正文 (18sp/16sp/14sp/12sp/10sp) - 内容文本
 * - Mark: 强调 (16sp/14sp/12sp/10sp) - 加粗版正文
 * - Link: 链接 (16sp/14sp/12sp) - 可点击文本
 *
 * 使用原则:
 * - ✅ 强制使用语义化名称 (TitleLarge/BodyMedium)
 * - ❌ 禁止硬编码 fontSize
 */
object Typography {

    /* ---------- Display (超大标题) ---------- */

    /** Display Large - 64sp/72sp - 营销大标题 */
    val DisplayLarge = TextStyle(64.sp, 72.sp, FontWeight.SemiBold)

    /** Display Medium - 48sp/56sp */
    val DisplayMedium = TextStyle(48.sp, 56.sp, FontWeight.SemiBold)

    /* ---------- Headline (大标题) ---------- */

    /** Headline Large - 36sp/44sp */
    val HeadlineLarge = TextStyle(36.sp, 44.sp, FontWeight.SemiBold)

    /** Headline Medium - 28sp/36sp */
    val HeadlineMedium = TextStyle(28.sp, 36.sp, FontWeight.SemiBold)

    /** Headline Small - 24sp/32sp */
    val HeadlineSmall = TextStyle(24.sp, 32.sp, FontWeight.SemiBold)

    /* ---------- Title (标题) ---------- */

    /** Title Extra Large - 20sp/28sp */
    val TitleExtraLarge = TextStyle(20.sp, 28.sp, FontWeight.SemiBold)

    /** Title Large - 18sp/26sp */
    val TitleLarge = TextStyle(18.sp, 26.sp, FontWeight.SemiBold)

    /** Title Medium - 16sp/24sp */
    val TitleMedium = TextStyle(16.sp, 24.sp, FontWeight.SemiBold)

    /** Title Small - 14sp/22sp */
    val TitleSmall = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /* ---------- Body (正文) ---------- */

    /** Body Extra Large - 18sp/26sp */
    val BodyExtraLarge = TextStyle(18.sp, 26.sp, FontWeight.Normal)

    /** Body Large - 16sp/24sp */
    val BodyLarge = TextStyle(16.sp, 24.sp, FontWeight.Normal)

    /** Body Medium - 14sp/22sp (最常用) */
    val BodyMedium = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /** Body Small - 12sp/20sp */
    val BodySmall = TextStyle(12.sp, 20.sp, FontWeight.Normal)

    /** Body Extra Small - 10sp/16sp */
    val BodyExtraSmall = TextStyle(10.sp, 16.sp, FontWeight.Normal)

    /* ---------- Mark (强调) ---------- */

    /** Mark Large - 16sp/24sp - 加粗 */
    val MarkLarge = TextStyle(16.sp, 24.sp, FontWeight.SemiBold)

    /** Mark Medium - 14sp/22sp - 加粗 */
    val MarkMedium = TextStyle(14.sp, 22.sp, FontWeight.SemiBold)

    /** Mark Small - 12sp/20sp - 加粗 */
    val MarkSmall = TextStyle(12.sp, 20.sp, FontWeight.SemiBold)

    /** Mark Extra Small - 10sp/16sp - 加粗 */
    val MarkExtraSmall = TextStyle(10.sp, 16.sp, FontWeight.SemiBold)

    /* ---------- Link (链接) ---------- */

    /** Link Large - 16sp/24sp */
    val LinkLarge = TextStyle(16.sp, 24.sp, FontWeight.Normal)

    /** Link Medium - 14sp/22sp */
    val LinkMedium = TextStyle(14.sp, 22.sp, FontWeight.Normal)

    /** Link Small - 12sp/20sp */
    val LinkSmall = TextStyle(12.sp, 20.sp, FontWeight.Normal)

    /* ---------- Caption (辅助文字) ---------- */

    /** Caption - 12sp/18sp - 辅助说明 */
    val Caption = TextStyle(12.sp, 18.sp, FontWeight.Normal)

    /* ---------- Label (标签文字) ---------- */

    /** Label - 10sp/16sp - 标签/徽章 */
    val Label = TextStyle(10.sp, 16.sp, FontWeight.Medium)
}
