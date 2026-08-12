package com.gearui.components.drawer

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.runtime.SafeAreaEdge

/**
 * DrawerPlacement - which edge the drawer sits on
 */
enum class DrawerPlacement {
    LEFT,
    RIGHT
}

/**
 * DrawerItem - one drawer list item
 *
 */
data class DrawerItem(
    val title: String,
    val icon: (@Composable () -> Unit)? = null,
    val content: (@Composable () -> Unit)? = null
)

private const val DRAWER_ANIMATION_DURATION = 300

/**
 * Drawer - side navigation drawer
 *
 *
 * Features:
 * - left or right side drawer
 * - slide in and out
 * - covers the full screen, status bar included
 * - optional title
 * - list items with icons
 * - optional bottom slot
 * - custom content
 * - scrim
 * - dismiss by tapping the scrim
 */
@Composable
fun Drawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placement: DrawerPlacement = DrawerPlacement.RIGHT,
    width: Dp = 280.dp,
    title: String? = null,
    titleWidget: (@Composable () -> Unit)? = null,
    items: List<DrawerItem>? = null,
    footer: (@Composable () -> Unit)? = null,
    showOverlay: Boolean = true,
    closeOnOverlayClick: Boolean = true,
    backgroundColor: Color? = null,
    bordered: Boolean = true,
    onItemClick: ((index: Int, item: DrawerItem) -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    val colors = Theme.colors
    val effectiveBackgroundColor = backgroundColor ?: colors.surface
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // Shared animation target
    val animationTarget = remember { mutableStateOf(false) }

    // Whether the overlay should be shown
    var shouldShowOverlay by remember { mutableStateOf(false) }

    // React to visible changes
    LaunchedEffect(visible) {
        if (visible) {
            // Showing: present the overlay first, then start the enter animation
            shouldShowOverlay = true
        } else {
            // Hiding: run the exit animation first
            animationTarget.value = false
            // Remove the overlay once the animation finishes
            kotlinx.coroutines.delay(DRAWER_ANIMATION_DURATION.toLong() + 50)
            shouldShowOverlay = false
        }
    }

    // Overlay presentation
    LaunchedEffect(shouldShowOverlay) {
        if (shouldShowOverlay && overlayId == null) {
            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = Color.Transparent,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = false
                    )
                ),
                onDismiss = onDismiss
            ) {
                DrawerOverlayContent(
                    animationTarget = animationTarget,
                    placement = placement,
                    width = width,
                    showOverlay = showOverlay,
                    closeOnOverlayClick = closeOnOverlayClick,
                    effectiveBackgroundColor = effectiveBackgroundColor,
                    title = title,
                    titleWidget = titleWidget,
                    items = items,
                    footer = footer,
                    bordered = bordered,
                    onItemClick = onItemClick,
                    customContent = content,
                    onDismiss = onDismiss,
                    modifier = modifier
                )
            }
            // Start the enter animation once the overlay is on screen
            kotlinx.coroutines.delay(16) // 等待一帧确保 Overlay 已渲染
            animationTarget.value = true
        } else if (!shouldShowOverlay && overlayId != null) {
            controller.dismiss(overlayId!!)
            overlayId = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            overlayId?.let { controller.dismiss(it) }
        }
    }
}

/**
 * DrawerOverlayContent - the drawer content inside the overlay
 *
 * Animation is driven through a shared MutableState.
 */
