package com.tencent.kuikly.h5app

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

/**
 * Keyboard and screen-reader access for the Web.
 *
 * Kuikly's Web renderer turns a node's label into `aria-label` and drops the rest of
 * what Android and iOS read natively, so a GearUI app in a browser could not be used
 * from the keyboard. This host fills the gap from three view properties:
 *
 * - `accessibilityInfo` (the node is clickable): a place in the tab order, and
 *   Enter/Space replayed as the pointer press the renderer already listens for.
 * - `accessibilityRole`: the matching ARIA role.
 * - `gearuiLayer`, set by GearUI's overlays and navigation: `modal` takes focus and
 *   makes everything outside it inert; `popup` only answers Escape; both return
 *   focus to where it was when they close; `hidden` (a page beneath the top one, an
 *   unselected tab) is inert.
 *
 * Escape sends Back ([onBack]) while a modal or popup is open, and only then: Back
 * on a page would pop it, which a browser user does not expect from Escape.
 */
object KeyboardAccess {
    private const val INFO = "accessibilityInfo"
    private const val ROLE = "accessibilityRole"
    private const val LAYER = "gearuiLayer"

    /** Delivers Back to the page; set by the host before the page attaches. */
    var onBack: () -> Unit = {}

    fun set(ele: Element, key: String, value: Any): Boolean = when (key) {
        INFO -> {
            // "<clickable> <longClickable>", each 0 or 1.
            if (value.toString().substringBefore(' ') == "1") makeFocusable(ele) else removeFocusable(ele)
            true
        }
        ROLE -> {
            val role = when (value.toString()) {
                "button" -> "button"
                "checkbox" -> "checkbox"
                "image" -> "img"
                "search" -> "searchbox"
                else -> null
            }
            if (role != null) ele.setAttribute("role", role) else ele.removeAttribute("role")
            true
        }
        LAYER -> {
            setLayer(ele, value.toString())
            true
        }
        else -> false
    }

    fun reset(ele: Element, key: String): Boolean = when (key) {
        INFO -> { removeFocusable(ele); true }
        ROLE -> { ele.removeAttribute("role"); true }
        LAYER -> { setLayer(ele, ""); true }
        else -> false
    }

    // ----- Clickable nodes -----

    private fun makeFocusable(ele: Element) {
        if (ele.asDynamic().gearuiKeyHandler != null) return
        ele.setAttribute("tabindex", "0")
        val handler: (Event) -> Unit = { event ->
            val key = event.unsafeCast<KeyboardEvent>()
            // Only the node itself: a focused text field inside handles its own keys.
            if (key.target == ele && (key.key == "Enter" || key.key == " ") && !key.repeat) {
                key.preventDefault()
                press(ele)
            }
        }
        ele.asDynamic().gearuiKeyHandler = handler
        ele.addEventListener("keydown", handler)
    }

    private fun removeFocusable(ele: Element) {
        val handler = ele.asDynamic().gearuiKeyHandler ?: return
        ele.removeEventListener("keydown", handler.unsafeCast<(Event) -> Unit>())
        ele.asDynamic().gearuiKeyHandler = null
        ele.removeAttribute("tabindex")
    }

    /**
     * A tap at the centre of [ele]. Compose does not listen for DOM `click`: its
     * gestures come from the pointer events the root view receives, so a key press
     * is replayed as a pointer going down and up in place.
     */
    private fun press(ele: Element) {
        val box = ele.getBoundingClientRect()
        val init: dynamic = js("({})")
        init.bubbles = true
        init.cancelable = true
        init.composed = true
        init.clientX = box.left + box.width / 2
        init.clientY = box.top + box.height / 2
        init.pointerId = KEYBOARD_POINTER_ID
        init.pointerType = "mouse"
        init.isPrimary = true
        init.button = 0
        for (type in arrayOf("pointerdown", "pointerup")) {
            init.buttons = if (type == "pointerdown") 1 else 0
            val event: Event = js("new PointerEvent(type, init)")
            ele.dispatchEvent(event)
        }
    }

