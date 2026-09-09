package com.gearui.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * The state one navigation entry keeps for as long as it is on the stack.
 *
 * This is the scope that was missing between the runtime root and a leaf page.
 * `SaveableStateHolder`, which Navigator already had, restores serialisable
 * *values* across recomposition; it cannot hold an object or a coroutine. With
 * nothing that could, screen state had only one place to live that outlived a
 * recomposition — the application's root composable — and that is how a single
 * function came to hold 34 `remember`s, two of them keyed by a route parameter.
 *
 * Held by [NavigatorState] rather than by the composition, so it survives the
 * entry being recomposed, being the layer below during a transition, or being
 * hidden inside a [TabHost]. It is destroyed exactly once, when the entry
 * finally leaves the stack.
 */
internal class RetainedEntry {
    /**
     * Cancelled when the entry leaves the stack.
     *
     * A `SupervisorJob` so one failed child does not take the rest of the
     * screen's work down with it. No dispatcher is attached: work launched here
     * inherits the caller's context, which on a UI screen is the main
     * dispatcher Compose already runs on.
     */
    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())

    private val retained = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> retain(key: String, factory: () -> T): T =
        retained.getOrPut(key, factory) as T

    fun dispose() {
        coroutineScope.cancel()
        // Reverse order, so a later object that captured an earlier one is torn
        // down first.
        retained.values.reversed().forEach { value ->
            if (value is AutoCloseable) {
                runCatching { value.close() }
            }
        }
        retained.clear()
    }
}

/**
 * Per-entry retained state, keyed by [NavEntry.key].
 *
 * Entries are created lazily: an entry that never asks for retained state never
 * allocates one.
 */
internal class RetainedEntryStore {
    private val entries = mutableMapOf<String, RetainedEntry>()

    fun of(key: String): RetainedEntry = entries.getOrPut(key) { RetainedEntry() }

    /** Called from the single exactly-once removal path, so it cannot double-dispose. */
    fun dispose(key: String) {
        entries.remove(key)?.dispose()
    }

    /** Reset — a logout, say — drops every entry's state. */
    fun disposeAll() {
        entries.values.forEach { it.dispose() }
        entries.clear()
    }
}
