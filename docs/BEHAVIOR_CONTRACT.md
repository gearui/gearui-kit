# Behaviour Contract (G2)

What the kit's components promise to do, beyond their signatures — the half of the public
contract `apiCheck` cannot see. Each rule names its evidence: a regression test, or a
device check recorded in [RELEASE_1.0_TODO.zh-Hans.md](./RELEASE_1.0_TODO.zh-Hans.md).
After RC1 a change to any rule here is a behaviour change and needs a recorded review, as an
API change does.

## 1. Controlled values

1. A component that shows a value takes it as a parameter (`value`, `checked`, `selected`,
   `current`) and reports the user's change through the paired callback (`onValueChange`,
   `onCheckedChange`, …). It does not change the value itself: the caller decides. A caller
   that ignores the callback keeps the old value on screen.
2. The callback fires once per user action, with the new value, during the event. Setting
   the value from code does not call it back.
3. Exceptions, by design:
   - **Pagers** (`Swiper`, `TabPager`, `ImageViewer`) report the page they settle on,
     whoever moved them — a swipe, autoplay or code — because the pager owns its position.
   - **`Slider`** follows the finger during a drag and settles to `value` on release, so a
     caller that clamps the value sees the thumb return. `onChangeStart`/`onChangeEnd` mark
     the drag.
4. **Formatted fields** (`Input(format = …)`) report the raw value (digits only), never the
   grouped display. An IME composition is a draft: nothing is reported until it commits, and
   the committed text is filtered. A caret move or a selection keeps the selection; typing
   over a selection replaces it. *Evidence:* `FormattedInputEditTest`, `FullFormattedInsertTest`;
   H1.3 and H2 on both platforms.

## 2. Overlays

1. An overlay closes for exactly the events its dismiss policy allows. The presets:

   | Preset | Outside tap | Scroll | Back | Route change | Anchor gone | Timeout |
   | --- | --- | --- | --- | --- | --- | --- |
   | `Dropdown` (Select, Cascader, Popover) | ✓ | ✓ | ✓ | ✓ | ✓ | — |
   | `Sheet` (BottomSheet, ActionSheet) | ✓ | — | ✓ | ✓ | — | — |
   | `Modal` (Dialog) | opt-in | — | ✓ | ✓ | — | — |
   | `toast()` (Toast, Snackbar) | — | — | — | — | — | ✓ |

2. `onDismiss` fires **once** per overlay and **immediately** when it starts to close, before
   the exit animation — so the caller's `visible` flag is current. Unmounting after the
   animation does not report again. *Evidence:* `OverlayContractTest`.
3. `ConfirmDialog` calls exactly one of `onConfirm`, `onCancel` or `onDismiss` for the action
   taken: the confirm button, the cancel button, or a dismissal (Back; an outside tap when
   enabled). `onDismiss` defaults to `onCancel`. The dialog does not close itself: the caller
   sets `visible = false` in the callback.
4. `onDismiss` reports a close the **runtime** made (Back, outside tap, scroll, timeout,
   route change). When the caller closes an overlay — `visible = false`, or the component
   leaving the composition — it is not called: the caller already knows, and confirming a
   dialog used to report a cancel as well. *Evidence:* `OverlayContractTest`; H9 on device.
5. With the keyboard up, the first Back hides the keyboard; the next one closes the overlay.
   A centred overlay stays above the keyboard. *Evidence:* H6, H8 on both platforms.
6. When the last focus-holding overlay closes, focus returns to the control that opened it
   if that control is still on screen (§6). *Evidence:* Web keyboard, J7; screen readers on
   Android and iOS pending (J1).

## 3. Navigation

1. `push`, `pop`, `replace`, `popTo` and `resetTo` change the stack; Back and the edge swipe
   are `pop`. A route's `onPopRequest` sees every pop and can allow, deny or hold it.
2. A held pop (`PopDecision.Pending`) freezes the stack until the caller confirms
   (`confirmPendingPop`) or cancels (`cancelPendingPop`); a late confirmation for an entry
   that is no longer on top does nothing. `resetTo` interrupts a transition and drops a
   pending confirmation — the forced-logout path.
3. A push during an enter animation settles that page first; nothing is dropped.
4. With the keyboard up, Back hides it before it pops. *Evidence:* `NavigatorStateTest`,
   `NavigatorPushTransitionTest`; H8.

## 4. Late async results and cancellation

1. A `TypedFormFieldState` validates one value at a time. An edit, a server error, a reset or
   disposal makes a running check stale: its result is dropped and it reports `false`. Asking
   again for the same value joins the running check. *Evidence:* `TypedFormFieldTest`.
2. Work started with `EntryScope.entryCoroutineScope` is cancelled when the entry leaves the
   stack; `retain` objects are closed then. *Evidence:* `NavigatorStateTest`.

## 5. Disabled versus read-only

1. **Disabled** (`enabled = false` for fields, `disabled = true` for actions): dimmed once
   (`FeedbackDefaults.disabledOpacity`), takes no input, and a field cannot be focused.
2. **Read-only** (`readOnly = true`, fields): shown at full contrast, not dimmed, not
   editable; no clear button.
3. Field-family components use `enabled`; action-family components use `disabled`; no
   component exposes both (COMPONENT_SPEC §2, `check_state_param_naming.sh`).
4. *Pending (J1):* how a screen reader announces each state, and whether a read-only field's
   text can be selected for copying on each platform.

## 6. Accessibility layers

1. Every overlay, every page under the top one and every unselected tab sits in a native
   view carrying the Kuikly property `gearuiLayer` (`AccessibilityLayer`):
   - `modal` — a dialog, sheet, drawer, picker or full-screen panel that answers Back;
   - `popup` — an anchored panel that answers Back (Select, Popover, ComboBox list);
   - `hidden` — a parked page under the top one, an unselected `TabHost` child;
   - empty — none of these, or an overlay that has started to close.
2. Kuikly does not act on it: the host renderer does. A host that ignores it loses only what
   this section promises; nothing else depends on it.
3. **Web** (`sample/jsApp`, `KeyboardAccess`): a `modal` takes focus, gets
   `role="dialog"`/`aria-modal`, and everything outside it is `inert` until it closes; a
   `popup` takes focus unless a text field has it (a combo box keeps typing); both return
   focus to the control that had it when they close; `hidden` is `inert`. Escape sends Back
   while a modal or popup is open, and only then. Clickable nodes get `tabindex`, an ARIA role
   and Enter/Space activation. *Evidence:* J7.
4. **Android** (`sample/src/androidMain/.../AccessibilityLayers.kt`, registered in the Kuikly
   delegate's `registerViewExternalPropHandler`) and **iOS** (`sample/iosApp/AccessibilityLayers.swift`,
   a `setCss_gearuiLayer:` setter on `UIView`): while a `modal` is open everything outside it is
   hidden from TalkBack/VoiceOver; the screen reader moves into it and, when it closes, back to
   what it was on. `hidden` is hidden. An app's host copies these files, as it wires
   `RuntimeInsetsBridge`. *Evidence:* J1.2, J1.3.
