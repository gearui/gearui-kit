package com.gearui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.gearui.gestures.SwipeBackConfig
import com.gearui.gestures.swipeBack
import com.tencent.kuikly.compose.BackHandler
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxWithConstraints
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
 * Commit 2：transition layer 接入。pop 时同时渲染 current（已是 old previous）+ 出场 snapshot 两层，
 * 由 Animatable 驱动 [graphicsLayer] translationX；edge swipe 复用 `Modifier.swipeBack` 的 `onProgress`
 * 跟手位移，commit/cancel 路径完整。
 *
 * @param initialRoute 栈底 route 字符串
 * @param swipeBackEnabled 全局开关；个别 entry 可在 [NavOptions.swipeBackEnabled] 再禁用
 * @param handleBack 是否接管系统返回；内部通过 Kuikly `BackHandler` 注册，**仅当 [NavigatorController.canPop] 为 true 时挂**
 * @param onEntryRemoved entry **最终**从栈中移除时回调（commit pop 动画结束 / replace / popTo / resetTo）。
 *                      exactly-once：同一个 entry 不会被重复触发
 * @param content 根据当前渲染的 entry 渲染对应页面；transition 期间会被栈顶和 previous 两层各调一次
 *
 * 注：edge 热区**不**暴露给业务（review 4）。Navigator 内部固定用 96dp，避开 Android 系统返回
 * 手势在最左 ~24dp 的抢占（Phase 0 spike finding）。iOS 上 96dp 也工作良好。
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
    val removedRef = rememberUpdatedState(onEntryRemoved)
    val animScope = rememberCoroutineScope()

    val state: NavigatorState = remember(initialRoute) {
        NavigatorState(
            initial = NavEntry(route = initialRoute, key = generateKey(initialRoute, 0)),
            removeSaveableState = { key -> saveableHolder.removeState(key) },
            onEntryRemovedRef = { entry -> removedRef.value?.invoke(entry) },
            animScope = animScope,
        )
    }

    // 关键：BackHandler **仅在 canPop=true 时挂**。
    // Kuikly consumed = backPressCallbackList.isNotEmpty()，先于 callback 同步回 native；
    // 栈底如果还挂着 BackHandler，native 永远拿不到 BACK。
    // exiting 动画进行中也不接 BACK，防止动画期间触发新 pop。
    if (handleBack && state.canPop) {
        BackHandler {
            state.requestPop(PopReason.BackButton)
        }
    }

    // Edge 热区 hardcode 96dp，避开 Android 系统返回手势在 ~24dp 的抢占（Phase 0 spike finding）；
    // remember 一次，整个 Navigator 生命周期复用，不要每帧重建避免 swipeBack pointerInput key 变化
    val swipeConfig = remember { SwipeBackConfig(edgeWidthDp = 96f) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        state.bindViewportWidth(widthPx)

        // belowEntry = transition 期间渲染在底层的 entry：
        // - swipe 进行中 → entries[size-2]（真 previous，栈未动）
        // - commit 动画 / programmatic pop → entries.last()（栈已 pop，old previous 升顶）
        // 渲染顺序（Box children 后画的在上）：below → scrim → top
        val belowEntry = state.belowEntry
        if (belowEntry != null) {
            // 1. below 层（previous 页）
            saveableHolder.SaveableStateProvider(belowEntry.key) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // 视差：出场进度 0→1 期间 previous 从 -W*0.25 滑到 0
                            translationX = -widthPx * PARALLAX_RATIO * (1f - state.exitingFraction)
                        },
                ) {
                    val scope = EntryScopeImpl(
                        entry = belowEntry,
                        controller = state,
                        isTop = false,
                        isForeground = false,
                    )
                    scope.content(belowEntry)
                }
            }

            // 2. Scrim：盖在 below 之上、top 之下；fraction 0→1 时 alpha 0.15→0
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = -widthPx * PARALLAX_RATIO * (1f - state.exitingFraction)
                        alpha = SCRIM_MAX_ALPHA * (1f - state.exitingFraction)
                    }
                    .background(Color.Black),
            )
        }

        // 3. 上层 = 出场 snapshot（pop 动画 / swipe 期间）或当前栈顶（平时 / push 动画）
        val topEntry = state.topEntry
        saveableHolder.SaveableStateProvider(topEntry.key) {
            val isExitingLayer = state.exiting != null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // 出场层：fraction 0→1 → translationX 0→W；其它层不偏移
                        translationX = if (isExitingLayer) widthPx * state.exitingFraction else 0f
                    }
                    .let { base ->
                        // 仅当 canPop 且全局/本 entry 都允许 swipeBack 时给当前栈顶接手势
                        val enable = swipeBackEnabled &&
                            topEntry.options.swipeBackEnabled &&
                            topEntry.options.presentation == NavPresentation.Push &&
                            state.canPop &&
                            !isExitingLayer
                        if (enable) {
                            base.swipeBack(
                                enabled = true,
                                config = swipeConfig,
                                onStart = { state.beginSwipe() },
                                onProgress = { progress, _ -> state.updateSwipe(progress) },
                                onCancel = { state.cancelSwipe() },
                                onCommit = { state.commitSwipe() },
                            )
                        } else {
                            base
                        }
                    },
            ) {
                val scope = EntryScopeImpl(
                    entry = topEntry,
                    controller = state,
                    isTop = !isExitingLayer,
                    isForeground = !state.isTransitioning,
                )
                scope.content(topEntry)
            }
        }
    }
}

