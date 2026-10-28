package com.gearui.sample

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewPropExternalHandler
import java.lang.ref.WeakReference

/**
 * TalkBack for GearUI's layers: the host half of the kit's `gearuiLayer` view property
 * (BEHAVIOR_CONTRACT §6).
 *
 * Kuikly renders the page under a dialog as ordinary views, so without this TalkBack
 * swipes straight past the dialog into the page behind it. While a `modal` layer is
 * open every view outside it is hidden from accessibility, TalkBack's focus moves to
 * its first element, and when it closes focus goes back to what had it. `hidden` (a
 * page under the top one, an unselected tab) is hidden from accessibility. `popup`
 * needs nothing here: an anchored panel shuts nothing out.
 *
 * Register it in the Kuikly delegate's `registerViewExternalPropHandler`.
 */
class AccessibilityLayers : IKuiklyRenderViewPropExternalHandler {

    private class Layer(val view: View, val kind: String, val opener: WeakReference<View>?) {
        /** Views this layer hid, with the importance they had before. */
        val hid = LinkedHashMap<View, Int>()
        var detachListener: View.OnAttachStateChangeListener? = null
    }

    private val layers = mutableListOf<Layer>()

    override fun setViewExternalProp(renderViewExport: IKuiklyRenderViewExport, propKey: String, propValue: Any): Boolean {
        if (propKey != PROP) return false
        apply(renderViewExport.view(), propValue.toString())
        return true
    }

    override fun resetViewExternalProp(renderViewExport: IKuiklyRenderViewExport, propKey: String): Boolean {
        if (propKey != PROP) return false
        apply(renderViewExport.view(), "")
        return true
    }

    private fun apply(view: View, kind: String) {
        val old = layers.firstOrNull { it.view === view }
        if (old?.kind == kind) return
        if (old != null) close(old)
        if (kind != "modal" && kind != "hidden") return
        val opener = if (kind == "modal") focusedView(view.rootView)?.let(::WeakReference) else null
        val layer = Layer(view, kind, opener)
        layers += layer
        // The property is set before the view is attached, and a layer usually leaves by
        // being removed, which resets nothing: both ends ride on attachment.
        layer.detachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = open(layer)
            override fun onViewDetachedFromWindow(v: View) = close(layer)
        }.also(view::addOnAttachStateChangeListener)
        if (view.isAttachedToWindow) open(layer)
    }

    private fun open(layer: Layer) {
        if (layer !in layers) return
        if (layer.kind == "hidden") {
            hide(layer, layer.view)
            return
        }
        // Everything outside the modal: the siblings of each view from it up to the page.
        var node: View = layer.view
        while (true) {
            val parent = node.parent as? ViewGroup ?: break
            for (i in 0 until parent.childCount) {
                val sibling = parent.getChildAt(i)
                if (sibling !== node) hide(layer, sibling)
            }
            if (parent.id == android.R.id.content) break
            node = parent
        }
        if (touchExploration(layer.view)) {
            // After its content is laid out: the property arrives before the children.
            layer.view.postDelayed({ if (layer in layers) firstReadable(layer.view)?.focusForAccessibility() }, FOCUS_DELAY_MS)
        }
    }

    private fun hide(layer: Layer, view: View) {
        if (view in layer.hid || layers.any { it !== layer && view in it.hid }) return
        layer.hid[view] = view.importantForAccessibility
        view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    private fun close(layer: Layer) {
        if (!layers.remove(layer)) return
        layer.detachListener?.let(layer.view::removeOnAttachStateChangeListener)
        layer.hid.entries.reversed().forEach { (view, importance) -> view.importantForAccessibility = importance }
        layer.hid.clear()
        val opener = layer.opener?.get() ?: return
        if (opener.isAttachedToWindow && opener.isShown && touchExploration(opener)) {
            opener.post { opener.focusForAccessibility() }
        }
    }

    private fun View.focusForAccessibility() {
        performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
    }

    private fun touchExploration(view: View): Boolean =
        (view.context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE) as AccessibilityManager)
            .isTouchExplorationEnabled

    /** The view TalkBack is on, if any — the one to return to. */
    private fun focusedView(root: View): View? {
        if (root.createAccessibilityNodeInfo().isAccessibilityFocused) return root
        if (root !is ViewGroup) return null
        for (i in 0 until root.childCount) focusedView(root.getChildAt(i))?.let { return it }
        return null
    }

    /** The first view in [root] that TalkBack would read. */
    private fun firstReadable(root: View): View? {
        if (root.isShown && root.isImportantForAccessibility &&
            (!root.contentDescription.isNullOrEmpty() || (root as? android.widget.TextView)?.text?.isNotEmpty() == true)
        ) return root
        if (root !is ViewGroup) return null
        for (i in 0 until root.childCount) firstReadable(root.getChildAt(i))?.let { return it }
        return null
    }

    private companion object {
        const val PROP = "gearuiLayer"
        const val FOCUS_DELAY_MS = 150L
    }
}
