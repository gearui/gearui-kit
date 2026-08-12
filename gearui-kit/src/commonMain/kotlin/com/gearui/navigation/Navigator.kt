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
import com.gearui.theme.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Navigator v1 entry point. Every stacked navigation in the app — messages,
 * contacts, profile, groups, QR codes — goes through this.
 *
 * Key invariants (see `gearui-kit/docs/NAVIGATOR_SWIPE_BACK_DESIGN.md`):
 *
 * 1. It does **not** replace Kuikly `@Page`. This is a stack inside a single
 *    ComposeContainer.
 * 2. **Kuikly's BACK is topmost-only**: `backPressCallbackList.isNotEmpty()`
 *    decides whether the event is consumed, and `dispatchOnBackEvent()` only
 *    invokes `list.last()`. So Navigator registers exactly **one** BackHandler,
 *    and **only while `canPop = true`** — at the bottom of the stack it must
 *    dispose and hand BACK back to native.
 * 3. A BackHandler registered above Navigator by Dialog / Sheet / ActionSheet
 *    automatically becomes `list.last()` and takes the event first.
 * 4. Typed params are **not** exposed. Callers keep an outer state holder and
 *    clear it in [onEntryRemoved], when the entry is actually gone.
 *
 * Commit 2 added the transition layer. A pop renders two layers at once —
 * current (already the old previous) and a snapshot of the outgoing screen —
 * with an Animatable driving [graphicsLayer] translationX. Edge swipe reuses
 * `Modifier.swipeBack`'s `onProgress` for finger tracking, with complete
 * commit and cancel paths.
 *
 * @param initialRoute route string at the bottom of the stack
 * @param swipeBackEnabled global switch; an individual entry can still opt out
 *                         via [NavOptions.swipeBackEnabled]
 * @param handleBack whether to take over the system back gesture, registered
 *                   through Kuikly's `BackHandler` and **only while
 *                   [NavigatorController.canPop] is true**
 * @param onEntryRemoved called when an entry **finally** leaves the stack: a
 *                       committed pop animation, replace, popTo or resetTo.
 *                       Exactly once — the same entry never fires twice.
 * @param content renders the page for the current entry; during a transition it
 *                is called once for the top and once for the previous layer
 *
 * Note: the edge hot zone is deliberately **not** exposed (review 4). Navigator
 * fixes it at 96dp to clear the leftmost ~24dp where Android's own back gesture
 * takes priority (Phase 0 spike finding). 96dp also works well on iOS.
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

    // Use the controller when one is passed in (typically from
    // [rememberNavigatorController]); otherwise remember one internally so older
    // callers keep working (the sample, and existing Phase 2 call sites).
    val state: NavigatorState = remember(controller, initialRoute) {
        (controller as? NavigatorState)
            ?: NavigatorState(initialRoute = initialRoute)
    }

    // Inject the Composable-scoped saveable holder, animation scope and
    // onEntryRemoved into the state, and detach on leaving composition — without
    // that, a callback can still fire against the old holder after a logout
    // reset.
    DisposableEffect(state, saveableHolder, animScope) {
        state.attach(
            saveable = { key -> saveableHolder.removeState(key) },
            onEntryRemovedRef = { entry -> removedRef.value?.invoke(entry) },
            animScope = animScope,
        )
        onDispose { state.detach() }
    }

    // Critical: the BackHandler is registered **only while canPop is true**.
    // Kuikly reports consumed = backPressCallbackList.isNotEmpty() back to native
    // before any callback runs, so a BackHandler still registered at the bottom
    // of the stack means native never sees BACK at all. BACK is also ignored
    // while an exit animation is running, so it cannot start a second pop.
    if (handleBack && state.canPop) {
        BackHandler {
            state.requestPop(PopReason.BackButton)
        }
    }

    // Full-screen gesture, matching WeChat on Android: the swipe may start
    // anywhere, not just at the left edge.
    //
    // Why not an edge hot zone plus setSystemGestureExclusionRects:
    // - Android caps back-gesture exclusion at 200dp of height (it takes only the
    //   bottom 200dp of the rect), so a full-height exclusion is impossible. A
    //   swipe starting at the far left in the middle of the screen would always
    //   be taken by the system's predictive back.
    // - What WeChat on Android actually does is full-screen swipe-to-go-back:
    //   swiping right from mid-screen returns to the previous page, while a
    //   swipe from the very edge is left to the system gesture (predictive back
    //   commit -> BACK -> Navigator's pop animation as the fallback).
    //
    // Accidental triggering is prevented by the SwipeBack.kt state machine:
    // directionRatio requires horizontal travel to clearly exceed vertical
    // before recognising, and nothing is consumed before recognition, so
    // vertically scrolling lists are unaffected.
    val swipeConfig = remember { SwipeBackConfig(edgeWidthDp = Float.MAX_VALUE) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        state.bindViewportWidth(widthPx)

        // ──────────────────── Rendering model: one keyed loop ────────────────────
        //
        // How it got here — every version below was broken on a real device:
        // - v1, "stable slot with eager remove": beginSwipe pulled the outgoing
        //   entry out of `entries` immediately. Commit did not flicker, but
        //   **cancel put the outgoing entry back**, at which point
        //   `entries.last() == _exiting` (the same key) and slot1's
        //   SaveableStateProvider(key) collided with slot3's — Compose raised
        //   `Key X was used multiple times`, which is fatal. Dragging back and
        //   cancelling crashed the app, and the conversation could not be
        //   reopened afterwards.
        //
        // Final version: **the stack does not change for the duration of a
        // transition**. Neither beginSwipe nor pop touches `entries`; removal
        // happens only when the animation finishes. Visible layers render
        // through a single `forEach + key(entry.key)` loop, so every entry has
        // exactly one stable call site (identity tracked by key) while its role
        // and transform vary with state. That gives:
        // - commit: the survivor goes BELOW -> FRONT at the same loop call site,
        //   so it is not remounted and does not flicker — which is what the v1
        //   stable-slot design was trying to solve in the first place;
        //   cancel: the survivor goes MOVING -> FRONT at the same call site and
        //   is likewise not remounted, while the below layer leaves the loop and
        //   is disposed. **Each key appears exactly once in any frame**, so the
        //   double registration cannot recur.
        //
        // Gesture host: a keyless, lifecycle-stable full-screen wrapper Box
        // **inside** BoxWithConstraints. Two more device lessons: it cannot live
        // inside a slot (the key changes and the coroutine is disposed), and it
        // cannot be BoxWithConstraints itself (pointerInput receives no events on
        // Kuikly's SubcomposeLayout). `enabled` is a constant; every dynamic
        // guard is decided by [NavigatorState.beginSwipe] in onStart.
        val layers = state.visibleLayers()
        // Opaque backing for every navigation "screen". Without it, a page with a
        // transparent background lets the layer beneath show through — on device,
        // swiping the chat page revealed the conversation list underneath. The
        // theme colour is read once here, in composable scope.
        val screenBackground = Theme.colors.background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .let { base ->
                    if (swipeBackEnabled) {
                        base.swipeBack(
                            enabled = true,
                            config = swipeConfig,
                            onStart = { state.beginSwipe() },
                            // 1:1 finger tracking: the page moves exactly as far as the finger, as WeChat does.
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
                        // Opaque backing everywhere except the fading (alpha) moving
                        // layer of an Overlay/Modal transition, which is meant to
                        // composite with the layer below and the scrim — that is what
                        // an image or video preview looks like. Every other layer,
                        // including a Push transition's moving layer, gets an opaque
                        // backing so layers cannot bleed through each other.
                        val opaque = !(layer.role == NavLayerRole.Moving &&
                            layer.entry.options.transition != NavTransition.SlidePush)
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
                                            // Push: parallax from -W*0.25 to 0. Overlay/Modal: stationary.
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
                                }
                                .let { m -> if (opaque) m.background(screenBackground) else m },
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

            // Scrim sits at a z between below (0) and moving (2). It fades only
            // during a Push transition; for Overlay/Modal it stays at a constant
            // dim.
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

/** The role a visible layer plays in the render loop. */
internal enum class NavLayerRole { Front, Below, Moving }

/** One visible layer to render. */
internal data class NavLayer(
    val entry: NavEntry,
    val role: NavLayerRole,
    val zIndex: Float,
    /** Whether the moving entry is an Overlay/Modal, which decides if the layer below stays still. */
    val movingIsOverlay: Boolean,
)

/** Parallax factor for the previous layer: -W*0.25 at exit progress 0, 0 at 1. Modelled on iOS and WeChat. */
private const val PARALLAX_RATIO = 0.25f

/** Scrim zIndex, between below (0f) and moving (2f). */
private const val SCRIM_Z = 1f

/** Peak dim opacity laid over the previous layer while a screen exits. */
private const val SCRIM_MAX_ALPHA = 0.15f

// ─────────────────────────────────────────────────────────────────────────────
// internal impl
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Creates a Navigator controller ahead of time, for driving the stack from
 * outside events such as forced logout, kick-out or an expired token calling
 * [NavigatorController.resetTo]. Callers never touch [NavigatorState] directly —
 * the stack is only manipulated through the [NavigatorController] interface.
 *
 * Usage:
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
 * @param initialRoute route at the bottom of the stack; must match the `initialRoute` passed to [Navigator] in the same composition
 */
@Composable
fun rememberNavigatorController(initialRoute: String): NavigatorController =
    remember(initialRoute) { NavigatorState(initialRoute = initialRoute) }

@Stable
internal class NavigatorState(initialRoute: String) : NavigatorController {

    private val _entries = mutableStateListOf(
        NavEntry(route = initialRoute, key = generateKey(initialRoute, 0)),
    )

    // These three callbacks only exist in composable scope, so a controller
    // created ahead of time is attached later. push/pop still work before
    // attaching, but saveable state and onEntryRemoved will not fire.
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

    /** Exactly-once guard: each key triggers onEntryRemoved and removeState at most once. */
    private val removedKeys = mutableSetOf<String>()

    /** Generates a unique key when the same route is pushed more than once. */
    private var keyCounter: Int = 1

    /** PopDecision.Pending: push and pop are blocked until the caller confirms the top entry. */
    private var pendingEntry: NavEntry? by mutableStateOf(null)

    /**
     * The entry currently transitioning out. **Key invariant: it stays in
     * [_entries] for the whole transition** (it is `entries.last()`). The stack
     * is only modified when the animation finishes — removed on commit/pop, left
     * alone on cancel. Null in the steady state; non-null during a pop animation
     * or an in-progress swipe.
     */
    private var _moving: NavEntry? by mutableStateOf(null)

    /** Swipe mode: while true the fraction is snapped by [updateSwipeByPixels]; while false it is animation-driven. */
    private var _swipeMode: Boolean by mutableStateOf(false)

    /** Exit progress 0..1 as a fraction of screen width: 0 = fully covering, 1 = fully off-screen right with the layer below exposed. */
    private val _fractionAnim = Animatable(0f)

    /** Viewport width in pixels, injected by [Navigator]'s BoxWithConstraints. */
    private var viewportWidth: Float = 0f

    val movingEntry: NavEntry? get() = _moving
    val transitionFraction: Float get() = _fractionAnim.value

    /**
     * Currently visible layers, bottom to top. The render loop tracks identity
     * by `key(entry.key)`. With no transition there is one top layer,
     * [NavLayerRole.Front]; during one there are below ([NavLayerRole.Below],
     * z=0) and moving ([NavLayerRole.Moving], z=2). Moving is `entries.last()`
     * and below is `entries[size-2]`, so their keys always differ and no key can
     * repeat within a frame. That is what removed the v1 stable-slot crash.
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
     * Hard v1 invariant: **every** stack-mutating API is refused during a
     * transition or while pending. A replace/resetTo/popTo mid-swipe would leave
     * the exit snapshot pointing at an already-removed entry, and behaviour
     * while pending would be undefined. Call [forcePop] or wait for the animation.
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
        // Deliberately skips onPopRequest: this is the only way out of Pending
        // once the caller has confirmed, so pendingEntry != null is an
        // **expected** state for forcePop and is allowed through.
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
        // Intermediate entries are dropped immediately without animation; only
        // the top one animates out. The top is not removed here — that happens
        // when the animation finishes — so the slice keeps up to idx+2.
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
     * Requests a pop, branching on onPopRequest's decision:
     * - Allow: pop for real and run the exit animation.
     * - Deny: the caller swallows it, the stack is unchanged, returns false.
     * - Pending: this BACK counts as consumed. Navigator holds no continuation;
     *   the caller calls forcePop itself when ready.
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
     * Programmatic pop with an exit animation. **The outgoing entry stays in
     * _entries** (it is `entries.last()`); the render loop treats it as the moving
     * layer with below = `entries[size-2]` and animates the fraction 0 -> 1.
     * Removal and notification happen only when the animation completes, so the
     * survivor goes Below -> Front at the same call site without remounting.
     */
    private fun startCommitPopAnim(outgoing: NavEntry) {
        _swipeMode = false
        _moving = outgoing
        val scope = animScope
        if (scope == null) {
            // Detached: no animation scope, so complete synchronously. This is the path when logout is triggered outside composition.
            removeMoving(outgoing)
            return
        }
        scope.launch {
            // The finally block guarantees removal even if animateTo is cancelled
            // concurrently. Without it a stale _moving leaves canPop permanently
            // false, every later push is refused by isMidFlight, and BACK falls
            // through to native and exits the app. That was a P0 seen on device.
            try {
                _fractionAnim.snapTo(0f)
                _fractionAnim.animateTo(1f, tween(durationMillis = ANIM_POP_MS))
            } finally {
                removeMoving(outgoing)
            }
        }
    }

    /** Animation complete: remove the outgoing entry, notify, and clear _moving — all three in one recomposition. */
    private fun removeMoving(outgoing: NavEntry) {
        if (_entries.lastOrNull()?.key == outgoing.key) {
            _entries.removeAt(_entries.size - 1)
        }
        notifyRemoved(outgoing)
        _moving = null
    }

    // ───── Swipe API, driven by Modifier.swipeBack onStart/onProgress/onCancel/onCommit ─────

    /**
     * Gesture recognised (onStart). **Every dynamic guard is evaluated here.** The
     * gesture modifier sits on Navigator's root with a constant `enabled` — if
     * beginSwipe changed the stack, pointerInput would restart and kill the
     * gesture — so whether a swipe is allowed can only be checked as it starts:
     * stack depth >= 2, no transition or pending decision in flight, and a top
     * entry that permits swiping with a Push presentation.
     *
     * The fraction is reset to 0 on entry: a leftover 1f from the previous commit
     * would render the snapshot off-screen, which reads as a flash. If the guards
     * refuse, the whole gesture is a no-op — updateSwipe, commitSwipe and
     * cancelSwipe all self-check and return.
     */
    internal fun beginSwipe() {
        if (_entries.size <= 1) return
        if (_moving != null) return
        if (pendingEntry != null) return
        val top = _entries.last()
        if (!top.options.swipeBackEnabled) return
        if (top.options.presentation != NavPresentation.Push) return
        // **No removal**: the top stays in entries as the moving layer, below = entries[size-2], and their keys differ.
        _moving = top
        _swipeMode = true
        // The previous animation may have stopped at 1f; snap back to 0 before tracking. snapTo suspends, hence animScope.
        animScope?.launch {
            if (_swipeMode) _fractionAnim.snapTo(0f)
        }
    }

    /** 1:1 tracking: dragX in pixels, normalised against viewport width into the fraction. */
    internal fun updateSwipeByPixels(dragX: Float) {
        if (!_swipeMode) return
        val width = viewportWidth.takeIf { it > 0f } ?: return
        animScope?.launch {
            // Double-check: the gesture may have committed or cancelled while this
            // launch was queued (_swipeMode flips false). Without this, a late
            // snapTo cancels the animateTo that commitSwipe is running, through
            // Animatable's mutual exclusion, and the removal never completes.
            if (!_swipeMode) return@launch
            _fractionAnim.snapTo((dragX / width).coerceIn(0f, 1f))
        }
    }

    /** Released past the threshold: finish the remaining animation, then remove and notify. */
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
                // Removal must complete even if the animation is cancelled; a stale _moving wedges the Navigator.
                removeMoving(outgoing)
            }
        }
    }

    /**
     * Released short of the threshold: spring back. **The stack never changed** —
     * the outgoing entry is still `entries.last()` — so this only animates the
     * fraction back to 0 and clears _moving, with no remount and no repeated key.
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
 * Generates the internal unique key. Callers pushing the same route repeatedly may pass their own stable key; otherwise `route#counter` is used.
 */
private fun generateKey(route: String, counter: Int): String = "$route#$counter"
