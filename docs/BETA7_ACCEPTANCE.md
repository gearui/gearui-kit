# beta7 Candidate Acceptance Record

Updated 2026-09-30. This is an evidence ledger, **not release approval**. The
full A–F gate remains in [QUALITY_STATUS.zh-Hans.md §7](./QUALITY_STATUS.zh-Hans.md)
and [QUALITY_STATUS.md §7](./QUALITY_STATUS.md). New feature scope follows
Claude Code's approved plan; this work does not replace that scope with a
smaller alternative.

## Implemented Candidate

- B6–B9: stable-value Picker and cancellable Cascader; constrained date/time
  pickers; typed asynchronous form validation; exact decimals and editable
  Stepper. Production components call the tested helpers, not parallel demo
  implementations. B3's IME composition draft handling is also wired into
  `Input`.
- C1–C2: ListBox, YearPicker, DateField/TimeField, Toolbar, SubMenu, Kbd,
  ColorPicker, Meter, User, Code/Snippet. Public API dumps, registered sample
  pages, color-size DTCG tokens and localized seconds are updated.
- Avatar loading/failure/source replacement, input-group disabled opacity,
  color-plane edge marker and grouped field error ownership were corrected.
- The Web sample now forwards viewport width changes to the Kuikly root. Numeric
  GearUI fields use a Web-safe text input because Kuikly's Web renderer calls
  `setSelectionRange` on HTML number inputs, which browsers reject. Native
  platforms still request their number keyboard.

## Review Fixes (2026-09-30)

A code review of the candidate found component-level defects that the helper tests
did not reach. Fixed, with tests on the production paths where they are pure:

- Number fields regressed to a text keyboard on iOS and Android (`Decimal` is not a
  KuiklyUI keyboard). Whole non-negative fields get the digit pad again; decimal or
  signed fields use the text keyboard, as KuiklyUI's only numeric pad has no "." or "-".
- Date and time wheels enumerated every admissible value (about 73,000 dates) on each
  recomposition, closed or open. They are now built per column on demand, and
  constraints compare by value so inline construction does not reset the wheels.
- `Picker.Linked` could not confirm a branch shallower than its columns; duplicate
  sibling values threw during composition; stored dates and times in other shapes
  were not read; coarse time slots could precede `min`.
- A typed field's superseded check returned false indistinguishably from a failure,
  and a new `initialValue` discarded typed input. Stepper became editable by default
  with a text keyboard; it is read-only again unless `editable = true`.

These still need device observation: the digit pad on each number field, and turning
the date wheels across month ends and bounds.

## Acceptance Run (2026-09-30)

Devices: Android 16 phone (2201122G, gesture navigation, 560 dpi); iOS 26.2
simulator "GearUI beta7 iPhone 17 Pro" — the maintainer accepted a simulator for
iOS on 2026-09-30. 93 routes, light and dark.

**A2 downstream.** Android: privchat-ui, live-chat and lms-app compile against the
candidate. iOS (after `:sdk:privchatCargoBuildAppleFfi` built the Rust archives):
privchat-app `linkDebugFrameworkIosSimulatorArm64` and live-chat
`linkPodDebugFrameworkIosSimulatorArm64` link; lms-app did not compile for iOS at all
before this run (JVM-only `@Volatile`/`synchronized` in common code, an unbound
`AVAudioSession.setActive`) and links after lms-app 22b7654. The Weey release build
was installed on the device: avatars and swipe back verified, no crash.

**D1 screen readers.**
- iOS: `scripts/acceptance/ios_accessibility_audit.sh` runs XCUITest
  `performAccessibilityAudit` (all types except Dynamic Type, which is D3) over the
  186 page-themes: 683 findings before fixes — contrast 467, text clipped 126,
  description 44, hit region 42, trait 2.
- Android: `scripts/acceptance/android_accessibility_audit.py` records, per page, the
  node tree TalkBack reads (readouts in `build/beta7-acceptance/android-a11y/`):
  1 unlabelled control, 240 targets under 48dp (186 under 44dp).
- Fixed: LinkedText read character by character on iOS (semantics now on a wrapper;
  links whole); Tag close unnamed; CloseButton, NumberField steps, Upload remove,
  calendar month buttons, SearchBar icon and Tag close take touches over 44dp without
  moving layout (KuiklyUI does not widen small targets — verified on device that a tap
  5dp outside a 32dp close button did nothing before and closes the card after).
