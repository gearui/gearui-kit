package com.gearui.foundation.color

import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 背景 → 内容色的解析契约。
 *
 * 品牌自己决定气泡底色（微信绿 / Telegram 蓝 / 闲鱼黄 / Weey 黄 …），我们**永远不为了
 * 链接去改品牌底色**，只负责在给定底色上选出可读的正文与链接。所以验收必须覆盖各种
 * 底色，而不是只测手头这一两个品牌。
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

    /** 正文和链接对背景都必须过普通正文的 4.5:1——这条没有例外，也没有「接近就算」。 */
    @Test
    fun bodyAndLinkAreReadableOnEveryBackground() {
        backgrounds.forEach { (name, bg) ->
            val s = resolveContentStyle(bg)
            assertTrue(s.textOnBackground >= 4.5f, "$name：正文对背景仅 ${s.textOnBackground}")
            assertTrue(s.linkOnBackground >= 4.5f, "$name：链接对背景仅 ${s.linkOnBackground}")
        }
    }

    /**
     * 色差不够时必须**如实置位**，让上层去加非颜色提示。
     *
     * 这条挡的是「悄悄选一个看不清的蓝，然后当作没事」——那正是这套解析器要取代的做法。
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

    /** 判据是实际对比度，不是「浅色模式/深色模式」：浅底一律深字，深底一律浅字。 */
    @Test
    fun textFollowsTheBackgroundNotTheThemeMode() {
        val onYellow = resolveContentStyle(Color(0xFFFFD238)).text
        val onNavy = resolveContentStyle(Color(0xFF0046BE)).text
        assertTrue(relativeLuminance(onYellow) < 0.2f, "浅黄底应配深色字")
        assertTrue(relativeLuminance(onNavy) > 0.8f, "深蓝底应配浅色字")
    }

    /** 同一个底色必须永远解析出同一套颜色——否则同屏会出现两种「同一个东西」。 */
    @Test
    fun sameBackgroundAlwaysResolvesTheSameWay() {
        backgrounds.values.forEach { bg ->
            assertTrue(resolveContentStyle(bg) == resolveContentStyle(bg))
        }
    }
}
