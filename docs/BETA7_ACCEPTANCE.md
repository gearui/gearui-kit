# beta7 Candidate Acceptance Record

Updated 2026-09-30. This is the evidence ledger, **not release approval**. The gate
table is [QUALITY_STATUS.zh-Hans.md §7](./QUALITY_STATUS.zh-Hans.md) /
[QUALITY_STATUS.md §7](./QUALITY_STATUS.md); each gate there carries one status and
points here. Feature scope follows the approved beta7 plan.

Candidate: the commit that adds this record on main (the code of 199838e, plus a token test and acceptance scripts). Devices: Android 16 phone (Xiaomi 2201122G, 560 dpi,
gesture navigation); iOS 26.2 simulator "GearUI beta7 iPhone 17 Pro" (the maintainer
accepted a simulator for iOS on 2026-09-30); Chromium (Playwright) for the Web. 93
sample routes, light and dark.

Standards: text contrast WCAG 2.1 1.4.3 (4.5:1; 3:1 for large text), non-text 1.4.11
(3:1); target size WCAG 2.2 2.5.8 (24 px, AA) with the platform recommendations
reported beside it — iOS HIG 44 pt, Android 48 dp. Disabled controls are exempt from
contrast (1.4.3 "incidental").

## How the evidence is produced

Every script opens a page through `scripts/acceptance/sample_driver.py`: each
adb/simctl/idb command is checked, and a page counts only once its title (the NavBar's
English name, or the heading of a page without one) and content are in the
accessibility tree. Each run writes a fresh `build/acceptance/<kind>-<time>-<sha>`
directory with `run.json` (SHA, dirty worktree, device, build type, pages expected) and
`result.json`; a page that fails makes the run exit non-zero. The drivers were checked
against injected failures (bad device, failed launch, page never rendered, failed or
unreadable tree dump) — each is reported, none passes.

| Script | What it proves | What it does not |
| --- | --- | --- |
| `capture_sample.py android\|ios <dev> [--scroll]` | every page rendered, light/dark first viewport; `--scroll` also the page end | a visual verdict (reviewed separately) |
| `ios_accessibility_audit.sh <udid>` | Apple's `performAccessibilityAudit` on every page (exit 0 clean, 1 findings, 2 broken run) | VoiceOver on a device |
| `android_accessibility_audit.py <serial>` | the node tree TalkBack reads: labels, state, sizes, overlapping targets | TalkBack speaking, focus order as spoken |
| `interactions.py android\|ios <dev>` | forms, pickers, dropdowns, dialogs, sheets, popups behave (values written, BACK, scrim, keyboard) | screen-reader focus after an overlay closes |
| `web_pages.mjs <url>` | all pages load, scroll, resize 1280↔390 without errors, both themes | what a tap did (a smoke run) |
| `web_overlay_resize.mjs <url>` | ComboBox/Select/Popover stay on their trigger across resize and scroll, value written | — |
| `scripts/perf/ios_perf.py <udid> --runs N` | startup, theme switch, fling frames on the simulator, raw samples kept | device numbers |

After an XCUITest audit the simulator's accessibility service often reads empty to idb;
reboot the simulator before idb-driven scripts (the driver says so when it happens).

## Evidence by gate

**A1 CI.** CI green on 199838e; Guardrails failed there on a token test still asserting the old primary (fixed with this record); the record's own commit is checked below

**A2 downstream.** Android: privchat-ui, live-chat and lms-app compile against the
candidate. iOS, after `:sdk:privchatCargoBuildAppleFfi` built the Rust archives:
privchat-app `linkDebugFrameworkIosSimulatorArm64` and live-chat
`linkPodDebugFrameworkIosSimulatorArm64` link; lms-app never compiled for iOS before
(JVM-only `@Volatile`/`synchronized` in common code, an unbound
`AVAudioSession.setActive`) and links after lms-app 22b7654. The Weey release build ran
on the device (avatars, swipe back, no crash). The later candidate commits changed no
public signature a downstream call site uses (`OverlayController.show` gained a
defaulted parameter before its trailing lambda).

**B6–B9** — on the device: a linked picker branch ending at 香港 (no children)
confirms and same-named nodes are told apart by value (B6); Oct 31 rolled to September
lands on the 30th and confirm writes 2026-09-30 (B7); the typed form shows "用户名已被
使用" after blur on "taken", a stale result does not overwrite an edited value, the
injected server error shows, submit yields typed values (B8); whole non-negative fields
get the digit pad (inputType 0x2), decimal/signed fields the text keyboard (0x1) (B9).
`interactions.py` repeats the picker, date picker, cascader and form paths on both
platforms (below).

