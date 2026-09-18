package com.gearui.foundation.material

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.layout.layout
import com.tencent.kuikly.compose.ui.unit.Constraints
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.CornerRadius
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.geometry.RoundRect
import com.tencent.kuikly.compose.ui.graphics.*
import com.tencent.kuikly.compose.ui.graphics.drawscope.*
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.math.*

/** Ordered source shadow layers; the first layer is painted on top. */
data class SurfaceShadow(
    val color: Color,
    val offsetX: Dp,
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val inset: Boolean = false,
)

enum class BorderLineStyle { Solid, Dashed, Dotted, Double, Groove, Ridge, Outset, Inset, Custom }

data class SurfaceStroke(
    val kind: BorderLineStyle = BorderLineStyle.Solid,
    val dashArray: List<Dp> = emptyList(),
    val lineCap: StrokeCap = StrokeCap.Butt,
)

data class SurfaceBorder(
    val color: Color,
    val width: Dp,
    val style: SurfaceStroke = SurfaceStroke(),
)

internal data class ShadowBand(val extent: Float, val alpha: Float)

/** Gaussian edge profile, discretized into bounded contour bands (not a bitmap convolution). */
internal fun shadowBands(blur: Float, alpha: Float): List<ShadowBand> {
    require(blur.isFinite() && blur >= 0f && alpha.isFinite() && alpha in 0f..1f)
    if (alpha == 0f) return emptyList()
    if (blur == 0f) return listOf(ShadowBand(0f, alpha))
    val sigma = blur / 2f
    val count = ceil(blur * 2f).toInt().coerceIn(12, 48)
    var previous = 0f
    return (0..count).map { index ->
        val z = 3f - 6f * index / count
        val t = 1f / (1f + 0.2316419f * abs(z))
        val tail = exp(-z * z / 2f) / sqrt(2f * PI.toFloat()) *
            t * (0.31938153f + t * (-0.35656378f + t * (1.7814779f + t * (-1.821256f + t * 1.3302745f))))
        val coverage = if (z >= 0) tail else 1f - tail
        val target = if (index == count) alpha else alpha * coverage
        // The bridge emits 8-bit alpha. Quantize cumulative coverage, otherwise
        // every band of a low-opacity token rounds to zero and the shadow vanishes.
        val delta = if (previous >= 1f) 0f else round(((target - previous) / (1f - previous)).coerceIn(0f, 1f) * 255f) / 255f
        previous += (1f - previous) * delta
        ShadowBand(z * sigma, delta)
    }
}

internal fun normaliseDashArray(values: List<Float>): List<Float> {
    require(values.all { it.isFinite() && it >= 0f }) { "Dash lengths must be finite and nonnegative" }
    if (values.isEmpty() || values.all { it == 0f }) return emptyList()
    return if (values.size % 2 == 0) values else values + values
}

private fun Outline.roundRect(): RoundRect = when (this) {
    is Outline.Rectangle -> RoundRect(rect)
    is Outline.Rounded -> roundRect
    is Outline.Generic -> error("Surface decoration currently requires a rectangular or rounded outline")
}

private fun RoundRect.insetBy(amount: Float): RoundRect? {
    if (width <= amount * 2 || height <= amount * 2) return null
    fun CornerRadius.adjust() = CornerRadius(max(0f, x - amount), max(0f, y - amount))
    return RoundRect(left + amount, top + amount, right - amount, bottom - amount,
        topLeftCornerRadius.adjust(), topRightCornerRadius.adjust(),
        bottomRightCornerRadius.adjust(), bottomLeftCornerRadius.adjust())
}

private fun RoundRect.path(): Path = Path().apply { addRoundRect(this@path) }

