package com.gearui.foundation.color

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * What content colours belong on a given background.
 *
 * @property text body colour, picked dark or light by **measured contrast** —
 *   not by the colour's name and not by whether the theme is light or dark. A
 *   pale yellow background gets dark text even inside a dark theme.
 * @property link link colour.
 * @property needsEmphasis the link and the body are too close in colour for
 *   colour alone to carry "this is tappable". **This reports the fact and does
 *   not choose the remedy** — whether to add an inline underlay is a matter for
 *   the rich-text or interaction layer.
 */
data class ContentStyle(
    val text: Color,
    val link: Color,
    val needsEmphasis: Boolean,
    val textOnBackground: Float,
    val linkOnBackground: Float,
    val linkOnText: Float,
)

/** WCAG relative luminance. */
fun relativeLuminance(c: Color): Float {
    fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else ((v + 0.055f) / 1.055f).pow(2.4f)
    return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
}

/** WCAG contrast ratio. */
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

/**
 * The reference point that keeps links one family. Candidates vary only its
 * lightness, holding hue and saturation, so a link stays recognisably the same
 * blue across backgrounds.
 */
val CanonicalLink: Color = Color(0xFF2563EB)

/**
 * Resolves the body and link colour for a background.
 *
 * The priorities, in a deliberate order:
 *
 *  1. **Body readable** — body against background >= [minReadable].
 *  2. **Link readable** — link against background >= [minReadable]. A
 *     constraint, not something to maximise.
 *  3. **Link identifiable** — link against body >= [minIdentifiable].
 *  4. **Family consistent** — among the candidates that satisfy the above, take
 *     the one closest in lightness to [canonical].
 *
 * Step 4 is a tie-break, not a constraint. An earlier version maximised the
 * difference from the body colour here, which picked near-black or oversaturated
 * results: the contrast numbers passed while the link stopped looking like the
 * same link colour.
 *
 * The candidate space is a lightness ladder over [canonical]. When none of them
 * is readable against the background — saturated reds and mid-luminance greys do
 * this — it falls back to the body colour, which is safe by construction, and
 * sets [ContentStyle.needsEmphasis] so a layer above can distinguish the link
 * without relying on colour. **That means "no solution in this candidate space",
 * not "no readable blue exists".**
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
            // Identifiable candidates beat non-identifiable ones outright; within
            // one tier, closeness to the canonical family decides.
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
