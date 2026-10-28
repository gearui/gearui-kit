package com.gearui.foundation.a11y

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.extension.MakeKuiklyComposeNode
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.layout.MeasurePolicy
import com.tencent.kuikly.core.views.DivView

/**
 * What a layer of the screen is to assistive technology and keyboard focus.
 *
 * Kuikly renders every node as a native view and gives the kit no way to hide a
 * subtree from a screen reader or to confine focus to one: `clearAndSetSemantics`
 * drops the Compose semantics, but the native text views underneath still speak,
 * and on the Web an `alpha = 0` page is still reachable with Tab. So the kit names
 * the layer on a native view of its own ([ACCESSIBILITY_LAYER_PROP]) and the host
 * renderer applies it — see `docs/BEHAVIOR_CONTRACT.md` §6 for what each host does.
 */
internal enum class AccessibilityLayer(val propValue: String) {
    /** Nothing to say; the host clears whatever it applied before. */
    None(""),

    /**
     * An open dialog, sheet or panel: focus moves into it, stays in it, and returns
     * to where it was when the layer closes; what is beneath is out of reach.
     */
    Modal("modal"),

    /**
     * An open anchored panel — a dropdown, a popover: Escape closes it and focus
     * returns to its trigger, but it does not take focus or shut out the page; a
     * combo box keeps typing into its field while its list is open.
     */
    Popup("popup"),

    /** Out of sight — a page under the top one, an unselected tab: out of reach. */
    Hidden("hidden"),
}

/** The Kuikly view property hosts read; its value is [AccessibilityLayer.propValue]. */
internal const val ACCESSIBILITY_LAYER_PROP = "gearuiLayer"

/**
 * Lays out [content] exactly as a `Box` would — children stacked at the top-left —
 * inside a native view that carries [layer].
 */
@Composable
internal fun AccessibilityLayerBox(
    layer: AccessibilityLayer,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    MakeKuiklyComposeNode<DivView>(
        factory = { DivView() },
        modifier = modifier,
        content = content,
        viewInit = { getViewAttr().apply { ACCESSIBILITY_LAYER_PROP with layer.propValue } },
        viewUpdate = { it.getViewAttr().apply { ACCESSIBILITY_LAYER_PROP with layer.propValue } },
        measurePolicy = StackMeasurePolicy,
    )
}

private val StackMeasurePolicy = MeasurePolicy { measurables, constraints ->
    val loose = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { it.measure(loose) }
    val width = maxOf(constraints.minWidth, placeables.maxOfOrNull { it.width } ?: 0)
    val height = maxOf(constraints.minHeight, placeables.maxOfOrNull { it.height } ?: 0)
    layout(width, height) {
        placeables.forEach { it.place(0, 0) }
    }
}
