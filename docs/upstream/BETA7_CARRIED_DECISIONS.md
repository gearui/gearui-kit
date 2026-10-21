# Beta7: Decisions Carried From Beta3

Gate E3 ([QUALITY_STATUS.md §7 E](../QUALITY_STATUS.md)): "Decisions carried from
beta3 — native type scale, iOS password visibility toggle, Kuikly text-field focus
crosstalk: decide and implement, or record as known limits."

## Where The Three Items Come From

The three items were first recorded together in `docs/BETA3_RELEASE_READINESS.md`
(commit `746514c`, 2026-09-20) as "decisions listed in the changelog's alignment
notes (native type scale, password reveal on iOS, intermittent Kuikly text-field
focus crossing)". The same one-line note was carried through the doc rewrite
(`4d8a499`) into QUALITY_STATUS §3. The type-scale item was spelled out earlier as
"Type scale vs HeroUI 16/14/18 pending decision" (`docs/COMPONENT_COVERAGE_SPEC.md`
at `0fbe89c`). No more detailed description of the password or focus items exists
in the docs or commit history.

Baseline: gearui-kit working tree on 2026-09-30, Kuikly `2.28.0`
(`gradle.properties:49`). KuiklyUI references are checked against tag `2.28.0`
(`c9c1c743`).

## Summary

| Item | Decision | Kit code change for beta7 |
| --- | --- | --- |
| E3a Native type scale | **Decided and implemented** (beta4, `24f97db`): one reference scale on every platform by default; the old iOS-native scale is an opt-in profile. Close the item. | None. Update the QUALITY_STATUS §3 wording (docs only). |
| E3b iOS password reveal | **Implemented** (beta3, `c46305d`): the kit works around the Kuikly bridge. The reveal button stays app-owned through `suffix`. Record two small edges as known limits. | None required. Optional one-line guard described below. |
| E3c Text-field focus crosstalk | **Known limitation** for beta7: the kit already has five mitigations. The rest is in Kuikly, and there is no recorded repro. | None. Collect a repro, then file upstream. |

---

## E3a. Native Type Scale

### Problem

Should GearUI use the platform-native type scale (iOS: 17 body / 20 title, emphasis
600) or the reference scale (HeroUI Native: 12/16, 14/20, 16/24, 18/28, emphasis
500)? A per-platform scale renders the same screen at different sizes on iOS and
web, so a layout checked on one platform cannot be trusted on the other.

### Current State: Already Decided In Code

- `gearui-kit/src/commonMain/kotlin/com/gearui/theme/Typography.kt:127`:
  `Typographies.Default = TypographyProfiles.Reference`. This is the default for
  `GearUITheme` (`theme/Theme.kt:88`) and `App` (`App.kt:99`).
- `Typography.kt:144`: `Typographies.Platform = TypographyProfiles.Native`, an opt-in
  profile with the old iOS metrics. `Typography.kt:147`: `Typographies.Web`, a denser
  web scale.
- The profiles are generated from `tokens/typography.tokens.json` into
  `theme/TypographyProfiles.kt` (Native `:9-33`, Reference `:35-59`, Web `:61-85`).
- CHANGELOG beta4: "The default type scale is now the reference scale ... the same on
  every platform. `Typographies.Platform` restores the old metrics." (commits
  `695d6f3`, then `24f97db`, 2026-09-20.)

### Decision

Close E3a as **implemented**. The default is the Reference scale, the same on every
platform. Apps that must sit next to native controls, or that must not reflow after
an upgrade, pass `GearUITheme(typography = Typographies.Platform)`.

Follow-up (docs only): QUALITY_STATUS §3 (both languages) still says the native type
scale is undecided. That line should point here instead.

### Out Of Scope Here: System Font-Size Following

This item is about which scale the kit uses. It does not cover whether text follows
the OS font-size setting (iOS Dynamic Type, Android font scale). That is gate D3.
Context for D3, from source and not measured at runtime:

- Kuikly Compose applies `configuration.fontSizeScale` to text
  (`compose/.../foundation/text/BasicText.kt:344-363`). Only the pager event
  `configurationDidChanged` updates it (`compose/.../ComposeContainer.kt:306-309`).
- Grepping `core-render-ios` and `core-render-android` at 2.28.0 finds no code that
  sends `fontSizeScale`. On iOS, following Dynamic Type may therefore need the host
  to post that event. **Unverified**: D3 must measure it on a device.
- The HarmonyOS text fields have their own follow-system switch
  (`core-render-ohos/.../input/KRTextFieldView.cpp:164-167`).

---

## E3b. iOS Password Visibility Toggle

