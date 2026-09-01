package com.gearui.components.calendar

/**
 * Platform clock behind [CalendarDate.today].
 *
 * Kuikly commonMain has no cross-platform date source, so the wall clock is
 * injected per target. Internal on purpose: the public entry point stays
 * [CalendarDate.today].
 */
internal expect fun currentCalendarDate(): CalendarDate
