package com.gearui.sample.perf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos

/**
 * Milliseconds since this process started, or null where the platform cannot say.
 *
 * Measured from the process's own start time rather than from a launch command, so
 * the number is the same on every platform and does not include a tool's latency.
 */
expect fun processUptimeMillis(): Long?

/**
 * Time from process start to the first frame of the home screen's content.
 *
 * `am start -W` stops at the host activity's first frame, which for a Kuikly app can
 * still be empty; what a user waits for is the list. The mark is taken once per
 * process, at the start of the second frame after the home screen composes — the
 * first frame draws it, the second starts only once that one is done.
 */
object StartupMark {
    /** Process start → the Kuikly page object is created: loading, host and runtime start. */
    var pageMillis by mutableStateOf<Long?>(null)
        private set

    /** Process start → first frame of the home content; the difference from [pageMillis] is the kit's share. */
    var contentMillis by mutableStateOf<Long?>(null)
        private set
    private var taken = false

    /** Call from the page's constructor. */
    fun pageCreated() {
        if (pageMillis == null) pageMillis = processUptimeMillis()
    }

    internal fun record() {
        if (taken) return
        taken = true
        contentMillis = processUptimeMillis()
    }
}

/** Call from the home screen. Records the mark once, after its content has drawn. */
@Composable
fun MarkStartupContent() {
    LaunchedEffect(Unit) {
        withFrameNanos { }
        withFrameNanos { }
        StartupMark.record()
    }
}
