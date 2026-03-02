package com.gearui.overlay

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.geometry.Rect

/**
 * Overlay 事件类型
 *
 * 用于 Runtime 统一事件分发
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
 * GearOverlayController - Overlay 核心状态管理器
 *
 * 这是 Overlay 系统的唯一真相来源（Single Source of Truth）
 * 所有关闭逻辑通过 DismissPolicy 声明，由 Runtime 统一调度
 */
@Stable
class GearOverlayController {

    private val _items = mutableStateListOf<GearOverlayItem>()
    internal val items: List<GearOverlayItem> get() = _items

    private var nextId = 0L

    /**
     * 显示 Overlay
     *
     * @param anchorBounds 锚点位置（可选，null 则使用 placement 定位）
     * @param options 配置选项（包含 dismissPolicy）
     * @param onDismiss 关闭时的回调
     * @param content 内容
     * @return Overlay ID，可用于 dismiss
     */
    fun show(
        anchorBounds: Rect? = null,
        options: GearOverlayOptions = GearOverlayOptions(),
        onDismiss: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ): Long {
        val id = nextId++

        _items += GearOverlayItem(
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
     * 关闭指定 Overlay
     */
    fun dismiss(id: Long) {
        println("[GearUI] Overlay.dismiss id=$id")
        val item = _items.find { it.id == id }
        item?.onDismiss?.invoke()
        _items.removeAll { it.id == id }
    }

    /**
     * 关闭所有 Overlay
     */
    fun dismissAll() {
        println("[GearUI] Overlay.dismissAll count=${_items.size}")
        _items.forEach { it.onDismiss?.invoke() }
        _items.clear()
    }

    /**
     * 是否有 Overlay 显示
     */
    fun hasOverlay(): Boolean = _items.isNotEmpty()

    /**
     * 分发事件，根据各 Overlay 的 DismissPolicy 决定是否关闭
     *
     * 这是 Overlay Runtime 的核心方法
     * 所有关闭逻辑集中在此处理，组件层不应该有任何关闭逻辑
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
     * 根据条件关闭 Overlay
     *
     * 用于更灵活的关闭场景
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
 * Overlay 项数据
 */
internal data class GearOverlayItem(
    val id: Long,
    val anchorBounds: Rect?,
    val options: GearOverlayOptions,
    val content: @Composable () -> Unit,
    val onDismiss: (() -> Unit)? = null
)

/**
 * 全局 Overlay Controller（通过 CompositionLocal 注入）
 */
val LocalGearOverlayController = staticCompositionLocalOf<GearOverlayController> {
    error("GearOverlayController not provided. Did you forget to wrap your app with GearOverlayRoot?")
}

/**
 * OverlayManager - 全局事件通知入口
 *
 * 提供静态方法供外部组件（如 ScrollView）通知 Overlay 系统
 * 事件源在组件层，消费在 Overlay Runtime
 */
object OverlayManager {
    private var controller: GearOverlayController? = null

    /**
     * 内部方法：绑定 Controller
     */
    internal fun bind(controller: GearOverlayController) {
        println("[GearUI] OverlayManager.bind controller=$controller")
        this.controller = controller
    }

    /**
     * 内部方法：解绑 Controller
     */
    internal fun unbind() {
        println("[GearUI] OverlayManager.unbind")
        this.controller = null
    }

    /**
     * 通知滚动事件
     *
     * 由 ScrollView / LazyColumn 等滚动组件调用
     */
    fun notifyScroll() {
        println("[GearUI] OverlayManager.notifyScroll controller=$controller")
        controller?.dispatchEvent(OverlayEvent.Scroll)
    }

    /**
     * 通知点击外部事件
     */
    fun notifyOutsideClick() {
        controller?.dispatchEvent(OverlayEvent.OutsideClick)
    }

    /**
     * 通知返回键事件
     */
    fun notifyBackPress() {
        controller?.dispatchEvent(OverlayEvent.BackPress)
    }

    /**
     * 通知路由切换事件
     */
    fun notifyRouteChange() {
        controller?.dispatchEvent(OverlayEvent.RouteChange)
    }

    /**
     * 是否有 Overlay 显示
     */
    fun hasOverlay(): Boolean = controller?.hasOverlay() ?: false
}
