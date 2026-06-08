package com.gearui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.BackHandler
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier

/**
 * Navigator v1 入口。所有业务页面（消息、联系人、个人资料、群、二维码…）的栈式跳转都用这个。
 *
 * 关键不变式（详见 `gearui-kit/docs/NAVIGATOR_SWIPE_BACK_DESIGN.md`）：
 *
 * 1. **不**替代 Kuikly `@Page`——这是单个 ComposeContainer 内部的栈
 * 2. **Kuikly BACK 是 topmost-only**：`backPressCallbackList.isNotEmpty()` 决定 consumed，
 *    `dispatchOnBackEvent()` 只调 `list.last()`。因此 Navigator 只挂**一个**总 BackHandler，
 *    且**只在 `canPop = true` 时挂**，栈底必须 dispose 让出 native
 * 3. Dialog / Sheet / ActionSheet 在 Navigator 之上注册的 BackHandler 自动成为 list.last() 先吃
 * 4. **不**公开 typed params；业务用 outer state holder + [onEntryRemoved] 在 entry 真正移除时清参数
 *
 * Commit 1：core stack + BackHandler 接入 + SaveableStateHolder。不含 transition 动画（Commit 2 加）。
 *
 * @param initialRoute 栈底 route 字符串
 * @param swipeBackEnabled 全局开关；个别 entry 可在 [NavOptions.swipeBackEnabled] 再禁用
 * @param handleBack 是否接管系统返回；内部通过 Kuikly `BackHandler` 注册，**仅当 [NavigatorController.canPop] 为 true 时挂**
 * @param onEntryRemoved entry **最终**从栈中移除时回调（commit pop 动画结束 / replace / popTo / resetTo）。
 *                      exactly-once：同一个 entry 不会被重复触发
 * @param content 根据当前渲染的 entry 渲染对应页面；transition 期间会被栈顶和 previous 两层各调一次
 */
