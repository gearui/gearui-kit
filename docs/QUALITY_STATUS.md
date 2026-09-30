# GearUI Quality Status And Release Progress

[English](./QUALITY_STATUS.md) | [简体中文](./QUALITY_STATUS.zh-Hans.md)

Where the kit stands against the 1.0.0 goal: what has been verified, by which
gate, and what remains open. Current version: `1.0.0-beta6`, published to
Maven Central on 2026-09-28 from tag `v1.0.0-beta6`.

## 1. The 1.0.0 Goal

1. **Frozen public API.** Token and component APIs stable under
   `binary-compatibility-validator`; breaking changes only with migration
   notes. The pre-1.0 token API consolidation is done; downstream consumers
   (`privchat-ui`, `live-chat`, `lms-app`) recompile against the frozen shape.
2. **Accepted visual consistency with GearUI's own
   [VISUAL_SPEC.md](./VISUAL_SPEC.md)** on iOS and Android, with recorded
   deviations as the only differences.
3. **Accepted accessibility**: name/state/role semantics verified with a
   screen reader, not only with `idb ui describe-all`.
4. **Measured performance** against the budgets: Android TTI ≤ 1200 ms,
   iOS ≤ 1000 ms, Web ≤ 1800 ms, scroll drops < 3%, theme change ≤ 120 ms —
   with hardware, workload and method recorded. Budgets are goals, not
   measured achievements yet.
5. **Four-platform evidence**: Web interaction acceptance and a HarmonyOS
   device pass, each separate from compilation.
6. **Component gaps closed per plan**: the P2 list in
   [COMPONENT_COVERAGE.md](./COMPONENT_COVERAGE.md), internationalized date
   formats, and the React Aria-style accessibility state model.

## 2. Verified Today

Local gate on `68fc1a4` (2026-09-20, the published beta3 line):

| Gate | Result |
| --- | --- |
| Android Kotlin tests | 198 passed + lint + apiCheck |
| Chrome Kotlin tests | 197 passed (headless) |
| iOS simulator Kotlin tests | 197 via `scripts/ios_native_tests.sh` against the real Kuikly host |
| Token compiler tests | 119 passed; generated defaults match sources |
| Shell guards | all `scripts/ci/check_*.sh` passed |
| Sample builds | Android / Web / iOS simulator build, install, launch |
| Maven staging | six modules; AAR contains 97 icon assets; independent artifact consumer compiles (Android, JS, three iOS targets) |
| Consumer builds | privchat-app Android and iOS compile against the kit |

Device acceptance (2026-09-18, iPhone 17 Pro simulator + Android device):
the critical keyboard/overlay/theme checklist ran end to end; four defects
were found and fixed (including an iOS ContextMenu crash). Component-level
device passes: BottomSheet (scrim/grabber/BACK/edge-swipe dismissal, theme
switch), Dialog (BACK/edge-swipe/outside-tap do not dismiss ConfirmDialog by
default), Drawer, ContextMenu, Input (typing, IME action, native maxLength,
dark theme, Android font scale 1.3), Popover, Select (open/close).

Gesture arbitration (2026-09-22, Weey production build, Android device):
the Navigator/TabPager swipe-back contract — page-first arbitration,
full-width 1:1 card tracking, stack-bottom overscroll tension in both
directions, and drag without the reverse-scroll hitch — verified on device
frame by frame.

Everything else in the component inventory is **static sweep only**: source
reviewed and guard-checked, not device-accepted. A source scan is not a
visual or behavioral pass; per-component acceptance is tracked against the
required checks (default/long/empty/large data, press/disabled/loading/focus,
light/dark/accent/shape live changes, overlay lifecycle, per-platform
evidence).

- **Dialog action layout** (`DialogActionLayout`): BLOCK / SPLIT / STACKED
  resolved by a tested pure function (10 unit tests); TRAILING opt-in only.
  Text-to-actions gap 32 → 20, full-width actions at the 48 medium height.
  Verified in the sample on iPhone 17 Pro: single-action alert, two-action
  confirm, destructive confirm, three-action stack, explicit trailing form.

**Performance** (2026-09-28, sample Performance page, `scripts/perf/`).

