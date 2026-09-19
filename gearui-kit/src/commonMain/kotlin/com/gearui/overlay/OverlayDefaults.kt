package com.gearui.overlay

import androidx.compose.runtime.Composable
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.theme.DefaultPalette
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.foundation.layout.Spacing

/**
 * Runtime defaults for overlays.
 *
 * Values follow HeroUI Native 1.0.9 (`src/styles/components/{dialog,popover,menu,
 * select,toast,bottom-sheet}.css` and the popup animation hooks) through the DTCG
 * sources in `tokens/`; nothing here is a free-standing number.
 *
 * ## Overlay surface contract
 *
 * | Group | Shape | Members |
 * |---|---|---|
 * | panel (trigger-anchored / transient) | [panelShape] 24 (`--radius-3xl`) | Select, Cascader and TreeSelect dropdowns, Popup, Popover, ContextMenu, Toast, Snackbar, Notification |
 * | modal (centred, takes focus) | [modalShape] 24 (`--radius-3xl`) | Dialog, Tour |
 * | sheet (edge-anchored) | [sheetShape] 32 top corners (`--radius-4xl`) | BottomSheet, ActionSheet, Drawer |
 *
 * Every overlay surface uses the overlay colour and the overlay shadow stack. They
 * draw no border: in the dark theme the shadow stack is itself a 1px inset hairline.
 *
 * Three runtime rules are guaranteed by [OverlayHost] and must not be
 * reimplemented per component:
 *  - the scrim always covers the whole viewport, unaffected by safe area;
 *  - safe area applies to overlay **content** only, declared through
 *    `OverlayOptions.safeArea*`;
 *  - sheets, drawers and action sheets start at the viewport edge, not at the
 *    safe-area edge.
 */
object OverlayDefaults {
    /** Surface shape for trigger-anchored and transient overlays. */
    val panelShape: Shape
        @Composable get() = RoundedCornerShape(ControlGeometry.radiusOverlay)

    /** Surface shape for centred modal cards. */
    val modalShape: Shape
        @Composable get() = RoundedCornerShape(ControlGeometry.radiusOverlay)

    /** Corner radius for edge-anchored sheets; only the corners facing content are rounded. */
    val sheetCornerRadius: Dp = ControlGeometry.radiusSheet

    /** Sheet rising from the bottom: only the top corners are rounded. */
    val sheetShape: Shape =
        RoundedCornerShape(topStart = sheetCornerRadius, topEnd = sheetCornerRadius)

    /**
     * Modal scrim: the reference `--backdrop`, black at 20%.
     *
     * The reference uses the same backdrop in both themes, so this stays a plain value
     * rather than a theme lookup; a unit test fails if the two tokens ever diverge.
     */
    val scrimColor: Color = DefaultPalette.lightBackdrop

    /**
     * How long an overlay takes to arrive.
     *
     * Surfaces that slide themselves in (sheets, drawers) use this for both directions,
     * and the host keeps dismissed content mounted this long so their exit can finish.
     */
    val transitionDurationMillis: Int = FeedbackDefaults.overlayEnterDuration

    /** How long a fading or scaling overlay takes to leave; shorter than arriving. */
    val exitDurationMillis: Int = FeedbackDefaults.overlayExitDuration

    /** Distance from the trigger for anchored panels (menu and popover `offset`). */
    val anchorOffset: Dp = ControlGeometry.overlayOffset
}

/**
 * Resolved top offset for top-floating feedback (Snackbar, Notification).
 *
 * These components expose a `topOffset` that means "at least this far from the
 * top", not an absolute position: a hardcoded 48dp puts the banner underneath
 * the Dynamic Island, where the top inset is around 59pt. The result is the
 * larger of the caller's floor and `safeArea.top + Spacing.sm`.
 *
 * Reads the stabilised [com.gearui.runtime.RuntimeEnvironment.safeArea] rather
 * than `rawSafeArea`, which can momentarily report 0 and would make the banner
 * jump.
 *
 * `internal` — this is an implementation detail of the overlay contract, not
 * public API.
 */
@Composable
internal fun rememberTopFloatingOffset(minOffset: Dp): Dp {
    val safeTop = LocalRuntimeEnvironment.current.safeArea.top
    val safeOffset = safeTop + Spacing.sm
    return if (safeOffset > minOffset) safeOffset else minOffset
}
