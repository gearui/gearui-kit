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
import com.gearui.foundation.border.BorderWidth

/**
 * PopoverTheme - bubble theme
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
 * PopoverPlacement - popup position
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
 * PopoverState - Popover state management
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
 * Remembers a Popover state
 */
@Composable
fun rememberPopoverState(): PopoverState {
    return remember { PopoverState() }
}

/**
 * Popover - popup bubble
 *
 * Positioned precisely through the GearUI Overlay system
 *
 * Features:
 * - 6 theme colours
 * - 12 placements
 * - optional arrow
 * - custom content
 * - tap outside to dismiss
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

    // Bounds of the trigger element
    var triggerBounds by remember { mutableStateOf<Rect?>(null) }

    // Background and text colour derived from the theme
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

    // Key the overlay so its lifecycle can be managed
    val isVisible = state.isVisible
    val bounds = triggerBounds

    // DisposableEffect drives the overlay lifecycle
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

    // Trigger element
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
 * Popover content layout
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

    // Arrow position follows the placement
    val isTop = placement in listOf(PopoverPlacement.TOP, PopoverPlacement.TOP_LEFT, PopoverPlacement.TOP_RIGHT)
    val isBottom = placement in listOf(PopoverPlacement.BOTTOM, PopoverPlacement.BOTTOM_LEFT, PopoverPlacement.BOTTOM_RIGHT)
    val isLeft = placement in listOf(PopoverPlacement.LEFT, PopoverPlacement.LEFT_TOP, PopoverPlacement.LEFT_BOTTOM)
    val isRight = placement in listOf(PopoverPlacement.RIGHT, PopoverPlacement.RIGHT_TOP, PopoverPlacement.RIGHT_BOTTOM)

    // Arrow alignment
    val arrowAlignment = when (placement) {
        PopoverPlacement.TOP_LEFT, PopoverPlacement.BOTTOM_LEFT -> Alignment.Start
        PopoverPlacement.TOP_RIGHT, PopoverPlacement.BOTTOM_RIGHT -> Alignment.End
        PopoverPlacement.LEFT_TOP, PopoverPlacement.RIGHT_TOP -> Alignment.Top
        PopoverPlacement.LEFT_BOTTOM, PopoverPlacement.RIGHT_BOTTOM -> Alignment.Bottom
        else -> Alignment.CenterHorizontally
    }

    if (isLeft || isRight) {
        // Horizontal layout (left / right)
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
        // Vertical layout (top / bottom)
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
                    horizontalPadding = Spacing.lg
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
                    horizontalPadding = Spacing.lg
                )
            }
        }
    }
}

/**
 * Popover body content
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
            .border(BorderWidth.thin, borderColor, OverlayDefaults.panelShape)
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
 * Arrow direction
 */
enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Popover arrow
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
 * Triangle Shape
 */
// Arrow tips are triangle geometry approximated with corner radii, not a
// surface radius, so they stay off the shape scale.
private fun TriangleShape(direction: ArrowDirection) = when (direction) {
    // shape-exempt: arrow tip geometry
    ArrowDirection.UP -> RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp,
        bottomStart = 50.dp, bottomEnd = 50.dp
    )
    // shape-exempt: arrow tip geometry
    ArrowDirection.DOWN -> RoundedCornerShape(
        topStart = 50.dp, topEnd = 50.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
    // shape-exempt: arrow tip geometry
    ArrowDirection.LEFT -> RoundedCornerShape(
        topStart = 0.dp, topEnd = 50.dp,
        bottomStart = 0.dp, bottomEnd = 50.dp
    )
    // shape-exempt: arrow tip geometry
    ArrowDirection.RIGHT -> RoundedCornerShape(
        topStart = 50.dp, topEnd = 0.dp,
        bottomStart = 50.dp, bottomEnd = 0.dp
    )
}

/**
 * Converts a PopoverPlacement into an OverlayPlacement
 */
private fun placementToOverlay(placement: PopoverPlacement): OverlayPlacement {
    return when (placement) {
        // Above
        PopoverPlacement.TOP_LEFT -> OverlayPlacement.TopLeft
        PopoverPlacement.TOP -> OverlayPlacement.TopCenter
        PopoverPlacement.TOP_RIGHT -> OverlayPlacement.TopRight
        // Below
        PopoverPlacement.BOTTOM_LEFT -> OverlayPlacement.BottomLeft
        PopoverPlacement.BOTTOM -> OverlayPlacement.BottomCenter
        PopoverPlacement.BOTTOM_RIGHT -> OverlayPlacement.BottomRight
        // Left
        PopoverPlacement.LEFT_TOP -> OverlayPlacement.LeftTop
        PopoverPlacement.LEFT -> OverlayPlacement.LeftCenter
        PopoverPlacement.LEFT_BOTTOM -> OverlayPlacement.LeftBottom
        // Right
        PopoverPlacement.RIGHT_TOP -> OverlayPlacement.RightTop
        PopoverPlacement.RIGHT -> OverlayPlacement.RightCenter
        PopoverPlacement.RIGHT_BOTTOM -> OverlayPlacement.RightBottom
    }
}

/**
 * CompositionLocal carrying the Popover text colour
 */
val LocalPopoverTextColor = compositionLocalOf { Color.Unspecified }

/**
 * Tooltip - simplified text hint
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
 * PopoverMenuItem - Popover menu item
 */
data class PopoverMenuItem(
    val label: String,
    val icon: (@Composable () -> Unit)? = null,
    val disabled: Boolean = false,
    val danger: Boolean = false,
    val onClick: () -> Unit
)

/**
 * PopoverMenu - Popover holding menu items
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
                                .height(BorderWidth.thin)
                                .background(colors.border.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        },
        trigger = trigger
    )
}
