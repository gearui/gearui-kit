package com.gearui.components.popover

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import kotlinx.coroutines.delay
import com.gearui.foundation.elevation.Elevation
import com.gearui.overlay.OverlayDefaults

/**
 * PopoverTheme - 气泡主题
 */
enum class PopoverTheme {
    DARK,       // 深色主题
    LIGHT,      // 浅色主题
    BRAND,      // 品牌色主题
    SUCCESS,    // 成功主题
    WARNING,    // 警告主题
    ERROR       // 错误主题
}

/**
 * PopoverPlacement - 弹出位置
 */
enum class PopoverPlacement {
    TOP_LEFT,       // 上左
    TOP,            // 上中
    TOP_RIGHT,      // 上右
    RIGHT_TOP,      // 右上
    RIGHT,          // 右中
    RIGHT_BOTTOM,   // 右下
    BOTTOM_RIGHT,   // 下右
    BOTTOM,         // 下中
    BOTTOM_LEFT,    // 下左
    LEFT_BOTTOM,    // 左下
    LEFT,           // 左中
    LEFT_TOP        // 左上
}

/**
 * PopoverState - Popover 状态管理
 */
@Stable
class PopoverState {
    var isVisible by mutableStateOf(false)
        internal set

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    fun toggle() {
        isVisible = !isVisible
    }
}

/**
 * 记住 Popover 状态
 */
@Composable
fun rememberPopoverState(): PopoverState {
    return remember { PopoverState() }
}

/**
 * Popover - 气泡弹出框
 *
 * 使用 GearUI Overlay 系统实现精确定位
 *
 * 特性：
 * - 6种主题颜色
 * - 12种弹出位置
 * - 可选箭头显示
 * - 自定义内容
 * - 点击外部关闭
 */
@Composable
fun Popover(
    state: PopoverState,
    modifier: Modifier = Modifier,
    placement: PopoverPlacement = PopoverPlacement.BOTTOM,
    theme: PopoverTheme = PopoverTheme.LIGHT,
    showArrow: Boolean = false,
    offset: Dp = 8.dp,
    closeOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
    trigger: @Composable (onClick: () -> Unit) -> Unit
) {
    val colors = Theme.colors
    val overlay = rememberOverlay()

    // 触发元素的边界
    var triggerBounds by remember { mutableStateOf<Rect?>(null) }

    // 根据主题获取背景色和文字颜色
    val backgroundColor = remember(theme, colors) {
        when (theme) {
            PopoverTheme.DARK -> colors.foreground
            PopoverTheme.LIGHT -> colors.surface
            PopoverTheme.BRAND -> colors.primary
            PopoverTheme.SUCCESS -> colors.success
            PopoverTheme.WARNING -> colors.warning
            PopoverTheme.ERROR -> colors.destructive
        }
    }

    val textColor = remember(theme, colors) {
        when (theme) {
            PopoverTheme.DARK -> colors.primaryForeground
            PopoverTheme.LIGHT -> colors.foreground
            PopoverTheme.BRAND -> colors.primaryForeground
            PopoverTheme.SUCCESS -> colors.primaryForeground
            PopoverTheme.WARNING -> colors.foreground
            PopoverTheme.ERROR -> colors.primaryForeground
        }
    }

    val borderColor = remember(theme, colors) {
        when (theme) {
            PopoverTheme.LIGHT -> colors.border
            else -> Color.Transparent
        }
    }

    // 使用 key 来管理 overlay 的生命周期
    val isVisible = state.isVisible
    val bounds = triggerBounds

    // 使用 DisposableEffect 来管理 overlay
    if (isVisible && bounds != null) {
        val currentPlacement = placement
        val currentOffset = offset
        val currentCloseOnClickOutside = closeOnClickOutside
        val currentBackgroundColor = backgroundColor
        val currentTextColor = textColor
        val currentShowArrow = showArrow

        DisposableEffect(bounds, currentPlacement, currentOffset) {
            val overlayId = overlay.show(
                anchorBounds = bounds,
                options = OverlayOptions(
                    placement = placementToOverlay(currentPlacement),
                    offsetX = when (currentPlacement) {
                        PopoverPlacement.LEFT, PopoverPlacement.LEFT_TOP, PopoverPlacement.LEFT_BOTTOM -> -currentOffset
                        PopoverPlacement.RIGHT, PopoverPlacement.RIGHT_TOP, PopoverPlacement.RIGHT_BOTTOM -> currentOffset
                        else -> 0.dp
                    },
                    offsetY = when (currentPlacement) {
                        PopoverPlacement.TOP, PopoverPlacement.TOP_LEFT, PopoverPlacement.TOP_RIGHT -> -currentOffset
                        PopoverPlacement.BOTTOM, PopoverPlacement.BOTTOM_LEFT, PopoverPlacement.BOTTOM_RIGHT -> currentOffset
                        else -> 0.dp
                    },
                    modal = false,
                    maskColor = null,
                    dismissPolicy = if (currentCloseOnClickOutside) {
                        OverlayDismissPolicy.Dropdown.copy(outsideClick = true)
                    } else {
                        OverlayDismissPolicy(
                            outsideClick = false,
                            scroll = false,
                            backPress = true,
                            routeChange = true,
                            anchorDetached = true
                        )
                    }
                ),
                onDismiss = {
                    state.isVisible = false
                }
            ) {
                PopoverContent(
                    placement = currentPlacement,
                    backgroundColor = currentBackgroundColor,
                    textColor = currentTextColor,
                    borderColor = borderColor,
                    showArrow = currentShowArrow,
                    content = content
                )
            }

            onDispose {
                overlay.dismiss(overlayId)
            }
        }
    }

    // 触发元素
    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                triggerBounds = coordinates.boundsInRoot()
            }
    ) {
        trigger {
            state.toggle()
        }
    }
}

