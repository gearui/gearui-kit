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
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing

/**
 * DrawerPlacement - 抽屉位置
 */
enum class DrawerPlacement {
    LEFT,
    RIGHT
}

/**
 * DrawerItem - 抽屉列表项数据
 *
 */
data class DrawerItem(
    val title: String,
    val icon: (@Composable () -> Unit)? = null,
    val content: (@Composable () -> Unit)? = null
)

private const val DRAWER_ANIMATION_DURATION = 300

/**
 * Drawer - 抽屉导航组件
 *
 *
 * 特性：
 * - 侧边抽屉（左/右）
 * - 滑入滑出动画
 * - 全屏覆盖（包括状态栏）
 * - 支持标题
 * - 支持列表项（带图标）
 * - 支持底部插槽
 * - 支持自定义内容
 * - 遮罩层
 * - 点击遮罩关闭
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

    // 共享的动画目标状态
    val animationTarget = remember { mutableStateOf(false) }

    // Overlay 是否应该显示
    var shouldShowOverlay by remember { mutableStateOf(false) }

    // 响应 visible 变化
    LaunchedEffect(visible) {
        if (visible) {
            // 显示：先显示 Overlay，稍后触发入场动画
            shouldShowOverlay = true
        } else {
            // 关闭：先触发退场动画
            animationTarget.value = false
            // 等待动画完成后移除 Overlay
            kotlinx.coroutines.delay(DRAWER_ANIMATION_DURATION.toLong() + 50)
            shouldShowOverlay = false
        }
    }

    // 管理 Overlay 显示
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
            // Overlay 显示后触发入场动画
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
 * DrawerOverlayContent - Drawer 在 Overlay 中的内容
 *
 * 使用共享的 MutableState 来控制动画
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

    // 读取共享状态
    val targetVisible = animationTarget.value

    // 遮罩透明度动画
    val maskAlpha by animateFloatAsState(
        targetValue = if (targetVisible) 1f else 0f,
        animationSpec = tween(DRAWER_ANIMATION_DURATION),
        label = "maskAlpha"
    )

    // 抽屉滑动动画 (0 = 完全隐藏, 1 = 完全显示)
    val slideProgress by animateFloatAsState(
        targetValue = if (targetVisible) 1f else 0f,
        animationSpec = tween(DRAWER_ANIMATION_DURATION),
        label = "slideProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 遮罩层
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

        // 抽屉内容
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = when (placement) {
                DrawerPlacement.LEFT -> Alignment.CenterStart
                DrawerPlacement.RIGHT -> Alignment.CenterEnd
            }
        ) {
            // 计算偏移量
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
 * DrawerContent - 抽屉内容组件
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
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val configuration = LocalConfiguration.current
    val topInset = if (runtimeFlags.unifiedSafeAreaPipeline) {
        if (runtimeFlags.drawerConsumesVerticalSafeArea) runtimeEnvironment.safeArea.top else 0.dp
    } else {
        configuration.safeAreaInsets.top.dp
    }
    val bottomInset = if (runtimeFlags.unifiedSafeAreaPipeline) {
        if (runtimeFlags.drawerConsumesVerticalSafeArea) runtimeEnvironment.safeArea.bottom else 0.dp
    } else {
        configuration.safeAreaInsets.bottom.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topInset, bottom = bottomInset)
    ) {
        // 无标题抽屉也保留与页面 NavBar 一致的顶部栏高度，避免顶部安全区与正文错位
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
                        .height(0.5.dp)
                        .background(colors.border)
                )
            }
        }

        // 标题区域
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
                        .height(0.5.dp)
                        .background(colors.border)
                )
            }
        }

        // 列表区域
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

        // 底部区域
        if (footer != null) {
            if (bordered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
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
 * DrawerListItem - 抽屉列表项
 */
@Composable
private fun DrawerListItem(
    item: DrawerItem,
    bordered: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    Column {
        // 如果有自定义内容，使用自定义内容
        if (item.content != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                item.content.invoke()
            }
        } else {
            // 默认的标题+图标布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // 图标
                if (item.icon != null) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        item.icon.invoke()
                    }
                }
                // 标题
                Text(
                    text = item.title,
                    style = Typography.BodyLarge,
                    color = colors.foreground
                )
            }
        }
        // 分割线
        if (bordered) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (item.icon != null) 52.dp else 16.dp)
                    .height(0.5.dp)
                    .background(colors.border)
            )
        }
    }
}

/**
 * DrawerWithHeader - 带标题的抽屉（兼容旧 API）
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
            // 标题栏
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

            // 内容区
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content()
            }

            // 底部区域
            if (footer != null) {
                footer()
            }
        }
    }
}

/**
 * DrawerState - 抽屉状态管理
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
