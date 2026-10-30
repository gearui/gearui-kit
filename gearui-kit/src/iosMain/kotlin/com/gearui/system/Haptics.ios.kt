package com.gearui.system

import platform.UIKit.UISelectionFeedbackGenerator

internal actual val platformHaptics: HapticFeedback? = object : HapticFeedback {
    // One generator, kept: a new one has to wake the motor, and the first tap lags.
    private val generator by lazy { UISelectionFeedbackGenerator() }

    override fun selectionChanged() {
        generator.prepare()
        generator.selectionChanged()
    }
}
