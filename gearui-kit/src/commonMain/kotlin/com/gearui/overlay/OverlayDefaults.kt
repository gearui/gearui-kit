package com.gearui.overlay

import androidx.compose.runtime.Composable
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.layout.Radius
import com.gearui.theme.Theme
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.foundation.layout.Spacing

/**
 * Runtime defaults for overlays.
 *
 * The scrim is a runtime-layer token rather than part of the core Colors set
 * (see TOKEN_FREEZE_DECISIONS, Decision 1). Overlay, Dialog, BottomSheet,
 * ActionSheet and the other modal layers all take their scrim colour from here.
 *
 * ## Overlay surface contract
 *
 * Overlays are grouped by how they are positioned. Shape and elevation are
 * fixed together per group; components do not pick steps themselves.
 *
 * | Group | Shape | Elevation | Members |
 * |---|---|---|---|
 * | panel (trigger-anchored / transient) | [panelShape] `md` 6dp | [Elevation.raised] / [Elevation.floating] | Select, Cascader and TreeSelect dropdowns, Popup, Popover, ContextMenu, Toast, Snackbar, Notification |
 * | modal (centred, takes focus) | [modalShape] `xl` 12dp | [Elevation.modal] | Dialog, Tour |
 * | sheet (edge-anchored) | [sheetShape] 12dp top corners | none — the scrim separates it | BottomSheet, ActionSheet, Drawer |
 *
 * Before this contract the three groups were mixed: Dialog used `lg` (8) while
 * the Select dropdown it covered used `xl` (12), so the modal was less rounded
 * than the dropdown. Cascader, TreeSelect, Popup and Snackbar used `sm` (4)
 * while Popover, ContextMenu and Notification used `md` (6) — four radii for
 * one kind of surface.
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
        @Composable get() = Theme.shapes.md

    /** Surface shape for centred modal cards. */
    val modalShape: Shape
        @Composable get() = Theme.shapes.xl

    /** Corner radius for edge-anchored sheets; only the corners facing content are rounded. */
    val sheetCornerRadius: Dp = Radius.xl

    /** Sheet rising from the bottom: only the top corners are rounded. */
    val sheetShape: Shape =
        RoundedCornerShape(topStart = sheetCornerRadius, topEnd = sheetCornerRadius)

    /**
     * Modal scrim: pure black at roughly 55% opacity.
     *
     * The base must be pure black rather than near-black such as 09090B. In the
     * dark theme the page background is itself close to 09090B, so a near-black
     * scrim darkens almost nothing and the overlay fails to separate from the
     * page. Pure black darkens the background in both themes.
     */
    val scrimColor: Color = Color(0x8C000000)
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
