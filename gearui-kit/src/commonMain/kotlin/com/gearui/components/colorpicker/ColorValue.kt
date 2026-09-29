package com.gearui.components.colorpicker

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.roundToInt

/** Runtime color value. Hue is in degrees; saturation, brightness and alpha are 0..1. */
data class ColorValue(val hue: Float, val saturation: Float, val brightness: Float, val alpha: Float = 1f) {
    init { require(hue.isFinite() && hue in 0f..360f && saturation in 0f..1f && brightness in 0f..1f && alpha in 0f..1f) }
    fun toColor(): Color {
        val h = (hue % 360f) / 60f
        val c = brightness * saturation
        val x = c * (1 - abs(h % 2 - 1))
        val m = brightness - c
        val (r, g, b) = when (h.toInt()) {
            0 -> Triple(c, x, 0f); 1 -> Triple(x, c, 0f); 2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c); 4 -> Triple(x, 0f, c); else -> Triple(c, 0f, x)
        }
        return Color(r + m, g + m, b + m, alpha)
    }
    fun toHex(includeAlpha: Boolean = alpha < 1f): String {
        val color = toColor()
        val values = listOf(color.red, color.green, color.blue) + if (includeAlpha) listOf(alpha) else emptyList()
        return "#" + values.joinToString("") { (it * 255).roundToInt().toString(16).padStart(2, '0').uppercase() }
    }
    companion object {
        fun fromColor(color: Color): ColorValue {
            val r = color.red; val g = color.green; val b = color.blue
            val max = maxOf(r, g, b); val delta = max - minOf(r, g, b)
            val h = when {
                delta == 0f -> 0f
                max == r -> 60f * (((g - b) / delta) % 6f)
                max == g -> 60f * ((b - r) / delta + 2f)
                else -> 60f * ((r - g) / delta + 4f)
            }
            return ColorValue((h + 360f) % 360f, if (max == 0f) 0f else delta / max, max, color.alpha)
        }
        fun parseHex(text: String): ColorValue? {
            val digits = text.removePrefix("#")
            if (digits.length !in listOf(3, 4, 6, 8) || digits.any { it.digitToIntOrNull(16) == null }) return null
            val expanded = if (digits.length <= 4) digits.flatMap { listOf(it, it) }.joinToString("") else digits
            val channels = expanded.chunked(2).map { it.toInt(16) / 255f }
            return fromColor(Color(channels[0], channels[1], channels[2], channels.getOrElse(3) { 1f }))
        }
    }
}

internal fun areaColor(value: ColorValue, x: Float, y: Float): ColorValue =
    value.copy(saturation = x.coerceIn(0f, 1f), brightness = (1f - y).coerceIn(0f, 1f))
