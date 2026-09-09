package com.gearui.navigation

import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.Stable

/**
 * Public API types for Navigator v1.
 *
 * Design choices (see `gearui-kit/docs/NAVIGATOR_SWIPE_BACK_DESIGN.md`):
 * - Typed params are **not** exposed; callers bridge them with an outer state holder plus [Navigator.onEntryRemoved]
 * - SaveableStateHolder is **not** exposed; Navigator manages it internally by [NavEntry.key]
 * - Back handling reuses Kuikly `BackHandler` and its topmost-only semantics, so Navigator must dispose its own handler at the bottom of the stack
 */

/**
 * One item in the stack.
 *
 * @property route dispatch key, such as "chat" or "profile_edit"
 * @property key unique identity; pushing the same route twice needs distinct keys or their SaveableState will be shared
 * @property options behaviour switches: swipeBack, transition, presentation, pop interception
 */
/**
 * What Navigator needs from an application's route type.
 *
 * [routeName] identifies the destination; the payload is whatever else the
 * implementing type carries. A sealed interface of data classes is the intended
 * shape, and then a route *is* its arguments.
 *
 * This replaces a bridge every consumer had to build: Navigator used to take a
 * `String` and hand it back, so the application kept its own
 * `entry.key -> payload` map, maintained exactly-once cleanup for it through
 * `onEntryRemoved`, and looked payloads up with a lookup that could miss —
 * `?: error("no payload for ...")`. There is nothing left to miss.
 */
@Stable
interface NavRoute {
    val routeName: String
    val options: NavOptions get() = NavOptions.Default
}

@Stable
data class NavEntry<out R : NavRoute>(
    val route: R,
    val key: String,
) {
    val options: NavOptions get() = route.options
}

/**
 * Per-entry behaviour switches. Defaults match an ordinary page: swipe back allowed, push animation, Push presentation.
 */
@Stable
data class NavOptions(
    val swipeBackEnabled: Boolean = true,
    val transition: NavTransition = NavTransition.SlidePush,
    val presentation: NavPresentation = NavPresentation.Push,
    /**
     * Pop interception hook. Returning [PopDecision.Pending] means this BACK is
     * already consumed and Navigator keeps **no** continuation; after showing a
     * confirmation the caller calls [NavigatorController.forcePop] to continue, or [NavigatorController.pop] to cancel.
     */
    val onPopRequest: ((PopRequest<*>) -> PopDecision)? = null,
) {
    companion object {
        val Default = NavOptions()
    }
}

/** Enter and exit animation style. Commit 1 only implements the instant cut; [SlidePush], [FadeIn] and [ModalSheet] arrive in Commit 2. */
enum class NavTransition { SlidePush, FadeIn, ModalSheet }

/** Presentation semantics. */
enum class NavPresentation {
    /** An ordinary page push, taking part in the WeChat-style edge swipe pop. The previous layer is kept during the swipe and the animation. */
    Push,

    /** An immersive overlay such as an image or video preview. The previous layer is kept but does not take part in edge swipe; closing returns straight to it. */
    Overlay,

    /** A fullscreen modal such as a task or form sheet. The previous layer does not move with it and edge swipe does not apply. */
    Modal,
}

/** Context carried with a pop request. */
@Stable
data class PopRequest<out R : NavRoute>(
    val entry: NavEntry<R>,
    val reason: PopReason,
)

/** What initiated the pop. */
enum class PopReason {
    /** The system back button, via Kuikly BackHandler. */
    BackButton,

    /** A committed edge swipe. */
    EdgeSwipe,

    /** A direct call to [NavigatorController.pop]. */
    Programmatic,
}

/**
 * Return value of [NavOptions.onPopRequest].
 *
 * - [Allow]: proceed; Navigator continues the pop.
 * - [Deny]: the caller swallows this BACK and the stack is unchanged.
 * - [Pending]: this BACK counts as consumed and the caller shows a confirmation. Navigator keeps
 *   no continuation, so the caller must call [NavigatorController.forcePop] to actually pop; doing nothing cancels.
 */
enum class PopDecision { Allow, Deny, Pending }

/**
 * Entry point for driving the Navigator. Obtained in composition through [EntryScope.controller].
 *
 * Note: do not infer "the globally current page" from [EntryScope.entry]. That is the entry
 * rendered by this layer, and during a transition Navigator renders both current and previous.
 */
@Stable
interface NavigatorController<R : NavRoute> {
    val current: NavEntry<R>
    val previous: NavEntry<R>?
    val canPop: Boolean
    val isTransitioning: Boolean

    /** Options come from the route itself; there is no separate channel for them. */
    fun push(route: R, key: String? = null)

    /** Fires [NavOptions.onPopRequest] and pops, refuses or suspends accordingly. Returns false at the bottom of the stack. */
    fun pop(): Boolean

    /** Skips [NavOptions.onPopRequest], for continuing after the caller has confirmed a dirty state. Returns false at the bottom of the stack. */
    fun forcePop(): Boolean

    /** Pops to the nearest entry whose [NavRoute.routeName] matches. Returns false if already on top or no match exists. */
    fun popTo(routeName: String): Boolean

    fun replace(route: R, key: String? = null)

    /** Clears the stack down to [route]. Every removed entry is disposed. */
    fun resetTo(route: R)
}

/**
 * Local scope for one rendered entry.
 *
 * - [entry] is the entry rendered by **this layer**, which is not necessarily the top
 * - [isTop] is whether it is the top; false for the previous layer during a transition
 * - [isForeground] is top with no transition in flight; callers can pause polling or animation on it
 */
@Stable
interface EntryScope<R : NavRoute> {
    val entry: NavEntry<R>
    val controller: NavigatorController<R>
    val isTop: Boolean
    val isForeground: Boolean

    /**
     * A scope that lives as long as this entry is on the stack, cancelled when
     * it leaves.
     *
     * Unlike `rememberCoroutineScope()` this survives the entry going off
     * screen — being the layer below during a transition, or hidden inside a
     * [TabHost] — so work started here does not restart every time the page
     * becomes visible again.
     */
    val entryCoroutineScope: CoroutineScope

    /**
     * Creates [factory] the first time and returns that same instance for as
     * long as the entry is on the stack. Destroyed with the entry; if the
     * object is [AutoCloseable] it is closed.
     *
     * This is the screen-scoped equivalent of a ViewModel, and the point of it
     * is where the state *does not* go: without it the only scope outliving a
     * recomposition is the application root, so page state ends up there.
     *
     * [key] separates several retained objects of the same type within one
     * entry. It does not need to be unique across entries — each entry has its
     * own store.
     */
    fun <T : Any> retain(key: String = "", factory: () -> T): T
}