/** Previous 层视差比例：出场进度 0 时 -W*0.25，1 时 0。设计参考 iOS / 微信。 */
private const val PARALLAX_RATIO = 0.25f

/** 出场期间贴在 previous 层上的最大暗罩透明度。 */
private const val SCRIM_MAX_ALPHA = 0.15f

// ─────────────────────────────────────────────────────────────────────────────
// internal impl
// ─────────────────────────────────────────────────────────────────────────────

@Stable
internal class NavigatorState(
    initial: NavEntry,
    private val removeSaveableState: (String) -> Unit,
    private val onEntryRemovedRef: (NavEntry) -> Unit,
    private val animScope: CoroutineScope,
) : NavigatorController {

    private val _entries = mutableStateListOf(initial)

    /** exactly-once guard：每个 key 只通知一次 onEntryRemoved + removeState。 */
    private val removedKeys = mutableSetOf<String>()

    /** 同 route 多次 push 时生成唯一 key 用。 */
    private var keyCounter: Int = 1

    /** PopDecision.Pending 状态：栈顶被业务确认前不允许 push/pop。 */
    private var pendingEntry: NavEntry? by mutableStateOf(null)

    /** transition 出场层 snapshot：null = 平时；非 null = pop animation 或 swipe in progress。 */
    private var _exiting: NavEntry? by mutableStateOf(null)

    /** Swipe 模式开关：true 期间 fraction 由 [updateSwipe] 手动 snap；false 期间由 animation 驱动。 */
    private var _swipeMode: Boolean by mutableStateOf(false)

    /** 出场层占屏宽比例 0..1：0=完全可见，1=完全在右屏外。 */
    private val _exitingFractionAnim = Animatable(0f)

    /** Viewport 宽（像素）。由 [Navigator] BoxWithConstraints 注入。 */
    private var viewportWidth: Float = 0f

    val exiting: NavEntry? get() = _exiting
    val exitingFraction: Float get() = _exitingFractionAnim.value

    val topEntry: NavEntry get() = _exiting ?: _entries.last()
    val belowEntry: NavEntry?
        get() = if (_exiting != null) {
            if (_swipeMode) _entries.getOrNull(_entries.size - 2) else _entries.lastOrNull()
        } else null

    override val current: NavEntry
        get() = _entries.last()

    override val previous: NavEntry?
        get() = _entries.getOrNull(_entries.size - 2)

    override val canPop: Boolean
        get() = _entries.size > 1 && pendingEntry == null && _exiting == null

    override val isTransitioning: Boolean
        get() = _exiting != null

    fun bindViewportWidth(width: Float) {
        viewportWidth = width
    }

    /**
     * v1 硬不变式：transition / pending 期间**所有** stack-mutating API 一律拒绝。
     * 否则 swipe 中途 replace/resetTo/popTo 会让出场 snapshot 指向已被移除的栈顶，
     * 或者 pending 状态下业务行为不确定。需要切栈的业务先 [forcePop] / 等动画完成。
     */
    private val isMidFlight: Boolean
        get() = _exiting != null || pendingEntry != null

    override fun push(route: String, key: String?, options: NavOptions) {
        if (isMidFlight) return
        val newKey = key ?: generateKey(route, keyCounter++)
        _entries.add(NavEntry(route = route, key = newKey, options = options))
    }

    override fun pop(): Boolean = requestPop(PopReason.Programmatic)

    override fun forcePop(): Boolean {
        // 显式跳过 onPopRequest——是 Pending 的「业务确认后继续返回」唯一出路；
        // 因此 pendingEntry != null 是 forcePop 的**预期**场景，单独放行。
        if (_entries.size <= 1) return false
        if (_exiting != null) return false
        pendingEntry = null
        val top = _entries.removeAt(_entries.size - 1)
        startCommitPopAnim(top)
        return true
    }

    override fun popTo(route: String): Boolean {
        if (isMidFlight) return false
        val idx = _entries.indexOfLast { it.route == route }
        if (idx < 0 || idx == _entries.size - 1) return false
        // 中间层 entry 立刻清掉（不动画），只有最顶层走 pop 动画
        while (_entries.size > idx + 2) {
            val removed = _entries.removeAt(_entries.size - 2)
            notifyRemoved(removed)
        }
        val top = _entries.removeAt(_entries.size - 1)
        startCommitPopAnim(top)
        return true
    }

    override fun replace(route: String, key: String?, options: NavOptions) {
        if (isMidFlight) return
        if (_entries.isEmpty()) return
        val old = _entries.removeAt(_entries.size - 1)
        notifyRemoved(old)
        val newKey = key ?: generateKey(route, keyCounter++)
        _entries.add(NavEntry(route = route, key = newKey, options = options))
    }

    override fun resetTo(route: String) {
        if (isMidFlight) return
        val snapshot = _entries.toList()
        _entries.clear()
        snapshot.forEach { notifyRemoved(it) }
        _entries.add(NavEntry(route = route, key = generateKey(route, keyCounter++)))
    }

    /**
     * 触发 pop。按 onPopRequest 的决定分支：
     * - Allow → 真 pop + 跑出场动画
     * - Deny → 业务吃掉，栈不动，返回 false
     * - Pending → 本次 BACK 视为 consumed；Navigator 不挂 continuation，业务自己 forcePop
     */
    internal fun requestPop(reason: PopReason): Boolean {
        if (_entries.size <= 1) return false
        if (pendingEntry != null) return false
        if (_exiting != null) return false
        val top = _entries.last()
        val decision = top.options.onPopRequest?.invoke(PopRequest(top, reason)) ?: PopDecision.Allow
        return when (decision) {
            PopDecision.Allow -> {
                _entries.removeAt(_entries.size - 1)
                startCommitPopAnim(top)
                true
            }

            PopDecision.Deny -> false

            PopDecision.Pending -> {
                pendingEntry = top
                false
            }
        }
    }

    private fun startCommitPopAnim(outgoing: NavEntry) {
        _swipeMode = false
        _exiting = outgoing
        animScope.launch {
            _exitingFractionAnim.snapTo(0f)
            _exitingFractionAnim.animateTo(1f, tween(durationMillis = ANIM_POP_MS))
            notifyRemoved(outgoing)
            _exiting = null
        }
    }

    // ───── swipe API（由 Modifier.swipeBack onStart/onProgress/onCancel/onCommit 触发） ─────

    /** 边缘手势识别成功：把视觉层从栈拆出，进入 swipe 跟手模式（entries 不变）。 */
    internal fun beginSwipe() {
        if (_entries.size <= 1) return
        if (_exiting != null) return
        if (pendingEntry != null) return
        _exiting = _entries.last()
        _swipeMode = true
        // fraction 起点是 0；onProgress 会持续推进
    }

    internal fun updateSwipe(progress: Float) {
        if (!_swipeMode) return
        animScope.launch {
            _exitingFractionAnim.snapTo(progress.coerceIn(0f, 1f))
        }
    }

    /** 用户松手且过阈值：跑完剩余动画后真 pop。 */
    internal fun commitSwipe() {
        val outgoing = _exiting ?: return
        if (!_swipeMode) return
        _swipeMode = false
        // 此时 entries 仍含 outgoing 在栈顶；先 remove 再跑剩余动画 + notify
        _entries.removeAt(_entries.size - 1)
        animScope.launch {
            _exitingFractionAnim.animateTo(1f, tween(durationMillis = ANIM_SWIPE_COMMIT_MS))
            notifyRemoved(outgoing)
            _exiting = null
        }
    }

    /** 用户松手但未过阈值：回弹到栈顶位置，entries 不变。 */
    internal fun cancelSwipe() {
        if (!_swipeMode) return
        _swipeMode = false
        animScope.launch {
            _exitingFractionAnim.animateTo(0f, spring())
            _exiting = null
        }
    }

    private fun notifyRemoved(entry: NavEntry) {
        if (removedKeys.add(entry.key)) {
            onEntryRemovedRef(entry)
            removeSaveableState(entry.key)
        }
    }
}

private const val ANIM_POP_MS: Int = 220
private const val ANIM_SWIPE_COMMIT_MS: Int = 160

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
