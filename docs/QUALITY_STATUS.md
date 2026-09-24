# GearUI Quality Status And Release Progress

[English](./QUALITY_STATUS.md) | [简体中文](./QUALITY_STATUS.zh-Hans.md)

Where the kit stands against the 1.0.0 goal: what has been verified, by which
gate, and what remains open. Current version: `1.0.0-beta3`, published to
Maven Central on 2026-09-20 from tag `v1.0.0-beta3`.

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

## 3. Open Risks And Limits

- **Screen reader and focus traversal**: unaccepted globally. Semantics are
  implemented and inspectable, but no screen-reader pass has been recorded.
- **Performance**: no measured numbers yet; nightly performance automation
  is not implemented.
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
