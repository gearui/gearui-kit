package com.gearui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.tencent.kuikly.compose.ui.zIndex
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
    controller: NavigatorController? = null,
    content: @Composable EntryScope.(NavEntry) -> Unit,
) {
    val saveableHolder = rememberSaveableStateHolder()
    val removedRef = rememberUpdatedState(onEntryRemoved)
    val animScope = rememberCoroutineScope()

    // controller 由外部传入（如 [rememberNavigatorController] 的产物）时直接使用；
    // 否则内部 remember 一份兼容旧 caller（sample / Phase 2 已有调用方）。
    val state: NavigatorState = remember(controller, initialRoute) {
        (controller as? NavigatorState)
            ?: NavigatorState(initialRoute = initialRoute)
    }

    // attach：每帧 SideEffect 模式 — 把 Composable 范围的 saveable/anim/onEntryRemoved
    // 注入 state；离开 Composition 时 detach，避免 logout reset 后回调跑到旧 holder。
    DisposableEffect(state, saveableHolder, animScope) {
        state.attach(
            saveable = { key -> saveableHolder.removeState(key) },
            onEntryRemovedRef = { entry -> removedRef.value?.invoke(entry) },
            animScope = animScope,
        )
        onDispose { state.detach() }
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

    // 全屏手势（微信 Android 同款）：起始点不限于左边缘，屏幕任意位置右滑即可返回。
    //
    // 为什么不再用边缘热区 + setSystemGestureExclusionRects：
    // - Android 对 back-gesture 区域的 exclusion 有 200dp 高度硬限制（系统只取 rect 底部
    //   200dp），全高豁免不可能 —— 用户在屏幕中部从最左边缘起手永远会被系统 predictive
    //   back 抢走。
    // - 微信 Android 的真实行为就是「全屏右滑返回」：从屏幕中间右滑返回上一页；从最左
    //   边缘起手则交给系统手势（predictive back commit → BACK → Navigator pop 动画兜底）。
    //
    // 误触安全性由 SwipeBack.kt 状态机保证：directionRatio 要求横向位移显著大于纵向才
    // Recognized；Recognized 之前不 consume 任何事件，垂直滚动列表不受影响。
    val swipeConfig = remember { SwipeBackConfig(edgeWidthDp = Float.MAX_VALUE) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        state.bindViewportWidth(widthPx)

        // ───────────────────────── 渲染模型：单 keyed-loop（最终版） ─────────────────────────
        //
        // 演进史（每一版都在真机踩过坑）：
        // - v1 stable-slot「eager-remove」：beginSwipe 立刻把 outgoing 从 entries 拆出 →
        //   commit 不闪，但 **cancel 会把 outgoing 重新 add 回 entries**，此刻
        //   `entries.last() == _exiting`（同一个 key）→ slot1 SaveableStateProvider(key) 与
        //   slot3 SaveableStateProvider(key) 同 key 双注册 → Compose `Key X was used multiple
        //   times` FATAL → app crash（cancel 一拖回就崩，再点会话进不去）。
        //
        // 最终版：**栈在整个 transition 期间不变**（beginSwipe / pop 都不动 entries，只在动画
        // 完成时才 remove）。可见层用唯一一个 `forEach + key(entry.key)` 循环渲染——每个 entry
        // 永远只有一个稳定 call-site（按 key 追踪 identity），role/transform 随状态变。这样：
        // - commit 完成：survivor(shell) 从 BELOW→FRONT 是同一 loop call-site → 不 remount →
        //   不闪烁（v1 stable-slot 当初要解的就是这个）。
        // - cancel 完成：survivor(chat) 从 MOVING→FRONT 同 call-site → 不 remount；
        //   below(shell) 退出 loop → dispose。**任一帧每个 key 只出现一次** → 永不双注册。
        //
        // 手势宿主：BoxWithConstraints **内部**一个无 key、跨生命周期稳定的全屏 wrapper Box。
        // 两条真机教训：① 不能挂 slot 内（key 变 → 协程被 dispose）；② 不能挂
        // BoxWithConstraints 自身（Kuikly SubcomposeLayout 上 pointerInput 收不到事件）。
        // enabled 恒为常量；动态 guard 全部由 [NavigatorState.beginSwipe] 在 onStart 判定。
        val layers = state.visibleLayers()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .let { base ->
                    if (swipeBackEnabled) {
                        base.swipeBack(
                            enabled = true,
                            config = swipeConfig,
                            onStart = { state.beginSwipe() },
                            // 1:1 像素跟手：页面位移 = 手指位移（微信式）。
                            onProgress = { _, dragX -> state.updateSwipeByPixels(dragX) },
                            onCancel = { state.cancelSwipe() },
                            onCommit = { state.commitSwipe() },
                        )
                    } else {
                        base
                    }
                },
        ) {
            layers.forEach { layer ->
                key(layer.entry.key) {
                    saveableHolder.SaveableStateProvider(layer.entry.key) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(layer.zIndex)
                                .graphicsLayer {
                                    when (layer.role) {
                                        NavLayerRole.Front -> {
                                            translationX = 0f
                                        }
                                        NavLayerRole.Below -> {
                                            // Push: 视差 -W*0.25→0；Overlay/Modal: 静止
                                            translationX = if (layer.movingIsOverlay) 0f
                                                else -widthPx * PARALLAX_RATIO * (1f - state.transitionFraction)
                                        }
                                        NavLayerRole.Moving -> when (layer.entry.options.transition) {
                                            NavTransition.SlidePush ->
                                                translationX = widthPx * state.transitionFraction
                                            NavTransition.FadeIn, NavTransition.ModalSheet -> {
                                                translationX = 0f
                                                alpha = 1f - state.transitionFraction
                                            }
                                        }
                                    }
                                },
                        ) {
                            val scope = EntryScopeImpl(
                                entry = layer.entry,
                                controller = state,
                                isTop = layer.role != NavLayerRole.Below,
                                isForeground = layer.role == NavLayerRole.Front,
                            )
                            scope.content(layer.entry)
                        }
                    }
                }
            }

            // Scrim：z 夹在 below(0) 与 moving(2) 之间。只在 Push transition 期间渐变；
            // Overlay/Modal 期间保持稳定暗罩。
            val moving = state.movingEntry
            if (moving != null) {
                val overlayMoving = moving.options.presentation == NavPresentation.Overlay ||
                    moving.options.presentation == NavPresentation.Modal
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(SCRIM_Z)
                        .graphicsLayer {
                            if (overlayMoving) {
                                translationX = 0f
                                alpha = SCRIM_MAX_ALPHA
                            } else {
                                translationX = -widthPx * PARALLAX_RATIO * (1f - state.transitionFraction)
                                alpha = SCRIM_MAX_ALPHA * (1f - state.transitionFraction)
                            }
                        }
                        .background(Color.Black),
                )
            }
        }
    }
}

