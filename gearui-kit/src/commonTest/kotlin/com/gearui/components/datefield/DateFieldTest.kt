package com.gearui.components.datefield

import com.gearui.components.calendar.CalendarDate
import com.gearui.components.picker.*
import kotlin.test.*

class DateFieldTest {
    @Test fun dateOrderAndReducedPrecision() {
        assertEquals(listOf(0, 1, 2), dateSegmentOrder("{year}/{month}/{day}"))
        assertEquals(listOf(1, 2, 0), dateSegmentOrder("{month}/{day}/{year}"))
        assertEquals(listOf(1, 0), dateSegmentOrder("{month}/{day}/{year}").filter { it <= DatePickerPrecision.MONTH.ordinal })
        assertEquals(CalendarDate(2026, 1, 1), parseDateParts(listOf("2026", "", ""), DatePickerPrecision.YEAR))
    }
    @Test fun leapDaysIncompleteAndInvalidFields() {
        assertEquals(CalendarDate(2024, 2, 29), parseDateParts(listOf("2024", "2", "29"), DatePickerPrecision.DAY))
        assertNull(parseDateParts(listOf("2025", "2", "29"), DatePickerPrecision.DAY))
        assertNull(parseDateParts(listOf("2024", "", ""), DatePickerPrecision.DAY))
        assertNull(parseDateParts(listOf("2024", "13", "1"), DatePickerPrecision.DAY))
    }
    @Test fun clockBoundsAndPrecision() {
        assertEquals(PickerTime(9), parseTimeParts(listOf("9"), TimePickerPrecision.HOUR))
        assertEquals(PickerTime(23, 59, 59), parseTimeParts(listOf("23", "59", "59"), TimePickerPrecision.SECOND))
        assertNull(parseTimeParts(listOf("24", "0"), TimePickerPrecision.MINUTE))
        assertNull(parseTimeParts(listOf("12", ""), TimePickerPrecision.MINUTE))
    }
}
