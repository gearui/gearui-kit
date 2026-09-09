package com.gearui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.zIndex

/**
 * A container of N children where one is visible and the rest stay composed.
 *
 * Switching between children must not destroy and rebuild their subtrees. In
 * Kuikly every Compose node is a real native view, so a destroy-plus-rebuild
 * costs several bridge messages per node plus a full property replay — and a
 * tab switch has no transition animation to spread that over, which is why it
 * hurts more than a push. Measured on a real device before this existed, a bare
 * `when (index)` bottom-nav gave 35–43% janky frames, p90 77ms, and p99 200ms
 * under fast switching. With the subtrees kept alive: 0% and p90 6ms.
 *
 * **Which mechanism hides a child is this container's business, not the
 * caller's.** The three facts below are properties of KuiklyUI, they are not
 * obvious, and an application that has to know them means the framework did not
 * do its job (`DESIGN_SYSTEM_SPEC` / the shell spec's red line 2):
 *
 * - `Modifier.alpha` is flushed to the native view's opacity by `KNode`, so it
 *   really hides. This is the one that works.
 * - `Modifier.graphicsLayer` looks like the right tool and is not: its native
 *   application is commented out in the current KuiklyUI, so setting it does
 *   nothing.
 * - `Modifier.visibility` is a true native hide, but it is deprecated and
 *   `KNode.updateKuiklyViewFrame`'s `resetViewVisible` overwrites it back.
 *
 * Hidden children are also made non-interactive. Opacity is not hit testing:
 * an `alpha = 0` view still receives touches on Android, so without this a tap
 * landing where the visible child happens to have nothing interactive could be
 * taken by an invisible one underneath.
 *
 * Children are mounted lazily — a child is composed the first time it is
 * selected, never before. **This changes when mount-time side effects run**: a
 * child that is never selected never runs its `LaunchedEffect(Unit)`. If a
 * child needs to do work before it is first shown, that work does not belong
 * inside it.
 *
 * @param selectedIndex which child is visible.
 * @param count how many children there are.
 * @param content composes child [index]; called once per mounted child and then
 *   kept, so it must not assume it is re-entered on every switch.
 */
@Composable
fun TabHost(
    selectedIndex: Int,
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit,
) {
    val mounted = remember { mutableStateListOf(selectedIndex) }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex !in mounted) mounted.add(selectedIndex)
    }

    Box(modifier = modifier) {
        for (index in 0 until count) {
            // The selected child always renders: on the very first frame
            // `mounted` has not been updated by the effect yet.
            if (index != selectedIndex && index !in mounted) continue
            val active = index == selectedIndex
            key(index) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(if (active) 1f else 0f)
                        .alpha(if (active) 1f else 0f)
                        .then(if (active) Modifier else Modifier.blockPointerInput())
                ) {
                    content(index)
                }
            }
        }
    }
}

/**
 * Swallows pointer events before children see them.
 *
 * Consuming on [PointerEventPass.Initial] is what makes this work: the initial
 * pass runs parent-first, so the event is taken before it can reach anything
 * inside.
 */
private fun Modifier.blockPointerInput(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
        }
    }
}