/** 可见层在渲染循环里的角色。 */
internal enum class NavLayerRole { Front, Below, Moving }

/** 一个待渲染的可见层。 */
internal data class NavLayer(
    val entry: NavEntry,
    val role: NavLayerRole,
    val zIndex: Float,
    /** moving entry 是否 Overlay/Modal（决定 below 是否静止）。 */
    val movingIsOverlay: Boolean,
)

/** Previous 层视差比例：出场进度 0 时 -W*0.25，1 时 0。设计参考 iOS / 微信。 */
private const val PARALLAX_RATIO = 0.25f

/** Scrim 的 zIndex，夹在 below(0f) 与 moving(2f) 之间。 */
private const val SCRIM_Z = 1f

/** 出场期间贴在 previous 层上的最大暗罩透明度。 */
private const val SCRIM_MAX_ALPHA = 0.15f

// ─────────────────────────────────────────────────────────────────────────────
// internal impl
// ─────────────────────────────────────────────────────────────────────────────

/**
 * 业务侧预先创建 Navigator 控制器。在外部事件（forced logout / kick out / token expired）
 * 触发 [NavigatorController.resetTo] 等操作时使用。Composable 业务侧不直接拿到 [NavigatorState]
 * 内部状态——只通过 [NavigatorController] interface 操作栈。
 *
 * 用法：
 * ```kotlin
 * val nav = rememberNavigatorController("shell")
 *
 * LaunchedEffect(Unit) {
 *     forcedLogoutEvents.collect {
 *         nav.resetTo("shell")
 *         legacyPageStack.clear()
 *     }
 * }
 *
 * Navigator(controller = nav, initialRoute = "shell") { entry -> ... }
 * ```
 *
 * @param initialRoute 栈底 route；同一次 Composition 内传给 [Navigator] 的 `initialRoute` 应一致
 */
