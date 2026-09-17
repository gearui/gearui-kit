package com.gearui.foundation.control

import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.Motions
import kotlin.math.sqrt
import com.tencent.kuikly.compose.animation.core.Easing

// Reanimated withTiming's default curve, used by the reference selection controls.
internal val selectionTimingEasing = Easing { fraction ->
    if (fraction < .5f) 2f * fraction * fraction
    else 1f - 2f * (1f - fraction) * (1f - fraction)
}

internal val switchDampingRatio: Float
    get() = FeedbackDefaults.switchDamping /
        (2f * sqrt(FeedbackDefaults.switchStiffness * FeedbackDefaults.switchMass))

internal fun switchSpringStiffness(motion: Motion): Float {
    val speed = Motions.Default.normal.toFloat() / motion.normal.coerceAtLeast(1)
    return FeedbackDefaults.switchStiffness / FeedbackDefaults.switchMass * speed * speed
}

internal fun switchThumbPosition(progress: Float, width: Float, thumb: Float, inset: Float): Float =
    inset + (width - thumb - inset * 2).coerceAtLeast(0f) * progress.coerceIn(0f, 1f)