@Composable
fun Navigator(
    initialRoute: String,
    modifier: Modifier = Modifier,
    swipeBackEnabled: Boolean = true,
    handleBack: Boolean = true,
    onEntryRemoved: ((NavEntry) -> Unit)? = null,
    content: @Composable EntryScope.(NavEntry) -> Unit,
) {
    val saveableHolder = rememberSaveableStateHolder()
    // rememberUpdatedState 让内部状态机始终用最新的 onEntryRemoved，避免 lambda capture 旧版本
    val removedRef = rememberUpdatedState(onEntryRemoved)

    val state: NavigatorState = remember(initialRoute) {
        NavigatorState(
            initial = NavEntry(route = initialRoute, key = generateKey(initialRoute, 0)),
            removeSaveableState = { key -> saveableHolder.removeState(key) },
            onEntryRemovedRef = { entry -> removedRef.value?.invoke(entry) },
        )
    }

    // 关键：BackHandler **仅在 canPop=true 时挂**。
    // Kuikly consumed = backPressCallbackList.isNotEmpty()，先于 callback 同步回 native；
    // 栈底如果还挂着 BackHandler，native 永远拿不到 BACK。
    if (handleBack && state.canPop) {
        BackHandler {
            state.requestPop(PopReason.BackButton)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val cur = state.current
        // SaveableStateProvider 让 entry 各自的 rememberSaveable 状态跟着 key 走；
        // 进栈 → 包住；出栈最终结算时 state.notifyRemoved() 调 saveableHolder.removeState(key) 释放
        saveableHolder.SaveableStateProvider(cur.key) {
            val scope = EntryScopeImpl(
                entry = cur,
                controller = state,
                isTop = true,
                isForeground = !state.isTransitioning,
            )
            scope.content(cur)
        }
        // Commit 2 将在这里加 previous 层渲染 + transition container；
        // swipeBackEnabled / cur.options.swipeBackEnabled / cur.options.presentation 一并参与判断
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// internal impl
// ─────────────────────────────────────────────────────────────────────────────

@Stable
internal class NavigatorState(
    initial: NavEntry,
    private val removeSaveableState: (String) -> Unit,
    private val onEntryRemovedRef: (NavEntry) -> Unit,
) : NavigatorController {

    private val _entries = mutableStateListOf(initial)

    /** exactly-once guard：每个 key 只通知一次 onEntryRemoved + removeState。 */
    private val removedKeys = mutableSetOf<String>()

    /** 同 route 多次 push 时生成唯一 key 用。 */
    private var keyCounter: Int = 1

    /** PopDecision.Pending 状态：栈顶被业务确认前不允许 push/pop。 */
    private var pendingEntry: NavEntry? by mutableStateOf(null)

    /** transition 进行中标记（Commit 2 启用 push/pop 动画时由 transition 容器置位）。 */
    private var transitioning: Boolean by mutableStateOf(false)

    override val current: NavEntry
        get() = _entries.last()

    override val previous: NavEntry?
        get() = _entries.getOrNull(_entries.size - 2)

    override val canPop: Boolean
        get() = _entries.size > 1 && pendingEntry == null

    override val isTransitioning: Boolean
        get() = transitioning

    internal fun setTransitioning(value: Boolean) {
        transitioning = value
    }

    override fun push(route: String, key: String?, options: NavOptions) {
        if (pendingEntry != null) return
        val newKey = key ?: generateKey(route, keyCounter++)
        _entries.add(NavEntry(route = route, key = newKey, options = options))
    }

    override fun pop(): Boolean = requestPop(PopReason.Programmatic)

    override fun forcePop(): Boolean {
        if (_entries.size <= 1) return false
        pendingEntry = null
        removeTop()
        return true
    }

    override fun popTo(route: String): Boolean {
        val idx = _entries.indexOfLast { it.route == route }
        if (idx < 0 || idx == _entries.size - 1) return false
        while (_entries.size > idx + 1) {
            val removed = _entries.removeAt(_entries.size - 1)
            notifyRemoved(removed)
        }
        pendingEntry = null
        return true
    }

    override fun replace(route: String, key: String?, options: NavOptions) {
        if (_entries.isEmpty()) return
        val old = _entries.removeAt(_entries.size - 1)
        notifyRemoved(old)
        val newKey = key ?: generateKey(route, keyCounter++)
        _entries.add(NavEntry(route = route, key = newKey, options = options))
        pendingEntry = null
    }

    override fun resetTo(route: String) {
        val snapshot = _entries.toList()
        _entries.clear()
        snapshot.forEach { notifyRemoved(it) }
        _entries.add(NavEntry(route = route, key = generateKey(route, keyCounter++)))
        pendingEntry = null
    }

    /**
     * 触发 pop。按 onPopRequest 的决定分支：
     * - Allow → 真 pop（栈底返回 false）
     * - Deny → 业务吃掉，栈不动，返回 false
     * - Pending → 本次 BACK 视为 consumed；Navigator 不挂 continuation，业务自己 forcePop
     */
    internal fun requestPop(reason: PopReason): Boolean {
        if (_entries.size <= 1) return false
        if (pendingEntry != null) return false
        val top = _entries.last()
        val decision = top.options.onPopRequest?.invoke(PopRequest(top, reason)) ?: PopDecision.Allow
        return when (decision) {
            PopDecision.Allow -> {
                removeTop()
                true
            }

            PopDecision.Deny -> false

            PopDecision.Pending -> {
                pendingEntry = top
                false
            }
        }
    }

    private fun removeTop() {
        val removed = _entries.removeAt(_entries.size - 1)
        notifyRemoved(removed)
    }

    private fun notifyRemoved(entry: NavEntry) {
        if (removedKeys.add(entry.key)) {
            onEntryRemovedRef(entry)
            removeSaveableState(entry.key)
        }
    }
}

@Stable
private class EntryScopeImpl(
    override val entry: NavEntry,
    override val controller: NavigatorController,
    override val isTop: Boolean,
    override val isForeground: Boolean,
) : EntryScope

/**
 * 生成内部唯一 key。同 route 多次 push 业务可自己传稳定 key 覆盖，否则用 `route#counter`。
 */
private fun generateKey(route: String, counter: Int): String = "$route#$counter"
