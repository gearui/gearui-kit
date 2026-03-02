package com.gearui.foundation.tokens

import com.gearui.ColorTokens
import com.gearui.Radius
import com.gearui.Spacing
import com.gearui.Typography

/**
 * GearUI 组件 Token 系统
 *
 * 将组件的样式参数抽象为可配置的 Token，实现真正的设计系统。
 * 所有组件共享 Token 体系，确保视觉一致性。
 *
 * 设计原则：
 * - 语义化：Token 名称反映用途而非具体值
 * - 层级化：Global Token → Component Token → Instance Token
 * - 可覆盖：支持主题切换和自定义
 * - 类型安全：使用 data class 而非 Map
 */

// ============ Button Token ============

/**
 * 按钮组件 Token
 *
 * 定义按钮在不同尺寸、类型下的所有样式参数
 */
data class ButtonTokens(
    /** 按钮高度 */
    val height: Float,

    /** 水平内边距 */
    val paddingHorizontal: Float,

    /** 垂直内边距（当需要时） */
    val paddingVertical: Float = 0f,

    /** 字体大小 */
    val fontSize: Float,

    /** 行高 */
    val lineHeight: Float,

    /** 圆角半径 */
    val borderRadius: Float,

    /** 边框宽度 */
    val borderWidth: Float = 1f,

    /** 图标大小 */
    val iconSize: Float,

    /** 图标与文本间距 */
    val iconSpacing: Float = Spacing.spacer8,

    /** 加载指示器大小 */
    val loadingSize: Float,

    /** 最小宽度（可选） */
    val minWidth: Float? = null
) {
    companion object {
        /**
         * 大尺寸按钮 Token
         */
        val Large = ButtonTokens(
            height = 48f,
            paddingHorizontal = Spacing.spacer24,
            fontSize = Typography.BodyLarge.fontSize,
            lineHeight = Typography.BodyLarge.lineHeight,
            borderRadius = Radius.Default,
            iconSize = 24f,
            loadingSize = 20f,
            minWidth = 96f
        )

        /**
         * 中等尺寸按钮 Token（默认）
         */
        val Medium = ButtonTokens(
            height = 40f,
            paddingHorizontal = Spacing.spacer16,
            fontSize = Typography.BodyMedium.fontSize,
            lineHeight = Typography.BodyMedium.lineHeight,
            borderRadius = Radius.Default,
            iconSize = 20f,
            loadingSize = 16f,
            minWidth = 80f
        )

        /**
         * 小尺寸按钮 Token
         */
        val Small = ButtonTokens(
            height = 32f,
            paddingHorizontal = Spacing.spacer12,
            fontSize = Typography.BodySmall.fontSize,
            lineHeight = Typography.BodySmall.lineHeight,
            borderRadius = Radius.Small,
            iconSize = 18f,
            loadingSize = 14f,
            minWidth = 64f
        )

        /**
         * 超小尺寸按钮 Token
         */
        val ExtraSmall = ButtonTokens(
            height = 28f,
            paddingHorizontal = Spacing.spacer8,
            fontSize = Typography.BodySmall.fontSize,
            lineHeight = Typography.BodySmall.lineHeight,
            borderRadius = Radius.Small,
            iconSize = 14f,
            loadingSize = 12f,
            minWidth = 56f
        )
    }
}

/**
 * 按钮颜色 Token
 *
 * 定义按钮在不同主题下的颜色方案
 */