    // Any id no real pointer uses, so the renderer tracks this press as its own.
    private const val KEYBOARD_POINTER_ID = 9_999

    // ----- Layers -----

    private class Layer(val ele: Element, val kind: String, val opener: Element?) {
        var focused = false
    }

    /** Open layers, oldest first. */
    private val layers = mutableListOf<Layer>()

    /** Elements this host made inert, so it never touches anyone else's. */
    private var inerted = setOf<Element>()

    private var installed = false

    private fun setLayer(ele: Element, kind: String) {
        install()
        val old = layers.firstOrNull { it.ele == ele }
        if (old?.kind == kind) return
        if (old != null) close(old)
        when (kind) {
            "modal", "popup" -> {
                val active = document.activeElement
                layers += Layer(ele, kind, active?.takeIf { it != document.body && !ele.contains(it) })
                if (kind == "modal") {
                    ele.setAttribute("role", "dialog")
                    ele.setAttribute("aria-modal", "true")
                }
                ele.setAttribute("tabindex", "-1")
            }
            "hidden" -> layers += Layer(ele, kind, null)
        }
        settle()
    }

    /**
     * Applies the layers to the document. A layer's property is set before its view
     * is inserted, so this runs again on every insertion: only then has a modal
     * siblings to make inert and a place to take focus.
     */
    private fun settle() {
        refreshInert()
        val top = layers.lastOrNull { it.kind != "hidden" } ?: return
        if (top.focused || !top.ele.isConnected) return
        top.focused = true
        // A popup leaves focus in a text field — a combo box keeps typing while its
        // list is open. Otherwise focus goes in: left on the trigger, the next Tab
        // would scroll the page, and scrolling closes a dropdown.
        val active = document.activeElement
        if (top.kind == "popup" && (active?.tagName == "INPUT" || active?.tagName == "TEXTAREA")) return
        // The container, not its first control: its content may not be built yet.
        // A screen reader announces the dialog; Tab goes on to its first control.
        top.ele.unsafeCast<HTMLElement>().focus()
    }

    private fun close(layer: Layer) {
        layers.remove(layer)
        listOf("role", "aria-modal", "tabindex").forEach { layer.ele.removeAttribute(it) }
        refreshInert()
        // Back to the trigger — if focus was in the layer or has nowhere to be, and the
        // trigger is still there to take it.
        val active = document.activeElement
        val lost = active == null || active == document.body || layer.ele.contains(active) || !active.isConnected
        val opener = layer.opener
        if (lost && opener != null && opener.isConnected && opener !in inerted) {
            opener.unsafeCast<HTMLElement>().focus()
        }
    }

    /** Inert: every hidden layer, and everything outside the topmost modal. */
    private fun refreshInert() {
        val want = mutableSetOf<Element>()
        layers.filter { it.kind == "hidden" }.forEach { want += it.ele }
        layers.lastOrNull { it.kind == "modal" }?.let { top ->
            var node: Element = top.ele
            while (true) {
                val parent = node.parentElement ?: break
                val siblings = parent.children
                for (i in 0 until siblings.length) {
                    val sibling = siblings.item(i)
                    if (sibling != null && sibling != node) want += sibling
                }
                node = parent
            }
        }
        (inerted - want).forEach { it.removeAttribute("inert") }
        (want - inerted).forEach { it.setAttribute("inert", "") }
        inerted = want
    }

    private fun install() {
        if (installed) return
        installed = true
        // A layer usually leaves by being removed, which resets no property.
        MutationObserver { _, _ ->
            layers.filter { !it.ele.isConnected }.forEach { close(it) }
            settle()
        }.observe(document.body!!, MutationObserverInit(childList = true, subtree = true))
        window.addEventListener("keydown", { event ->
            val key = event.unsafeCast<KeyboardEvent>()
            if (key.key == "Escape" && layers.any { it.kind != "hidden" && it.ele.isConnected }) {
                key.preventDefault()
                onBack()
            }
        })
    }
}
