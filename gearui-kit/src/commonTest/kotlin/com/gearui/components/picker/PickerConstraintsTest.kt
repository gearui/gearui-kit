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
        assertFailsWith<IllegalArgumentException> { pickerPath(listOf(a, a), emptyList(), 1) }
    }
    @Test fun boundsIncludeLeapDayAndOnlyRealDates() {
        val dates = DatePickerConstraints(CalendarDate(2024, 2, 28), CalendarDate(2024, 3, 1)).dates()
        assertEquals(listOf(CalendarDate(2024, 2, 28), CalendarDate(2024, 2, 29), CalendarDate(2024, 3, 1)), dates)
    }
    @Test fun removedParentDoesNotSelectADescendantFromAnUnrelatedBranch() {
        val root = PickerOption("new", "New", listOf(PickerOption("first", "First"), PickerOption("shared", "Shared")))
        assertEquals(listOf("new", "first"), pickerPath(listOf(root), listOf("removed", "shared"), 2).map { it.value })
    }
    @Test fun crossingYearAndMonthAndEmptyFilter() {
        assertEquals(2, DatePickerConstraints(CalendarDate(2025, 12, 31), CalendarDate(2026, 1, 1)).dates().size)
        assertTrue(DatePickerConstraints(CalendarDate(2026, 1, 1), CalendarDate(2026, 1, 5), filter = { false }).dates().isEmpty())
        assertFailsWith<IllegalArgumentException> { DatePickerConstraints(CalendarDate(2025, 2, 29)) }
    }
    @Test fun precisionAndCustomFormatsKeepCanonicalValues() {
        val date = CalendarDate(2026, 9, 29)
        assertEquals("2026", pickerDateText(date, DatePickerPrecision.YEAR, "YYYY-MM-DD"))
        assertEquals("2026-09", pickerDateText(date, DatePickerPrecision.MONTH, "YYYY-MM-DD"))
        assertEquals("29/09/2026", pickerDateText(date, DatePickerPrecision.DAY, "DD/MM/YYYY"))
    }
    @Test fun timeStepBoundsAndEmptyResult() {
        val times = TimePickerConstraints(PickerTime(9, 10), PickerTime(9, 45), minuteStep = 15).times()
        assertEquals(listOf(PickerTime(9, 15), PickerTime(9, 30), PickerTime(9, 45)), times)
        assertTrue(TimePickerConstraints(filter = { false }).times().isEmpty())
        assertEquals("09:30:45", pickerTimeText(PickerTime(9, 30, 45), TimePickerPrecision.SECOND))
    }
    @Test fun coarseTimePrecisionKeepsBucketsThatIntersectTheBounds() {
        assertEquals(listOf(PickerTime(9)),
            TimePickerConstraints(PickerTime(9, 30), PickerTime(9, 45), TimePickerPrecision.HOUR).times())
        assertEquals(listOf(PickerTime(9, 10)),
            TimePickerConstraints(PickerTime(9, 10, 30), PickerTime(9, 10, 45), TimePickerPrecision.MINUTE).times())
    }
}
