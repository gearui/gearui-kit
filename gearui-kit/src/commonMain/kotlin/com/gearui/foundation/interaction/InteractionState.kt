package com.gearui.foundation.interaction

/**
 * Component interaction state.
 *
 * One place for the interaction state every component shares, so the same state
 * logic is not rewritten per component. Button, Input, Card and the rest use it.
 *
 * Principles:
 * - States are mutually exclusive: only one at a time.
 * - Priority is explicit: Disabled > Loading > Pressed > Focused > Normal.
 * - Each state means one interaction, unambiguously.
 */
sealed class InteractionState {

    /**
     * Normal - the default; interactive.
     */
    object Normal : InteractionState()

    /**
     * Pressed - the user is touching or clicking the component.
     */
    object Pressed : InteractionState()

    /**
     * Focused - the component holds focus (keyboard navigation, Tab).
     */
    object Focused : InteractionState()

    /**
     * Hovered - pointer hover, mainly on desktop.
     */
    object Hovered : InteractionState()

    /**
     * Disabled - not interactive.
     */
    object Disabled : InteractionState()

    /**
     * Loading - an async operation is running; not interactive.
     */
    object Loading : InteractionState()

    /**
     * LongPressed - the user is long-pressing the component.
     */
    object LongPressed : InteractionState()

    /**
     * Dragging - the component is being dragged.
     */
    object Dragging : InteractionState()
}

/**
 * Interaction state extensions
 */

/**
 * Whether the component is interactive.
 *
 * Disabled and Loading are not.
 */
val InteractionState.isInteractive: Boolean
    get() = this !is InteractionState.Disabled && this !is InteractionState.Loading

/**
 * Whether the state counts as active.
 *
 * Pressed, Focused, Hovered, LongPressed and Dragging all do.
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
 * Whether the disabled styling applies.
 */
val InteractionState.needsDisabledStyle: Boolean
    get() = this is InteractionState.Disabled || this is InteractionState.Loading

/**
 * Interaction state source.
 *
 * Tracks the current interaction state of a component.
 */
interface InteractionSource {
    /**
     * Current interaction state
     */
    val currentState: InteractionState

    /**
     * State history, used for animation and transitions
     */
    val stateHistory: List<InteractionState>
}

/**
 * Mutable interaction state source
 */
interface MutableInteractionSource : InteractionSource {
    /**
     * Sets the interaction state
     */
    fun updateState(newState: InteractionState)

    /**
     * Attempts a state change, respecting priority.
     *
     * @return whether the state actually changed
     */
    fun tryUpdateState(newState: InteractionState): Boolean
}

/**
 * Creates a mutable interaction state source
 */
fun createMutableInteractionSource(
    initialState: InteractionState = InteractionState.Normal
): MutableInteractionSource = MutableInteractionSourceImpl(initialState)

/**
 * Interaction state source implementation
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

            // Keep the history from growing without bound
            if (_stateHistory.size > 10) {
                _stateHistory.removeAt(0)
            }
        }
    }

    override fun tryUpdateState(newState: InteractionState): Boolean {
        // Priority: Disabled > Loading > LongPressed > Pressed > Dragging > Focused > Hovered > Normal
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
 * State transition helpers
 */
object InteractionTransition {

    /**
     * Handles a press
     */
    fun onPressStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Pressed)
    }

    /**
     * Handles a release
     */
    fun onPressEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Pressed) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * Handles focus gained
     */
    fun onFocus(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Focused)
    }

    /**
     * Handles focus lost
     */
    fun onBlur(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Focused) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * Handles hover start
     */
    fun onHoverStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Hovered)
    }

    /**
     * Handles hover end
     */
    fun onHoverEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Hovered) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * Handles long-press start
     */
    fun onLongPressStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.LongPressed)
    }

    /**
     * Handles long-press end
     */
    fun onLongPressEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.LongPressed) {
            source.updateState(InteractionState.Normal)
        }
    }

    /**
     * Handles drag start
     */
    fun onDragStart(source: MutableInteractionSource) {
        source.tryUpdateState(InteractionState.Dragging)
    }

    /**
     * Handles drag end
     */
    fun onDragEnd(source: MutableInteractionSource) {
        if (source.currentState is InteractionState.Dragging) {
            source.updateState(InteractionState.Normal)
        }
    }
}