data class ButtonColorTokens(
    /** 背景色 */
    val backgroundColor: Int,

    /** 文本颜色 */
    val textColor: Int,

    /** 边框颜色 */
    val borderColor: Int? = null,

    /** 悬停背景色 */
    val hoverBackgroundColor: Int? = null,

    /** 点击背景色 */
    val activeBackgroundColor: Int? = null,

    /** 禁用背景色 */
    val disabledBackgroundColor: Int? = null,

    /** 禁用文本颜色 */
    val disabledTextColor: Int? = null
) {
    companion object {
        /**
         * 主要按钮颜色（填充）
         */
        val PrimaryFill = ButtonColorTokens(
            backgroundColor = ColorTokens.BrandNormal,
            textColor = ColorTokens.TextWhite1,
            hoverBackgroundColor = ColorTokens.BrandHover,
            activeBackgroundColor = ColorTokens.BrandClick,
            disabledBackgroundColor = ColorTokens.Gray4,
            disabledTextColor = ColorTokens.TextDisabled
        )

        /**
         * 主要按钮颜色（描边）
         */
        val PrimaryOutline = ButtonColorTokens(
            backgroundColor = 0x00000000,
            textColor = ColorTokens.BrandNormal,
            borderColor = ColorTokens.BrandNormal,
            hoverBackgroundColor = ColorTokens.BrandLight,
            activeBackgroundColor = ColorTokens.BrandFocus,
            disabledBackgroundColor = 0x00000000,
            disabledTextColor = ColorTokens.TextDisabled
        )

        /**
         * 危险按钮颜色（填充）
         */
        val DangerFill = ButtonColorTokens(
            backgroundColor = ColorTokens.ErrorNormal,
            textColor = ColorTokens.TextWhite1,
            hoverBackgroundColor = ColorTokens.ErrorHover,
            activeBackgroundColor = ColorTokens.ErrorClick,
            disabledBackgroundColor = ColorTokens.Gray4,
            disabledTextColor = ColorTokens.TextDisabled
        )

        /**
         * 警告按钮颜色（填充）
         */
        val WarningFill = ButtonColorTokens(
            backgroundColor = ColorTokens.WarningNormal,
            textColor = ColorTokens.TextWhite1,
            hoverBackgroundColor = ColorTokens.WarningHover,
            activeBackgroundColor = ColorTokens.WarningClick,
            disabledBackgroundColor = ColorTokens.Gray4,
            disabledTextColor = ColorTokens.TextDisabled
        )

        /**
         * 成功按钮颜色（填充）
         */
        val SuccessFill = ButtonColorTokens(
            backgroundColor = ColorTokens.SuccessNormal,
            textColor = ColorTokens.TextWhite1,
            hoverBackgroundColor = ColorTokens.SuccessHover,
            activeBackgroundColor = ColorTokens.SuccessClick,
            disabledBackgroundColor = ColorTokens.Gray4,
            disabledTextColor = ColorTokens.TextDisabled
        )
    }
}

// ============ Input Token ============

/**
 * 输入框组件 Token
 */
data class InputTokens(
    /** 输入框高度 */
    val height: Float,

    /** 水平内边距 */
    val paddingHorizontal: Float,

    /** 字体大小 */
    val fontSize: Float,

    /** 行高 */
    val lineHeight: Float,

    /** 圆角半径 */
    val borderRadius: Float,

    /** 边框宽度 */
    val borderWidth: Float = 1f,

    /** 聚焦边框宽度 */
    val focusBorderWidth: Float = 2f
) {
    companion object {
        val Large = InputTokens(
            height = 48f,
            paddingHorizontal = Spacing.spacer16,
            fontSize = Typography.BodyLarge.fontSize,
            lineHeight = Typography.BodyLarge.lineHeight,
            borderRadius = Radius.Default
        )

        val Medium = InputTokens(
            height = 40f,
            paddingHorizontal = Spacing.spacer12,
            fontSize = Typography.BodyMedium.fontSize,
            lineHeight = Typography.BodyMedium.lineHeight,
            borderRadius = Radius.Default
        )

        val Small = InputTokens(
            height = 32f,
            paddingHorizontal = Spacing.spacer8,
            fontSize = Typography.BodySmall.fontSize,
            lineHeight = Typography.BodySmall.lineHeight,
            borderRadius = Radius.Small
        )
    }
}

// ============ Card Token ============

/**
 * 卡片组件 Token
 */
data class CardTokens(
    /** 内边距 */
    val padding: Float,

    /** 圆角半径 */
    val borderRadius: Float,

    /** 阴影级别（0-5） */
    val shadowLevel: Int = 1,

    /** 边框宽度 */
    val borderWidth: Float = 0f
) {
    companion object {
        val Default = CardTokens(
            padding = Spacing.spacer12,
            borderRadius = Radius.Large,
            shadowLevel = 1
        )

        val Compact = CardTokens(
            padding = Spacing.spacer12,
            borderRadius = Radius.Default,
            shadowLevel = 0,
            borderWidth = 1f
        )
    }
}

// ============ Tag Token ============

/**
 * 标签组件 Token
 */
