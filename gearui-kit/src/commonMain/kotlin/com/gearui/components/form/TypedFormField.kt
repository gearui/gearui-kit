package com.gearui.components.form

import androidx.compose.runtime.*
import kotlinx.coroutines.CancellationException
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

/** A field owns its value and validation generation, including same-value resets and server errors. */
class TypedFormFieldState<T>(
    private val initialValue: T,
    val rules: List<TypedFormRule<T>> = emptyList(),
    val trigger: FormValidationTrigger = FormValidationTrigger.BLUR,
    private val validationScope: CoroutineScope? = null,
) {
    var value by mutableStateOf(initialValue)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var touched by mutableStateOf(false)
        private set
    var validating by mutableStateOf(false)
        private set
    val dirty: Boolean get() = value != initialValue
    private var version = 0L
    internal var revision = 0L
        private set
    private var active = true
    private var job: Job? = null

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

    private fun schedule() {
        if (active) job = validationScope?.launch { validate() }
    }

    suspend fun validate(): Boolean {
        if (!active) return false
        val generation = ++version
        val candidate = value
        touched = true
        validating = true
        try {
            var failure: String? = null
            for (rule in rules) {
                failure = rule.validate(candidate) ?: rule.validateAsync?.invoke(candidate)
                currentCoroutineContext().ensureActive()
                if (!active || generation != version) return false
                if (failure != null) break
            }
            if (!active || generation != version) return false
            error = failure
            return failure == null
        } catch (e: CancellationException) {
            throw e
        } finally {
            if (generation == version) validating = false
        }
    }

    /** Server errors invalidate any in-flight check; a later edit clears them. */
    fun setServerError(message: String?) { invalidate(); error = message; touched = true }

    fun reset() { invalidate(); value = initialValue; error = null; touched = false }
    fun dispose() { active = false; invalidate() }
    private fun invalidate() { version++; revision++; job?.cancel(); job = null; validating = false }
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
    val state = remember(initialValue, trigger) { TypedFormFieldState(initialValue, forwarding, trigger, scope) }
    DisposableEffect(state) { onDispose { state.dispose() } }
    DisposableEffect(name, formState, state) {
        if (name != null && formState != null) formState.registerField(name, state)
        onDispose { if (name != null && formState != null) formState.unregisterTypedField(name, state) }
    }
    return state
}