@Composable
fun rememberNavigatorController(initialRoute: String): NavigatorController =
    remember(initialRoute) { NavigatorState(initialRoute = initialRoute) }

@Stable
internal class NavigatorState(initialRoute: String) : NavigatorController {

    private val _entries = mutableStateListOf(
        NavEntry(route = initialRoute, key = generateKey(initialRoute, 0)),
    )

    // attach/detach：外部预先创建 controller 时这三个回调在 Composable scope 内才有；
    // 未 attach 时调用 push/pop 会照常工作但 saveable / onEntryRemoved 不会触发。
    private var removeSaveableState: ((String) -> Unit)? = null
    private var onEntryRemovedRef: ((NavEntry) -> Unit)? = null
    private var animScope: CoroutineScope? = null

    internal fun attach(
        saveable: (String) -> Unit,
        onEntryRemovedRef: (NavEntry) -> Unit,
        animScope: CoroutineScope,
    ) {
        this.removeSaveableState = saveable
        this.onEntryRemovedRef = onEntryRemovedRef
        this.animScope = animScope
    }

    internal fun detach() {
        removeSaveableState = null
        onEntryRemovedRef = null
        animScope = null
    }

    /** exactly-once guard：每个 key 只通知一次 onEntryRemoved + removeState。 */
    private val removedKeys = mutableSetOf<String>()

    /** 同 route 多次 push 时生成唯一 key 用。 */
    private var keyCounter: Int = 1

    /** PopDecision.Pending 状态：栈顶被业务确认前不允许 push/pop。 */
    private var pendingEntry: NavEntry? by mutableStateOf(null)

    /**
     * 正在 transition 出场的 entry。**关键不变式：transition 期间它仍留在 [_entries] 里**
     * （== `entries.last()`）。栈只在动画完成时才 remove（commit/pop）或保持不变（cancel）。
     * null = 平时；非 null = pop animation 或 swipe in progress。
     */
    private var _moving: NavEntry? by mutableStateOf(null)

    /** Swipe 模式开关：true 期间 fraction 由 [updateSwipeByPixels] 手动 snap；false 期间由 animation 驱动。 */
    private var _swipeMode: Boolean by mutableStateOf(false)

    /** 出场层占屏宽比例 0..1：0=完全可见(moving 覆盖)，1=完全在右屏外(below 完全露出)。 */
    private val _fractionAnim = Animatable(0f)

    /** Viewport 宽（像素）。由 [Navigator] BoxWithConstraints 注入。 */
    private var viewportWidth: Float = 0f

    val movingEntry: NavEntry? get() = _moving
    val transitionFraction: Float get() = _fractionAnim.value

