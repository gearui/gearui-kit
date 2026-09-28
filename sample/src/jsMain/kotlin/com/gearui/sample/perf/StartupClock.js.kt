package com.gearui.sample.perf

/** A browser tab has no process start to read; page timing belongs to the host page. */
actual fun processUptimeMillis(): Long? = null