Android — Xiaomi 12 Pro (2201122G), Android 16, 120 Hz, the `benchmark` build
type (release code, not debuggable):

| Measure | Result | Budget |
| --- | --- | --- |
| Startup to home content (in-app mark: process start → first frame of the home list), 5 cold starts | median 310 ms (306–357): 60 to the page, 250 page → content | ≤ 1200 ms |
| Activity first frame, `am start -W -S` TotalTime, 10 cold starts | median 297 ms (293–304) | — |
| Theme switch, whole app, ~200 components on the page, 20 flips (state change → second frame after it) | median 36.9 ms, p90 44.5, max 51.1 | ≤ 120 ms |
| 1000-row `List` flung for 5 s, `dumpsys gfxinfo` | 496 frames, janky 0.20 %, p50 8 ms, p99 11 ms | < 3 % |
| Same flings, in-app frame intervals | 589 frames, period 8.2 ms, janky 1.3 % | < 3 % |

The same device on a **debuggable** build read 1496 ms to first frame and a
125 ms theme switch — five times slower at start. A debuggable build runs with
ART's optimisations off; it measures the debugger, not the kit. Measure the
`benchmark` build.

iOS 26.2 simulator, Release build (regression baseline — a simulator runs on
the Mac's CPU): startup to content median 1030 ms — 764 to the page, 270 page →
content; theme switch median 36.8 ms; scroll 300 frames at 16.6 ms, 0.0 % janky.
The kit's share of startup, page → content, is the same on both platforms
(250–270 ms). The iOS excess is before the page exists: loading the 81 MB
statically linked binary and the host and Kuikly start-up on the simulator. It
needs an iOS device pass before it counts against the 1000 ms budget.

## 3. Open Risks And Limits

- **Screen reader and focus traversal**: unaccepted globally. Semantics are
  implemented and inspectable, but no screen-reader pass has been recorded.
- **Performance**: Android meets every budget on a 2022 flagship; a low-end
  device has not been measured. iOS has simulator numbers only; startup there
  is 1030 ms, almost all of it before the Kotlin page exists — an iOS device
  pass decides whether that is the simulator or the binary. Nothing runs
  nightly.
- **Web**: development builds pass; live viewport-resize behavior and full
  browser interaction acceptance are open.
- **HarmonyOS**: no connected device for visual acceptance; build evidence
  only.
- **Renderer limits**: hot rounded-to-square native border refresh does not
  propagate; blur is off by default pending the four upstream gaps
  ([VISUAL_SPEC.md](./VISUAL_SPEC.md) §5); iOS Dynamic Type unverified.
- **Known product-level decisions carried from beta3**: the native type
  scale is undecided, password reveal on iOS, intermittent Kuikly text-field
  focus crossing.
- **RTL**: typed packs exist; layout direction is not accepted.
- Full pixel/motion consistency against
  [VISUAL_SPEC.md](./VISUAL_SPEC.md) is unaccepted as a whole; recorded
  deviations are the documented differences, everything else still needs
  per-component visual passes.

## 4. Release Procedure

Candidate gate — a local success on uncommitted sources is not approval:

1. Commit the complete candidate (token sources, generated code, tests, API
   baselines, migration notes, resources). Never publish a partially tracked
   tree.
2. Run every check in
   [COMPONENT_SPEC.md §8](./COMPONENT_SPEC.md) and require remote CI green on
   that exact commit. Generating an API dump is not verifying it.
3. Check supported consumers, the full iOS host, Android/Web sample and the
   critical keyboard/overlay/theme paths; record untested targets explicitly.
4. Stage all six Maven modules on macOS; inspect assets, metadata, KLibs and
   sources. A composite source build does not test Maven consumption.
5. Review breaking changes and renderer limitations, obtain approval, then
   set version/tag and sign/upload that exact candidate.

Publishing (Central Portal, via `com.vanniktech.maven.publish`):

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<token_name>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<token_secret>
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_gpg_private_key>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<passphrase>
./gradlew :gearui-kit:publishToMavenCentral
```

Publish from macOS — the three iOS targets build nowhere else, and on Linux
they are silently missing from the upload. Do not set `signingInMemoryKeyId`
unless a specific subkey is meant; a master key id there makes every signing
task fail with "no configured signatory". Verify against the Portal rather
than trusting Gradle's exit code: beta1's `publishToMavenCentral` reported
BUILD SUCCESSFUL without uploading (the bundle had to be posted to the
Portal API directly, `publishingType=USER_MANAGED`, which stops at VALIDATED
so the final publish stays a human decision). Local staging only:

```bash
./gradlew :gearui-kit:publishToMavenLocal \
  -Dmaven.repo.local=/tmp/gearui-staging \
  -PPOM_VERSION=<version> -PsigningInMemoryKey=
```

Beta limitations must be disclosed; known crash/data-loss/input-blocking
regressions are not acceptable beta caveats.

## 5. Reproducing The Gate

```bash
./gradlew :gearui-kit:cleanTestDebugUnitTest :gearui-kit:testDebugUnitTest \
  :gearui-kit:apiCheck :gearui-kit:lintDebug :sample:assembleDebug \
  :sample:jsApp:jsBrowserDevelopmentWebpack
CHROME_BIN='/Applications/Google Chrome.app/Contents/MacOS/Google Chrome' \
  ./gradlew :gearui-kit:jsBrowserTest
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/generate_tokens.py --check
for check in scripts/ci/check_*.sh; do bash "$check" || exit; done
bash scripts/ios_native_tests.sh   # needs -PgearuiIosTestHostDir host
```

## 6. Maintenance

Update §2 when a gate is re-run or a component gains device acceptance; move
rows to §3 when a limit is discovered, and out of §3 only with recorded
evidence. Superseded milestone reports are deleted, not accumulated — git
history is the archive.

## 7. beta7 Release Gate (opened 2026-09-29)

beta7 is not "fix a few bugs and ship". The §1 goals, the §3 open risks, the gaps in
[COMPONENT_COVERAGE.md](./COMPONENT_COVERAGE.md) §3, and accepted mobile-business capabilities
all fold into this gate. The historical competitor comparison was retired after its decisions
were incorporated; this table is the sole acceptance source.
Each item is closed by evidence — unit tests, a sample path, on-device screenshots or logs, script output —
recorded in §2. A green compile closes nothing. Items that cannot be done state what is missing (a device,
an upstream fix, a decision) and are marked externally blocked; before release the maintainer releases or
defers each one explicitly. Nothing is skipped silently.

Status: ☐ not started · ◐ in progress · ☑ done (with evidence) · ⛔ externally blocked · ⏸ deferred by the maintainer

### A. Release hygiene

| ID | Scope | Evidence | Status |
| --- | --- | --- | --- |
| A1 | Push main; remote CI green on the candidate commit, iOS job included | CI run link and per-job result | ◐ CI green on 199838e; Guardrails failed there on a token test still asserting the old primary, fixed in the commit that adds the acceptance record; that commit's CI decides (BETA7_ACCEPTANCE A1) |
| A2 | Downstream migration: privchat-ui, live-chat, lms-app build against the candidate; privchat-app Android/iOS build | Migration commits and build logs | ☑ Android: privchat-ui, live-chat, lms-app compile. iOS: after building the Rust FFI, privchat-app and live-chat link; lms-app links after fixing its long-standing iOS compile errors (lms-app 22b7654). Weey release verified on a device. See BETA7_ACCEPTANCE |
| A3 | Incorporate accepted items from the historical comparison into groups B/C and retire the outdated document | B/C gates and documentation cleanup commit | ☑ B/C entries recorded; comparison remains in git history, not the release specification |

### B. Mobile-business capabilities

| ID | Scope | Evidence | Status |
| --- | --- | --- | --- |
| B1 | Consent row: inline tappable link spans in Text; an unchecked-by-default Checkbox; tapping a link opens it and never ticks the box; reachable by screen readers | Unit tests + sample page + taps on both platforms | ☑ `LinkPiecesTest` (7); sample `agreement`; tapped on iOS and an Android device: a link only opens its document, the rest of the sentence toggles the box; iOS screen reader reads the checkbox as the sentence plus state and each link as its own button. Kuikly delivers no `LinkAnnotation` taps and no character positions, so `LinkedText` lays the sentence out per character |
| B2 | LoadMore list footer: idle/loading/error/exhausted, no repeat requests while parked at the bottom, retry on failure, strings in language packs | State tests + sample order list + on-device scrolling | ☑ `LoadMoreTest` (6: loads only when idle and the footer is visible, no repeat while loading, a failure waits for the user, an appended page that pushes the footer off screen does not chain); sample `loadmore`; failed state on iOS; retry loads page 3 on an Android device |
| B3 | Input formatting layer: raw value, display value and caret mapping kept apart; phone 3-4-4, bank-card groups of four, ID-card trailing X | Pure-function tests + on-device paste, mid-string edits, Chinese IME | ◐ Automated part passes on the device (digit by digit, deleting across a separator, inserting into an unfilled number); found: a key burst drops a digit, arrow-key insertion into a full number lands one place left; IME composition, paste and selection replacement await a person (BETA7_ACCEPTANCE manual checks) |
| B4 | Calendar first day of week from the language pack; Monday for Simplified Chinese; Sunday for Traditional Chinese (Hong Kong) | Tests + screenshots | ☑ Simplified Monday, Traditional Chinese Sunday per Hong Kong convention; FormatStringsTest and iOS calendar evidence |
| B5 | Compact numbers (万/亿 by language pack) and Chinese relative time in the i18n formatting layer | Tests: boundaries, four languages | ☑ `FormatStringsTest` (万/亿 boundaries, rounding down, negatives, K/M/B, six relative-time cases including a year boundary and a fast clock); the kit ships three packs (Simplified, Traditional, English), not four |
| B6 | Picker with stable IDs separate from labels; Cascader distinguishes leaf/unloaded/loading/failed/empty; three-level address example | Tests: duplicate labels, parent change, stale results dropped + device | ☑ Stable-value Picker and cancellable Cascader have tests; Android device: a linked branch ending at 香港 (no children) confirms, same-named nodes told apart by value |
| B7 | DatePicker min/max date, year/month/day and time precision, minute step and filter | Tests: leap day, month end, year bounds, empty column after filter + device | ☑ Bounds, precision, filters and steps have boundary tests; columns computed on demand (a few hundred checks for the default range); Android device: Oct 31 rolled to September lands on the 30th, confirm writes 2026-09-30 |
| B8 | Form: typed field values, dirty/touched/validating, trigger policy, versioned async validation, server-side field errors | Tests: slow result never overwrites a newer value, a left field never writes back + sample form | ☑ Typed async form tested (including joined same-value checks and typed input surviving a new initial value); Android device: "taken" shows the async error after blur, a stale result does not overwrite an edited value, the injected server error shows, submit yields typed values |
| B9 | NumberField exact decimals (symmetric parser/formatter, transient input); optional Stepper input (`editable`, off by default) | Tests + device | ☑ Exact decimals tested; inputType read on an Android device: whole non-negative fields get the digit pad (0x2), decimal/signed fields the text keyboard (0x1, Kuikly has no decimal pad); Stepper read-only by default, `editable` to opt in |

### C. Component coverage (from COMPONENT_COVERAGE §3)

| ID | Scope | Evidence | Status |
| --- | --- | --- | --- |
| C1 | P2: standalone ListBox, YearPicker, segmented date/time fields with localized formats, Toolbar, SubMenu, IndexBar | Per component: API baseline, tests, sample page, screenshots on both platforms | ☑ All P2 entries in API, registry and sample; Android light/dark reviewed page by page and fixed (BETA7_ACCEPTANCE D2). Maintainer decision 2026-10-01: segmented date/time fields (DateField/TimeField) are a desktop keyboard pattern and YearPicker duplicates DatePicker's year precision, so both were removed before release; see COMPONENT_SPEC "Choosing a selection component" |
| C2 | P3: Kbd, ColorPicker family, Meter, User, Code/Snippet | Same | ☑ All P3 entries in API, registry and sample; Kbd/Code text placement fixed; flat ColorPicker sliders registered as a deviation |
| C3 | Registry, README index, COMPONENT_COVERAGE, language packs, API baselines in sync | Guards green | ☑ Registry, README index, language packs and API baselines in sync; guards green |

### D. Runtime acceptance

| ID | Scope | Evidence | Status |
| --- | --- | --- | --- |
| D1 | Screen readers: TalkBack on an Android device across every page with the spoken text recorded; iOS audited with XCUITest `performAccessibilityAudit` on every page, VoiceOver spot checks on a device for the key families | TalkBack logs, audit report, issue list and fixes | ◐ Contrast fixed (palette, components, brand derivation; PaletteContrastTest); hit regions follow the spec and iOS 27 (HIG 44×44), 44 measured on the device, no hit-region finding in the iOS audit; iOS audit and Android node tree rerun on the candidate; TalkBack/VoiceOver manual |
| D2 | Visual: every sample page × light/dark × Android device and iOS, archived and judged against VISUAL_SPEC page by page | Screenshot set + per-page verdict + deviations | ◐ First viewports light/dark and page ends reviewed and fixed on both platforms; interaction runs (forms, pickers, dropdowns, overlays, keyboard avoidance) pass on both; keyboard avoidance follows iOS (a focused field in a list scrolls above the keyboard); manual items in BETA7_ACCEPTANCE |
| D3 | Large text: Android font scale 1.3 and maximum, iOS largest Dynamic Type, truncation/overflow on every page | Screenshots + issues and fixes | ⏸ Deferred to the next phase by the maintainer (2026-09-29) |
| D4 | RTL: Android forced RTL layout direction, page by page | Screenshots + issues | ⛔ Not supported: Kuikly does not pass the platform RTL setting through, and an RTL layout direction provided at the root does not mirror either; recorded for upstream; the kit ships no right-to-left language |
| D5 | Performance on an iOS device (iPhone 16 Pro Max): cold start, theme switch, scrolling against the §1 budgets | `scripts/perf` output | ◐ Simulator, Release, 20 cold starts (host load 36–49, so pessimistic): median 989.5 ms (budget 1000), p90 1082; theme switch 46.1 ms; fling p95 16.9 ms, 0.3 % janky; raw samples kept; a device number awaits the maintainer's iPhone |
| D6 | Web: browser automation across pages, including viewport resizing | Script and results | ☑ 186 page-themes smoke-tested (load, scroll, resize) and the overlay resize test passes; the Kuikly Web renderer's runtime CDN dependency recorded |
| D7 | HarmonyOS: sample on the DevEco emulator across every page; device acceptance | Emulator screenshots; device is externally blocked | ⛔ shared library and unsigned HAP build; signed emulator and device runtime unavailable |
| D8 | Chinese ROM matrix: Xiaomi covered; Huawei, OPPO, vivo | Screenshots per device | ⛔ no devices |

### E. Known limits

| ID | Scope | Evidence | Status |
| --- | --- | --- | --- |
| E1 | Native border not refreshed on rounded↔square hot swap: fix in the kit if it can be worked around, else record with a minimal repro | Fix commit or repro project | ☑ 3ef6158 rebuilds decorations on size/shape/border; Android device and iOS simulator: the Surface lab switched round ↔ square with borders and shadows redrawn at once |
| E2 | The four upstream blur gaps: written up as Kuikly issues ready to file | Issue drafts; filing is the maintainer's call | ◐ Upstream drafts (four blur gaps + RTL, iOS ellipsis, minimum touch target, iOS autocorrect, Web CDN) await maintainer review before filing |
| E3 | Decisions carried from beta3 — native type scale, iOS password visibility toggle, Kuikly text-field focus crosstalk: decide and implement, or record as known limits | Decision record and commits | ☑ Native type scale decided in beta4 (reference default, Platform opt-in); iOS password reveal implemented in beta3 (single-line limit documented); focus crosstalk a known limitation with five kit-side mitigations |

### F. Release

F1: every gate in [COMPONENT_SPEC.md §8](./COMPONENT_SPEC.md) and remote CI pass on the exact candidate; all six Maven
modules staged and inspected; published to Central and checked in the Portal; tag `v1.0.0-beta7`; website synced;
downstream moved to beta7. All of A–E are ☑, or each ⛔ item has been released explicitly by the maintainer.
