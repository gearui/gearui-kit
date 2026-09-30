package com.gearui.components.picker

import com.gearui.components.calendar.CalendarDate
import kotlin.test.*

class PickerConstraintsTest {
    @Test fun snappingUsesDensityAndClampsEnds() {
        assertEquals(0, wheelSnapIndex(0, 21, 44f, 3))
        assertEquals(1, wheelSnapIndex(0, 23, 44f, 3))
        assertEquals(0, wheelSnapIndex(0, 43, 88f, 3))
        assertEquals(1, wheelSnapIndex(0, 45, 88f, 3))
        assertEquals(2, wheelSnapIndex(2, 88, 88f, 3))
    }
    @Test fun formattedTimeIsUsedForSelectionAndConfirmation() {
        assertEquals("09时30分45秒", pickerFormattedTime(PickerTime(9, 30, 45), TimePickerPrecision.SECOND, "HH时mm分ss秒"))
    }
    @Test fun duplicateLabelsKeepStableIdentityAndChangingParentResetsDescendants() {
        val a = PickerOption("a", "Same", listOf(PickerOption("a1", "Child")))
        val b = PickerOption("b", "Same", listOf(PickerOption("b1", "Child")))
        assertEquals(listOf("b", "b1"), pickerPath(listOf(a, b), listOf("b", "a1"), 2).map { it.value })
    }
    @Test fun boundsIncludeLeapDayAndOnlyRealDates() {
        val columns = DateColumns(DatePickerConstraints(CalendarDate(2024, 2, 28), CalendarDate(2024, 3, 1)))
        assertEquals(listOf(2024), columns.years)
        assertEquals(listOf(2, 3), columns.months(2024))
        assertEquals(listOf(28, 29), columns.days(2024, 2))
        assertEquals(listOf(1), columns.days(2024, 3))
    }
    @Test fun removedParentDoesNotSelectADescendantFromAnUnrelatedBranch() {
        val root = PickerOption("new", "New", listOf(PickerOption("first", "First"), PickerOption("shared", "Shared")))
        assertEquals(listOf("new", "first"), pickerPath(listOf(root), listOf("removed", "shared"), 2).map { it.value })
    }
    @Test fun crossingYearAndMonthAndEmptyFilter() {
        val cross = DateColumns(DatePickerConstraints(CalendarDate(2025, 12, 31), CalendarDate(2026, 1, 1)))
        assertEquals(listOf(2025, 2026), cross.years)
        assertEquals(listOf(12), cross.months(2025))
        assertEquals(listOf(1), cross.months(2026))
        val empty = DateColumns(DatePickerConstraints(CalendarDate(2026, 1, 1), CalendarDate(2026, 1, 5), filter = { false }))
        assertTrue(empty.years.isEmpty())
        assertNull(empty.nearest(2026, 1, 3))
        assertFailsWith<IllegalArgumentException> { DatePickerConstraints(CalendarDate(2025, 2, 29)) }
    }
    @Test fun precisionAndCustomFormatsKeepCanonicalValues() {
        val date = CalendarDate(2026, 9, 29)
        assertEquals("2026", pickerDateText(date, DatePickerPrecision.YEAR, "YYYY-MM-DD"))
        assertEquals("2026-09", pickerDateText(date, DatePickerPrecision.MONTH, "YYYY-MM-DD"))
        assertEquals("29/09/2026", pickerDateText(date, DatePickerPrecision.DAY, "DD/MM/YYYY"))
    }
    @Test fun timeStepBoundsAndEmptyResult() {
        val columns = TimeColumns(TimePickerConstraints(PickerTime(9, 10), PickerTime(9, 45), minuteStep = 15))
        assertEquals(listOf(9), columns.hours)
        assertEquals(listOf(15, 30, 45), columns.minutes(9))
        assertTrue(TimeColumns(TimePickerConstraints(filter = { false })).hours.isEmpty())
        assertEquals("09:30:45", pickerTimeText(PickerTime(9, 30, 45), TimePickerPrecision.SECOND))
    }
    @Test fun aCoarseSlotIsOfferedOnlyWhenItStartsWithinTheBounds() {
        // 09:00 would fall before a 09:30 minimum, so the hour picker starts at 10:00.
        val hour = TimeColumns(TimePickerConstraints(PickerTime(9, 30), PickerTime(11), TimePickerPrecision.HOUR))
        assertEquals(listOf(10, 11), hour.hours)
        val minute = TimeColumns(TimePickerConstraints(PickerTime(9, 10, 30), PickerTime(9, 12), TimePickerPrecision.MINUTE))
        assertEquals(listOf(11, 12), minute.minutes(9))
    }