/**
 * Popover 内容布局
 */
@Composable
private fun PopoverContent(
    placement: PopoverPlacement,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color,
    showArrow: Boolean,
    content: @Composable () -> Unit
) {
    val arrowSize = 8.dp

    // 根据 placement 决定箭头位置
    val isTop = placement in listOf(PopoverPlacement.TOP, PopoverPlacement.TOP_LEFT, PopoverPlacement.TOP_RIGHT)
    val isBottom = placement in listOf(PopoverPlacement.BOTTOM, PopoverPlacement.BOTTOM_LEFT, PopoverPlacement.BOTTOM_RIGHT)
    val isLeft = placement in listOf(PopoverPlacement.LEFT, PopoverPlacement.LEFT_TOP, PopoverPlacement.LEFT_BOTTOM)
    val isRight = placement in listOf(PopoverPlacement.RIGHT, PopoverPlacement.RIGHT_TOP, PopoverPlacement.RIGHT_BOTTOM)

    // 箭头对齐
    val arrowAlignment = when (placement) {
        PopoverPlacement.TOP_LEFT, PopoverPlacement.BOTTOM_LEFT -> Alignment.Start
        PopoverPlacement.TOP_RIGHT, PopoverPlacement.BOTTOM_RIGHT -> Alignment.End
        PopoverPlacement.LEFT_TOP, PopoverPlacement.RIGHT_TOP -> Alignment.Top
        PopoverPlacement.LEFT_BOTTOM, PopoverPlacement.RIGHT_BOTTOM -> Alignment.Bottom
        else -> Alignment.CenterHorizontally
    }

    if (isLeft || isRight) {
        // 横向布局 (左/右)
        Row(
            verticalAlignment = when (arrowAlignment) {
                Alignment.Top -> Alignment.Top
                Alignment.Bottom -> Alignment.Bottom
                else -> Alignment.CenterVertically
            }
        ) {
            if (isRight && showArrow) {
                PopoverArrow(
                    direction = ArrowDirection.LEFT,
                    color = backgroundColor,
                    size = arrowSize
                )
            }

            PopoverBody(
                backgroundColor = backgroundColor,
                textColor = textColor,
                borderColor = borderColor,
                content = content
            )

            if (isLeft && showArrow) {
                PopoverArrow(
                    direction = ArrowDirection.RIGHT,
                    color = backgroundColor,
                    size = arrowSize
                )
            }
        }
    } else {
        // 纵向布局 (上/下)
        Column(
            horizontalAlignment = when (arrowAlignment) {
                Alignment.Start -> Alignment.Start
                Alignment.End -> Alignment.End
                else -> Alignment.CenterHorizontally
            }
        ) {
            if (isBottom && showArrow) {
                PopoverArrow(
                    direction = ArrowDirection.UP,
                    color = backgroundColor,
                    size = arrowSize,
                    horizontalPadding = 16.dp
                )
            }

            PopoverBody(
                backgroundColor = backgroundColor,
                textColor = textColor,
                borderColor = borderColor,
                content = content
            )

            if (isTop && showArrow) {
                PopoverArrow(
                    direction = ArrowDirection.DOWN,
                    color = backgroundColor,
                    size = arrowSize,
                    horizontalPadding = 16.dp
                )
            }
        }
    }
}

/**
 * Popover 主体内容
 */
@Composable
private fun PopoverBody(
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    val shapes = Theme.shapes

    Box(
        modifier = Modifier
            .shadow(Elevation.raised, OverlayDefaults.panelShape)
            .clip(OverlayDefaults.panelShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, OverlayDefaults.panelShape)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        CompositionLocalProvider(
            LocalPopoverTextColor provides textColor
        ) {
            content()
        }
    }
}

/**
 * 箭头方向
 */
enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Popover 箭头
 */
@Composable
private fun PopoverArrow(
    direction: ArrowDirection,
    color: Color,
    size: Dp,
    horizontalPadding: Dp = 0.dp
) {
    val width = when (direction) {
        ArrowDirection.UP, ArrowDirection.DOWN -> size * 2
        ArrowDirection.LEFT, ArrowDirection.RIGHT -> size
    }
    val height = when (direction) {
        ArrowDirection.UP, ArrowDirection.DOWN -> size
        ArrowDirection.LEFT, ArrowDirection.RIGHT -> size * 2
    }

    Box(
        modifier = Modifier
            .padding(horizontal = horizontalPadding)
            .size(width = width, height = height)
            .background(color, shape = TriangleShape(direction))
    )
}

