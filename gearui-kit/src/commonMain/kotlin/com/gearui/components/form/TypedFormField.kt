package com.gearui.components.form

import androidx.compose.runtime.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

enum class FormValidationTrigger { CHANGE, BLUR, SUBMIT }

/** Return a localized error or null. Async checks may suspend; cancellation is propagated. */
class TypedFormRule<T>(
    val validate: (T) -> String? = { null },
    val validateAsync: (suspend (T) -> String?)? = null,
)

/**
 * A field owns its value and validation generation, including same-value resets and server errors.
 *
 * Validation runs against one value. An edit, a server error, a reset or disposal makes a
 * running check stale: its result is dropped and it reports false. Asking again for the
 * same value while a check is running joins that check instead of replacing it, so a
 * submit that coincides with a blur-triggered check gets the real answer.
 */
class TypedFormFieldState<T>(
    initialValue: T,
    val rules: List<TypedFormRule<T>> = emptyList(),
    trigger: FormValidationTrigger = FormValidationTrigger.BLUR,
    private val validationScope: CoroutineScope? = null,
) {
    /** The value [dirty] compares against and [reset] returns to. */
    var initialValue by mutableStateOf(initialValue)
        private set
    var trigger by mutableStateOf(trigger)
        internal set
    var value by mutableStateOf(initialValue)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var touched by mutableStateOf(false)
        private set
    var validating by mutableStateOf(false)
        private set
    val dirty: Boolean get() = value != initialValue
    internal var revision = 0L
        private set
    private var active = true
    private var job: Job? = null
    private var running: Pair<Long, CompletableDeferred<Boolean>>? = null

    fun update(next: T) {
        invalidate()
        value = next
        error = null
        if (trigger == FormValidationTrigger.CHANGE) schedule()
    }

    fun touch() {
        touched = true
        if (trigger == FormValidationTrigger.BLUR) schedule()
    }

    /**
     * Moves the baseline, as when the record being edited finishes loading. A field the
     * user has not changed follows it; a changed one keeps what the user typed.
     */
    fun rebase(next: T) {
        if (next == initialValue) return
        val follow = !dirty
        initialValue = next
        if (follow) {
            invalidate()
            value = next
            error = null
        }
    }

    private fun schedule() {
        if (active) job = validationScope?.launch { validate() }
    }

    /** True when the current value passes; false when it fails or changes before the check ends. */
    suspend fun validate(): Boolean {
        if (!active) return false
        running?.let { (revisionOfCheck, result) -> if (revisionOfCheck == revision) return result.await() }
        val result = CompletableDeferred<Boolean>()
        val checked = revision
        running = checked to result
        val candidate = value
        touched = true
        validating = true
        try {
            var failure: String? = null
            for (rule in rules) {
                failure = rule.validate(candidate) ?: rule.validateAsync?.invoke(candidate)
                currentCoroutineContext().ensureActive()
                if (!active || checked != revision) return false.also { result.complete(it) }
                if (failure != null) break
            }
            error = failure
            return (failure == null).also { result.complete(it) }
        } catch (e: Throwable) {
            // A cancelled or failing rule must not leave joined callers waiting.
            result.complete(false)
            throw e
        } finally {
            if (running?.second === result) {
                running = null
                validating = false
            }
        }
    }

    /** Server errors invalidate any in-flight check; a later edit clears them. */
    fun setServerError(message: String?) { invalidate(); error = message; touched = true }

    fun reset() { invalidate(); value = initialValue; error = null; touched = false }
    fun dispose() { active = false; invalidate() }
    private fun invalidate() { revision++; running = null; job?.cancel(); job = null; validating = false }
}

@Composable
fun <T> rememberTypedFormFieldState(
    initialValue: T,
    rules: List<TypedFormRule<T>> = emptyList(),
    trigger: FormValidationTrigger = FormValidationTrigger.BLUR,
    name: String? = null,
    formState: FormState? = null,
): TypedFormFieldState<T> {
    val scope = rememberCoroutineScope()
    // Rule identities are often new lambdas on recomposition; do not replace the user's field value.
    val currentRules by rememberUpdatedState(rules)
    val forwarding = remember { listOf(TypedFormRule<T>(validateAsync = { candidate ->
        var error: String? = null
        for (rule in currentRules) {
            error = rule.validate(candidate) ?: rule.validateAsync?.invoke(candidate)
            if (error != null) break
        }
        error
    })) }
    // One state for the field's life: a new initial value (a record that finished loading)
    // or trigger must not throw away what the user has typed.
    val state = remember { TypedFormFieldState(initialValue, forwarding, trigger, scope) }
    LaunchedEffect(initialValue) { state.rebase(initialValue) }
    SideEffect { state.trigger = trigger }
    DisposableEffect(state) { onDispose { state.dispose() } }
    DisposableEffect(name, formState, state) {
        if (name != null && formState != null) formState.registerField(name, state)
        onDispose { if (name != null && formState != null) formState.unregisterTypedField(name, state) }
    }
    return state
}
