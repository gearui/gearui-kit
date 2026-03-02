package com.gearui.foundation.interaction

/**
 * GearUI 组件交互状态
 *
 * 统一管理所有组件的交互状态，避免在每个组件中重复状态判断逻辑。
 * 所有交互组件（Button、Input、Card 等）共享此状态系统。
 *
 * 设计原则：
 * - 状态互斥：同一时间只能处于一种状态
 * - 优先级明确：Disabled > Loading > Pressed > Focused > Normal
 * - 语义清晰：每个状态都有明确的交互含义
 */
sealed class InteractionState {

    /**
     * 正常状态 - 默认状态，可交互
     */
    object Normal : InteractionState()

    /**
     * 按下状态 - 用户正在触摸/点击组件
     */
    object Pressed : InteractionState()

    /**
     * 聚焦状态 - 组件获得焦点（键盘导航、Tab 键等）
     */
    object Focused : InteractionState()

    /**
     * 悬停状态 - 鼠标悬停（主要用于桌面端）
     */
    object Hovered : InteractionState()

    /**
     * 禁用状态 - 组件不可交互
     */
    object Disabled : InteractionState()

    /**
     * 加载状态 - 组件正在执行异步操作，不可交互
     */
    object Loading : InteractionState()

    /**
     * 长按状态 - 用户长按组件
     */
    object LongPressed : InteractionState()

    /**
     * 拖拽状态 - 组件正在被拖拽
     */
    object Dragging : InteractionState()
}

/**
 * 交互状态扩展属性
 */

/**
 * 是否可交互
 *
 * Disabled 和 Loading 状态下不可交互
 */
val InteractionState.isInteractive: Boolean
    get() = this !is InteractionState.Disabled && this !is InteractionState.Loading

/**
 * 是否处于激活状态
 *
 * Pressed、Focused、Hovered、LongPressed、Dragging 都视为激活状态
 */
val InteractionState.isActive: Boolean
    get() = when (this) {
        is InteractionState.Pressed,
        is InteractionState.Focused,
        is InteractionState.Hovered,
        is InteractionState.LongPressed,
        is InteractionState.Dragging -> true

        else -> false
    }

/**
 * 是否需要禁用样式
 */
val InteractionState.needsDisabledStyle: Boolean
    get() = this is InteractionState.Disabled || this is InteractionState.Loading

/**
 * 交互状态源
 *
 * 用于跟踪当前组件的交互状态
 */
interface InteractionSource {
    /**
     * 当前交互状态
     */
    val currentState: InteractionState

    /**
     * 状态历史（用于动画和过渡）
     */
    val stateHistory: List<InteractionState>
}

/**
 * 可变交互状态源
 */
interface MutableInteractionSource : InteractionSource {
    /**
     * 更新交互状态
     */
    fun updateState(newState: InteractionState)

    /**
     * 尝试更新状态（根据优先级）
     *
     * @return 是否成功更新
     */
    fun tryUpdateState(newState: InteractionState): Boolean
}

/**
 * 创建可变交互状态源
 */
fun createMutableInteractionSource(
    initialState: InteractionState = InteractionState.Normal
): MutableInteractionSource = MutableInteractionSourceImpl(initialState)

/**
 * 交互状态源实现
 */
private class MutableInteractionSourceImpl(
    initialState: InteractionState
) : MutableInteractionSource {

    private var _currentState: InteractionState = initialState
    private val _stateHistory = mutableListOf(initialState)

    override val currentState: InteractionState
        get() = _currentState

    override val stateHistory: List<InteractionState>
        get() = _stateHistory.toList()

    override fun updateState(newState: InteractionState) {
        if (_currentState != newState) {
            _currentState = newState
            _stateHistory.add(newState)

            // 保持历史记录在合理范围内
            if (_stateHistory.size > 10) {
                _stateHistory.removeAt(0)
            }
        }
    }

    override fun tryUpdateState(newState: InteractionState): Boolean {
        // 状态优先级：Disabled > Loading > LongPressed > Pressed > Dragging > Focused > Hovered > Normal
        val canUpdate = when (_currentState) {
            is InteractionState.Disabled -> newState is InteractionState.Normal
            is InteractionState.Loading -> newState is InteractionState.Normal || newState is InteractionState.Disabled
            is InteractionState.LongPressed -> newState !is InteractionState.Pressed && newState !is InteractionState.Hovered && newState !is InteractionState.Focused
            is InteractionState.Pressed -> newState !is InteractionState.Hovered && newState !is InteractionState.Focused
            is InteractionState.Dragging -> newState !is InteractionState.Pressed && newState !is InteractionState.Hovered && newState !is InteractionState.Focused
            is InteractionState.Focused -> newState !is InteractionState.Hovered
            is InteractionState.Hovered -> true
            is InteractionState.Normal -> true
        }

        if (canUpdate) {
            updateState(newState)
            return true
        }
        return false
    }
}

/**
 * 状态过渡辅助函数
 */
object InteractionTransition {

    /**
     * 处理按下事件
     */
    fun onPressStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Pressed)
    }

    /**
     * 处理释放事件
     */
    fun onPressEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Pressed) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * 处理聚焦事件
     */
    fun onFocus(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Focused)
    }

    /**
     * 处理失焦事件
     */
    fun onBlur(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Focused) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * 处理悬停开始
     */
    fun onHoverStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Hovered)
    }

    /**
     * 处理悬停结束
     */
    fun onHoverEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Hovered) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * 处理长按开始
     */
    fun onLongPressStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.LongPressed)
    }

    /**
     * 处理长按结束
     */
    fun onLongPressEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.LongPressed) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * 处理拖拽开始
     */
    fun onDragStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Dragging)
    }

    /**
     * 处理拖拽结束
     */
    fun onDragEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Dragging) {
            source.updateState(InteractionState.Normal)
        }
    }
}