@Composable
private fun DrawerOverlayContent(
    animationTarget: MutableState<Boolean>,
    placement: DrawerPlacement,
    width: Dp,
    showOverlay: Boolean,
    closeOnOverlayClick: Boolean,
    effectiveBackgroundColor: Color,
    title: String?,
    titleWidget: (@Composable () -> Unit)?,
    items: List<DrawerItem>?,
    footer: (@Composable () -> Unit)?,
    bordered: Boolean,
    onItemClick: ((index: Int, item: DrawerItem) -> Unit)?,
    customContent: (@Composable () -> Unit)?,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    val colors = Theme.colors

    // Read the shared state
    val targetVisible = animationTarget.value

    // Scrim opacity animation
    val maskAlpha by animateFloatAsState(
        targetValue = if (targetVisible) 1f else 0f,
        animationSpec = tween(DRAWER_ANIMATION_DURATION),
        label = "maskAlpha"
    )

    // Drawer slide animation (0 = fully hidden, 1 = fully shown)
    val slideProgress by animateFloatAsState(
        targetValue = if (targetVisible) 1f else 0f,
        animationSpec = tween(DRAWER_ANIMATION_DURATION),
        label = "slideProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrim
        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(maskAlpha)
                    .background(OverlayDefaults.scrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (closeOnOverlayClick) {
                                onDismiss()
                            }
                        }
                    )
            )
        }

        // Drawer content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = when (placement) {
                DrawerPlacement.LEFT -> Alignment.CenterStart
                DrawerPlacement.RIGHT -> Alignment.CenterEnd
            }
        ) {
            // Offset
            val offsetX = when (placement) {
                DrawerPlacement.LEFT -> -width.value * (1f - slideProgress)
                DrawerPlacement.RIGHT -> width.value * (1f - slideProgress)
            }

            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .width(width)
                    .graphicsLayer {
                        translationX = offsetX * density
                    }
                    .background(effectiveBackgroundColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* 阻止点击穿透 */ }
                    )
            ) {
                if (customContent != null) {
                    customContent()
                } else {
                    DrawerContent(
                        title = title,
                        titleWidget = titleWidget,
                        items = items,
                    footer = footer,
                    bordered = bordered,
                    onItemClick = onItemClick,
                    onDismiss = onDismiss
                )
            }
            }
        }
    }
}

/**
 * DrawerContent - drawer content
 */
@Composable
private fun DrawerContent(
    title: String?,
    titleWidget: (@Composable () -> Unit)?,
    items: List<DrawerItem>?,
    footer: (@Composable () -> Unit)?,
    bordered: Boolean,
    onItemClick: ((index: Int, item: DrawerItem) -> Unit)?,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val topInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Top,
        consume = runtimeFlags.drawerConsumesVerticalSafeArea,
    )
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = runtimeFlags.drawerConsumesVerticalSafeArea,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topInset, bottom = bottomInset)
    ) {
        // A drawer without a title still keeps the same top bar height as the page NavBar, so the safe area and the body stay aligned
        if (titleWidget == null && title == null) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
            if (bordered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BorderWidth.hairline)
                        .background(colors.border)
                )
            }
        }

        // Title area
        if (titleWidget != null) {
            titleWidget()
        } else if (title != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = Spacing.lg),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = Typography.TitleLarge,
                    color = colors.foreground
                )
            }
            if (bordered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BorderWidth.hairline)
                        .background(colors.border)
                )
            }
        }

        // List area
        if (items != null && items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(items) { index, item ->
                    DrawerListItem(
                        item = item,
                        bordered = bordered && index < items.size - 1,
                        onClick = {
                            onItemClick?.invoke(index, item)
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Footer area
        if (footer != null) {
            if (bordered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BorderWidth.hairline)
                        .background(colors.border)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                footer()
            }
        }
    }
}

/**
 * DrawerListItem - drawer list row
 */
@Composable
private fun DrawerListItem(
    item: DrawerItem,
    bordered: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    Column {
            // Custom content wins when present
        if (item.content != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                item.content.invoke()
            }
        } else {
            // Default title + icon layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Icon
                if (item.icon != null) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        item.icon.invoke()
                    }
                }
                // Title
                Text(
                    text = item.title,
                    style = Typography.BodyLarge,
                    color = colors.foreground
                )
            }
        }
        // Divider
        if (bordered) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (item.icon != null) 52.dp else 16.dp)
                    .height(BorderWidth.hairline)
                    .background(colors.border)
            )
        }
    }
}

/**
 * DrawerWithHeader - drawer with a header (legacy API)
 */
@Composable
fun DrawerWithHeader(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    placement: DrawerPlacement = DrawerPlacement.LEFT,
    width: Dp = 280.dp,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = Theme.colors

    Drawer(
        visible = visible,
        onDismiss = onDismiss,
        placement = placement,
        width = width,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title bar
            if (header != null) {
                header()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        style = Typography.TitleLarge,
                        color = colors.foreground
                    )
                }
            }

            // Content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content()
            }

            // Footer area
            if (footer != null) {
                footer()
            }
        }
    }
}

/**
 * DrawerState - drawer state
 */
class DrawerState {
    var isOpen by mutableStateOf(false)
        private set

    fun open() {
        isOpen = true
    }

    fun close() {
        isOpen = false
    }

    fun toggle() {
        isOpen = !isOpen
    }
}

@Composable
fun rememberDrawerState(): DrawerState {
    return remember { DrawerState() }
}
