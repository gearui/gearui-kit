package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.ui.Modifier

/**
 * ScrollPhysics - 统一滚动手感层
 *
 * 全局只需要改这里即可统一:
 * - iOS 弹性回弹
 * - Android EdgeEffect
 * - 滚动阻尼
 * - 惯性参数
 *
 * 未来扩展:
 * - Custom friction
 * - Snap scrolling
 * - Overscroll effects
 */
sealed class ScrollPhysics {

    abstract fun modifier(): Modifier

    /** 跟随平台默认行为 */
    object Platform : ScrollPhysics() {
        override fun modifier() = Modifier
    }

    /** iOS 风格弹性回弹 */
    object IOS : ScrollPhysics() {
        override fun modifier() = Modifier
        // TODO: 添加 iOS bounce 效果
        // .overscroll(orientation = Orientation.Vertical)
    }

    /** Android 风格 EdgeEffect */
    object Android : ScrollPhysics() {
        override fun modifier() = Modifier
        // TODO: 添加 Android edge glow 效果
    }

    /** 关闭所有效果 */
    object None : ScrollPhysics() {
        override fun modifier() = Modifier
    }
}
