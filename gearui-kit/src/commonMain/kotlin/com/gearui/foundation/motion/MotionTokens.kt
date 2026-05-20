package com.gearui.foundation.motion

/**
 * GearUI motion duration scale (milliseconds).
 *
 * Scale (see `docs/TOKEN_FREEZE_DECISIONS.md` Decision 4):
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
object MotionTokens {
    const val instant: Int = 0
    const val fast: Int = 100
    const val normal: Int = 150
    const val slow: Int = 200
    const val emphasized: Int = 250
}
