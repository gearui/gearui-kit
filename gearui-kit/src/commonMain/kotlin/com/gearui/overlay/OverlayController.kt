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
     */
    fun dismiss(id: Long) {
        println("[GearUI] Overlay.dismiss id=$id")
        val item = _items.find { it.id == id }
        item?.onDismiss?.invoke()
        _items.removeAll { it.id == id }
    }

    /**
     * Dismisses every Overlay
     */
    fun dismissAll() {
        println("[GearUI] Overlay.dismissAll count=${_items.size}")
        _items.forEach { it.onDismiss?.invoke() }
        _items.clear()
    }

    /**
     * Whether any Overlay is showing
     */
    fun hasOverlay(): Boolean = _items.isNotEmpty()

    /**
     * Dispatches an event; each Overlay dismisses or not according to its DismissPolicy
     *
     * This is the core method of the Overlay Runtime.
     * All dismissal logic lives here; component code must contain none of it.
     */
    fun dispatchEvent(event: OverlayEvent) {
        println("[GearUI] Overlay.dispatchEvent event=$event, items=${_items.size}")

        val itemsToRemove = _items.filter { item ->
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
        itemsToRemove.forEach { item ->
            item.onDismiss?.invoke()
        }
        _items.removeAll { it in itemsToRemove }
    }

    /**
     * Dismisses Overlays matching a predicate
     *
     * For more flexible dismissal cases
     */
    fun dismissByPolicy(predicate: (OverlayDismissPolicy) -> Boolean) {
        val itemsToRemove = _items.filter { predicate(it.options.dismissPolicy) }
        itemsToRemove.forEach { item ->
            item.onDismiss?.invoke()
        }
        _items.removeAll { it in itemsToRemove }
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
    val onDismiss: (() -> Unit)? = null
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