- Accepted as is: inline links (WCAG 2.5.8 inline exception); IndexBar letters (a
  drag strip); text fields whose inner line is 20dp — tapping the field's padding
  focuses it (verified on device), so the target is the 44dp field; "Label not
  human-readable" on values such as "10.0%", e-mail addresses and file names;
  "Text clipped" on multi-line descriptions that render complete (KuiklyUI lays text
  out itself; UIKit's estimate disagrees).
- Open, needs a decision: **contrast**. The reference palette measures
  lightMutedForeground on background 4.43:1 (on muted 4.05), primary text on light
  surfaces 3.38–3.68, white on a primary button 3.59; dark theme passes (4.8–7.7).
  AA for body text is 4.5. Stepper ±, 40dp small buttons, 24–28dp segmented, radio
  and checkbox rows are under 44dp by design and need the same decision.
- Upstream: an iOS multi-line text field has no accessible name (the kit names fields
  through the native placeholder; UITextView has none).

**D2 visual.** Android light and dark first viewports reviewed page by page against
VISUAL_SPEC (contact sheets in `build/beta7-acceptance/android-sheets/`). Fixed: Kbd
and Code text flush top-left; Card content centred (read as a triple inset); Upload
file tiles unreadable under a scrim; Timeline connector gaps; Watermark tiles
wrapping mid-word; Navigator v1 demo under the status bar; a hard-coded white card
in the navigator spike in dark; unlabeled ListBox value; typed-form placeholder;
ungrouped stable-value picker row; nine captures that landed on Home (deep links now
render the target on the first frame). Registered deviations: Switch thumb is a pill;
Stepper keeps its bordered style; SearchBar default and rounded variants look alike;
Image page shows placeholders only; User rows have no card; long icon names wrap;
ToggleButton "delete" is not destructive; ColorPicker sliders are flat; Code block
scrolls without a fade cue.

iOS simulator light and dark reviewed the same way (`build/beta7-acceptance/ios-sheets/`):
90 of 93 routes clean. The first iOS set was unusable — `capture_sample.sh` shot one
second after launch, so 52 frames were blank or mid-transition (which also produced
false "dark status bar", "slider thumbs at 0" and "segmented indicator misplaced"
findings); the script now waits and retakes a uniform frame, and the re-shoot has no
blank frame. Remaining iOS-only finding: a single-line NavBar title is cut without an
ellipsis (the kit asks for `TextOverflow.Ellipsis`, which KuiklyUI maps to "tail";
recorded for upstream). Diagnostic pages (runtime-performance, runtime-insets) have
cosmetic issues only.

**D4 RTL.** Not supported. Android's force-RTL setting does not reach Compose, and
providing `LocalLayoutDirection` Rtl at the root (Arabic tag, confirmed by the kit's
strings) still lays every page out left to right, although KuiklyUI's placement code
mirrors. Recorded for upstream; the kit ships no right-to-left language.

**D5 performance (iOS simulator, Release).** `scripts/perf/ios_perf.py`, 5 cold
starts: startup to Home content median 1029 ms (1009–1319; process → Kuikly page
786, page → content 243) against 1000 — over by 3% on a simulator; theme switch of
the whole app median 47.7 ms (p90 51.7) against 120; 1000-row fling 0% janky frames,
p95 17.0 ms. The Debug build measured 1312 / 138.7 / 0% and is not the release figure.

**D6 Web.** `scripts/acceptance/web_pages.mjs` over all 93 routes at 390×844: load,
scroll, tap, resize 1280 → 390. 91 clean; `avatar` logs the demo's intentionally
broken image URL; `combo-box` leaves an open dropdown where the desktop layout put it
after the window narrows (overlays are not re-anchored on viewport resize; minor).

**E1.** Fixed by 3ef6158's decoration keys (size, shape, border): on the Android
device and the iOS simulator the Surface lab switched round → square → round with
every border and shadow redrawn in place.

**E2/E3.** Upstream issue drafts for the four blur gaps and the carried decisions are
written for the maintainer's review (kept out of the repository until approved):
native type scale — decided in beta4 (reference scale default, `Typographies.Platform`
opt-in); iOS password reveal — implemented in beta3 (explicit keyboard type), with
the single-line limit documented; text-field focus crosstalk — known limitation with
five kit-side mitigations.

## Local Checks

On the working tree, Android, browser JS and iOS simulator Kotlin tests, API
check, lint, sample Android APK and Web bundle passed. HarmonyOS shared library
and unsigned HAP built. All six Maven publications staged under
`/tmp/gearui-beta7-staging` with a beta7 version override, without upload,
signing or tag. The staging directory is local and not a Central verification.
The last exact-source repeat and remote CI must be attached to a committed SHA
before A1/C3/F1 can be closed.

Reproduce screenshots after installing the candidate app:

```bash
scripts/acceptance/capture_sample.sh android <adb-serial>
scripts/acceptance/capture_sample.sh ios <simulator-udid>
```

The script captures **first viewports only** for all 93 sample routes, light and
dark. A generated PNG or successful launch is not a visual verdict, and a
simulator is not an iOS device. Review each result against `VISUAL_SPEC`, scroll
long pages and exercise state changes before marking D2 complete.

## Open Acceptance and External Prerequisites

- B3: Chinese IME middle edits and actual paste on devices remain to be
  observed. The code preserves native composition, but the Kuikly bridge's
  exact IME events are platform behavior, not provable by a pure function test.
- B2: the layout wait and retry paths are exercised by tests. The two-frame
  footer delay is a mitigation, not proof that every native lazy measurement
  completed; test page append/scroll on both platforms before calling it final.
- D1–D4: all-page TalkBack/VoiceOver/XCUITest audit, visual decisions, largest
  font and RTL checks remain. Screen capture alone closes none of them.
- D5: the approved target is an iPhone 16 Pro Max **physical device**. The
  iPhone 17 Pro simulator and Android device cannot substitute for its numbers.
- D6: selected Web interactions, a 390px viewport, a desktop-to-phone width
  resize, and DateField editing have been exercised. Kuikly's HTML number-input
  crash was found and worked around at the GearUI input boundary; all routes
  and resize transitions have not been exercised.
- D7–D8: an unsigned HarmonyOS HAP built, but no signed emulator/target is
  available for runtime acceptance; Huawei, OPPO and vivo devices are absent.
- A2: privchat-ui, live-chat, lms-app and privchat-app Android compiled. The
  downstream iOS builds require missing Rust `libprivchat_sdk_ffi.a` archives;
  this is a separate SDK prerequisite, not a passing downstream check.
- E1–E3: border hot-switch, four upstream blur issues and font/password/focus
  limits need explicit disposition before release. Glass remains off by default.

No Central publication, tag or downstream beta7 version bump is authorized by
this record. Each unchecked gate needs evidence or an explicit maintainer
defer/waiver on the candidate commit.