### Problem (Upstream Root Cause, Kuikly 2.28.0)

1. **Masking on iOS only happens through `keyboardType`.**
   `core-render-ios/Extension/Components/KRTextFieldView.m:249-252` calls
   `setSecureTextEntry:[css_keyboardType isEqualToString:@"password"]`.
2. **Compose never sends a keyboard type when it goes back to plain text.**
   `compose/.../foundation/text/CoreTextField.kt:679-684` maps only
   Number/Email/Password, and `else -> {}` sends nothing. Toggling
   `KeyboardType.Password` → `Text` leaves the native field secure, so the password
   stays masked after "reveal".
3. **`visualTransformation` is accepted and dropped.**
   `compose/.../foundation/text/BasicTextField.kt:251` and `:411` declare the
   parameter, but it never reaches `CoreTextField`. `PasswordVisualTransformation`
   therefore has no effect on any platform.
4. **The multi-line native view never masks.**
   `KRTextAreaView.m:313-315` sets `keyboardType` without `secureTextEntry`.
   `CoreTextField.kt:228-233` forces a single-line view for `KeyboardType.Password`,
   but it picks the view kind once, in `remember { AutoHeightTextAreaView(...) }`.

### Current State: Implemented In The Kit

- `components/input/Input.kt:147-159`: after a field has once been a password field
  (`passwordCapable`), it always sets its keyboard type explicitly. When revealed it
  sends `"text"` (or `"number"`/`"email"`). Applied with
  `Modifier.setProp("keyboardType", ...)` at `Input.kt:413`.
- `Input.kt:373-377`: masking goes through `KeyboardType.Password`.
  `Input.kt:398`: `PasswordVisualTransformation` is kept for semantics only.
- Fix commit `c46305d` ("revealing a password works on iOS"): "Verified on the iPhone
  17 Pro simulator: mask, reveal and mask again, with focus and text kept."
- Reveal UI is app-owned. The kit ships localized labels
  `FieldStrings.showPassword` / `hidePassword` (`i18n/DomainStrings.kt:195-196`, in all
  language packs), and the sample shows the pattern with `suffix` +
  `PressableFeedback` + `Icons.eye`/`eye_slash`
  (`sample/.../examples/input/InputExample.kt:221-243`).

### Options

1. Keep it as is: `isPassword` works both ways, and the app draws the eye button in
   `suffix`.
2. Add a built-in reveal toggle (`Input(passwordToggle = true)`), rendering the eye
   button with the shipped strings. This is new public API during the beta7 freeze,
   and it fits better in a minor release.

### Decision

Close E3b as **implemented (option 1)**. No built-in toggle in 1.0; the documented
pattern is `isPassword = !revealed` plus an eye button in `suffix` that uses
`I18n.strings.field.showPassword` / `hidePassword`.

Record these as known limits:

- **`isPassword` implies a single-line field.** On iOS, `Input(maxLines > 1)` built
  with `isPassword = false` and later switched to `true` does not mask
  (`KRTextAreaView.m:313-315`, view kind chosen once at `CoreTextField.kt:228-233`).
  Workaround: keep password fields at the default `maxLines = 1`.
- **Re-masking follows UIKit.** A `UITextField` that becomes secure again while it has
  focus and text may clear the whole text on the next keystroke. This is standard iOS
  secure-entry behaviour. The change arrives through the normal text-change path, so
  Compose state stays consistent. **Not verified in this kit**: the `c46305d`
  simulator check covered toggling, not typing after re-masking. Not checked on a
  physical device.

Optional guard, a small change that is not needed for beta7: in `Input.kt`, compute
the effective line count as `if (passwordCapable) 1 else maxLines`, use it wherever
`maxLines` feeds `singleLine`/`maxLines`/`imeAction` (`Input.kt:380-383`, `:387`,
`:394-395`), and add a KDoc line on `isPassword`. One file, no API change.

---

## E3c. Kuikly Text-Field Focus Crosstalk

### Problem As Recorded

The only record is the one-line note "intermittent Kuikly text-field focus crossing".
No steps, platform or build are recorded. The user-visible symptoms consistent with
the code below are:

- (a) the field the user tapped looks focused, but the keyboard or caret goes to a
  different field;
- (b) the field loses focus right after it gains it.

### What The Kit Already Does (Mitigations In Code)

1. `components/input/Input.kt:193-198`: the border colour does not depend on focus.
   On Kuikly, a focus-dependent modifier rebuilds the chain and recreates the native
   `EditText`, which drops focus. The focus ring is drawn by `rememberInputFeedback`
   instead (`Input.kt:210-213`).