    /**
     * 当前可见层（bottom→top）。渲染循环按 `key(entry.key)` 追踪 identity：
     * - 无 transition：只有栈顶一层 [NavLayerRole.Front]。
     * - transition 中：below([NavLayerRole.Below], z=0) + moving([NavLayerRole.Moving], z=2)。
     *   moving == `entries.last()`（栈未动），below == `entries[size-2]` —— **两者 key 必然不同**，
     *   所以同一帧不会出现重复 key（v1 stable-slot 的 crash 根因被消除）。
     */
    fun visibleLayers(): List<NavLayer> {
        val moving = _moving
        if (moving == null) {
            return listOf(NavLayer(_entries.last(), NavLayerRole.Front, zIndex = 0f, movingIsOverlay = false))
        }
        val overlayMoving = moving.options.presentation == NavPresentation.Overlay ||
            moving.options.presentation == NavPresentation.Modal
        val below = _entries.getOrNull(_entries.size - 2)
        return buildList {
            if (below != null) {
                add(NavLayer(below, NavLayerRole.Below, zIndex = 0f, movingIsOverlay = overlayMoving))
            }
            add(NavLayer(moving, NavLayerRole.Moving, zIndex = 2f, movingIsOverlay = overlayMoving))
        }
    }

    override val current: NavEntry
        get() = _entries.last()

    override val previous: NavEntry?
        get() = _entries.getOrNull(_entries.size - 2)

    override val canPop: Boolean
        get() = _entries.size > 1 && pendingEntry == null && _moving == null

    override val isTransitioning: Boolean
        get() = _moving != null

    fun bindViewportWidth(width: Float) {
        viewportWidth = width
    }