    @Test fun theDefaultRangeCostsHundredsOfChecksNotTensOfThousands() {
        var checks = 0
        val columns = DateColumns(DatePickerConstraints(filter = { checks++; true }))
        assertEquals(201, columns.years.size)
        columns.months(2026)
        columns.days(2026, 9)
        assertTrue(checks < 1_000, "filter ran $checks times")
    }
    @Test fun turningAColumnKeepsTheOtherPartsAsCloseAsPossible() {
        val columns = DateColumns(DatePickerConstraints.Default)
        assertEquals(CalendarDate(2025, 2, 28), columns.nearest(2025, 2, 29)) // leap day in a common year
        assertEquals(CalendarDate(2026, 2, 28), columns.nearest(2026, 2, 31)) // month end
        val bounded = DateColumns(DatePickerConstraints(min = CalendarDate(2026, 9, 30)))
        assertEquals(CalendarDate(2026, 9, 30), bounded.nearest(2026, 9, 1))
        assertEquals(CalendarDate(2026, 9, 30), bounded.nearest(2020, 1, 1))
    }
    @Test fun storedValuesAreReadLeniently() {
        val columns = DateColumns(DatePickerConstraints.Default)
        assertEquals(CalendarDate(2024, 3, 5), initialPickerDate(columns, "2024-3-5", "YYYY-MM-DD"))
        assertEquals(CalendarDate(2024, 3, 5), initialPickerDate(columns, "05/03/2024", "DD/MM/YYYY"))
        assertEquals(CalendarDate(2026, 9, 1), initialPickerDate(DateColumns(DatePickerConstraints(precision = DatePickerPrecision.MONTH)), "2026-09", "YYYY-MM-DD"))
        assertEquals(PickerTime(9, 30), initialPickerTime(TimeColumns(TimePickerConstraints.Default), "09:30:00", "HH:mm"))
        assertEquals(PickerTime(9, 30), initialPickerTime(TimeColumns(TimePickerConstraints(minuteStep = 15)), "9:37", "HH:mm"))
    }
    @Test fun constraintsBuiltInlineAreEqual() {
        val weekdays: (CalendarDate) -> Boolean = { it.day != 1 }
        assertEquals(DatePickerConstraints(min = CalendarDate(2026, 1, 1)), DatePickerConstraints(min = CalendarDate(2026, 1, 1)))
        assertEquals(DatePickerConstraints(filter = weekdays), DatePickerConstraints(filter = weekdays))
        assertEquals(TimePickerConstraints(minuteStep = 5), TimePickerConstraints(minuteStep = 5))
    }
    @Test fun aShallowBranchIsCompleteAtItsLeaf() {
        val hongKong = PickerOption("hk", "香港")
        val zhejiang = PickerOption("zj", "浙江", listOf(PickerOption("hz", "杭州", listOf(PickerOption("xh", "西湖区")))))
        val shallow = pickerPath(listOf(hongKong, zhejiang), listOf("hk"), 3)
        assertEquals(listOf("hk"), shallow.map { it.value })
        assertTrue(pickerPathComplete(shallow, 3))
        assertTrue(pickerPathComplete(pickerPath(listOf(zhejiang), listOf("zj", "hz", "xh"), 3), 3))
        assertFalse(pickerPathComplete(pickerPath(listOf(zhejiang), emptyList(), 2).take(1), 2))
    }
    @Test fun duplicateValuesFromBadDataKeepTheFirst() {
        val first = PickerOption("x", "First")
        val second = PickerOption("x", "Second")
        assertEquals(listOf("First"), pickerPath(listOf(first, second), listOf("x"), 1).map { it.label })
        assertEquals(listOf(first), listOf(first, second).distinctByValue())
    }
}
