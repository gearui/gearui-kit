package com.gearui.components.swipecell

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectHorizontalDragGestures
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.IntOffset
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.swipecell.SwipeCellDefaults
import com.gearui.foundation.swipecell.SwipeCellTokens
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * SwipeCellDirection - 滑动方向
 */
enum class SwipeCellDirection {
    LEFT,   // 左滑（显示右侧操作）
    RIGHT,  // 右滑（显示左侧操作）
    NONE    // 无滑动
}

/**
 * SwipeCellActionTheme - 操作按钮主题
 */
enum class SwipeCellActionTheme {
    PRIMARY,    // 主要色
    DANGER,     // 危险色
    WARNING,    // 警告色
    SUCCESS     // 成功色
}

/**
 * SwipeCellAction - 滑动操作项
 */
data class SwipeCellAction(
    val label: String,
    val theme: SwipeCellActionTheme = SwipeCellActionTheme.PRIMARY,
    val icon: String? = null,
    val iconPosition: SwipeCellIconPosition = SwipeCellIconPosition.LEFT,
    val flex: Int = 1,
    val onClick: () -> Unit
)

/**
 * SwipeCellIconPosition - 图标位置
 */
enum class SwipeCellIconPosition {
    LEFT,       // 图标在左边（横向）
    TOP         // 图标在上边（纵向）
}

/**
 * SwipeCellState - SwipeCell 状态管理
 */
@Stable
class SwipeCellState internal constructor(
    private val tokens: SwipeCellTokens
) {
    internal val offsetX = Animatable(0f)
    internal var currentDirection by mutableStateOf(SwipeCellDirection.NONE)

    suspend fun close() {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = tokens.springDampingRatio,
                stiffness = tokens.springStiffness
            )
        )
        currentDirection = SwipeCellDirection.NONE
    }

    suspend fun openLeft(width: Float) {
        offsetX.animateTo(
            targetValue = width,
            animationSpec = spring(
                dampingRatio = tokens.springDampingRatio,
                stiffness = tokens.springStiffness
            )
        )
        currentDirection = SwipeCellDirection.RIGHT
    }

    suspend fun openRight(width: Float) {
        offsetX.animateTo(
            targetValue = -width,
            animationSpec = spring(
                dampingRatio = tokens.springDampingRatio,
                stiffness = tokens.springStiffness
            )
        )
        currentDirection = SwipeCellDirection.LEFT
    }
}

/**
 * 记住 SwipeCell 状态
 */
@Composable
fun rememberSwipeCellState(
    tokens: SwipeCellTokens = SwipeCellDefaults.Default
): SwipeCellState {
    return remember(tokens) { SwipeCellState(tokens) }
}

/**
 * SwipeCellGroupState - 管理一组 SwipeCell 的互斥关闭
 */
@Stable
class SwipeCellGroupState {
    private val cells = mutableListOf<SwipeCellState>()

    fun register(state: SwipeCellState) {
        if (!cells.contains(state)) {
            cells.add(state)
        }
    }

    fun unregister(state: SwipeCellState) {
        cells.remove(state)
    }

    suspend fun closeOthers(except: SwipeCellState) {
        cells.filter { it != except }.forEach { it.close() }
    }
}

/**
 * 记住 SwipeCell 组状态
 */
@Composable
fun rememberSwipeCellGroupState(): SwipeCellGroupState {
    return remember { SwipeCellGroupState() }
}

/**
 * SwipeCell - 滑动单元格组件
 *
 * 特性：
 * - 流畅的弹性动画
 * - 支持左滑/右滑操作
 * - 支持单个或多个操作按钮
 * - 按钮高度自动匹配内容高度
 * - 支持组内互斥（打开一个自动关闭其他）
 * - 支持图标+文字
 * - 阻尼感滑动
 * - 通过设计系统 Token 控制样式
 *
 * @param modifier Modifier
 * @param state SwipeCell 状态
 * @param groupState 组状态，用于互斥关闭
 * @param leftActions 左侧操作项（右滑显示）
 * @param rightActions 右侧操作项（左滑显示）
 * @param tokens 设计 Token，控制尺寸、间距、动画参数
 * @param disabled 是否禁用滑动
 * @param onChange 滑动状态变化回调
 * @param content 主内容
 */
