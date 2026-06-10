package com.gearui.navigation

/**
 * iOS 端 `SystemGestureExclusion` 实现。
 *
 * iOS 系统没有 Android 13+ predictive back gesture 这种「OS 抢左边缘手势」的行为，
 * Navigator 的 `Modifier.swipeBack` 直接接 pointer input 即可工作。所以这里全 no-op。
 */
actual object SystemGestureExclusion {
    actual fun setLeftEdgeExclusion(widthDp: Float) { /* no-op on iOS */ }
    actual fun clearLeftEdgeExclusion() { /* no-op on iOS */ }
}
