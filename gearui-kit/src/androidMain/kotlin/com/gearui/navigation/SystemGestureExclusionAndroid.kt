package com.gearui.navigation

import android.app.Activity
import android.graphics.Rect
import android.os.Build

/**
 * Android 端 `SystemGestureExclusion` 实现。
 *
 * 业务方在 `Activity.onCreate` 调一次 [register]；Navigator 内部根据 `canPop` 自动
 * 调用 [setLeftEdgeExclusion] / [clearLeftEdgeExclusion]。
 *
 * 实现细节：
 * - `View.setSystemGestureExclusionRects` 仅在 Android Q (API 29) 及以上有效；老版本静默忽略。
 * - exclusion 高度铺满 decorView；宽度由 [widthDp] × 屏幕 density 换算成 px。
 * - 单 Activity 单 set；多 Navigator 共存时按调用顺序覆盖（v1 业务只有一个 Navigator）。
 * - 系统对 exclusion 总长有 200dp 上限，96dp 在限度内安全。
 */
actual object SystemGestureExclusion {

    private var activity: Activity? = null

    /** 业务方在 `MainActivity.onCreate` 调一次。 */
    fun register(activity: Activity) {
        this.activity = activity
    }

    /** Activity 被销毁前调，避免 leak。 */
    fun unregister() {
        this.activity = null
    }

    actual fun setLeftEdgeExclusion(widthDp: Float) {
        val act = activity ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        act.runOnUiThread {
            val view = act.window.decorView
            val density = act.resources.displayMetrics.density
            val widthPx = (widthDp * density).toInt()
            val height = view.height.takeIf { it > 0 } ?: act.resources.displayMetrics.heightPixels
            view.systemGestureExclusionRects = listOf(Rect(0, 0, widthPx, height))
        }
    }

    actual fun clearLeftEdgeExclusion() {
        val act = activity ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        act.runOnUiThread {
            act.window.decorView.systemGestureExclusionRects = emptyList()
        }
    }
}