**B3 formatted input** — automated part on the Android device (`value：` readouts on the
input page's 格式化输入 section): typing one digit at a time gives 138 1234 5678 / raw
13812345678; deleting just after a separator removes the digit before it
(138 1235 678); inserting in the middle of an unfilled number lands at the caret. Found:
a burst of key events (adb `input text`) drops a digit — the second character is
applied to text the field has not yet been given back; inserting into an already full
number with hardware arrow keys lands one place to the left. Measured further: the
drop happens only for key events under 50 ms apart (a single `input keyevent` burst);
at 50, 100 and 150 ms every digit lands, and human typing, IME candidates, voice input
and paste all commit at slower rates or at once — a known limitation of KuiklyUI's
asynchronous text bridge, not reachable by typing. The full-number insertion maps
correctly in the kit (tests added); the device offset came from the native caret under
arrow keys, which the manual caret-by-tap check covers. IME composition, paste and
selection replacement need a person (manual checklist below).

**C1–C3.** All P2/P3 entries in API, registry and sample; registry, README index,
language packs and API baselines in sync.

**D1 accessibility.**
- Contrast: fixed in the palette (68a8eb7). `primary`/`destructive` fills deepen so
  white reads at 4.6:1; light secondary text reaches 4.6:1 on page, card and field; each
  colour's text form (`*SoftForeground`) reaches 5:1 on every ground and its soft fill
  (a margin for anti-aliased strokes, which Apple's audit measures as rendered).
  Components draw coloured text in the text form; `withBrandAccent` derives a readable
  text form and a visible focus ring for any brand. `PaletteContrastTest` holds both
  palettes and seven brand colours. The overrides live in the semantic tokens with
  their reason; the `reference` set stays HeroUI's. iOS audit contrast findings: 467 →
  0 (disabled controls excepted).
- iOS audit on the candidate (186 page-themes): 186 of 186 audited (`build/acceptance/ios-a11y-20261001-004247-199838e4a2`): contrast 0, hit region 38
(all IndexBar letters), description 38, text clipped 124, trait 2 (a demo description
naming "CloseButton"). Accepted: "Label not
  human-readable" on data values ("10.0%", e-mail addresses, file names, code
  identifiers in demo descriptions); "Text clipped" where the text renders complete
  (KuiklyUI lays text out itself; UIKit's estimate disagrees).
- Android node tree on the candidate (both themes, 199838e build): 180 of 186
  page-themes audited, no unlabelled control, 476 nodes under 48 dp (their bounds, not
  their touch areas — see targets), 2 overlapping targets (SearchBar, below). Three pages
  animate forever (loading, noticebar, runtime-material); uiautomator cannot dump them,
  so they are reported as failed rather than passed — the TalkBack check covers them.
- Fixed: LinkedText read character by character; Tag close unnamed; `Textarea` had no
  name on iOS (Kuikly draws its placeholder in a separate text view; the name is now the
  input's own label there); the OTP's hidden field was transparent black and failed
  contrast in dark.
- Targets, measured by tapping outward from each control's edge on the device (a node's
  bounds are not its touch area in KuiklyUI: a Collapse header reports 24 dp and takes
  56): Collapse ≥ 48; checkbox/radio rows 42–48; small Button and ToggleButton 40;
  Stepper ± 32 (24–40 by size); Segmented items 26; Tag chips 28; IndexBar letters 18.
  All but IndexBar meet WCAG 2.5.8 (24 px); IndexBar falls under its equivalent-control
  exception (the list scrolls). Decided 2026-10-01 to follow the spec and iOS (HIG: a
  hit region of at least 44×44 pt, unchanged in iOS 27): every control's own node is
  now its hit region with the visual centred inside (`hitTarget()`). Re-measured on the
  device the same way: ToggleButton, Segmented, TagGroup chips, Stepper ±, swatches,
  CloseButton, links 44; the iOS audit reports no hit-region finding on those pages;
  `interactions.py` 37/37 (Android), 31/32 (iOS; the miss passed three reruns). KuiklyUI keeps
  Compose's touch-target widening code with a 48 dp default, yet a tap 8 dp outside a
  28 dp chip does nothing, and a node's touch area cannot extend past its parent, so
  only real layout space would lift them. Enlarged targets that overlap: the SearchBar
  icon and its field (the field wins, verified); an open ComboBox panel over the next
  field (the page is sealed while it is open).
- Screen readers: TalkBack could not be driven from adb on this phone — injected swipes
  bypass touch exploration and navigated the app, TalkBack's keyboard shortcuts via
  `input keycombination` were ignored, TalkBack logs no speech. VoiceOver needs a
  device. Both are manual checks below.

**D2 visual.** First viewports, light and dark, reviewed page by page on both
platforms; page ends (`--scroll`, light) reviewed on both: Android 55/59 and iOS 61/64
scrolling pages clean. Fixed: Kbd/Code text placement, Card inset, Upload tiles,
Timeline connector, Watermark wrap, Navigator demo under the status bar, sample slides
pairing fills with foregrounds, the table's status column width, overlay shadows
widening the Web page. Registered deviations: Switch thumb is a pill; Stepper keeps its
bordered style; SearchBar default and rounded look alike; Image page shows placeholders;
User rows have no card; long icon names wrap; ToggleButton "delete" is not destructive;
ColorPicker sliders are flat; Code block has no scroll fade. Upstream: iOS draws no
ellipsis for `TextOverflow.Ellipsis` (NavBar title, Text maxLines, NoticeBar).

**Interactions (D1/D2)** — `interactions.py`, candidate build:
- Android: 37 of 37 pass — empty submit shows errors, a changed value clears its error,
  reset clears them, a disabled form ignores its buttons; picker/date picker cancel
  leaves the value and confirm writes it; cascader writes the leaf path; Select opens,
  toggles on its trigger, closes outside, writes the choice; ComboBox keeps focus and
  keyboard on a second tap, filters, writes; Dialog ignores its scrim, BACK closes the
  dialog not the page, the keyboard comes up for its input and goes when it closes;
  ActionSheet BACK and cancel; BottomSheet choice and scrim; Popup outside tap, a
  popup that forbids it stays, BACK still closes it.
- iOS: 30 of 32 in the full run; the two misses (form family) passed on rerun, and the dropdown family passed three runs in a row (no BACK; cancel buttons instead). The form family depends on
  scrolling a long page; simulator scroll inertia makes a tap after a swipe stop the
  scroll instead of tapping, so an occasional run misses one step.
- Found and fixed on the way: a tap on an overlay's scrim reached a native text field
  beneath — the sample's hidden Home search field raised the keyboard after a bottom
  sheet closed (Navigator's front page and scrims now consume native touches); the
  ComboBox never stayed open on Android (its panel cleared focus); dropdowns kept their
  opening place and width on resize (they now follow their trigger); an open ComboBox
  panel was rebuilt under a tap as the query changed. Upstream: iOS ignores
  `autoCorrectEnabled` — a correction committed after a ComboBox choice overwrites it.
- Fixed: with the kit's edge-to-edge contract a field focused near the bottom of a page
  was covered by the keyboard (the window no longer pans). `GearLazyColumn` now keeps
  it above the keyboard (see decision 2).

**D3 large text.** Deferred to the next phase by the maintainer (2026-09-29).

**D4 RTL.** Not supported: Android's force-RTL setting does not reach Compose, and an
RTL `LocalLayoutDirection` at the root still lays every page out left to right,
although KuiklyUI's placement code mirrors. The kit ships no right-to-left language.

**D5 performance (iOS simulator, Release, 20 cold starts).** `scripts/perf/ios_perf.py --runs 20`, raw samples in
`build/acceptance/perf-20260930-225258-199838e4a2/perf.json`. The Mac never became quiet: the script waited
90 minutes for a 1-minute load under 10 and measured at 36–49 (other work on the
machine), so these are pessimistic. Startup to Home content: 20 of 20 runs, median
989.5 ms against the 1000 ms budget, p90 1082, range 953–1828. Process → Kuikly page
created: median 749 ms (process, host app, Kuikly runtime); page created → content:
236 ms — Compose setup, GearUI's theme and runtime, the sample's Home composition and
the renderer's first layout together, not GearUI alone. Whole-app theme switch: median
46.1 ms, p90 49.9 (budget 120). 1000-row fling: p95 16.9 ms, 0.3 % janky frames, from
the Compose frame clock (not Core Animation). A device number needs the maintainer's
iPhone.

**D6 Web.** `web_pages.mjs`, both themes: 185 of 186 page-themes clean; the one failure was a closed connection to `cdn.jsdelivr.net` (below). `web_overlay_resize.mjs`: ComboBox,
Select and Popover stay on their trigger at 1280 → 390 → 1280 and after a scroll, with
the panel as wide as its field, and the chosen value is written — it failed before the
fix (panels kept their opening place and width). KuiklyUI's Web renderer loads
`libpag` from `cdn.jsdelivr.net` at run time; one run met a closed connection there.

**D7–D8.** HarmonyOS: an unsigned HAP builds; no signed emulator or target. Huawei,
OPPO and vivo devices are absent.

**E1.** Fixed by 3ef6158's decoration keys: on the Android device and the iOS simulator
the Surface lab switched round → square → round with every border and shadow redrawn.

**E2/E3.** Upstream issue drafts (four blur gaps, and the findings above: RTL, iOS
ellipsis, minimum touch target widening, iOS autocorrect, Web CDN dependency) are
written for the maintainer's review and kept out of the repository until approved.
Native type scale decided in beta4; iOS password reveal implemented in beta3 (single-
line limit documented); text-field focus crosstalk a known limitation with five kit-side
mitigations.

## Code review (2026-10-01)

An independent review of the session's kit changes found six defects, all fixed before
this record: a caller's width, weight or height on `Button` sized the hit region instead
of the button (now `HitRegion` hands the caller's sizing to the drawn button); focus
return was taken by toasts, could target a removed view and kept a popped page's view
alive; the keyboard-avoidance scroll restarted itself every frame (now keyed on which
field has focus); dropdowns ignored the keyboard height GearUI observes on iOS; corner
buttons (Upload remove, Cascader close) moved inward and covered their tile; a stretched
Tag's close square drifted from the ×. The same pass caught a regression of its own on
the Web: consuming native touches (the fall-through fix) swallowed every Compose click
in the browser, so no Select or Popover opened — limited to Android and iOS, and
`web_overlay_resize.mjs` is part of every run because the page smoke run cannot see it.

## Needs a maintainer decision

1. ~~Target sizes~~ — decided 2026-10-01: follow the spec, close to iOS 27 (HIG hit
   region 44×44). Done; see D1.
2. ~~Keyboard avoidance~~ — decided 2026-10-01: as iOS does. Done: a focused field in
   a `GearLazyColumn` is kept above the keyboard (only fields inside that list, so a page
   handling its own composer is not padded twice; `avoidKeyboard = false` to opt out).
   `interactions.py` keyboard family: the lowest field ends above the keyboard and the
   NavBar stays put, Android and iOS (software keyboard), 4/4 each.
3. **Waivers:** D4 (RTL), D7 (HarmonyOS runtime), D8 (other vendors' devices), D5's
   device number (simulator figures only).
4. **Upstream drafts (E2):** review and approve filing.

## Manual checks (a person on a device)

**B3 — Chinese IME on the input page, 格式化输入 section** (手机号 / 银行卡号 / 身份证号;
each shows `value：` under it). For each field record the displayed text, the `value：`
and where the caret ends:
1. Type with a Chinese IME (pinyin, digits via its number row); the field must not
   break the composition while pinyin is being typed.
2. Place the caret by tapping in the middle; insert a digit; delete across a separator.
3. Paste a full number (e.g. "+86 138 0013 8000", a card with spaces, an ID ending in x).
4. Select several digits and type over them.
5. With a full phone number, tap the caret to the middle and type one digit.

**TalkBack (Android) and VoiceOver (iPhone)**, on these pages: button, checkbox, radio,
switch, input, textarea, select, combo-box, picker, dialog, actionsheet, bottomsheet,
tabs, calendar. Check: each control announces name, role and state; swipe order follows
the visual order; double-tap activates; after a dialog, sheet, menu or dropdown closes,
focus returns to the control that opened it (implemented 2026-10-01 through KuiklyUI's
`accessibilityFocus()`; only a screen reader shows whether it lands).

## Reproduce

```bash
scripts/acceptance/capture_sample.py android <adb-serial> [--scroll]
scripts/acceptance/capture_sample.py ios <simulator-udid> --build release [--scroll]
scripts/acceptance/android_accessibility_audit.py <adb-serial> [--themes light,dark]
scripts/acceptance/ios_accessibility_audit.sh <simulator-udid> [themes] [routes]
scripts/acceptance/interactions.py android|ios <device> [--only form,picker,…]
node scripts/acceptance/web_pages.mjs <base-url>
node scripts/acceptance/web_overlay_resize.mjs <base-url>
python3 scripts/perf/ios_perf.py <simulator-udid> --runs 20
```

No Central publication, tag or downstream version bump is authorized by this record.
