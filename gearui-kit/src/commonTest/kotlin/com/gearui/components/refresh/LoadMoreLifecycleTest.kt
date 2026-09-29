package com.gearui.components.refresh

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class LoadMoreLifecycleTest {
    @Test fun oldVisibleLayoutNeverTriggersAnAdditionalRequest() = runTest {
        val layout = CompletableDeferred<Unit>()
        val visible = CompletableDeferred<Unit>()
        var calls = 0
        val job = launch {
            requestLoadMoreAfterLayout({ layout.await() }, { visible.await() }) { calls++ }
        }
        runCurrent()
        assertEquals(0, calls)
        layout.complete(Unit)
        runCurrent()
        assertEquals(0, calls)
        visible.complete(Unit)
        job.join()
        assertEquals(1, calls)
    }

    @Test fun cancellationWhileWaitingForLayoutNeverRequests() = runTest {
        var calls = 0
        val job = launch {
            requestLoadMoreAfterLayout({ CompletableDeferred<Unit>().await() }, {}) { calls++ }
        }
        runCurrent()
        job.cancelAndJoin()
        assertEquals(0, calls)
    }
}
