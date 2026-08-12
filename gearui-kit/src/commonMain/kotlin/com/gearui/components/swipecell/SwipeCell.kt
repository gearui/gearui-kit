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
 * SwipeCellDirection - swipe direction
 */
enum class SwipeCellDirection {
    LEFT,   // 左滑（显示右侧操作）
    RIGHT,  // 右滑（显示左侧操作）
    NONE    // 无滑动
}

/**
 * SwipeCellActionTheme - action button theme
 */
enum class SwipeCellActionTheme {
    PRIMARY,    // 主要色
    DANGER,     // 危险色
    WARNING,    // 警告色
    SUCCESS     // 成功色
}

/**
 * SwipeCellAction - one swipe action
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
 * SwipeCellIconPosition - icon position
 */
enum class SwipeCellIconPosition {
    LEFT,       // 图标在左边（横向）
    TOP         // 图标在上边（纵向）
}

/**
 * SwipeCellState - SwipeCell state holder
 */
@Stable
class SwipeCellState internal constructor(
    private val tokens: SwipeCellTokens
) {
    internal val offsetX = Animatable(0f)
    internal var currentDirection by mutableStateOf(SwipeCellDirection.NONE)

    val isOpen: Boolean
        get() = currentDirection != SwipeCellDirection.NONE || offsetX.value != 0f

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
 * Remembers a SwipeCell state
 */
@Composable
fun rememberSwipeCellState(
    tokens: SwipeCellTokens = SwipeCellDefaults.Default
): SwipeCellState {
    return remember(tokens) { SwipeCellState(tokens) }
}

/**
 * SwipeCellGroupState - keeps a group of SwipeCells mutually exclusive
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

    suspend fun closeAll() {
        cells.forEach { it.close() }
    }

    val isAnyOpen: Boolean
        get() = cells.any { it.isOpen }
}

/**
 * Remembers a SwipeCell group state
 */
@Composable
fun rememberSwipeCellGroupState(): SwipeCellGroupState {
    return remember { SwipeCellGroupState() }
}

/**
 * SwipeCell - swipeable cell
 *
 * Features:
 * - smooth spring animation
 * - swipe from either side
 * - one or several action buttons
 * - button height follows the content height
 * - mutual exclusion within a group: opening one closes the others
 * - icon plus label
 * - damped swiping
 * - styled through design system tokens
 *
 * @param modifier Modifier
 * @param state SwipeCell state
 * @param groupState group state, for mutual exclusion
 * @param leftActions actions on the left, revealed by swiping right
 * @param rightActions actions on the right, revealed by swiping left
 * @param tokens design tokens controlling size, spacing and animation
 * @param disabled whether swiping is disabled
 * @param onChange called when the swipe state changes
 * @param content the main content
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

    // Read configuration from tokens
    val actionWidthPx = with(density) { tokens.actionWidth.toPx() }
    val openThreshold = tokens.openThreshold
    val velocityThreshold = tokens.velocityThreshold
    val dampingRatio = tokens.dampingRatio

    // Content size
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    // Action area width in pixels
    val leftActionsWidth = leftActions.sumOf { it.flex } * actionWidthPx
    val rightActionsWidth = rightActions.sumOf { it.flex } * actionWidthPx

    // Register with the group
    DisposableEffect(groupState, state) {
        groupState?.register(state)
        onDispose {
            groupState?.unregister(state)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.none)
    ) {
        // Left actions, revealed by swiping right; pinned to the left
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

        // Right actions, revealed by swiping left; pinned to the right
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

        // Main content
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
                                    // Close the other SwipeCells when a drag starts
                                    scope.launch {
                                        groupState?.closeOthers(state)
                                    }
                                },
                                onDragEnd = {
                                    scope.launch {
                                        val currentOffset = state.offsetX.value
                                        val velocity = state.offsetX.velocity

                                        // Pick the target state from offset and velocity
                                        when {
                                            // Fast swipe: let the direction of the velocity decide
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
                                            // Swiped left past the threshold: open the right actions
                                            currentOffset < -rightActionsWidth * openThreshold && rightActions.isNotEmpty() -> {
                                                state.openRight(rightActionsWidth)
                                                onChange?.invoke(SwipeCellDirection.LEFT, true)
                                            }
                                            // Swiped right past the threshold: open the left actions
                                            currentOffset > leftActionsWidth * openThreshold && leftActions.isNotEmpty() -> {
                                                state.openLeft(leftActionsWidth)
                                                onChange?.invoke(SwipeCellDirection.RIGHT, true)
                                            }
                                            // Below the threshold: close
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

                                        // Damping: slow the swipe once it leaves the valid range
                                        val dampedOffset = when {
                                            // Swiping right with no left actions: resist
                                            newOffset > 0 && leftActions.isEmpty() -> {
                                                newOffset * dampingRatio
                                            }
                                            // Swiping left with no right actions: resist
                                            newOffset < 0 && rightActions.isEmpty() -> {
                                                newOffset * dampingRatio
                                            }
                                            // Past the maximum: resist
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
 * SwipeCellActionButton - a swipe action button
 *
 * Colours come from Theme.colors semantics, sizes from SwipeCellTokens.
 *
 */
@Composable
private fun SwipeCellActionButton(
    action: SwipeCellAction,
    tokens: SwipeCellTokens,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    // Foreground must be paired with its **own** background from the same theme.
    // Using primaryForeground for everything meant that in the dark theme it was
    // a dark colour (primary being light), leaving the label nearly invisible on
    // the destructive red background.
    val backgroundColor = when (action.theme) {
        SwipeCellActionTheme.PRIMARY -> colors.primary
        SwipeCellActionTheme.DANGER -> colors.destructive
        SwipeCellActionTheme.WARNING -> colors.warning
        SwipeCellActionTheme.SUCCESS -> colors.success
    }
    val foregroundColor = when (action.theme) {
        SwipeCellActionTheme.PRIMARY -> colors.primaryForeground
        SwipeCellActionTheme.DANGER -> colors.destructiveForeground
        SwipeCellActionTheme.WARNING -> colors.warningForeground
        SwipeCellActionTheme.SUCCESS -> colors.successForeground
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
            // Vertical: icon above
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            ) {
                Text(
                    text = action.icon,
                    style = Typography.TitleMedium,
                    color = foregroundColor,
                    maxLines = 1
                )
                if (action.label.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(tokens.iconSpacing))
                    Text(
                        text = action.label,
                        style = Typography.BodySmall,
                        color = foregroundColor,
                        maxLines = 1
                    )
                }
            }
        } else if (action.icon != null && action.label.isNotEmpty()) {
            // Horizontal: icon to the left
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            ) {
                Text(
                    text = action.icon,
                    style = Typography.BodyMedium,
                    color = foregroundColor,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(tokens.iconSpacing))
                Text(
                    text = action.label,
                    style = Typography.BodySmall,
                    color = foregroundColor,
                    maxLines = 1
                )
            }
        } else if (action.icon != null) {
            // Icon only
            Text(
                text = action.icon,
                style = Typography.TitleMedium,
                color = foregroundColor,
                maxLines = 1
            )
        } else {
            // Label only
            Text(
                text = action.label,
                style = Typography.BodyMedium,
                color = foregroundColor,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = tokens.actionPaddingHorizontal)
            )
        }
    }
}

/**
 * SwipeCellGroup - a group of swipeable cells.
 * Cells in a group are mutually exclusive: opening one closes the others.
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
