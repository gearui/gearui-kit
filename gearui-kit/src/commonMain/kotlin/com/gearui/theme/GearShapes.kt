package com.gearui.theme

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * GearUI Framework 语义形状系统
 *
 * 参考: tdesign-flutter/tdesign-component/lib/src/theme/td_radius.dart
 *
 * ⚠️ 规则：
 * 组件层 ONLY 使用这些语义形状
 * 禁止出现 RoundedCornerShape(xx.dp) / 硬编码值
 *
 * 使用方式：
 * val shapes = Theme.shapes
 * Modifier.clip(shapes.default)
 */
@Immutable
data class GearShapes(

    /** 3dp - 小圆角 (按钮、小组件边角) */
    val small: Shape,

    /** 6dp - 默认圆角 (输入框、卡片、默认组件) */
    val default: Shape,

    /** 9dp - 大圆角 (较大卡片、模态框) */
    val large: Shape,

    /** 12dp - 特大圆角 (特殊强调组件) */
    val extraLarge: Shape,

    /** 胶囊型 (胶囊按钮、完全圆角) */
    val round: Shape,

    /** 圆形 (圆形头像、徽章、单选框) */
    val circle: Shape,
)

/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

object GearShapesDefault {

    /**
     * Default Shapes - 默认形状系统
     *
     * 圆角值完全对齐
     */
    val Default = GearShapes(
        small = RoundedCornerShape(3.dp),
        default = RoundedCornerShape(6.dp),
        large = RoundedCornerShape(9.dp),
        extraLarge = RoundedCornerShape(12.dp),
        round = RoundedCornerShape(9999.dp),
        circle = CircleShape,
    )
}
