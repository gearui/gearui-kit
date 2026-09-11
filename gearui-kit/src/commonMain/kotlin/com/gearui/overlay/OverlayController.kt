package com.gearui.overlay

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.geometry.Rect

/**
 * Overlay event type
 *
 * Used by the Runtime for unified event dispatch
 */
enum class OverlayEvent {
    OutsideClick,   // 点击外部
    Scroll,         // 页面滚动
    BackPress,      // 返回键
    RouteChange,    // 路由切换
    Timeout,        // 定时超时
    AnchorDetached, // 锚点消失
}

/**
 * OverlayController - core Overlay state manager
 *
 * The single source of truth for the Overlay system.
 * Every dismissal is declared through DismissPolicy and scheduled by the Runtime.
 */
@Stable
class OverlayController {

    private val _items = mutableStateListOf<OverlayItem>()
    internal val items: List<OverlayItem> get() = _items

    private var nextId = 0L

    /**
     * Shows an Overlay
     *
     * @param anchorBounds anchor position; null means position by placement
     * @param options configuration, including dismissPolicy
     * @param onDismiss called when dismissed
     * @param content the content
     * @return the Overlay ID, usable with dismiss
     */
    fun show(
        anchorBounds: Rect? = null,
        options: OverlayOptions = OverlayOptions(),
        onDismiss: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ): Long {
        val id = nextId++

        _items += OverlayItem(
            id = id,
            anchorBounds = anchorBounds,
            options = options,
            content = content,
            onDismiss = onDismiss
        )

        println("[GearUI] Overlay.show id=$id, policy=${options.dismissPolicy}")
        return id
    }

    /**
     * Dismisses one Overlay
     *
     * 🔴 Dismissal starts the exit animation; it does not unmount anything.
     *
     * The item stays in the list with [OverlayItem.exiting] set, so the host can animate
     * it away, and [OverlayHost] calls [remove] once that finishes. Unmounting here
     * instead would make every exit instant, however carefully the animation is written:
     * content that is gone from the composition cannot animate.
     *
     * `onDismiss` still fires immediately. It is what flips the caller's `visible` flag,
     * and delaying it would leave the caller believing the overlay is still open for as
     * long as the animation runs.
     */
    fun dismiss(id: Long) {
        println("[GearUI] Overlay.dismiss id=$id")
        val item = _items.find { it.id == id } ?: return
        if (item.exiting.value) return
        item.exiting.value = true
        item.onDismiss?.invoke()
    }

    /**
     * Unmounts an Overlay whose exit animation has finished.
     *
     * Called by the host, not by components — components call [dismiss].
     */
    internal fun remove(id: Long) {
        _items.removeAll { it.id == id }
    }

    /**
     * Dismisses every Overlay
     */
    fun dismissAll() {
        println("[GearUI] Overlay.dismissAll count=${_items.size}")
        _items.toList().forEach { dismiss(it.id) }
    }

    /**
     * Whether any Overlay is showing
     *
     * One that is playing its exit animation does not count: it is on its way out and
     * must not, for instance, keep swallowing the back button.
     */
    fun hasOverlay(): Boolean = _items.any { !it.exiting.value }

    /**
     * Dispatches an event; each Overlay dismisses or not according to its DismissPolicy
     *
     * This is the core method of the Overlay Runtime.
     * All dismissal logic lives here; component code must contain none of it.
     */
    fun dispatchEvent(event: OverlayEvent) {
        println("[GearUI] Overlay.dispatchEvent event=$event, items=${_items.size}")

        val itemsToRemove = _items.filter { item ->
            if (item.exiting.value) return@filter false
            val policy = item.options.dismissPolicy
            val shouldRemove = when (event) {
                OverlayEvent.OutsideClick -> policy.outsideClick
                OverlayEvent.Scroll -> policy.scroll
                OverlayEvent.BackPress -> policy.backPress
                OverlayEvent.RouteChange -> policy.routeChange
                OverlayEvent.Timeout -> true  // timeout 由定时器触发，直接关闭
                OverlayEvent.AnchorDetached -> policy.anchorDetached
            }
            println("[GearUI] Overlay item id=${item.id}, policy.scroll=${policy.scroll}, shouldRemove=$shouldRemove")
            shouldRemove
        }

        println("[GearUI] Overlay itemsToRemove=${itemsToRemove.size}")
        itemsToRemove.forEach { dismiss(it.id) }
    }

    /**
     * Dismisses Overlays matching a predicate
     *
     * For more flexible dismissal cases
     */
    fun dismissByPolicy(predicate: (OverlayDismissPolicy) -> Boolean) {
        _items.filter { !it.exiting.value && predicate(it.options.dismissPolicy) }
            .forEach { dismiss(it.id) }
    }
}

/**
 * Overlay item data
 */
internal data class OverlayItem(
    val id: Long,
    val anchorBounds: Rect?,
    val options: OverlayOptions,
    val content: @Composable () -> Unit,
    val onDismiss: (() -> Unit)? = null,
    /**
     * Set the moment the overlay is dismissed; cleared only by unmounting.
     *
     * A MutableState rather than a field of the data class so that marking it does not
     * replace the item in the list — replacing it would give the content a new identity
     * and restart it from its enter animation, mid-exit.
     */
    val exiting: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Global Overlay Controller, injected through a CompositionLocal
 */
val LocalOverlayController = staticCompositionLocalOf<OverlayController> {
    error("OverlayController not provided. Did you forget to wrap your app with OverlayRoot?")
}

/**
 * OverlayManager - global event notification entry point
 *
 * Static methods that let outside components (ScrollView and friends) notify the Overlay system.
 * Events originate in component code and are consumed by the Overlay Runtime.
 */
object OverlayManager {
    private var controller: OverlayController? = null

    /**
     * Internal: binds the Controller
     */
    internal fun bind(controller: OverlayController) {
        println("[GearUI] OverlayManager.bind controller=$controller")
        this.controller = controller
    }

    /**
     * Internal: unbinds the Controller
     */
    internal fun unbind() {
        println("[GearUI] OverlayManager.unbind")
        this.controller = null
    }

    /**
     * Notifies a scroll event
     *
     * Called by scrolling components such as ScrollView / LazyColumn
     */
    fun notifyScroll() {
        println("[GearUI] OverlayManager.notifyScroll controller=$controller")
        controller?.dispatchEvent(OverlayEvent.Scroll)
    }

    /**
     * Notifies a tap-outside event
     */
    fun notifyOutsideClick() {
        controller?.dispatchEvent(OverlayEvent.OutsideClick)
    }

    /**
     * Notifies a back-key event
     */
    fun notifyBackPress() {
        controller?.dispatchEvent(OverlayEvent.BackPress)
    }

    /**
     * Notifies a route change event
     */
    fun notifyRouteChange() {
        controller?.dispatchEvent(OverlayEvent.RouteChange)
    }

    /**
     * Whether any Overlay is showing
     */
    fun hasOverlay(): Boolean = controller?.hasOverlay() ?: false
}
