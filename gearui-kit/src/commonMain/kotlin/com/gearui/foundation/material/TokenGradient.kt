package com.gearui.foundation.material

import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.SolidColor

/** Direction is a rendering choice, not part of a DTCG gradient value. */
internal fun List<TokenGradientStop>.verticalBrush(): Brush {
    require(isNotEmpty()) { "Cannot render an empty gradient" }
    require(all { it.position.isFinite() && it.position in 0f..1f }) {
        "Gradient positions must be resolved into 0..1"
    }
    require(zipWithNext().all { (a, b) -> a.position <= b.position }) {
        "This adapter requires nondecreasing gradient positions"
    }
    if (size == 1) return SolidColor(first().color)
    return Brush.verticalGradient(*map { it.position to it.color }.toTypedArray())
}

/** Explicit alpha-mask operation; ordinary gradients retain their source colors. */
internal fun List<TokenGradientStop>.tintedMask(surface: Color): List<TokenGradientStop> =
    map { it.copy(color = surface.copy(alpha = surface.alpha * it.color.alpha)) }