/** Nonzero winding hole: unlike ClipOp.Difference, all Kuikly canvases support this. */
private fun excludingPath(bounds: Rect, hole: List<Offset>): Path = Path().apply {
    addRect(bounds)
    hole.asReversed().forEachIndexed { index, point ->
        if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}

/** Flattened rounded contour, used for portable dash placement where PathEffect is unavailable. */
private fun RoundRect.contour(): List<Offset> {
    val radiusScale = minOf(1f,
        width / max(1e-6f, topLeftCornerRadius.x + topRightCornerRadius.x),
        width / max(1e-6f, bottomLeftCornerRadius.x + bottomRightCornerRadius.x),
        height / max(1e-6f, topLeftCornerRadius.y + bottomLeftCornerRadius.y),
        height / max(1e-6f, topRightCornerRadius.y + bottomRightCornerRadius.y))
    val result = mutableListOf<Offset>()
    fun corner(cx: Float, cy: Float, r: CornerRadius, start: Float) {
        val rx = r.x * radiusScale
        val ry = r.y * radiusScale
        val steps = ceil(max(rx, ry) / 2f).toInt().coerceIn(4, 48)
        for (i in 0..steps) {
            val angle = (start + i * 90f / steps) * PI.toFloat() / 180f
            result += Offset(cx + cos(angle) * rx, cy + sin(angle) * ry)
        }
    }
    corner(right - topRightCornerRadius.x * radiusScale, top + topRightCornerRadius.y * radiusScale, topRightCornerRadius, -90f)
    corner(right - bottomRightCornerRadius.x * radiusScale, bottom - bottomRightCornerRadius.y * radiusScale, bottomRightCornerRadius, 0f)
    corner(left + bottomLeftCornerRadius.x * radiusScale, bottom - bottomLeftCornerRadius.y * radiusScale, bottomLeftCornerRadius, 90f)
    corner(left + topLeftCornerRadius.x * radiusScale, top + topLeftCornerRadius.y * radiusScale, topLeftCornerRadius, 180f)
    result += result.first()
    return result
}

internal data class DashSegment(val start: Offset, val end: Offset, val startsDash: Boolean, val endsDash: Boolean)

internal fun dashSegments(points: List<Offset>, pattern: List<Float>): List<DashSegment> {
    val dash = normaliseDashArray(pattern)
    require(dash.isNotEmpty())
    val output = mutableListOf<DashSegment>()
    var index = 0
    var remaining = dash[0].toDouble()
    var begins = true
    for ((a, b) in points.zipWithNext()) {
        val length = (b - a).getDistance()
        if (length <= 1e-5f) continue
        var consumed = 0.0
        while (consumed < length.toDouble()) {
            require(output.size < 10000) { "Dash pattern exceeds the rendering segment budget" }
            while (remaining <= 0.0) {
                if (dash[index] == 0f && index % 2 == 0) {
                    val point = a + (b - a) * (consumed / length).toFloat()
                    output += DashSegment(point, point, true, true)
                }
                index = (index + 1) % dash.size
                remaining = dash[index].toDouble()
                begins = true
            }
            val step = min(remaining, length - consumed)
            if (index % 2 == 0) output += DashSegment(
                a + (b - a) * (consumed / length).toFloat(), a + (b - a) * ((consumed + step) / length).toFloat(),
                begins, step >= remaining)
            consumed += step
            remaining -= step
            begins = false
        }
    }
    return output
}

/**
 * Shared rectangular-surface renderer. Outer shadows do not affect measurement;
 * ancestors must allow overflow. Insets and borders paint above content.
 * Blur uses a bounded Gaussian edge approximation, shared by all renderers.
 */
private fun DrawScope.paintDecoration(
    shape: Shape = RectangleShape,
    shadows: List<SurfaceShadow> = emptyList(),
    border: SurfaceBorder? = null,
    bleed: Dp = 0.dp,
) {
    shadows.forEach {
        require(listOf(it.offsetX.value, it.offsetY.value, it.blur.value, it.spread.value).all { v -> v.isFinite() })
        require(it.blur.value >= 0f)
    }
    require(border == null || (border.width.value.isFinite() && border.width.value >= 0f))
    val margin = bleed.toPx()
    val base = shape.createOutline(Size(max(0f, size.width - margin * 2), max(0f, size.height - margin * 2)), layoutDirection, this).roundRect()
    val rect = RoundRect(base.left + margin, base.top + margin, base.right + margin, base.bottom + margin,
        base.topLeftCornerRadius, base.topRightCornerRadius, base.bottomRightCornerRadius, base.bottomLeftCornerRadius)
    val boundary = rect.path()
    val layers = shadows.asReversed().map { shadow ->
        shadow to shadowBands(shadow.blur.toPx(), shadow.color.alpha).map { band ->
            val amount = if (shadow.inset) shadow.spread.toPx() + band.extent else -shadow.spread.toPx() - band.extent
            Triple(rect.insetBy(amount), band.alpha, Offset(shadow.offsetX.toPx(), shadow.offsetY.toPx()))
        }
    }
    fun DrawScope.paintShadows(inset: Boolean) {
        for ((shadow, bands) in layers) {
            if (shadow.inset != inset) continue
            val canvasBounds = Rect(0f, 0f, size.width, size.height)
            clipPath(if (inset) boundary else excludingPath(canvasBounds, rect.contour())) {
                for ((bandRect, alpha, offset) in bands) {
                    if (alpha == 0f) continue
                    if (inset) {
                        if (bandRect == null) drawRect(shadow.color.copy(alpha = alpha))
                        else {
                            // Move the hole, not the surface clip.
                            val moved = bandRect.contour().map { it + offset }
                            drawPath(excludingPath(canvasBounds, moved), shadow.color.copy(alpha = alpha))
                        }
                    } else if (bandRect != null) translate(offset.x, offset.y) {
                        drawPath(bandRect.path(), shadow.color.copy(alpha = alpha))
                    }
                }
            }
        }
    }
    val width = border?.width?.toPx() ?: 0f
    val center = rect.insetBy(width / 2f)
    val pattern = border?.let {
        when (it.style.kind) {
            BorderLineStyle.Dashed -> listOf(width * 3f, width * 3f)
            BorderLineStyle.Dotted -> listOf(width * 0.001f, width * 2f)
            BorderLineStyle.Custom -> normaliseDashArray(it.style.dashArray.map { d -> d.toPx() })
            else -> emptyList()
        }
    } ?: emptyList()
    val segments = if (width > 0f && center != null && pattern.isNotEmpty()) dashSegments(center.contour(), pattern) else emptyList()
    fun DrawScope.ring(outer: Float, thickness: Float, color: Color) {
        val outside = rect.insetBy(outer)?.path() ?: return
        val inside = rect.insetBy(outer + thickness)
        if (inside == null) drawPath(outside, color)
        else clipPath(excludingPath(Rect(0f, 0f, size.width, size.height), inside.contour())) { drawPath(outside, color) }
    }
    fun DrawScope.bevel(outer: Float, thickness: Float, raised: Boolean) {
        val color = border!!.color
        val light = Color(color.red + (1f - color.red) * .35f, color.green + (1f - color.green) * .35f, color.blue + (1f - color.blue) * .35f, color.alpha)
        val dark = Color(color.red * .65f, color.green * .65f, color.blue * .65f, color.alpha)
        val points = listOf(Offset(rect.left, rect.bottom), Offset(rect.left, rect.top), Offset(rect.right, rect.top), Offset((rect.left + rect.right) / 2f, (rect.top + rect.bottom) / 2f))
        val topLeft = Path().apply { points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        clipPath(topLeft) { ring(outer, thickness, if (raised) light else dark) }
        clipPath(excludingPath(Rect(0f, 0f, size.width, size.height), points)) { ring(outer, thickness, if (raised) dark else light) }
    }
    paintShadows(false)
    paintShadows(true)
    if (border != null && width > 0f) clipPath(boundary) {
        when (border.style.kind) {
            BorderLineStyle.Double -> { ring(0f, width / 3f, border.color); ring(width * 2f / 3f, width / 3f, border.color) }
            BorderLineStyle.Inset, BorderLineStyle.Outset -> bevel(0f, width, border.style.kind == BorderLineStyle.Outset)
            BorderLineStyle.Groove, BorderLineStyle.Ridge -> {
                bevel(0f, width / 2f, border.style.kind == BorderLineStyle.Ridge)
                bevel(width / 2f, width / 2f, border.style.kind == BorderLineStyle.Groove)
            }
            else -> if (segments.isEmpty()) ring(0f, width, border.color) else {
                val cap = if (border.style.kind == BorderLineStyle.Dotted) StrokeCap.Round else border.style.lineCap
                var dashPath: Path? = null
                fun flushDash() {
                    dashPath?.let { drawPath(it, border.color, style = Stroke(width, cap = cap, join = StrokeJoin.Round)) }
                    dashPath = null
                }
                for (segment in segments) {
                    if (segment.start == segment.end) {
                        flushDash()
                        if (cap == StrokeCap.Round) drawCircle(border.color, width / 2, segment.start)
                        else if (cap == StrokeCap.Square) drawRect(border.color, segment.start - Offset(width / 2, width / 2), Size(width, width))
                        continue
                    }
                    if (segment.startsDash) flushDash()
                    if (dashPath == null) dashPath = Path().apply { moveTo(segment.start.x, segment.start.y) }
                    dashPath!!.lineTo(segment.end.x, segment.end.y)
                    if (segment.endsDash) flushDash()
                }
                flushDash()
            }
        }
    }
}

/**
 * Token-driven surface decoration on real Canvas nodes (draw modifiers on a
 * Kuikly native Box alone do not provide a canvas). Content remains clipped;
 * shadow overflow is allocated without changing the parent's measured size.
 */
@Composable
fun DecoratedSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    shadows: List<SurfaceShadow> = emptyList(),
    border: SurfaceBorder? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val outer = shadows.filter { !it.inset && it.color.alpha > 0f }
    val inner = shadows.filter { it.inset && it.color.alpha > 0f }
    val density = LocalDensity.current
    val rawBleed = (outer.maxOfOrNull {
        max(abs(it.offsetX.value), abs(it.offsetY.value)) + max(0f, it.spread.value) + it.blur.value * 1.5f
    } ?: 0f).dp
    val bleed = with(density) { ceil(rawBleed.toPx()).toInt().toDp() }
    Box(modifier, propagateMinConstraints = true) {
        if (outer.isNotEmpty()) Canvas(
            Modifier.matchParentSize().layout { measurable, constraints ->
                val pad = ceil(bleed.toPx()).toInt()
                // Intrinsic passes (for example a parent using IntrinsicSize.Max) measure
                // this modifier with unbounded constraints. Constraints.Infinity plus the
                // bleed overflows Int and Constraints.fixed throws, which on iOS killed the
                // app the first time a ContextMenu opened. The decoration matches the
                // parent, so an unbounded axis contributes nothing here.
                val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
                val height = if (constraints.hasBoundedHeight) constraints.maxHeight else 0
                val padW = if (constraints.hasBoundedWidth) pad else 0
                val padH = if (constraints.hasBoundedHeight) pad else 0
                val child = measurable.measure(Constraints.fixed(width + padW * 2, height + padH * 2))
                layout(width, height) { child.place(-padW, -padH) }
            }
        ) { paintDecoration(shape, outer, bleed = bleed) }
        Box(Modifier.clip(shape), propagateMinConstraints = true, content = content)
        if (inner.isNotEmpty() || border != null) Canvas(
            Modifier.matchParentSize()
        ) { paintDecoration(shape, inner, border) }
    }
}
