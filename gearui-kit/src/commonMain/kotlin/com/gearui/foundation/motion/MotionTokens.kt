package com.gearui.foundation.motion

/**
 * GearUI motion duration scale (milliseconds).
 *
 * Scale (see `docs/DESIGN_SYSTEM.md` §5):
 *
 *   instant    = 0    — no animation, immediate state change
 *   fast       = 100  — micro-interactions: button press feedback, icon toggle
 *   normal     = 150  — default for most state changes (tab select, focus ring)
 *   slow       = 200  — overlay / dialog reveal, content swap
 *   emphasized = 250  — bottom sheet drag/drop settle, major scene transition
 *
 * Stored as `Int` milliseconds for direct use with `tween(durationMillis = ...)`.
 *
 * Components must not invent one-off durations. Use a token from this scale,
 * or expose a duration through the component's own `XxxTokens` class.
 * Overlay entrance/exit animations are owned by Runtime, which selects
 * `slow` or `emphasized` by default.
 */
data class Motion(
    val instant: Int = 0,
    val fast: Int = 100,
    val normal: Int = 150,
    val slow: Int = 200,
    val emphasized: Int = 250,
)

/** Built-in motion scales. */
object Motions {
    val Default = Motion()
}

/** Scales a reference duration by the theme's motion speed.
 * Setting normal to zero suppresses press animation without suppressing state feedback.
 */
internal fun Motion.feedbackDuration(reference: Int): Int =
    (reference.toDouble() * normal.coerceAtLeast(0) / Motions.Default.normal)
        .coerceAtMost(Int.MAX_VALUE.toDouble()).toInt()
