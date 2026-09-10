package com.gearui.foundation.color

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 一个背景上应该配什么内容色。
 *
 * @property text 正文色：按**实际对比度**取深或浅，不看颜色名、也不看主题是浅色还是深色。
 *   所以深色主题里的浅黄背景照样是深色字。
 * @property link 链接色。
 * @property needsEmphasis 链接与正文的色差不足以单独承担「这是可点的」这件事。
 *   **这里只做判定，不决定怎么强调**——加不加行内底纹是富文本/交互层的事。
 */
data class ContentStyle(
    val text: Color,
    val link: Color,
    val needsEmphasis: Boolean,
    val textOnBackground: Float,
    val linkOnBackground: Float,
    val linkOnText: Float,
)

/** WCAG 相对亮度。 */
fun relativeLuminance(c: Color): Float {
    fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else ((v + 0.055f) / 1.055f).pow(2.4f)
    return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
}

/** WCAG 对比度。 */
fun contrastRatio(a: Color, b: Color): Float {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    return (max(la, lb) + 0.05f) / (min(la, lb) + 0.05f)
}

private fun hueSatLight(c: Color): Triple<Float, Float, Float> {
    val mx = maxOf(c.red, c.green, c.blue)
    val mn = minOf(c.red, c.green, c.blue)
    val l = (mx + mn) / 2f
    if (mx == mn) return Triple(0f, 0f, l)
    val d = mx - mn
    val s = if (l > 0.5f) d / (2f - mx - mn) else d / (mx + mn)
    val h = when (mx) {
        c.red -> ((c.green - c.blue) / d + if (c.green < c.blue) 6f else 0f)
        c.green -> (c.blue - c.red) / d + 2f
        else -> (c.red - c.green) / d + 4f
    } / 6f
    return Triple(h, s, l)
}

private fun fromHsl(h: Float, s: Float, l: Float): Color {
    if (s == 0f) return Color(l, l, l, 1f)
    fun hue2rgb(p: Float, q: Float, tIn: Float): Float {
        var t = tIn
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        return when {
            t < 1f / 6f -> p + (q - p) * 6f * t
            t < 1f / 2f -> q
            t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
            else -> p
        }
    }
    val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
    val p = 2f * l - q
    return Color(hue2rgb(p, q, h + 1f / 3f), hue2rgb(p, q, h), hue2rgb(p, q, h - 1f / 3f), 1f)
}

/** 统一链接色系的参照点。候选只在它的色相/饱和度上变明度，保证跨背景仍是「同一个蓝」。 */
val CanonicalLink: Color = Color(0xFF2563EB)

/**
 * 给定背景，解析出该用什么正文色和链接色。
 *
 * 优先级（顺序是刻意的）：
 *
 *  1. **正文可读**——正文对背景 ≥ [minReadable]。
 *  2. **链接可读**——链接对背景 ≥ [minReadable]。这是约束，不是优化目标。
 *  3. **链接可识别**——链接与正文 ≥ [minIdentifiable]。
 *  4. **色系一致**——在满足以上条件的候选里，取明度最接近 [canonical] 的那个。
 *
 * 第 4 步是「选择依据」而不是约束：早先的版本在这里最大化「与正文的色差」，结果会挑出
 * 近黑或过艳的颜色——对比度达标了，但看起来不再是同一个链接色。
 *
 * 候选域是 [canonical] 的明度阶梯。若其中没有对背景可读的解（高饱和红、中等亮度灰这类
 * 背景会出现），退回与正文同色的安全前景色，并置 [ContentStyle.needsEmphasis]，由上层
 * 用非颜色提示区分。**注意这是「当前候选色域内无解」，不是「不存在可读的蓝」。**
 */
fun resolveContentStyle(
    background: Color,
    canonical: Color = CanonicalLink,
    minReadable: Float = 4.5f,
    minIdentifiable: Float = 3.0f,
): ContentStyle {
    val dark = Color(0xFF0A0A0A)
    val light = Color(0xFFFFFFFF)
    val text = if (contrastRatio(dark, background) >= contrastRatio(light, background)) dark else light

    val (h, s, canonicalL) = hueSatLight(canonical)
    var best: Color? = null
    var bestScore = Float.MAX_VALUE
    var bestIdentifiable = false
    var l = 0.08f
    while (l <= 0.86f) {
        val cand = fromHsl(h, s, l)
        if (contrastRatio(cand, background) >= minReadable) {
            val identifiable = contrastRatio(cand, text) >= minIdentifiable
            val score = abs(l - canonicalL)
            // 可识别的候选整体优先于不可识别的；同一档内再比色系接近度。
            val better = when {
                identifiable && !bestIdentifiable -> true
                identifiable == bestIdentifiable -> score < bestScore
                else -> false
            }
            if (better) {
                best = cand; bestScore = score; bestIdentifiable = identifiable
            }
        }
        l += 0.011f
    }

    val link = best ?: text
    return ContentStyle(
        text = text,
        link = link,
        needsEmphasis = contrastRatio(link, text) < minIdentifiable,
        textOnBackground = contrastRatio(text, background),
        linkOnBackground = contrastRatio(link, background),
        linkOnText = contrastRatio(link, text),
    )
}
