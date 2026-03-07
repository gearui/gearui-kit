package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * GearUI Framework 语义颜色系统
 *
 * 参考: 内部主题规范
 *
 * ⚠️ 规则：
 * 组件层 ONLY 使用这些语义颜色
 * 禁止出现 Color(xxx) / 硬编码值
 */
@Immutable
data class GearColors(

    /* ---------------- Surface 层级 ---------------- */

    val background: Color,           // 页面背景 (bgColorPage)
    val surface: Color,              // 容器背景 (bgColorContainer)
    val surfaceVariant: Color,       // 次级容器 (bgColorSecondaryContainer)
    val surfaceComponent: Color,     // 组件背景 (bgColorComponent)
    val overlay: Color,              // Dialog / Modal 背景
    val mask: Color,                 // 遮罩层

    /* ---------------- Brand 品牌色 ---------------- */

    val primary: Color,              // 品牌主色 (brandNormalColor)
    val primaryHover: Color,         // 悬停态 (brandHoverColor)
    val primaryActive: Color,        // 点击态 (brandClickColor)
    val primaryLight: Color,         // 浅色背景 (brandLightColor)
    val primaryDisabled: Color,      // 禁用态 (brandDisabledColor)
    val onPrimary: Color,            // 品牌色上的文字

    /* ---------------- Content 内容色 ---------------- */

    val textPrimary: Color,          // 主要文本 (fontGyColor1 90%)
    val textSecondary: Color,        // 次要文本 (fontGyColor2 60%)
    val textPlaceholder: Color,      // 占位文本 (fontGyColor3 40%)
    val textDisabled: Color,         // 禁用文本 (fontGyColor4 26%)
    val textAnti: Color,             // 反色文本 (白色)
    val textBrand: Color,            // 品牌文本 (链接色)

    val iconPrimary: Color,          // 主要图标
    val iconSecondary: Color,        // 次要图标

    /* ---------------- Border / Divider 边框分割线 ---------------- */

    val border: Color,               // 边框色 (componentBorderColor)
    val stroke: Color,               // 笔划色 (componentStrokeColor)
    val divider: Color,              // 分割线

    /* ---------------- State 状态色 ---------------- */

    val disabled: Color,             // 禁用前景
    val disabledContainer: Color,    // 禁用背景

    /* ---------------- Feedback 反馈色 ---------------- */

    val success: Color,              // 成功色 (successNormalColor)
    val successLight: Color,         // 成功浅色背景
    val warning: Color,              // 警告色 (warningNormalColor)
    val warningLight: Color,         // 警告浅色背景
    val danger: Color,               // 错误色 (errorNormalColor)
    val dangerLight: Color,          // 错误浅色背景
    val info: Color,                 // 信息色 (= primary)
    val infoLight: Color,            // 信息浅色背景

    /* ---------------- Inverse 反转色 (用于 Toast 等) ---------------- */

    val inverseSurface: Color,       // 反转背景 (用于 Toast)
    val inverseOnSurface: Color,     // 反转背景上的文字
)

/* --------------------------------------------------------- */
/* Theme Spec = 一等公民 */
/* --------------------------------------------------------- */

@Immutable
data class ThemeSpec(
    val colors: GearColors
)

/* --------------------------------------------------------- */
/* 内置 Light / Dark 主题 */
/* --------------------------------------------------------- */

object Themes {

    /**
     * Light Theme - 亮色主题
     *
     */
    val Light = ThemeSpec(
        colors = GearColors(

            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF4F4F5),
            surfaceComponent = Color(0xFFFAFAFA),
            overlay = Color(0xFFFFFFFF),
            mask = Color(0x6609090B),

            primary = Color(0xFF18181B),
            primaryHover = Color(0xFF27272A),
            primaryActive = Color(0xFF09090B),
            primaryLight = Color(0xFFF4F4F5),
            primaryDisabled = Color(0xFFD4D4D8),
            onPrimary = Color.White,

            textPrimary = Color(0xFF09090B),
            textSecondary = Color(0xFF52525B),
            textPlaceholder = Color(0xFFA1A1AA),
            textDisabled = Color(0xFFB4B4BC),
            textAnti = Color(0xFFFFFFFF),
            textBrand = Color(0xFF18181B),

            iconPrimary = Color(0xFF09090B),
            iconSecondary = Color(0xFF52525B),

            border = Color(0xFFE4E4E7),
            stroke = Color(0xFFE4E4E7),
            divider = Color(0xFFE4E4E7),

            // State 状态色
            disabled = Color(0xFFA1A1AA),
            disabledContainer = Color(0xFFF4F4F5),

            success = Color(0xFF16A34A),
            successLight = Color(0xFFDCFCE7),
            warning = Color(0xFFF59E0B),
            warningLight = Color(0xFFFEF3C7),
            danger = Color(0xFFDC2626),
            dangerLight = Color(0xFFFEE2E2),
            info = Color(0xFF2563EB),
            infoLight = Color(0xFFDBEAFE),

            // Inverse 反转色 - 用于 Toast 等浮层
            inverseSurface = Color(0xFF18181B),
            inverseOnSurface = Color(0xFFFAFAFA),
        )
    )

    /**
     * Dark Theme - 暗色主题
     *
     */
    val Dark = ThemeSpec(
        colors = GearColors(

            background = Color(0xFF09090B),
            surface = Color(0xFF09090B),
            surfaceVariant = Color(0xFF18181B),
            surfaceComponent = Color(0xFF18181B),
            overlay = Color(0xFF18181B),
            mask = Color(0x99000000),

            // Brand 品牌色 - 暗色调整
            primary = Color(0xFFFAFAFA),
            primaryHover = Color(0xFFF4F4F5),
            primaryActive = Color(0xFFE4E4E7),
            primaryLight = Color(0xFF27272A),
            primaryDisabled = Color(0xFF3F3F46),
            onPrimary = Color(0xFF09090B),

            textPrimary = Color(0xFFFAFAFA),
            textSecondary = Color(0xFFA1A1AA),
            textPlaceholder = Color(0xFF71717A),
            textDisabled = Color(0xFF52525B),
            textAnti = Color(0xFFFFFFFF),
            textBrand = Color(0xFFFAFAFA),

            iconPrimary = Color(0xFFFAFAFA),
            iconSecondary = Color(0xFFA1A1AA),

            // Border / Divider - 暗色 Gray 色阶
            border = Color(0xFF27272A),
            stroke = Color(0xFF27272A),
            divider = Color(0xFF27272A),

            // State 状态色
            disabled = Color(0xFF52525B),
            disabledContainer = Color(0xFF18181B),

            // Feedback 反馈色 - 暗色调整
            success = Color(0xFF22C55E),
            successLight = Color(0xFF14532D),
            warning = Color(0xFFF59E0B),
            warningLight = Color(0xFF78350F),
            danger = Color(0xFFF87171),
            dangerLight = Color(0xFF7F1D1D),
            info = Color(0xFF60A5FA),
            infoLight = Color(0xFF1E3A8A),

            // Inverse 反转色 - 用于 Toast 等浮层 (暗色主题下用亮色)
            inverseSurface = Color(0xFFF4F4F5),
            inverseOnSurface = Color(0xFF09090B),
        )
    )
}
