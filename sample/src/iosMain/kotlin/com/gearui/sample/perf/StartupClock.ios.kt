package com.gearui.sample.perf

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import kotlinx.cinterop.value
import platform.darwin.CTL_KERN
import platform.darwin.KERN_PROC
import platform.darwin.KERN_PROC_PID
import platform.darwin.sysctl
import platform.posix.getpid
import platform.posix.gettimeofday
import platform.posix.size_tVar
import platform.posix.timeval

/**
 * The kernel's record of when this process started, against the wall clock now.
 *
 * `kinfo_proc` is not exported to Kotlin/Native on iOS, so the struct is read as raw
 * bytes. Its first member is `kp_proc`, whose first member is the `p_un` union, whose
 * `__p_starttime` is a `struct timeval` — so the start time sits at offset 0: a 64-bit
 * `tv_sec` followed by a 32-bit `tv_usec`. The buffer is larger than the struct.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun processUptimeMillis(): Long? = memScoped {
    val mib = allocArray<IntVar>(4)
    mib[0] = CTL_KERN
    mib[1] = KERN_PROC
    mib[2] = KERN_PROC_PID
    mib[3] = getpid()
    val bufferSize = 1024
    val buffer = allocArray<ByteVar>(bufferSize)
    val size = alloc<size_tVar>()
    size.value = bufferSize.convert()
    if (sysctl(mib, 4u, buffer, size.ptr, null, 0u) != 0) return@memScoped null
    val startSec = buffer.reinterpret<LongVar>().pointed.value
    val startUsec = (buffer + 8L)!!.reinterpret<IntVar>().pointed.value.toLong()
    if (startSec <= 0L) return@memScoped null
    val now = alloc<timeval>()
    gettimeofday(now.ptr, null)
    val nowMs = now.tv_sec.toLong() * 1000L + now.tv_usec.toLong() / 1000L
    nowMs - (startSec * 1000L + startUsec / 1000L)
}
