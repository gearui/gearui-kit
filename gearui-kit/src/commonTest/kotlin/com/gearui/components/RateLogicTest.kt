package com.gearui.components

import com.gearui.components.rate.nextRateValue
import com.gearui.components.rate.rateText
import com.gearui.components.rate.starFraction
import kotlin.test.Test
import kotlin.test.assertEquals

/** How a score maps to stars, and what a tap on a star means. */
class RateLogicTest {

    @Test
    fun wholeStarsRoundAFractionalScore() {
        // 4.3 with whole stars is four stars, not four and a sliver.
        assertEquals(1f, starFraction(4.3f, 3, allowHalf = false))
        assertEquals(0f, starFraction(4.3f, 4, allowHalf = false))
        assertEquals(1f, starFraction(3.8f, 3, allowHalf = false))
    }

    @Test
    fun halfStarsSnapToTheNearestHalf() {
        assertEquals(0.5f, starFraction(4.3f, 4, allowHalf = true))
        assertEquals(1f, starFraction(3.8f, 3, allowHalf = true))
        assertEquals(0f, starFraction(3.1f, 3, allowHalf = true))
        assertEquals(0.5f, starFraction(3.5f, 3, allowHalf = true))
    }

    @Test
    fun starsBeforeAndAfterTheScoreAreFullAndEmpty() {
        assertEquals(1f, starFraction(3.5f, 0, allowHalf = true))
        assertEquals(1f, starFraction(3.5f, 2, allowHalf = true))
        assertEquals(0f, starFraction(3.5f, 4, allowHalf = true))
    }

    @Test
    fun tappingTheLeadingHalfGivesAHalfStar() {
        assertEquals(4.5f, nextRateValue(3f, index = 4, atStart = true, allowHalf = true, allowClear = false))
        assertEquals(5f, nextRateValue(3f, index = 4, atStart = false, allowHalf = true, allowClear = false))
        // Without allowHalf the tapped half makes no difference.
        assertEquals(5f, nextRateValue(3f, index = 4, atStart = true, allowHalf = false, allowClear = false))
    }

    @Test
    fun tappingTheCurrentValueClearsOnlyWhenAllowed() {
        assertEquals(0f, nextRateValue(3f, index = 2, atStart = false, allowHalf = false, allowClear = true))
        assertEquals(3f, nextRateValue(3f, index = 2, atStart = false, allowHalf = false, allowClear = false))
        assertEquals(0f, nextRateValue(2.5f, index = 2, atStart = true, allowHalf = true, allowClear = true))
    }

    @Test
    fun theLabelPrefersTheDescriptionAndDropsATrailingZero() {
        val texts = listOf("很差", "较差", "一般", "满意", "很满意")
        assertEquals("满意", rateText(3.5f, texts))
        assertEquals("一般", rateText(3f, texts))
        assertEquals("3", rateText(3f, null))
        assertEquals("3.5", rateText(3.5f, null))
        assertEquals("0", rateText(0f, texts))
    }
}