@Composable
fun SwipeCell(
    modifier: Modifier = Modifier,
    state: SwipeCellState = rememberSwipeCellState(),
    groupState: SwipeCellGroupState? = null,
    leftActions: List<SwipeCellAction> = emptyList(),
    rightActions: List<SwipeCellAction> = emptyList(),
    tokens: SwipeCellTokens = SwipeCellDefaults.Default,
    disabled: Boolean = false,
    onChange: ((SwipeCellDirection, Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // 从 Token 获取配置
    val actionWidthPx = with(density) { tokens.actionWidth.toPx() }
    val openThreshold = tokens.openThreshold
    val velocityThreshold = tokens.velocityThreshold
    val dampingRatio = tokens.dampingRatio

    // 内容区域尺寸
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    // 操作区域宽度（像素）
    val leftActionsWidth = leftActions.sumOf { it.flex } * actionWidthPx
    val rightActionsWidth = rightActions.sumOf { it.flex } * actionWidthPx

    // 注册到组
    DisposableEffect(groupState, state) {
        groupState?.register(state)
        onDispose {
            groupState?.unregister(state)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
    ) {
        // 左侧操作区域（右滑显示）- 固定在左边
        if (leftActions.isNotEmpty() && state.offsetX.value > 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(with(density) { state.offsetX.value.coerceAtLeast(0f).toDp() })
                    .height(with(density) { contentSize.height.toDp() }),
                horizontalArrangement = Arrangement.Start
            ) {
                val totalFlex = leftActions.sumOf { it.flex }
                leftActions.forEach { action ->
                    SwipeCellActionButton(
                        action = action,
                        tokens = tokens,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(action.flex.toFloat()),
                        onClick = {
                            action.onClick()
                            scope.launch { state.close() }
                        }
                    )
                }
            }
        }

        // 右侧操作区域（左滑显示）- 固定在右边
        if (rightActions.isNotEmpty() && state.offsetX.value < 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(with(density) { (-state.offsetX.value).coerceAtLeast(0f).toDp() })
                    .height(with(density) { contentSize.height.toDp() }),
                horizontalArrangement = Arrangement.End
            ) {
                val totalFlex = rightActions.sumOf { it.flex }
                rightActions.forEach { action ->
                    SwipeCellActionButton(
                        action = action,
                        tokens = tokens,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(action.flex.toFloat()),
                        onClick = {
                            action.onClick()
                            scope.launch { state.close() }
                        }
                    )
                }
            }
        }

        // 主内容区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(state.offsetX.value.roundToInt(), 0) }
                .onSizeChanged { contentSize = it }
                .background(colors.surface)
                .then(
                    if (!disabled) {
                        Modifier.pointerInput(state) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    // 开始拖动时，关闭其他 SwipeCell
                                    scope.launch {
                                        groupState?.closeOthers(state)
                                    }
                                },
                                onDragEnd = {
                                    scope.launch {
                                        val currentOffset = state.offsetX.value
                                        val velocity = state.offsetX.velocity

                                        // 根据偏移量和速度判断目标状态
                                        when {
                                            // 快速滑动 - 根据速度方向决定
                                            abs(velocity) > velocityThreshold -> {
                                                if (velocity > 0 && leftActions.isNotEmpty()) {
                                                    state.openLeft(leftActionsWidth)
                                                    onChange?.invoke(SwipeCellDirection.RIGHT, true)
                                                } else if (velocity < 0 && rightActions.isNotEmpty()) {
                                                    state.openRight(rightActionsWidth)
                                                    onChange?.invoke(SwipeCellDirection.LEFT, true)
                                                } else {
                                                    state.close()
                                                    onChange?.invoke(state.currentDirection, false)
                                                }
                                            }
                                            // 左滑超过阈值，展开右侧操作
                                            currentOffset < -rightActionsWidth * openThreshold && rightActions.isNotEmpty() -> {
                                                state.openRight(rightActionsWidth)
                                                onChange?.invoke(SwipeCellDirection.LEFT, true)
                                            }
                                            // 右滑超过阈值，展开左侧操作
                                            currentOffset > leftActionsWidth * openThreshold && leftActions.isNotEmpty() -> {
                                                state.openLeft(leftActionsWidth)
                                                onChange?.invoke(SwipeCellDirection.RIGHT, true)
                                            }
                                            // 未超过阈值，收起
                                            else -> {
                                                val previousDirection = state.currentDirection
                                                state.close()
                                                if (previousDirection != SwipeCellDirection.NONE) {
                                                    onChange?.invoke(previousDirection, false)
                                                }
                                            }
                                        }
                                    }
                                },
                                onDragCancel = {
                                    scope.launch { state.close() }
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    scope.launch {
                                        val newOffset = state.offsetX.value + dragAmount

                                        // 添加阻尼效果：超出范围时减缓滑动
                                        val dampedOffset = when {
                                            // 右滑但没有左侧操作 - 添加阻力
                                            newOffset > 0 && leftActions.isEmpty() -> {
                                                newOffset * dampingRatio
                                            }
                                            // 左滑但没有右侧操作 - 添加阻力
                                            newOffset < 0 && rightActions.isEmpty() -> {
                                                newOffset * dampingRatio
                                            }
                                            // 超出最大范围 - 添加阻力
                                            newOffset > leftActionsWidth -> {
                                                leftActionsWidth + (newOffset - leftActionsWidth) * dampingRatio
                                            }
                                            newOffset < -rightActionsWidth -> {
                                                -rightActionsWidth + (newOffset + rightActionsWidth) * dampingRatio
                                            }
                                            else -> newOffset
                                        }

                                        state.offsetX.snapTo(dampedOffset)
                                    }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * SwipeCellActionButton - 滑动操作按钮
 *
 * 颜色使用 Theme.colors 语义色
 * 尺寸使用 SwipeCellTokens
 */
@Composable
private fun SwipeCellActionButton(
    action: SwipeCellAction,
    tokens: SwipeCellTokens,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    // 从 Theme.colors 获取语义颜色
    val backgroundColor = when (action.theme) {
        SwipeCellActionTheme.PRIMARY -> colors.primary
        SwipeCellActionTheme.DANGER -> colors.danger
        SwipeCellActionTheme.WARNING -> colors.warning
        SwipeCellActionTheme.SUCCESS -> colors.success
    }

    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(
                if (isPressed) backgroundColor.copy(alpha = 0.85f) else backgroundColor
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (action.icon != null && action.iconPosition == SwipeCellIconPosition.TOP) {
            // 纵向排列：图标在上
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            ) {
                Text(
                    text = action.icon,
                    style = Typography.TitleMedium,
                    color = colors.textAnti,
                    maxLines = 1
                )
                if (action.label.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(tokens.iconSpacing))
                    Text(
                        text = action.label,
                        style = Typography.BodySmall,
                        color = colors.textAnti,
                        maxLines = 1
                    )
                }
            }
        } else if (action.icon != null && action.label.isNotEmpty()) {
            // 横向排列：图标在左
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            ) {
                Text(
                    text = action.icon,
                    style = Typography.BodyMedium,
                    color = colors.textAnti,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(tokens.iconSpacing))
                Text(
                    text = action.label,
                    style = Typography.BodySmall,
                    color = colors.textAnti,
                    maxLines = 1
                )
            }
        } else if (action.icon != null) {
            // 仅图标
            Text(
                text = action.icon,
                style = Typography.TitleMedium,
                color = colors.textAnti,
                maxLines = 1
            )
        } else {
            // 仅文字
            Text(
                text = action.label,
                style = Typography.BodyMedium,
                color = colors.textAnti,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            )
        }
    }
}

/**
 * SwipeCellGroup - 滑动单元格组
 * 同一组内的 SwipeCell 互斥，打开一个会关闭其他
 */
@Composable
fun SwipeCellGroup(
    modifier: Modifier = Modifier,
    state: SwipeCellGroupState = rememberSwipeCellGroupState(),
    content: @Composable (SwipeCellGroupState) -> Unit
) {
    Column(modifier = modifier) {
        content(state)
    }
}
