package com.gearui.sample.adapter

import com.tencent.kuikly.core.render.android.adapter.IKRThreadAdapter
import java.util.concurrent.Executors

/**
 * Gives Kuikly's context thread the main thread's stack size.
 *
 * Composition, measure and layout all run on that thread, and placement recurses once
 * per level of the layout tree. With the platform default (about 1 MB) a debuggable
 * build — ART without optimisations, so larger frames — overflows on the deeper sample
 * pages (Form, Input, Switch). Release builds fit today, but a host should not run its
 * whole UI on a stack an eighth the size of the main thread's.
 */
class SampleThreadAdapter : IKRThreadAdapter {
    private val executor = Executors.newCachedThreadPool()

    override fun executeOnSubThread(task: () -> Unit) {
        executor.execute(task)
    }

    override fun stackSize(): Long = STACK_SIZE

    private companion object {
        const val STACK_SIZE = 8L * 1024 * 1024
    }
}
