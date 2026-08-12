package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.ui.Modifier

/**
 * ScrollPhysics - unified scroll feel
 *
 * Changing this one place changes it globally:
 * - iOS elastic bounce
 * - Android EdgeEffect
 * - scroll damping
 * - inertia parameters
 *
 * Future extensions:
 * - Custom friction
 * - Snap scrolling
 * - Overscroll effects
 */
sealed class ScrollPhysics {

    abstract fun modifier(): Modifier

    /** follow the platform default */
    object Platform : ScrollPhysics() {
        override fun modifier() = Modifier
    }

    /** iOS-style elastic bounce */
    object IOS : ScrollPhysics() {
        override fun modifier() = Modifier
        // TODO: add the iOS bounce effect
        // .overscroll(orientation = Orientation.Vertical)
    }

    /** Android-style EdgeEffect */
    object Android : ScrollPhysics() {
        override fun modifier() = Modifier
        // TODO: add the Android edge glow effect
    }

    /** all effects off */
    object None : ScrollPhysics() {
        override fun modifier() = Modifier
    }
}
