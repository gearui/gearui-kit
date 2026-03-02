package com.gearui.sample.theme

import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.theme.GearColors
import com.gearui.theme.ThemeSpec

/**
 * 自定义主题集合
 *
 * 包含：
 * - DarkPurple: 暗紫主题 - 纯黑背景，紫色关键色
 */
object CustomThemes {

    /**
     * 暗紫主题 (Dark Purple)
     *
     * 特点：
     * - 低饱和深靛紫基底，减少“荧光紫”感
     * - 头尾栏比内容层更深，层级更清晰
     * - 中性色全部带冷紫倾向，风格统一
     */
    val DarkPurple = ThemeSpec(
        colors = GearColors(

            // Surface 层级 - 内容中性暗紫（品牌紫交给导航/强调）
            background = Color(0xFF181427),           // 页面背景
            surface = Color(0xFF26203A),              // 基础容器背景
            surfaceVariant = Color(0xFF221B34),       // 次级容器
            surfaceComponent = Color(0xFF2C2442),     // 组件容器背景
            overlay = Color(0xFF332A4D),              // 弹层背景
            mask = Color(0x99201533),                 // 遮罩层（带紫调）

            // Brand 品牌色 - 亮紫强调
            primary = Color(0xFFBFA6FF),              // 主色亮紫
            primaryHover = Color(0xFFCEB9FF),         // 悬停态
            primaryActive = Color(0xFF6E48CF),        // 品牌紫（导航/底栏）
            primaryLight = Color(0xFF3D315F),         // 弱强调背景
            primaryDisabled = Color(0xFF7663AA),      // 禁用态
            onPrimary = Color(0xFFFFFFFF),            // 主色上白字

            // Content 内容色 - 清晰可读的冷白体系
            textPrimary = Color(0xF2F6F1FF),          // 主文本
            textSecondary = Color(0xB3CDC2E4),        // 次文本
            textPlaceholder = Color(0x669F96BF),      // 占位文本
            textDisabled = Color(0x4D8A82A7),         // 禁用文本
            textAnti = Color(0xFFFFFFFF),             // 反色文本
            textBrand = Color(0xFFB89CFF),            // 品牌文本

            iconPrimary = Color(0xF2F6F1FF),
            iconSecondary = Color(0xB3CDC2E4),

            // Border / Divider - 低饱和紫调
            border = Color(0xFF4C406F),               // 边框
            stroke = Color(0xFF3D325A),               // 笔划
            divider = Color(0xFF342C4D),              // 分割线

            // State 状态色
            disabled = Color(0x598A82A7),             // 禁用前景
            disabledContainer = Color(0xFF332B4D),    // 禁用背景

            // Feedback 反馈色 - 与主色协调
            success = Color(0xFF5FE0B8),              // 成功绿
            successLight = Color(0xFF223E35),
            warning = Color(0xFFF3C17A),              // 警告琥珀
            warningLight = Color(0xFF3A2C1D),
            danger = Color(0xFFFF2DA8),               // 徽标/错误高饱和洋红粉
            dangerLight = Color(0xFF4A1736),
            info = Color(0xFFB89CFF),                 // 信息色
            infoLight = Color(0xFF3A2E5D),

            // Inverse 反转色
            inverseSurface = Color(0xFFF5EEFF),       // 浅紫背景
            inverseOnSurface = Color(0xFF1D1333),     // 深紫文字
        )
    )
}
