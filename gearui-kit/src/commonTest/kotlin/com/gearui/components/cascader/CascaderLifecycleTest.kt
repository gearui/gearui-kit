package com.gearui.components.cascader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class CascaderLifecycleTest {
    @Test fun closingDuringLoadAllowsReopening() = runTest {
        val cache = mutableMapOf<String, LevelState>("province" to LevelState.Loading)
        val response = CompletableDeferred<List<CascaderOption>>()
        val job = launch { fetchCascaderLevel(cache, "province") { response.await() } }
        runCurrent()
        job.cancelAndJoin()
        assertFalse(cache.containsKey("province"))
        val cities = listOf(CascaderOption("city", "City"))
        fetchCascaderLevel(cache, "province") { cities }
        assertEquals(LevelState.Loaded(cities), cache["province"])
    }

    @Test fun failureCanBeRetriedAndEmptyIsACompletedResult() = runTest {
        val cache = mutableMapOf<String, LevelState>()
        fetchCascaderLevel(cache, "province") { error("offline") }
        assertEquals(LevelState.Failed, cache["province"])
        fetchCascaderLevel(cache, "province") { emptyList() }
        assertEquals(LevelState.Loaded(emptyList()), cache["province"])
    }
}