data class TagTokens(
    /** 高度 */
    val height: Float,

    /** 水平内边距 */
    val paddingHorizontal: Float,

    /** 字体大小 */
    val fontSize: Float,

    /** 圆角半径 */
    val borderRadius: Float
) {
    companion object {
        val Large = TagTokens(
            height = 32f,
            paddingHorizontal = Spacing.spacer12,
            fontSize = Typography.BodyMedium.fontSize,
            borderRadius = Radius.Default
        )

        val Medium = TagTokens(
            height = 24f,
            paddingHorizontal = Spacing.spacer8,
            fontSize = Typography.BodySmall.fontSize,
            borderRadius = Radius.Small
        )

        val Small = TagTokens(
            height = 20f,
            paddingHorizontal = Spacing.spacer8,
            fontSize = Typography.MarkSmall.fontSize,
            borderRadius = Radius.Small
        )
    }
}

/**
 * @deprecated 使用 Theme.colors 替代
 *
 * Tag 颜色应通过 Theme.colors 获取：
 * - PRIMARY: colors.primary / colors.primaryLight
 * - SUCCESS: colors.success / colors.successLight
 * - WARNING: colors.warning / colors.warningLight
 * - DANGER: colors.danger / colors.dangerLight
 */
@Deprecated("Use Theme.colors instead - Tag component now uses semantic colors directly")
data class TagColorTokens(
    val backgroundColor: Long,
    val textColor: Long,
    val disabledBackgroundColor: Long,
    val disabledTextColor: Long,
    val activeBackgroundColor: Long = backgroundColor
) {
    companion object {
        // Dark variant
        val PrimaryDark = TagColorTokens(0xFF0052D9, 0xFFFFFFFF, 0xFFE0EDFF, 0xFFB5DAFF)
        val SuccessDark = TagColorTokens(0xFF00A870, 0xFFFFFFFF, 0xFFE3F9E9, 0xFFC6F3D7)
        val WarningDark = TagColorTokens(0xFFED7B2F, 0xFFFFFFFF, 0xFFFFF1E9, 0xFFFFDDB8)
        val DangerDark = TagColorTokens(0xFFE34D59, 0xFFFFFFFF, 0xFFFFECEE, 0xFFFFB9BE)
        val DefaultDark = TagColorTokens(0xFF000000, 0xFFFFFFFF, 0xFFF3F3F3, 0xFFE7E7E7)

        // Light variant
        val PrimaryLight = TagColorTokens(0xFFE0EDFF, 0xFF0052D9, 0xFFD6E4FF, 0xFFB5DAFF)
        val SuccessLight = TagColorTokens(0xFFE3F9E9, 0xFF00A870, 0xFFD1F5E0, 0xFFC6F3D7)
        val WarningLight = TagColorTokens(0xFFFFF1E9, 0xFFED7B2F, 0xFFFFE7D9, 0xFFFFDDB8)
        val DangerLight = TagColorTokens(0xFFFFECEE, 0xFFE34D59, 0xFFFFDBDD, 0xFFFFB9BE)
        val DefaultLight = TagColorTokens(0xFFF3F3F3, 0xFF000000, 0xFFE7E7E7, 0xFFDCDCDC)

        // Outline variant
        val PrimaryOutline = TagColorTokens(0x000052D9, 0xFF0052D9, 0xFFE0EDFF, 0xFFB5DAFF)
        val SuccessOutline = TagColorTokens(0x0000A870, 0xFF00A870, 0xFFE3F9E9, 0xFFC6F3D7)
        val WarningOutline = TagColorTokens(0x00ED7B2F, 0xFFED7B2F, 0xFFFFF1E9, 0xFFFFDDB8)
        val DangerOutline = TagColorTokens(0x00E34D59, 0xFFE34D59, 0xFFFFECEE, 0xFFFFB9BE)
        val DefaultOutline = TagColorTokens(0x00000000, 0xFF000000, 0xFFF3F3F3, 0xFFE7E7E7)
    }
}

// ============ Badge Token ============

/**
 * 徽章组件 Token
 */
data class BadgeTokens(
    /** 大小（圆形徽章的直径） */
    val size: Float,

    /** 字体大小 */
    val fontSize: Float,

    /** 最小宽度（数字徽章） */
    val minWidth: Float
) {
    companion object {
        val Default = BadgeTokens(
            size = 16f,
            fontSize = Typography.MarkSmall.fontSize,
            minWidth = 16f
        )

        val Large = BadgeTokens(
            size = 20f,
            fontSize = Typography.BodySmall.fontSize,
            minWidth = 20f
        )
    }
}
