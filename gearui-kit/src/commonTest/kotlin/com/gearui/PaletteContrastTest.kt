package com.gearui

import com.gearui.theme.Colors
import com.gearui.theme.Themes
import com.gearui.theme.contrastRatio
import com.gearui.theme.withBrandAccent
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
import kotlin.test.Test
import kotlin.test.fail

/**
 * Every text pairing of the built-in palettes, and of a brand accent applied to them,
 * reads at WCAG 1.4.3's 4.5:1: body and secondary text on every ground, labels on
 * filled controls, and each colour's text form (`*SoftForeground`) on the grounds and
 * on its own soft fill. The focus ring, not text, is held to 1.4.11's 3:1.
 */
class PaletteContrastTest {
    private fun grounds(c: Colors) = listOf("background" to c.background, "surface" to c.surface, "muted" to c.muted)

    private fun failures(name: String, c: Colors): List<String> {
        val out = mutableListOf<String>()
        fun check(what: String, text: Color, ground: Color) {
            val ratio = contrastRatio(text, ground)
            if (ratio < 4.5) out += "$name: $what ${(ratio * 100).toInt() / 100.0}:1"
        }
        for ((g, ground) in grounds(c)) {
            check("foreground on $g", c.foreground, ground)
            check("mutedForeground on $g", c.mutedForeground, ground)
        }
        // The focus ring is not text: WCAG 1.4.11 asks 3:1 against what it sits on. The
        // built-in ring keeps the reference brand blue; only the filled primary darkens.
        for ((g, ground) in grounds(c).take(2)) {
            val ratio = contrastRatio(c.ring, ground)
            if (ratio < 3.0) out += "$name: ring on $g ${(ratio * 100).toInt() / 100.0}:1"
        }
        check("primaryForeground on primary", c.primaryForeground, c.primary)
        check("destructiveForeground on destructive", c.destructiveForeground, c.destructive)
        check("successForeground on success", c.successForeground, c.success)
        check("warningForeground on warning", c.warningForeground, c.warning)
        check("infoForeground on info", c.infoForeground, c.info)
        check("secondaryForeground on secondary", c.secondaryForeground, c.secondary)
        val textForms = listOf(
            Triple("primarySoftForeground", c.primarySoftForeground, c.primarySoft),
            Triple("destructiveSoftForeground", c.destructiveSoftForeground, c.destructiveSoft),
            Triple("successSoftForeground", c.successSoftForeground, c.successSoft),
            Triple("warningSoftForeground", c.warningSoftForeground, c.warningSoft),
        )
        for ((label, text, soft) in textForms) {
            for ((g, ground) in grounds(c)) check("$label on $g", text, ground)
            check("$label on its soft fill over surface", text, lerp(c.surface, soft.copy(alpha = 1f), soft.alpha))
            check("$label on its soft fill over background", text, lerp(c.background, soft.copy(alpha = 1f), soft.alpha))
        }
        return out
    }

    @Test
    fun builtInPalettesReadAtAA() {
        val all = failures("light", Themes.Light.colors) + failures("dark", Themes.Dark.colors)
        if (all.isNotEmpty()) fail(all.joinToString("\n"))
    }

    @Test
    fun brandAccentsKeepTextReadable() {
        val brands = listOf(
            "red" to Color(0xFFE53935), "blue" to Color(0xFF3B82F6), "yellow" to Color(0xFFFACC15),
            "purple" to Color(0xFF7C3AED), "green" to Color(0xFF22C55E), "teal" to Color(0xFF14B8A6),
            "grey" to Color(0xFF777777),
        )
        val all = brands.flatMap { (n, color) ->
            failures("light+$n", Themes.Light.withBrandAccent(color).colors) +
                failures("dark+$n", Themes.Dark.withBrandAccent(color).colors)
        }
        if (all.isNotEmpty()) fail(all.joinToString("\n"))
    }
}
