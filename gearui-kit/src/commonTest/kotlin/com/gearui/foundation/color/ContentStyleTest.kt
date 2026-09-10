package com.gearui.foundation.color

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The background -> content colour contract.
 *
 * A brand picks its own bubble fill (WeChat green, Telegram blue, Xianyu yellow,
 * Weey yellow...). GearUI **never changes a brand's fill to suit a link**; it
 * only picks readable body and link colours for whatever fill it is given. So
 * the tests have to cover a spread of fills rather than the one or two brands
 * that happen to be at hand.
 */
class ContentStyleTest {

    private val backgrounds = mapOf(
        "微信绿" to Color(0xFF95EC69),
        "Telegram 蓝" to Color(0xFF3390EC),
        "闲鱼黄" to Color(0xFFFFE411),
        "Weey 黄" to Color(0xFFFFD238),
        "PrivChat 蓝" to Color(0xFF0046BE),
        "浅灰" to Color(0xFFE5E5EA),
        "深灰" to Color(0xFF2E2E33),
        "中等亮度" to Color(0xFF7A7A80),
        "高饱和红" to Color(0xFFE1121D),
        "高饱和紫" to Color(0xFF7B2FF7),
        "近黑" to Color(0xFF101014),
        "近白" to Color(0xFFFAFAFA),
    )

    /** Body and link must both clear 4.5:1 against the background. No exceptions, and no "close enough". */
    @Test
    fun bodyAndLinkAreReadableOnEveryBackground() {
        backgrounds.forEach { (name, bg) ->
            val s = resolveContentStyle(bg)
            assertTrue(s.textOnBackground >= 4.5f, "$name：正文对背景仅 ${s.textOnBackground}")
            assertTrue(s.linkOnBackground >= 4.5f, "$name：链接对背景仅 ${s.linkOnBackground}")
        }
    }

    /**
     * When the colours are too close it must **say so**, leaving a layer above
     * to add a non-colour cue.
     *
     * This is the guard against quietly picking an indistinct blue and treating
     * it as fine, which is the practice this resolver exists to replace.
     */
    @Test
    fun insufficientSeparationIsReportedRatherThanHidden() {
        backgrounds.forEach { (name, bg) ->
            val s = resolveContentStyle(bg)
            if (s.linkOnText < 3.0f) {
                assertTrue(s.needsEmphasis, "$name：色差只有 ${s.linkOnText} 却没有要求强调")
            }
        }
    }

    /** The test is measured contrast, not light-mode/dark-mode: pale fills get dark text, dark fills get light text. */
    @Test
    fun textFollowsTheBackgroundNotTheThemeMode() {
        val onYellow = resolveContentStyle(Color(0xFFFFD238)).text
        val onNavy = resolveContentStyle(Color(0xFF0046BE)).text
        assertTrue(relativeLuminance(onYellow) < 0.2f, "浅黄底应配深色字")
        assertTrue(relativeLuminance(onNavy) > 0.8f, "深蓝底应配浅色字")
    }

    /** One fill must always resolve to one set of colours, or the same thing appears twice on screen looking different. */
    @Test
    fun sameBackgroundAlwaysResolvesTheSameWay() {
        backgrounds.values.forEach { bg ->
            assertTrue(resolveContentStyle(bg) == resolveContentStyle(bg))
        }
    }
}
