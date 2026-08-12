package com.gearui.foundation.animation

/**
 * GearUI animation system
 *
 * One place for every component's animation spec, so motion stays consistent
 *
 * Principles:
 * - Immediate feedback uses short durations (80-150ms)
 * - State changes use medium durations (200-300ms)
 * - Ease curves rather than linear, so motion reads as natural
 * - Restraint: avoid animating for its own sake
 */
object Animation {

    // ============ Durations ============

    /**
     * Instant - 40ms
     * For: the smallest visual acknowledgements, such as a ripple starting
     */
    const val DURATION_INSTANT = 40

    /**
     * Fast - 80ms
     * For: immediate feedback like a button press or a switch toggle
     */
    const val DURATION_FAST = 80

    /**
     * Standard - 150ms
     * For: fades, opacity changes, small movements
     */
    const val DURATION_NORMAL = 150

    /**
     * Medium - 200ms
     * For: card expansion, list item swipes
     */
    const val DURATION_MEDIUM = 200

    /**
     * Slow - 300ms
     * For: page transitions, drawer open and close
     */
    const val DURATION_SLOW = 300

    /**
     * Very slow - 500ms
     * For: complex multi-stage sequences
     */
    const val DURATION_VERY_SLOW = 500

    // ============ Easing curves ============

    /**
     * Standard easing - Ease
     * The common case; suits most situations
     */
    const val EASING_STANDARD = "ease"

    /**
     * Ease In
     * For: elements leaving the screen
     */
    const val EASING_EASE_IN = "ease-in"

    /**
     * Ease Out
     * For: elements entering the screen
     */
    const val EASING_EASE_OUT = "ease-out"

    /**
     * Ease In Out
     * For: elements moving within the screen
     */
    const val EASING_EASE_IN_OUT = "ease-in-out"

    /**
     * Linear
     * For: constant-speed motion such as spinners and progress bars
     */
    const val EASING_LINEAR = "linear"

    /**
     * Spring
     * For: interactions that should feel physical (iOS style)
     */
    const val EASING_SPRING = "spring"

    // ============ Preset animation specs ============

    /**
     * Button press
     * - Duration: 80ms
     * - Curve: Ease Out
     * - Effect: scale to 0.98
     */
    object Press {
        const val duration = DURATION_FAST
        const val easing = EASING_EASE_OUT
        const val scaleTarget = 0.98f
    }

    /**
     * Fade
     * - Duration: 150ms
     * - Curve: Ease In Out
     */
    object Fade {
        const val duration = DURATION_NORMAL
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * Scale
     * - Duration: 200ms
     * - Curve: Ease Out
     */
    object Scale {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_OUT
    }

    /**
     * Slide
     * - Duration: 200ms
     * - Curve: Ease In Out
     */
    object Slide {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * Rotate
     * - Duration: 300ms
     * - Curve: Linear
     */
    object Rotate {
        const val duration = DURATION_SLOW
        const val easing = EASING_LINEAR
    }

    /**
     * Spring
     * - Duration: 500ms
     * - Curve: Spring
     */
    object Bounce {
        const val duration = DURATION_VERY_SLOW
        const val easing = EASING_SPRING
    }

    /**
     * Expand / collapse
     * - Duration: 300ms
     * - Curve: Ease In Out
     */
    object Expand {
        const val duration = DURATION_SLOW
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * Ripple
     * - Duration: 200ms
     * - Curve: Ease Out
     */
    object Ripple {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_OUT
        const val initialAlpha = 0.2f
        const val maxRadius = 300f
    }

    /**
     * Loading
     * - Duration: 1000ms, looping
     * - Curve: Linear
     */
    object Loading {
        const val duration = 1000
        const val easing = EASING_LINEAR
    }

    // ============ Derived durations ============

    /**
     * Scales a duration by travel distance.
     *
     * @param distance travel distance in dp
     * @param baseDistance reference distance in dp
     * @param baseDuration reference duration in ms
     * @return the scaled duration
     */
    fun calculateDurationByDistance(
        distance: Float,
        baseDistance: Float = 100f,
        baseDuration: Int = DURATION_MEDIUM
    ): Int {
        val ratio = (distance / baseDistance).coerceIn(0.5f, 2.0f)
        return (baseDuration * ratio).toInt()
    }

    /**
     * Scales a duration by element size.
     *
     * @param size element size
     * @param baseSize reference size
     * @param baseDuration reference duration
     * @return the scaled duration
     */
    fun calculateDurationBySize(
        size: Float,
        baseSize: Float = 100f,
        baseDuration: Int = DURATION_MEDIUM
    ): Int {
        val ratio = (size / baseSize).coerceIn(0.8f, 1.5f)
        return (baseDuration * ratio).toInt()
    }
}

/**
 * Animation delay presets
 */
object AnimationDelay {

    /**
     * No delay
     */
    const val NONE = 0

    /**
     * Very short - 50ms
     */
    const val TINY = 50

    /**
     * Short - 100ms
     */
    const val SHORT = 100

    /**
     * Medium - 200ms
     */
    const val MEDIUM = 200

    /**
     * Long - 300ms
     */
    const val LONG = 300

    /**
     * Staggered delay for a list item.
     *
     * @param index item index
     * @param baseDelay base delay in ms
     * @return the staggered delay
     */
    fun calculateStaggerDelay(index: Int, baseDelay: Int = 30): Int {
        return index * baseDelay
    }
}

/**
 * Animation composition strategies
 */
object AnimationCombination {

    /**
     * Run in sequence
     */
    const val SEQUENTIAL = "sequential"

    /**
     * Run in parallel
     */
    const val PARALLEL = "parallel"

    /**
     * Run staggered
     */
    const val STAGGER = "stagger"
}
