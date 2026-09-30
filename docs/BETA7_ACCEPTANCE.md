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
