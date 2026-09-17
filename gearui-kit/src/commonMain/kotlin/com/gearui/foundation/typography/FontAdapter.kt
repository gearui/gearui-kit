package com.gearui.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.text.font.FontStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.font.KuiklyFont

/** Host-registered font names. Unknown families fall through to the next candidate. */
val LocalFontRegistry = staticCompositionLocalOf<Map<String, String>> { emptyMap() }

internal expect fun nativeGenericFont(name: String): String?

internal fun resolveFontFamily(
    families: List<String>,
    weight: FontWeight,
    registered: Map<String, String>,
): FontFamily {
    for (name in families) {
        registered[name]?.let { return FontFamily(KuiklyFont(it, weight, FontStyle.Normal)) }
        nativeGenericFont(name.lowercase())?.let { return FontFamily(KuiklyFont(it, weight, FontStyle.Normal)) }
        when (name.lowercase()) {
            "system-ui" -> return FontFamily.Default
            "sans-serif" -> return FontFamily.SansSerif
            "serif" -> return FontFamily.Serif
            "monospace" -> return FontFamily.Monospace
            "cursive" -> return FontFamily.Cursive
        }
    }
    return nativeGenericFont("system-ui")?.let {
        FontFamily(KuiklyFont(it, weight, FontStyle.Normal))
    } ?: FontFamily.Default
}

/** Registration means the host has installed the font; this does not download font files. */
@Composable
fun TextStyle.resolveFontFamily(): FontFamily =
    resolveFontFamily(fontFamily, fontWeight, LocalFontRegistry.current)
