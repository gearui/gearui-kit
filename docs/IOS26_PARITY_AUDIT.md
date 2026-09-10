# iOS 26 Parity Audit

Scope: overlays, inputs, and the global interaction conventions — the areas
where GearUI's reference (`DESIGN_SYSTEM_SPEC` §0.1) is most visible and where a
divergence is felt rather than seen.

Blur is out of scope by decision: `materialPolicy` ships `Never` until KuiklyUI's
renderers agree on what a blur is. See `UPSTREAM_KUIKLYUI_BLUR.md`.

Each finding says what was checked and what the evidence was, so a later reader
can disagree with the conclusion rather than the premise.

## Closed in this pass

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

## Open, with reasons

### SearchBar's Cancel button is manual

`showCancel: Boolean = false`; the caller decides. On the reference platform the
Cancel button animates in when the field becomes first responder and out when it
resigns.

**Not changed here.** Making it focus-driven means a layout change adjacent to a
focused text field, and this repo has a recorded hazard in exactly that place:
`Input.kt` documents that on Kuikly a chain rebuilt on focus recreates the
EditText. The field's own chain would not change, so it is probably safe — and
"probably safe" is not enough to flip a default in a UI kit without seeing it on
a device. The sample is JS-only, and the JS renderer is not where this would
break.

Needs: a device pass on Android and iOS. Then `SearchBarCancel { NEVER,
WHILE_EDITING, ALWAYS }` defaulting to `WHILE_EDITING`, with `showCancel`
deprecated rather than removed.

### List separators have no convention

`DividerInset()` exists — 16dp leading inset, the right shape for a row without
a leading element — and **no component uses it**. `Cell` draws no separator at
all, so each caller picks: `DividerFull`, `DividerInset`, or nothing.

Two divergences follow. On the reference platform a grouped list insets the
separator to align with the row's *text*, which for a row with a leading icon or
avatar is further than 16dp; and the last row in a group has no separator.
Neither can hold when the caller decides.

**Not changed here.** Making `Cell` draw its own separator would double the
separators everywhere a caller already adds one, across every list in every host
app. This needs a `CellGroup` that owns the run of rows — which is the component
that should have decided this all along — and that is a new component, not a
detail fix.

### Drawer has no dismiss gesture

An edge-anchored panel that can only be closed by the scrim or the back button.
Not strictly an iOS 26 question — the reference platform has no drawer, and
Apple's equivalent is a sheet — so the convention to match is not obvious.
`Modifier.swipeDismiss` is directional and would need a horizontal variant.

Left open deliberately: the right answer is probably "GearUI's own convention",
which is a decision rather than a parity fix.
