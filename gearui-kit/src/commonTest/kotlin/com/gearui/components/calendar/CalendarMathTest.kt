package com.gearui.components.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for the pure calendar math behind the Calendar component.
 * Weekday expectations are checked against real-world dates.
 */
class CalendarMathTest {

    @Test
    fun leapYearFollowsTheGregorianRule() {
        assertTrue(CalendarMath.isLeapYear(2024))
        assertTrue(CalendarMath.isLeapYear(2000))
        assertFalse(CalendarMath.isLeapYear(1900))
        assertFalse(CalendarMath.isLeapYear(2023))
    }

    @Test
    fun daysInMonthMatchesTheCalendar() {
        assertEquals(31, CalendarMath.daysInMonth(2024, 1))
        assertEquals(29, CalendarMath.daysInMonth(2024, 2))
        assertEquals(28, CalendarMath.daysInMonth(2023, 2))
        assertEquals(30, CalendarMath.daysInMonth(2024, 4))
        assertEquals(31, CalendarMath.daysInMonth(2024, 12))
    }

    @Test
    fun firstWeekdayOfMonthMatchesKnownDates() {
        // 2023-01-01 was a Sunday, 2024-01-01 a Monday, 2024-02-01 a Thursday.
        assertEquals(0, CalendarMath.firstWeekdayOfMonth(2023, 1))
        assertEquals(1, CalendarMath.firstWeekdayOfMonth(2024, 1))
        assertEquals(4, CalendarMath.firstWeekdayOfMonth(2024, 2))
        // 2024-09-01 was also a Sunday.
        assertEquals(0, CalendarMath.firstWeekdayOfMonth(2024, 9))
    }

    @Test
    fun leadingBlanksRespectTheFirstDayOfWeek() {
        // January 2024 starts on a Monday (index 1).
        assertEquals(1, CalendarMath.leadingBlanks(2024, 1, firstDayOfWeek = 0))
        assertEquals(0, CalendarMath.leadingBlanks(2024, 1, firstDayOfWeek = 1))
        assertEquals(6, CalendarMath.leadingBlanks(2024, 1, firstDayOfWeek = 2))
    }

    @Test
    fun monthNavigationRollsAcrossYears() {
        assertEquals(CalendarDate(2023, 12, 1), CalendarMath.previousMonth(CalendarDate(2024, 1, 1)))
        assertEquals(CalendarDate(2024, 2, 1), CalendarMath.nextMonth(CalendarDate(2024, 1, 15)))
        assertEquals(CalendarDate(2025, 1, 1), CalendarMath.nextMonth(CalendarDate(2024, 12, 1)))
        assertEquals(CalendarDate(2024, 6, 1), CalendarMath.previousMonth(CalendarDate(2024, 7, 31)))
    }

    @Test
    fun minMaxBoundsDisableCells() {
        val date = CalendarDate(2024, 1, 15)
        val disabledByMin = CalendarMath.selectType(
            date = date,
            type = CalendarType.Single,
            selectedDate = date,
            selectedDates = emptyList(),
            rangeStart = null,
            rangeEnd = null,
            minDate = CalendarDate(2024, 1, 20),
            maxDate = null
        )
        assertEquals(DateSelectType.Disabled, disabledByMin)

        val disabledByMax = CalendarMath.selectType(
            date = date,
            type = CalendarType.Single,
            selectedDate = date,
            selectedDates = emptyList(),
            rangeStart = null,
            rangeEnd = null,
            minDate = null,
            maxDate = CalendarDate(2024, 1, 10)
        )
        assertEquals(DateSelectType.Disabled, disabledByMax)
    }

    @Test
    fun singleAndMultipleSelectionStates() {
        val selected = CalendarDate(2024, 1, 15)
        val other = CalendarDate(2024, 1, 16)

        assertEquals(
            DateSelectType.Selected,
            CalendarMath.selectType(selected, CalendarType.Single, selected, emptyList(), null, null, null, null)
        )
        assertEquals(
            DateSelectType.Empty,
            CalendarMath.selectType(other, CalendarType.Single, selected, emptyList(), null, null, null, null)
        )
        assertEquals(
            DateSelectType.Selected,
            CalendarMath.selectType(selected, CalendarType.Multiple, null, listOf(selected), null, null, null, null)
        )
    }

    @Test
    fun rangeSelectionStatesCoverStartCentreEnd() {
        val start = CalendarDate(2024, 1, 10)
        val centre = CalendarDate(2024, 1, 12)
        val end = CalendarDate(2024, 1, 15)
        val outside = CalendarDate(2024, 1, 20)

        assertEquals(
            DateSelectType.Start,
            CalendarMath.selectType(start, CalendarType.Range, null, emptyList(), start, end, null, null)
        )
        assertEquals(
            DateSelectType.Centre,
            CalendarMath.selectType(centre, CalendarType.Range, null, emptyList(), start, end, null, null)
        )
        assertEquals(
            DateSelectType.End,
            CalendarMath.selectType(end, CalendarType.Range, null, emptyList(), start, end, null, null)
        )
        assertEquals(
            DateSelectType.Empty,
            CalendarMath.selectType(outside, CalendarType.Range, null, emptyList(), start, end, null, null)
        )
        // A range start without an end yet renders as Start.
        assertEquals(
            DateSelectType.Start,
            CalendarMath.selectType(start, CalendarType.Range, null, emptyList(), start, null, null, null)
        )
    }
}
