package com.gearui.i18n

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.posix.CLOCK_REALTIME
import platform.posix.clock_gettime
import platform.posix.localtime_r
import platform.posix.time_tVar
import platform.posix.timespec
import platform.posix.tm

internal actual fun currentEpochMillis(): Long = memScoped {
    val ts = alloc<timespec>()
    clock_gettime(CLOCK_REALTIME, ts.ptr)
    ts.tv_sec * 1000L + ts.tv_nsec / 1_000_000L
}

internal actual fun localMomentOf(epochMillis: Long): LocalMoment = memScoped {
    // No Foundation on HarmonyOS: localtime_r applies the device time zone.
    val seconds = alloc<time_tVar>()
    seconds.value = epochMillis / 1000
    val local = alloc<tm>()
    localtime_r(seconds.ptr, local.ptr)
    LocalMoment(local.tm_year + 1900, local.tm_mon + 1, local.tm_mday, local.tm_hour, local.tm_min)
}
