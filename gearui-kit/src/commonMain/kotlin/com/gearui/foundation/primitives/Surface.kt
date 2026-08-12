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
 * Surface - the core primitive of the KuiklyUI Design System
 *
 * Equivalent in role to:
 * - Material3: Surface
 * - Flutter: Container + InkWell
 * - FluentUI: BaseButton / Surface
 * - AntD: InternalBase
 *
 * Responsibility: handles the same 7 concerns for every component
 * 1. interaction state (pressed / focused / disabled / hovered)
 * 2. animation (press scale / ripple / opacity)
 * 3. background colour
 * 4. border
 * 5. corner radius
 * 6. click handling
 * 7. token injection
 *
 * Design rule: component code carries no such logic
 * - components never contain clickable / background / border / clip / interactionSource / animation
 * - components only combine tokens + content
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
    // Animation (unified animation system)
    // =========================
    val targetScale = if (isPressed && enabled) tokens.pressScale else 1f

    // =========================
    // Surface Node (Primitive View)
    // =========================
    // height = 0.dp means "height decided by the content" (self-sizing); no fixed height is applied.
    // Card and other wrapper components rely on this; fixed-height components (Button, Tag, ...) pass a concrete height.
    Box(
        modifier = modifier
            .then(if (tokens.height > 0.dp) Modifier.height(tokens.height) else Modifier)
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
 * Surface size and structure tokens
 */
data class SurfaceTokens(
    /** height */
    val height: Dp,

    /** corner radius */
    val radius: Dp,

    /** border width */
    val borderWidth: Dp = 0.dp,

    /** inner padding */
    val padding: PaddingValues = PaddingValues(0.dp),

    /** press scale factor */
    val pressScale: Float = 0.98f
)

/**
 * Surface state colour tokens
 */
data class SurfaceColorTokens(
    /** normal background */
    val background: Color,

    /** border colour */
    val border: Color = Color.Transparent,

    /** disabled background */
    val disabledBackground: Color,

    /** disabled border colour */
    val disabledBorder: Color = Color.Transparent,

    /** pressed background */
    val pressedBackground: Color = background
)

