package com.gearui.foundation

import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * GearUI 组件规格系统
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/components/
 *
 * 所有组件统一使用这些规格，确保视觉一致性
 */

/* ========================================
 * Button 按钮规格
 * 参考: td_button.dart
 * ======================================== */

object ButtonSpecs {
    // 高度规格
    object Height {
        val large: Dp = 48.dp
        val medium: Dp = 40.dp
        val small: Dp = 32.dp
        val extraSmall: Dp = 28.dp
    }

    // 图标尺寸
    object IconSize {
        val large: Dp = 24.dp
        val medium: Dp = 20.dp
        val small: Dp = 18.dp
        val extraSmall: Dp = 14.dp
    }

    // 水平内边距 (非等边)
    object PaddingHorizontal {
        val large: Dp = 20.dp
        val medium: Dp = 16.dp
        val small: Dp = 12.dp
        val extraSmall: Dp = 8.dp
    }

    // 垂直内边距
    object PaddingVertical {
        val large: Dp = 12.dp
        val medium: Dp = 8.dp
        val small: Dp = 5.dp
        val extraSmall: Dp = 3.dp
    }

    // 图标与文字间距
    val iconTextSpacing: Dp = 8.dp

    // 通栏按钮外边距
    val blockMarginHorizontal: Dp = 16.dp
}

/* ========================================
 * Cell 单元格规格
 * 参考: td_cell_style.dart
 * ======================================== */

object CellSpecs {
    // 内边距 (使用 spacer16 = 16dp)
    val padding: Dp = 16.dp

    // 标题字号 (fontBodyLarge = 16sp)
    val titleFontSize: Float = 16f

    // 描述字号 (fontBodyMedium = 14sp)
    val descriptionFontSize: Float = 14f

    // 边框宽度
    val borderWidth: Dp = 0.5.dp

    // 卡片圆角 (radiusLarge = 9dp)
    val cardRadius: Dp = 9.dp

    // 卡片水平内边距
    val cardPaddingHorizontal: Dp = 16.dp

    // 组标题上边距
    val groupTitlePaddingTop: Dp = 24.dp

    // 组标题下边距
    val groupTitlePaddingBottom: Dp = 8.dp
}

/* ========================================
 * Avatar 头像规格
 * 参考: td_avatar.dart
 * ======================================== */

object AvatarSpecs {
    object Size {
        val large: Dp = 64.dp
        val medium: Dp = 48.dp
        val small: Dp = 40.dp
    }

    // 默认圆形
    val defaultRadius: Dp = 9999.dp

    // 方形圆角 (radiusDefault = 6dp)
    val squareRadius: Dp = 6.dp
}

/* ========================================
 * Badge 徽章规格
 * 参考: td_badge.dart
 * ======================================== */

object BadgeSpecs {
    object Size {
        val large: Dp = 20.dp
        val small: Dp = 16.dp
    }

    // 红点尺寸
    val dotSize: Dp = 8.dp

    // 大圆角
    val radiusLarge: Dp = 8.dp

    // 小圆角
    val radiusSmall: Dp = 2.dp
}

/* ========================================
 * Divider 分割线规格
 * 参考: td_divider.dart
 * ======================================== */

object DividerSpecs {
    // 默认高度
    val height: Dp = 0.5.dp

    // 文字与线条间距
    val gapPadding: Dp = 8.dp

    // 带文字时线条最小宽度
    val lineMinWidth: Dp = 16.dp
}

/* ========================================
 * TabBar 标签栏规格
 * 参考: td_tab_bar.dart
 * ======================================== */

object TabSpecs {
    // 默认高度
    val height: Dp = 48.dp

    // 指示器高度
    val indicatorHeight: Dp = 2.dp

    // 指示器宽度 (默认跟随文字)
    val indicatorMinWidth: Dp = 16.dp

    // 分割线高度
    val dividerHeight: Dp = 0.5.dp
}

/* ========================================
 * Card 卡片规格
 * ======================================== */

object CardSpecs {
    // 圆角 (radiusLarge = 9dp)
    val radius: Dp = 9.dp

    // 内边距（收敛到 shadcn 常用的紧凑卡片节奏）
    val padding: Dp = 12.dp

    // 边框宽度
    val borderWidth: Dp = 0.5.dp
}

/* ========================================
 * Input 输入框规格
 * 参考: td_input.dart, td_input_spacer.dart
 * ======================================== */

object InputSpecs {
    object Height {
        val large: Dp = 48.dp
        val small: Dp = 40.dp
    }

    // 图标与标签间距
    val iconLabelSpace: Dp = 4.dp

    // 标签与输入框间距
    val labelInputSpace: Dp = 16.dp

    // 输入框与右侧间距
    val inputRightSpace: Dp = 16.dp

    // 右侧间距
    val rightSpace: Dp = 16.dp
}

/* ========================================
 * SectionHeader 区块标题规格
 * ======================================== */

object SectionHeaderSpecs {
    // 上边距
    val paddingTop: Dp = 24.dp

    // 下边距
    val paddingBottom: Dp = 8.dp

    // 水平边距
    val paddingHorizontal: Dp = 16.dp

    // 标题字号 (fontTitleLarge = 18sp)
    val titleFontSize: Float = 18f
}
