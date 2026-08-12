package com.gearui.overlay

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Popup docking strategy, relative to the anchor
 */
enum class OverlayPlacement {
    // Above
    TopLeft,        // 上左 - 左对齐，在上方
    TopCenter,      // 上中 - 居中，在上方
    TopRight,       // 上右 - 右对齐，在上方

    // Below
    BottomLeft,     // 下左 - 左对齐，在下方
    BottomCenter,   // 下中 - 居中，在下方
    BottomRight,    // 下右 - 右对齐，在下方

    // Left
    LeftTop,        // 左上 - 在左侧，顶部对齐
    LeftCenter,     // 左中 - 在左侧，垂直居中
    LeftBottom,     // 左下 - 在左侧，底部对齐

    // Right
    RightTop,       // 右上 - 在右侧，顶部对齐
    RightCenter,    // 右中 - 在右侧，垂直居中
    RightBottom,    // 右下 - 在右侧，底部对齐

    // Special
    Center,         // 屏幕居中（无 anchor）
    Fullscreen,     // 全屏
}

/**
 * Overlay dismissal policy
 *
 * Defines the conditions under which an overlay closes itself. This is a core
 * capability of the overlay runtime; all dismissal logic is declared here.
 *
 * Examples:
 * ```
 * // Select and Popover: anchored, dismissed by scrolling
 * OverlayDismissPolicy(
 *     outsideClick = true,
 *     scroll = true,
 *     anchorDetached = true
 * )
 *
 * // BottomSheet and ActionSheet: modal, not dismissed by scrolling
 * OverlayDismissPolicy(
 *     outsideClick = true,
 *     backPress = true
 * )
 *
 * // Toast and Snackbar: dismissed on a timer
 * OverlayDismissPolicy(
 *     timeoutMillis = 2000
 * )
 * ```
 */
@Immutable
data class OverlayDismissPolicy(
    /** Dismiss when tapping outside the overlay */
    val outsideClick: Boolean = false,

    /** Dismiss when the page scrolls; for anchored overlays */
    val scroll: Boolean = false,

    /** Dismiss on the back button */
    val backPress: Boolean = true,

    /** Dismiss on route change */
    val routeChange: Boolean = true,

    /** Auto-dismiss delay in milliseconds; null disables it */
    val timeoutMillis: Long? = null,

    /** Dismiss when the anchor element leaves the DOM */
    val anchorDetached: Boolean = false,
) {
    companion object {
        /** Default for Select, TreeSelect, Cascader and Popover */
        val Dropdown = OverlayDismissPolicy(
            outsideClick = true,
            scroll = true,
            backPress = true,
            anchorDetached = true
        )

        /** Default for BottomSheet and ActionSheet */
        val Sheet = OverlayDismissPolicy(
            outsideClick = true,
            backPress = true
        )

        /** Default for Dialog: modal, back button only */
        val Modal = OverlayDismissPolicy(
            outsideClick = false,
            backPress = true
        )

        /** Default for Toast and Snackbar: timed dismissal */
        fun toast(durationMillis: Long = 2000) = OverlayDismissPolicy(
            outsideClick = false,
            scroll = false,
            backPress = false,
            routeChange = false,
            timeoutMillis = durationMillis
        )

        /** Default for Tour: manual control, no auto-dismiss */
        val Manual = OverlayDismissPolicy(
            outsideClick = false,
            scroll = false,
            backPress = false,
            routeChange = false
        )
    }
}

/**
 * Overlay behaviour configuration
 */
data class OverlayOptions(
    /** Overlay placement */
    val placement: OverlayPlacement = OverlayPlacement.BottomLeft,

    /** X offset */
    val offsetX: Dp = 0.dp,

    /** Y offset */
    val offsetY: Dp = 4.dp,

    /** 是否为模态弹层（阻断底层交互） */
    val modal: Boolean = false,

    /** 遮罩颜色，null 表示无遮罩 */
    val maskColor: Color? = null,

    /** 层级 */
    val zIndex: Float = 0f,

    /** 空间不足时自动翻转方向 */
    val autoFlip: Boolean = true,

    /** 关闭策略（唯一关闭入口） */
    val dismissPolicy: OverlayDismissPolicy = OverlayDismissPolicy(),

    /** Fullscreen 模式下是否应用顶部安全区 */
    val safeAreaTop: Boolean = false,

    /** Fullscreen 模式下是否应用底部安全区 */
    val safeAreaBottom: Boolean = false,

    /** Fullscreen 模式下是否应用左侧安全区 */
    val safeAreaLeft: Boolean = false,

    /** Fullscreen 模式下是否应用右侧安全区 */
    val safeAreaRight: Boolean = false,

    /**
     * 内容之外的区域是否放行手势（仅 Fullscreen、非 modal 生效）。
     *
     * Fullscreen 默认铺一层全屏点击拦截层，非模态的通知横幅用它就会在整个展示期间
     * 冻结全屏交互 —— 用户点不动列表、按不了返回。置 true 时不铺该层，横幅本身仍可点，
     * 其余区域的滚动/点击照常落到下面的页面（微信式 in-app 横幅语义）。
     */
    val passThroughOutside: Boolean = false
)
