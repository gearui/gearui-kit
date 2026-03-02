package com.gearui.overlay

import androidx.compose.runtime.Immutable
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Popup 停靠位置策略（对齐 anchor）
 */
enum class GearOverlayPlacement {
    // 上方
    TopLeft,        // 上左 - 左对齐，在上方
    TopCenter,      // 上中 - 居中，在上方
    TopRight,       // 上右 - 右对齐，在上方

    // 下方
    BottomLeft,     // 下左 - 左对齐，在下方
    BottomCenter,   // 下中 - 居中，在下方
    BottomRight,    // 下右 - 右对齐，在下方

    // 左侧
    LeftTop,        // 左上 - 在左侧，顶部对齐
    LeftCenter,     // 左中 - 在左侧，垂直居中
    LeftBottom,     // 左下 - 在左侧，底部对齐

    // 右侧
    RightTop,       // 右上 - 在右侧，顶部对齐
    RightCenter,    // 右中 - 在右侧，垂直居中
    RightBottom,    // 右下 - 在右侧，底部对齐

    // 特殊
    Center,         // 屏幕居中（无 anchor）
    Fullscreen,     // 全屏

    // 向后兼容别名
    @Deprecated("Use TopLeft", ReplaceWith("TopLeft"))
    TopStart,
    @Deprecated("Use BottomLeft", ReplaceWith("BottomLeft"))
    BottomStart
}

/**
 * Overlay 关闭策略
 *
 * 定义 Overlay 在什么条件下自动关闭
 * 这是 Overlay Runtime 的核心能力，所有关闭逻辑统一在此声明
 *
 * 使用示例：
 * ```
 * // Select/Popover 类（锚点定位，滚动关闭）
 * OverlayDismissPolicy(
 *     outsideClick = true,
 *     scroll = true,
 *     anchorDetached = true
 * )
 *
 * // BottomSheet/ActionSheet 类（模态，不因滚动关闭）
 * OverlayDismissPolicy(
 *     outsideClick = true,
 *     backPress = true
 * )
 *
 * // Toast/Snackbar 类（定时自动消失）
 * OverlayDismissPolicy(
 *     timeoutMillis = 2000
 * )
 * ```
 */
@Immutable
data class OverlayDismissPolicy(
    /** 点击 Overlay 外部区域关闭 */
    val outsideClick: Boolean = false,

    /** 页面滚动时关闭（适用于锚点定位的弹层） */
    val scroll: Boolean = false,

    /** 按返回键关闭 */
    val backPress: Boolean = true,

    /** 路由切换时关闭 */
    val routeChange: Boolean = true,

    /** 定时自动关闭（毫秒），null 表示不自动关闭 */
    val timeoutMillis: Long? = null,

    /** 锚点元素从 DOM 移除时关闭 */
    val anchorDetached: Boolean = false,
) {
    companion object {
        /** Select/TreeSelect/Cascader/Popover 默认策略 */
        val Dropdown = OverlayDismissPolicy(
            outsideClick = true,
            scroll = true,
            backPress = true,
            anchorDetached = true
        )

        /** BottomSheet/ActionSheet 默认策略 */
        val Sheet = OverlayDismissPolicy(
            outsideClick = true,
            backPress = true
        )

        /** Dialog 默认策略（模态，只响应返回键） */
        val Modal = OverlayDismissPolicy(
            outsideClick = false,
            backPress = true
        )

        /** Toast/Snackbar 默认策略（定时消失） */
        fun toast(durationMillis: Long = 2000) = OverlayDismissPolicy(
            outsideClick = false,
            scroll = false,
            backPress = false,
            routeChange = false,
            timeoutMillis = durationMillis
        )

        /** Tour 默认策略（手动控制，不自动关闭） */
        val Manual = OverlayDismissPolicy(
            outsideClick = false,
            scroll = false,
            backPress = false,
            routeChange = false
        )
    }
}

/**
 * Overlay 行为配置
 */
data class GearOverlayOptions(
    /** 弹层位置 */
    val placement: GearOverlayPlacement = GearOverlayPlacement.BottomLeft,

    /** X 轴偏移 */
    val offsetX: Dp = 0.dp,

    /** Y 轴偏移 */
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
    val safeAreaRight: Boolean = false
)
