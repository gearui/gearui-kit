package com.gearui.foundation.primitives

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.scale
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.*

/**
 * Surface - KuiklyUI Design System 的核心 Primitive
 *
 * 地位等价于:
 * - Material3: Surface
 * - Flutter: Container + InkWell
 * - FluentUI: BaseButton / Surface
 * - AntD: InternalBase
 *
 * 职责: 统一处理所有组件的 7 件核心事项
 * 1. 交互状态 (pressed/focused/disabled/hovered)
 * 2. 动画 (press scale/ripple/opacity)
 * 3. 背景色
 * 4. 边框
 * 5. 圆角
 * 6. 点击事件
 * 7. Tokens 注入
 *
 * 设计原则: 组件层 0 逻辑
 * - 组件不再出现: clickable/background/border/clip/interactionSource/animation
 * - 组件只负责: tokens + content 组合
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tokens: SurfaceTokens,
    colors: SurfaceColorTokens,
    interactionSource: MutableInteractionSource = remember { createMutableInteractionSource() },
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    // =========================
    // Interaction State
    // =========================
    val isPressed = interactionSource.currentState is InteractionState.Pressed

    // =========================
    // State-driven Colors
    // =========================
    val backgroundColor = when {
        !enabled -> colors.disabledBackground
        isPressed -> colors.pressedBackground
        else -> colors.background
    }

    val borderColor = when {
        !enabled -> colors.disabledBorder
        else -> colors.border
    }

    // =========================
    // Animation (统一动画系统)
    // =========================
    val targetScale = if (isPressed && enabled) tokens.pressScale else 1f

    // =========================
    // Surface Node (Primitive View)
    // =========================
    Box(
        modifier = modifier
            .height(tokens.height)
            .scale(targetScale)
            .clip(RoundedCornerShape(tokens.radius))
            .background(backgroundColor)
            .then(
                if (tokens.borderWidth > 0.dp) {
                    Modifier.border(tokens.borderWidth, borderColor, RoundedCornerShape(tokens.radius))
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(tokens.padding),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Surface 尺寸 + 结构 Tokens
 */
data class SurfaceTokens(
    /** 高度 */
    val height: Dp,

    /** 圆角半径 */
    val radius: Dp,

    /** 边框宽度 */
    val borderWidth: Dp = 0.dp,

    /** 内边距 */
    val padding: PaddingValues = PaddingValues(0.dp),

    /** 按压缩放比例 */
    val pressScale: Float = 0.98f
)

/**
 * Surface 状态颜色 Tokens
 */
data class SurfaceColorTokens(
    /** 正常背景色 */
    val background: Color,

    /** 边框颜色 */
    val border: Color = Color.Transparent,

    /** 禁用背景色 */
    val disabledBackground: Color,

    /** 禁用边框色 */
    val disabledBorder: Color = Color.Transparent,

    /** 按压背景色 */
    val pressedBackground: Color = background
)

/**
 * 扩展: 从 ComponentTokens 转换为 SurfaceTokens
 */
fun com.gearui.foundation.tokens.ButtonTokens.toSurfaceTokens(): SurfaceTokens {
    return SurfaceTokens(
        height = this.height.dp,
        radius = this.borderRadius.dp,
        borderWidth = this.borderWidth.dp,
        padding = PaddingValues(horizontal = this.paddingHorizontal.dp)
    )
}

fun com.gearui.foundation.tokens.TagTokens.toSurfaceTokens(): SurfaceTokens {
    return SurfaceTokens(
        height = this.height.dp,
        radius = this.borderRadius.dp,
        borderWidth = 0.dp,
        padding = PaddingValues(horizontal = this.paddingHorizontal.dp)
    )
}
