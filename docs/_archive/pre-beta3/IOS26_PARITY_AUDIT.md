# iOS 26 Parity Audit

Scope: overlays, inputs, and the global interaction conventions — the areas
where GearUI's reference (`DESIGN_SYSTEM_SPEC` §0.1) is most visible and where a
divergence is felt rather than seen.

Blur is out of scope by decision: `materialPolicy` ships `Never` until KuiklyUI's
renderers agree on what a blur is. See `UPSTREAM_KUIKLYUI_BLUR.md`.

Each finding says what was checked and what the evidence was, so a later reader
can disagree with the conclusion rather than the premise.

## Closed

### Materials reached nothing

The seven surfaces §11.2 calls materials each painted their own opaque fill;
`MaterialSurface` was referenced only by the sample's probe page. Both the
`materialPolicy` KDoc and the spec said flipping one default was all that
remained once upstream lands. It was not — flipping it would have changed no
pixel in any app.

Fixed in `5554b0b`; guarded by `check_material_surfaces.sh`.

### Sheets could not be dragged away

`BottomSheet`, `ActionSheet` and `Drawer` had no grabber and no drag gesture.
On the reference platform a modal sheet shows a grabber and is dragged away, and
this kit already treats gestures as global conventions — swipe-back is one.

Fixed for `BottomSheet` in `bc14c0b`; spec §11.3.1; guarded by
`check_sheet_grabber.sh`.

`ActionSheet` deliberately excluded: a platform action sheet is a menu with a
Cancel card and is not draggable. A grabber there would promise a gesture it
does not have.

### SearchBar's Cancel button was manual

`showCancel: Boolean = false`; the caller decided. On the reference platform
Cancel arrives when the field takes focus and leaves when it gives it up.

Now `SearchBarCancel { Never, WhileEditing, Always }`, defaulting to
`WhileEditing`. `showCancel` is kept and still wins when true, so a host that
asked for a Cancel button keeps exactly what it had rather than having the
button start appearing and disappearing under it.

The Kuikly hazard that held this back is avoided rather than risked: the
`onFocusChanged` sits on a chain that does not itself depend on focus, which is
the distinction `Input.kt` records — a chain rebuilt *because* focus changed is
what recreates the EditText.

### List separators had no owner

`DividerInset()` existed and no component used it; `Cell` drew no separator, so
each caller picked. The sample's own component list showed the cost: its section
headers sat flush against the card edge, one padding step left of the rows they
labelled.

`CellGroup` now owns the header alignment, the separator inset, and "no
separator after the last row" — the one a caller cannot get right without
counting. Spec §11.3.2. The sample uses it, which is what fixed the header.

### Drawer had no dismiss gesture

Closed with a horizontal `Modifier.swipeDismiss`. The gesture attaches to the
whole panel, unlike a sheet's: a drawer body scrolls vertically, so a horizontal
drag is never competing with a scroll.

No grabber, deliberately — a drawer slides in from an edge and the edge is the
affordance. `check_sheet_grabber.sh` flagged it the first time the gesture
landed, which was the guard being too broad rather than the component being
wrong; the rule is now scoped to sheets.

## Checked and already correct

| Area | Evidence |
|---|---|
| Alert anatomy | §11.1 — centred text, full-width stacked actions under hairlines, 44dp rows, no filled buttons, `CANCEL` always last. Implemented in `Dialog`. |
| Action sheet anatomy | §11.3 — two cards with the backdrop showing between them, not one card with a painted gap. |
| Overlay shape and elevation | `OverlayDefaults` fixes shape and elevation per group (panel / modal / sheet), so components cannot pick their own steps. |
| Scrim and safe area | `OverlayHost` guarantees the scrim covers the viewport and that sheets start at the viewport edge, not the safe-area edge. |
| Input focus ring | No border change on focus, deliberately: on Kuikly a modifier chain that depends on focus is rebuilt and recreates the underlying EditText. The reference platform does not ring its text fields either — the caret carries focus. |
| Swipe-back | `Modifier.swipeBack`, recognise-then-consume, 1:1 tracking. |
| Tap-outside dismisses the keyboard | `keyboardDismissExempt` claims the gesture inside inputs, rather than treating "was the touch consumed" as a proxy — buttons consume touches too. |

## Open

Nothing from this pass. Blur remains out of scope by decision; see
`UPSTREAM_KUIKLYUI_BLUR.md`.