    /**
     * v1 硬不变式：transition / pending 期间**所有** stack-mutating API 一律拒绝。
     * 否则 swipe 中途 replace/resetTo/popTo 会让出场 snapshot 指向已被移除的栈顶，
     * 或者 pending 状态下业务行为不确定。需要切栈的业务先 [forcePop] / 等动画完成。
     */
    private val isMidFlight: Boolean
        get() = _moving != null || pendingEntry != null

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
        if (_moving != null) return false
        pendingEntry = null
        startCommitPopAnim(_entries.last())
        return true
    }

    override fun popTo(route: String): Boolean {
        if (isMidFlight) return false
        val idx = _entries.indexOfLast { it.route == route }
        if (idx < 0 || idx == _entries.size - 1) return false
        // 中间层 entry 立刻清掉（不动画），只有最顶层走 pop 动画。
        // 栈顶不 remove（留给动画完成时），所以保留到 idx+2。
        while (_entries.size > idx + 2) {
            val removed = _entries.removeAt(_entries.size - 2)
            notifyRemoved(removed)
        }
        startCommitPopAnim(_entries.last())
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
        if (_moving != null) return false
        val top = _entries.last()
        val decision = top.options.onPopRequest?.invoke(PopRequest(top, reason)) ?: PopDecision.Allow
        return when (decision) {
            PopDecision.Allow -> {
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

    /**
     * 程序化 pop 出场动画。**outgoing 留在 _entries 不动**（== entries.last()），
     * 渲染 loop 把它当 Moving 层、below = entries[size-2]，fraction 0→1 滑出。
     * 动画完成才 remove + notify —— survivor(below) 从 Below→Front 同一 loop call-site
     * 不 remount（无闪烁）。
     */
    private fun startCommitPopAnim(outgoing: NavEntry) {
        _swipeMode = false
        _moving = outgoing
        val scope = animScope
        if (scope == null) {
            // detached：没有动画 scope，直接同步完成（业务在 Composition 外触发 logout 时走这）
            removeMoving(outgoing)
            return
        }
        scope.launch {
            // finally 保证：即使 animateTo 被并发取消（CancellationException），移除语义也必须
            // 完成 —— 否则 _moving 残留会让 canPop 永远 false、后续 push 全被 isMidFlight 拒绝、
            // BACK 直接让出 native 误退 app（P0，真机踩过）。
            try {
                _fractionAnim.snapTo(0f)
                _fractionAnim.animateTo(1f, tween(durationMillis = ANIM_POP_MS))
            } finally {
                removeMoving(outgoing)
            }
        }
    }

    /** 动画完成：把 outgoing 从栈顶移除 + notify + 清 _moving。三步原子（同一帧 recompose）。 */
    private fun removeMoving(outgoing: NavEntry) {
        if (_entries.lastOrNull()?.key == outgoing.key) {
            _entries.removeAt(_entries.size - 1)
        }
        notifyRemoved(outgoing)
        _moving = null
    }

    // ───── swipe API（由 Modifier.swipeBack onStart/onProgress/onCancel/onCommit 触发） ─────

    /**
     * 手势识别成功（onStart）：**所有动态 guard 在这里判定**——手势 modifier 挂在
     * Navigator 根节点且 enabled 恒定（否则 beginSwipe 改栈会重启 pointerInput 杀手势），
     * 所以「能不能 swipe」只能在手势开始时刻检查：
     * - 栈深 ≥ 2（canPop 语义）
     * - 没有进行中的 transition / pending
     * - 栈顶 entry 允许 swipe 且是 Push presentation
     *
     * 通过后**立刻**把 outgoing 从 entries 移除（stable-slot：slot 1 切回 previous，
     * slot 3 渲染 outgoing snapshot）。同时把 fraction 同步复位 0 —— 上一轮 commit
     * 留下的 1f 残值会让 snapshot 直接渲染在屏幕外（「闪回」观感）。
     * 拒绝时本轮手势 no-op（updateSwipe/commitSwipe/cancelSwipe 自检直接 return）。
     */
    internal fun beginSwipe() {
        if (_entries.size <= 1) return
        if (_moving != null) return
        if (pendingEntry != null) return
        val top = _entries.last()
        if (!top.options.swipeBackEnabled) return
        if (top.options.presentation != NavPresentation.Push) return
        // **不 remove**：top 留在 entries 当 Moving 层，below = entries[size-2]，两者 key 不同。
        _moving = top
        _swipeMode = true
        // 上一轮动画可能停在 1f；复位到 0 再开始跟手。snapTo 是 suspend，借 animScope。
        animScope?.launch {
            if (_swipeMode) _fractionAnim.snapTo(0f)
        }
    }

    /** 1:1 跟手：dragX（px）按视口宽归一化进 fraction。 */
    internal fun updateSwipeByPixels(dragX: Float) {
        if (!_swipeMode) return
        val width = viewportWidth.takeIf { it > 0f } ?: return
        animScope?.launch {
            // double-check：launch 排队期间手势可能已经 commit/cancel（_swipeMode 翻 false）。
            // 不加这层，迟到的 snapTo 会通过 Animatable 互斥取消掉 commitSwipe 正在跑的
            // animateTo（CancellationException），打断移除流程。
            if (!_swipeMode) return@launch
            _fractionAnim.snapTo((dragX / width).coerceIn(0f, 1f))
        }
    }

    /** 用户松手且过阈值：跑完剩余动画 → 真 remove + notify。 */
    internal fun commitSwipe() {
        val outgoing = _moving ?: return
        if (!_swipeMode) return
        _swipeMode = false
        val scope = animScope
        if (scope == null) {
            removeMoving(outgoing)
            return
        }
        scope.launch {
            try {
                _fractionAnim.animateTo(1f, tween(durationMillis = ANIM_SWIPE_COMMIT_MS))
            } finally {
                // 即使动画被取消也必须完成移除语义，否则 _moving 残留 = Navigator 卡死。
                removeMoving(outgoing)
            }
        }
    }

    /**
     * 用户松手未过阈值：回弹。**栈一直没动**（outgoing 仍是 entries.last()），所以这里只需
     * 把 fraction 弹回 0、清 _moving —— outgoing 从 Moving→Front 是同一 loop call-site，
     * 不 remount、不重复 key（v1 cancel-crash 根因被消除）。
     */
    internal fun cancelSwipe() {
        if (!_swipeMode) return
        _swipeMode = false
        val scope = animScope
        if (scope == null) {
            _moving = null
            return
        }
        scope.launch {
            try {
                _fractionAnim.animateTo(0f, spring())
            } finally {
                _moving = null
            }
        }
    }

    private fun notifyRemoved(entry: NavEntry) {
        if (removedKeys.add(entry.key)) {
            onEntryRemovedRef?.invoke(entry)
            removeSaveableState?.invoke(entry.key)
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