2. `Input.kt:262-287`: a container `pointerInput(requireUnconsumed = false)`
   re-requests focus on every tap release, through a one-frame-deferred
   `LaunchedEffect` tick (`Input.kt:218-231`). It compensates for intermittent focus
   loss inside the native field.
3. `Input.kt:314-319`: the placeholder lives inside `decorationBox`, so a sibling
   `Text` cannot take the tap and focus from the field.
4. `Input.kt:441-445` onward: the clear button consumes the down event in the Initial
   pass, so the native field never goes through blur → IME hides → refocus.
5. `Input.kt:165-172`: `autoFocus` waits for `onGloballyPositioned`, because a
   first-composition request is dropped inside lazy lists.

`foundation/keyboard/KeyboardDismiss.kt:54-86` also makes sure a tap on another input
does not dismiss the keyboard, using `keyboardDismissExempt`.

### Likely Upstream Causes (Kuikly 2.28.0, From Reading Code — Not Reproduced)

1. **A single last-write-wins slot for keyboard commands.**
   `compose/.../ui/platform/SoftwareKeyboardController.kt:91-121` merges every
   `startInput`/`stopInput`/`show`/`hide` issued before the next `setTimeout` tick into
   one `(pendingView, pendingAction)` pair. Examples:
   - `startInput(B)` followed by `hide()` in the same tick: B never gets native focus,
     and `activeView` still points at the previous field A (`:110-111`). A later
     `show()` (`:107-108`, reached through
     `CoreTextField.kt:935-936 requestFocusAndShowKeyboardIfNeeded`) calls
     `A.focus()`. The keyboard goes to A while Compose thinks B is focused. That is
     symptom (a).
   - `startInput(B)` then `stopInput(A)` in one tick leaves `STOP_INPUT(A)`. B is never
     focused natively. That is symptom (b).
2. **Native blur is not observed.** `CoreTextField.kt:436-442` subscribes only to the
   native `inputFocus` event; `inputBlur` (`core/.../views/TextAreaView.kt:727`) is
   never hooked. When the platform resigns first responder or focus (system UI, another
   native responder), Compose `hasFocus` stays `true` for the old field.
3. **Lazy-list reuse window.** `CoreTextField.kt:927-933` drops the focus request when
   the native view has already handled the touch but the `FocusRequester` has not been
   re-attached yet. Native and Compose focus then disagree.

All three live in KuiklyUI's Compose text/keyboard bridge. None of them can be fixed
inside gearui-kit without replacing `BasicTextField`.

### Options

1. Record it as a known limitation, keep the mitigations, and collect a repro to file
   upstream.
2. Change more kit code blind, for example serialising focus requests. Without a repro
   this cannot be accepted, and earlier attempts show the risk: a focus-dependent
   border was rejected because it brought the focus loss back (Input.kt:193 comment).

### Decision

**Known limitation for beta7 (option 1).** No kit code change.

- **User-visible impact:** occasionally, typing lands in a different field than the
  one tapped, or a field loses focus straight after the tap. Tapping the field again
  recovers. Nothing is lost silently: text goes to a visible field.
- **App workaround:**
  - Do not make field modifiers depend on focus state.
  - When moving focus programmatically (for example "next field" on IME action), do
    not call `keyboardController.hide()` or `clearFocus()` in the same handler as
    `requestFocus()`. Request the next field's focus alone, or in a `LaunchedEffect`
    one frame later.
- **To close upstream:**
  1. Reproduce with the sample Input page (three `Input`s): rapidly alternate taps,
     use IME Done then tap the next field, and repeat inside a `LazyColumn`. Record
     on the iOS simulator and an Android device.
  2. File a KuiklyUI issue citing `SoftwareKeyboardController.kt:91-121` and
     `CoreTextField.kt:436-442`. The suggested fix is to keep a queue, or
     per-view intents, instead of one pending slot, and to subscribe to `inputBlur`
     to clear Compose focus.

Unrelated to the decision: two comments in `Input.kt` are cut off mid-sentence.
`Input.kt:193-194` ends at "...recreates the underlying EditText;", and
`Input.kt:262-266` ends at "...inside the EditText. pointerInput". They should be
finished when `Input.kt` is next edited. That is a comment-only change.

---

## Doc Follow-Ups When E3 Is Marked Done

- QUALITY_STATUS.md / .zh-Hans.md §3: replace the "decisions carried from beta3" bullet
  with a link to this file. Record E3a and E3b as done, and E3c as a known limitation.
- COMPONENT_SPEC (Input guidance): state "`isPassword` implies single-line; the reveal
  button is app-owned via `suffix`", and add the focus workaround above.
