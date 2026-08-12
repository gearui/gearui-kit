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

    /** whether it is modal (blocks interaction underneath) */
    val modal: Boolean = false,

    /** scrim colour; null means no scrim */
    val maskColor: Color? = null,

    /** z-order */
    val zIndex: Float = 0f,

    /** flip direction automatically when space runs out */
    val autoFlip: Boolean = true,

    /** dismissal policy (the single dismissal entry point) */
    val dismissPolicy: OverlayDismissPolicy = OverlayDismissPolicy(),

    /** whether the top safe area applies in Fullscreen mode */
    val safeAreaTop: Boolean = false,

    /** whether the bottom safe area applies in Fullscreen mode */
    val safeAreaBottom: Boolean = false,

    /** whether the left safe area applies in Fullscreen mode */
    val safeAreaLeft: Boolean = false,

    /** whether the right safe area applies in Fullscreen mode */
    val safeAreaRight: Boolean = false,

    /**
     * Whether gestures pass through outside the content (Fullscreen and non-modal only).
     *
     * Fullscreen lays a fullscreen tap interceptor by default, and a non-modal notification banner using it would freeze the
     * whole screen for as long as it is shown - the user could not scroll the list or press back. Set to true and that layer
     * is not laid: the banner stays tappable while scrolls and taps elsewhere reach the page below (WeChat-style in-app banner).
     */
    val passThroughOutside: Boolean = false
)
