package com.gearui.system

/** A device's light "moved one step" tap. */
interface HapticFeedback {
    fun selectionChanged()
}

/**
 * The kit's haptic feedback, for "the selection moved one step" — an index bar crossing a
 * letter, a wheel crossing a value. One strength only: the platform's lightest selection
 * tap, which also follows the user's system setting for touch feedback.
 *
 * iOS needs nothing. An Android host sets [host] once, as it registers its status bar
 * controller: `Haptics.host = AndroidHapticFeedback(context)`. Without one, nothing
 * happens — a device without a motor, or a host that did not register, must never make a
 * scroll throw.
 */
object Haptics {
    var host: HapticFeedback? = null

    fun selectionChanged() {
        runCatching { (host ?: platformHaptics)?.selectionChanged() }
    }
}

/** What the platform does on its own; null where the host has to provide it. */
internal expect val platformHaptics: HapticFeedback?
