package com.gearui.navigation

/**
 * 系统手势排除区域控制器。Android 上配 `Window.setSystemGestureExclusionRects` 把左边缘
 * 一条窄带从系统返回手势里排除，让 Navigator 的 `Modifier.swipeBack` 能接到 pointer
 * input；iOS 不需要（系统没有 predictive back 抢手势）。
 *
 * **架构演进说明**：spec §5.4.1 v1 阶段决定「让出 Android 系统手势」，所以
 * [setLeftEdgeExclusion] 那时是 no-op。v1.1 阶段把决策推翻为「Navigator 内的 route
 * 全自动获得右滑预览，跨平台一致」—— Android 端落地方案就是这个 exclusion rect。
 *
 * - 每个 `Activity` 启动时调一次 `register(activity)`；Navigator 内部按
 *   `canPop` / `handleBack` 自动 set / clear。
 * - Exclusion 宽度跟 `Navigator` 内部 `SwipeBackConfig.edgeWidthDp` 一致（默认 96dp）。
 * - Android Q (API 29) 才有 `setSystemGestureExclusionRects`；老版本 silently 失败。
 */
expect object SystemGestureExclusion {
    /** 把左边 [widthDp] 排除出系统返回手势区。 */
    fun setLeftEdgeExclusion(widthDp: Float)

    /** 清掉之前 set 的排除区域，恢复系统手势全幅可用。 */
    fun clearLeftEdgeExclusion()
}
