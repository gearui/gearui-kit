package com.gearui.sample.perf

import android.os.Process
import android.os.SystemClock

actual fun processUptimeMillis(): Long? =
    SystemClock.elapsedRealtime() - Process.getStartElapsedRealtime()
