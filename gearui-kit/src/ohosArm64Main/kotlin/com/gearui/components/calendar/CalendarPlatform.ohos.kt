package com.gearui.components.calendar

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.localtime_r
import platform.posix.time
import platform.posix.time_tVar
import platform.posix.tm

internal actual fun currentCalendarDate(): CalendarDate = memScoped {
    // HarmonyOS has no Foundation; go through POSIX. localtime_r applies the
    // device time zone, which is what "today" means on a calendar. tm_year is
    // years since 1900 and tm_mon is zero-based, so both are offset here.
    val now = alloc<time_tVar>()
    time(now.ptr)
    val local = alloc<tm>()
    localtime_r(now.ptr, local.ptr)
    CalendarDate(
        year = local.tm_year + 1900,
        month = local.tm_mon + 1,
        day = local.tm_mday
    )
}
