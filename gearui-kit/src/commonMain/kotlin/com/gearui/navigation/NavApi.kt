package com.gearui.navigation

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
@Stable
data class NavEntry(
    val route: String,
    val key: String,
    val options: NavOptions = NavOptions.Default,
)

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
    val onPopRequest: ((PopRequest) -> PopDecision)? = null,
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
data class PopRequest(
    val entry: NavEntry,
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
interface NavigatorController {
    val current: NavEntry
    val previous: NavEntry?
    val canPop: Boolean
    val isTransitioning: Boolean

    fun push(route: String, key: String? = null, options: NavOptions = NavOptions.Default)

    /** Fires [NavOptions.onPopRequest] and pops, refuses or suspends accordingly. Returns false at the bottom of the stack. */
    fun pop(): Boolean

    /** Skips [NavOptions.onPopRequest], for continuing after the caller has confirmed a dirty state. Returns false at the bottom of the stack. */
    fun forcePop(): Boolean

    /** Pops to the nearest entry matching [route]. Returns false if already on top or no match exists. */
    fun popTo(route: String): Boolean

    fun replace(route: String, key: String? = null, options: NavOptions = NavOptions.Default)

    /** Clears the stack down to [route]. Every removed entry fires onEntryRemoved. */
    fun resetTo(route: String)
}

/**
 * Local scope for one rendered entry.
 *
 * - [entry] is the entry rendered by **this layer**, which is not necessarily the top
 * - [isTop] is whether it is the top; false for the previous layer during a transition
 * - [isForeground] is top with no transition in flight; callers can pause polling or animation on it
 */
@Stable
interface EntryScope {
    val entry: NavEntry
    val controller: NavigatorController
    val isTop: Boolean
    val isForeground: Boolean
}
