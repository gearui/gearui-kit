package com.gearui.components.form

import kotlin.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TypedFormFieldTest {
    @Test fun submissionRejectsAnEarlierFieldChangingDuringAnotherValidation() = runTest {
        val response = CompletableDeferred<String?>()
        val first = TypedFormFieldState("valid", listOf(TypedFormRule(validate = { if (it.isBlank()) "Required" else null })))
        val second = TypedFormFieldState("waiting", listOf(TypedFormRule(validateAsync = { response.await() })))
        val form = FormState()
        form.registerField("first", first)
        form.registerField("second", second)
        val submitted = async { form.validateAll() }
        runCurrent()
        first.update("")
        response.complete(null)
        assertFalse(submitted.await())
    }
    @Test fun aLateValidationCannotOverwriteANewerValue() = runTest {
        val response = CompletableDeferred<String?>()
        val field = TypedFormFieldState("old", listOf(TypedFormRule(validateAsync = { response.await() })))
        val old = launch { field.validate() }
        runCurrent()
        field.update("new")
        response.complete("Old failure")
        old.join()
        assertNull(field.error)
        assertEquals("new", field.value)
        assertFalse(field.validating)
    }
    @Test fun serverErrorAndDisposalInvalidateRunningChecks() = runTest {
        val response = CompletableDeferred<String?>()
        val field = TypedFormFieldState(42, listOf(TypedFormRule(validateAsync = { response.await() })))
        val job = launch { field.validate() }
        runCurrent()
        field.setServerError("Server rejected")
        response.complete(null)
        job.join()
        assertEquals("Server rejected", field.error)
        val other = CompletableDeferred<String?>()
        val disposed = TypedFormFieldState(false, listOf(TypedFormRule(validateAsync = { other.await() })))
        val pending = launch { disposed.validate() }
        runCurrent()
        disposed.dispose()
        other.complete("Late")
        pending.join()
        assertNull(disposed.error)
    }
    @Test fun triggerDirtyTouchedResetAndTypedSubmission() = runTest {
        val field = TypedFormFieldState(1, trigger = FormValidationTrigger.CHANGE, validationScope = this)
        field.update(2)
        runCurrent()
        assertTrue(field.dirty)
        assertTrue(field.touched)
        val form = FormState()
        form.registerField("quantity", field)
        assertEquals(2, form.getTypedValues()["quantity"])
        assertTrue(form.validateAll())
        form.reset()
        assertEquals(1, field.value)
        assertFalse(field.dirty)
        assertFalse(field.touched)
    }
    @Test fun cancellationPropagatesAndClearsValidating() = runTest {
        val field = TypedFormFieldState("x", listOf(TypedFormRule(validateAsync = { awaitCancellation() })))
        val job = launch { field.validate() }
        runCurrent()
        assertTrue(field.validating)
        job.cancelAndJoin()
        assertFalse(field.validating)
        assertNull(field.error)
    }
    @Test fun aSubmitThatCoincidesWithABlurCheckGetsTheRealAnswer() = runTest {
        val response = CompletableDeferred<String?>()
        val field = TypedFormFieldState("name", listOf(TypedFormRule(validateAsync = { response.await() })),
            validationScope = this)
        val form = FormState()
        form.registerField("name", field)
        val submitted = async { form.validateAll() }
        runCurrent()
        field.touch() // blur while the submit's check is waiting
        runCurrent()
        response.complete(null)
        assertTrue(submitted.await())
        assertNull(field.error)
    }
    @Test fun aNewBaselineMovesUntouchedFieldsAndKeepsEditedOnes() = runTest {
        val untouched = TypedFormFieldState("")
        untouched.rebase("Alice")
        assertEquals("Alice", untouched.value)
        assertFalse(untouched.dirty)
        val edited = TypedFormFieldState("")
        edited.update("typed before load")
        edited.rebase("Alice")
        assertEquals("typed before load", edited.value)
        assertTrue(edited.dirty)
        edited.reset()
        assertEquals("Alice", edited.value)
    }
    @Test fun aThrowingRuleDoesNotLeaveAJoinedCheckWaiting() = runTest {
        val gate = CompletableDeferred<Unit>()
        val field = TypedFormFieldState("x", listOf(TypedFormRule(validateAsync = { gate.await(); error("boom") })))
        val first = async { runCatching { field.validate() } }
        runCurrent()
        val joined = async { field.validate() }
        runCurrent()
        gate.complete(Unit)
        assertTrue(first.await().isFailure)
        assertFalse(joined.await())
        assertFalse(field.validating)
    }
}