/**
 * 三角形 Shape
 */
private fun TriangleShape(direction: ArrowDirection) = when (direction) {
    ArrowDirection.UP -> RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp,
        bottomStart = 50.dp, bottomEnd = 50.dp
    )
    ArrowDirection.DOWN -> RoundedCornerShape(
        topStart = 50.dp, topEnd = 50.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
    ArrowDirection.LEFT -> RoundedCornerShape(
        topStart = 0.dp, topEnd = 50.dp,
        bottomStart = 0.dp, bottomEnd = 50.dp
    )
    ArrowDirection.RIGHT -> RoundedCornerShape(
        topStart = 50.dp, topEnd = 0.dp,
        bottomStart = 50.dp, bottomEnd = 0.dp
    )
}

/**
 * 将 PopoverPlacement 转换为 OverlayPlacement
 */
private fun placementToOverlay(placement: PopoverPlacement): OverlayPlacement {
    return when (placement) {
        // 上方
        PopoverPlacement.TOP_LEFT -> OverlayPlacement.TopLeft
        PopoverPlacement.TOP -> OverlayPlacement.TopCenter
        PopoverPlacement.TOP_RIGHT -> OverlayPlacement.TopRight
        // 下方
        PopoverPlacement.BOTTOM_LEFT -> OverlayPlacement.BottomLeft
        PopoverPlacement.BOTTOM -> OverlayPlacement.BottomCenter
        PopoverPlacement.BOTTOM_RIGHT -> OverlayPlacement.BottomRight
        // 左侧
        PopoverPlacement.LEFT_TOP -> OverlayPlacement.LeftTop
        PopoverPlacement.LEFT -> OverlayPlacement.LeftCenter
        PopoverPlacement.LEFT_BOTTOM -> OverlayPlacement.LeftBottom
        // 右侧
        PopoverPlacement.RIGHT_TOP -> OverlayPlacement.RightTop
        PopoverPlacement.RIGHT -> OverlayPlacement.RightCenter
        PopoverPlacement.RIGHT_BOTTOM -> OverlayPlacement.RightBottom
    }
}

/**
 * Popover 文字颜色的 CompositionLocal
 */
val LocalPopoverTextColor = compositionLocalOf { Color.Unspecified }

/**
 * Tooltip - 简化的文本提示
 */
@Composable
fun Tooltip(
    text: String,
    state: PopoverState,
    modifier: Modifier = Modifier,
    placement: PopoverPlacement = PopoverPlacement.BOTTOM,
    theme: PopoverTheme = PopoverTheme.DARK,
    autoDismissMillis: Long = 1500L,
    trigger: @Composable (onClick: () -> Unit) -> Unit
) {
    LaunchedEffect(state.isVisible, autoDismissMillis) {
        if (state.isVisible && autoDismissMillis > 0) {
            delay(autoDismissMillis)
            if (state.isVisible) {
                state.hide()
            }
        }
    }

    Popover(
        state = state,
        modifier = modifier,
        placement = placement,
        theme = theme,
        closeOnClickOutside = true,
        content = {
            Text(
                text = text,
                style = Typography.BodySmall,
                color = LocalPopoverTextColor.current
            )
        },
        trigger = { _ ->
            trigger {
                state.toggle()
            }
        }
    )
}

/**
 * PopoverMenuItem - Popover 菜单项
 */
data class PopoverMenuItem(
    val label: String,
    val icon: (@Composable () -> Unit)? = null,
    val disabled: Boolean = false,
    val danger: Boolean = false,
    val onClick: () -> Unit
)

/**
 * PopoverMenu - 带菜单项的 Popover
 */
@Composable
fun PopoverMenu(
    state: PopoverState,
    items: List<PopoverMenuItem>,
    modifier: Modifier = Modifier,
    placement: PopoverPlacement = PopoverPlacement.BOTTOM,
    theme: PopoverTheme = PopoverTheme.LIGHT,
    trigger: @Composable (onClick: () -> Unit) -> Unit
) {
    val colors = Theme.colors

    Popover(
        state = state,
        modifier = modifier,
        placement = placement,
        theme = theme,
        showArrow = true,
        content = {
            Column(
                modifier = Modifier.width(160.dp)
            ) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .clickable(enabled = !item.disabled) {
                                item.onClick()
                                state.hide()
                            }
                            .padding(vertical = Spacing.md, horizontal = Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.icon != null) {
                            item.icon.invoke()
                            Spacer(modifier = Modifier.width(Spacing.sm))
                        }

                        Text(
                            text = item.label,
                            style = Typography.BodyMedium,
                            color = when {
                                item.disabled -> colors.mutedForeground
                                item.danger -> colors.destructive
                                else -> LocalPopoverTextColor.current
                            }
                        )
                    }

                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colors.border.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        },
        trigger = trigger
    )
}
