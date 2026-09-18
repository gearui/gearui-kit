# beta3 Release Readiness

For the subsequent KuiklyUI 2.28.0 dependency upgrade, see the
[new dependency audit](DEPENDENCY_UPGRADE_2_28.md). The results below describe the
earlier candidate and do not certify the upgraded graph.

Update 2026-09-18: the [device acceptance pass](BETA3_DEVICE_ACCEPTANCE.md) ran the
critical keyboard/overlay/theme checklist on iOS and Android, found and fixed four
defects (including an iOS crash on ContextMenu), and gave the native test gate a
real mechanism. Remaining before publication: a remote CI run on the final commit,
then version/tag/signing.

Audit date: 2026-09-17. Verdict: **candidate preparation is justified; publication
is not approved yet**. This is a tested working tree based on `2f306a7`, not an
immutable release commit. Existing changes and new files remain uncommitted.
`POM_VERSION` remains `1.0.0-beta2`; beta3 was used only for isolated local staging.

## Verified In This Audit

| Gate | Result | Scope |
| --- | --- | --- |
| Android Kotlin tests | 191 passed, zero failures/skips | Clean test execution, not compile-only |
| Chrome Kotlin tests | 190 passed, zero failures/skips | Actual headless Chrome run |
| Token compiler tests | 119 passed | Format/Resolver/adapters/generation regression suite |
| Generated defaults | Pass | All five generated outputs match sources |
| Shell guards | 22 passed | All current `scripts/ci/check_*.sh` |
| HeroUI source reference | Pass | Version 1.0.9, 42 pinned source hashes |
| API baseline | Pass | JVM and KLib against current reviewed files, not beta2 ABI compatibility |
| Android lint | 0 errors, 3 warnings | Dependency/SDK/Gradle update notices, not suppressed |
| Android sample | Build/install/launch pass | Connected Android device; dark home screenshot inspected |
| Web sample | Development webpack build pass | Not exhaustive browser interaction acceptance |
| iOS sample | Full Xcode host build/install/launch pass | iPhone 17 Pro, iOS 26.2; light home screenshot inspected |
| Maven staging | Six modules produced | Metadata root, Android, JS, iOS arm64/simulatorArm64/x64 |
| Archive inspection | Pass | ZIP integrity, beta3 POM/module versions, Android AAR includes 97 icon assets |
| Independent artifact consumer | Pass | Android, JS and three iOS compilations; no composite substitution |
| privchat-app Android consumer | Compile pass | Includes current sibling kit/ui source builds |
| privchat-app iOS consumer | **Fail** | `privchat-ui` AvatarBitmapRenderer.ios.kt:78 unresolved `timeIntervalSince1970` |
| Standalone iOS Kotlin tests | **Blocked at native linking** | Missing Kuikly host symbol, details below |
| Final-commit remote CI | **Not verified** | Candidate not committed; current CLI access also receives repository token-policy HTTP 403 |

The independent artifact consumer checks dependency resolution and a small public
API use (`Themes.Light.colors`, `NavRoute`); it is not a replacement for full
application integration or runtime resource loading on each platform.

## Findings And Changes

1. Browser tests exposed an Android-only assumption in the system-font fallback
   test. Web intentionally resolves to explicit `system-ui`, not FontFamily.Default.
   The test now checks platform-specific resolution and fallback ordering. Runtime
   font behavior was not changed to make the assertion pass.
2. CI lacked explicit Kotlin/browser regression-test steps. Android tests/lint and
   browser tests are now wired into CI. These workflow changes still need a remote run.
3. Consolidated current specifications under [SPEC](SPEC.md). Replaced contradictory
   historic visual/token documents with ownership-based current rules and redirects;
   originals remain in [_archive/pre-beta3](_archive/pre-beta3/README.md).
4. Corrected stale Button default/geometry documentation, release/migration guidance,
   shadow-rendering descriptions and the old guard diagnostic requiring flat cards.

## Before Publication

- ~~Resolve the iOS consumer compilation failure~~ Resolved: `privchat-ui` `39ba0cd`;
  the 2.28 audit compiled the PrivChat iOS application.
- ~~Decide the native-test gate honestly~~ Resolved on 2026-09-18: the test link now
  takes the real OpenKuiklyIOSRender framework via `-PgearuiIosTestHostDir`
  (`scripts/ios_native_tests.sh` builds it from the sample Pods; the CI iOS job runs
  it). 190 tests, 0 failures locally. Without the property the link fails fast with
  an explanation. No symbol is stubbed. The CI step still needs its first remote run.
- ~~Finish the critical-path device checklist below~~ Executed on 2026-09-18 for iOS
  and Android; see [device acceptance](BETA3_DEVICE_ACCEPTANCE.md) for results,
  fixes and what remains unaccepted (screen reader, performance, Web, HarmonyOS,
  iOS Dynamic Type).
- Review/commit the complete candidate, including untracked required files. Run
  remote CI against that exact commit; restore permitted repository access as needed.
- After approval, set version/tag and verify signed Central staging and dependency
  resolution. No tag, commit, push, signing or remote publication occurred here.

## Critical Runtime Checklist Still Open

Use the [component matrix](COMPONENT_ACCEPTANCE_MATRIX.md) and retain per-platform
evidence. Do not substitute compilation for these checks.

- Input/textarea keyboard, selection, focus retention, validation, disabled/readonly
  behavior and large text after live brand/shape/light/dark changes.
- Select/tree selection and dialog/sheet/drawer/menu cancellation, nested overlays,
  Back handling, gesture interruption, safe areas and keyboard overlap.
- Open overlays receiving new callbacks/content/theme without stale values.
- Material/font behavior across supported platforms and long/localized labels.
- Screen-reader/focus traversal and frame performance remain unaccepted globally.
- HarmonyOS has no connected device for visual acceptance. Earlier build evidence
  is in the dated material report, not a new device pass in this audit.

## Documented Limits, Not Hidden Completion Claims

DTCG data support follows [the Format/Resolver and adapter contract](../tokens/README.md),
not a claim of universal certification. Shared shadow rendering uses bounded
Gaussian edge approximation and rectangular/rounded outlines; remaining legacy
consumers are not all migrated. Full HeroUI Native pixel/motion parity is unaccepted.
The Web host's live viewport-resize behavior and hot native rounded/square outline
updates require separate validation/fixes; reload screenshots do not prove them.

These limits do not automatically prohibit a beta release, but must be disclosed
and assessed against the advertised scope rather than silently marked complete.

## Reproduction

```bash
./gradlew :gearui-kit:cleanTestDebugUnitTest :gearui-kit:testDebugUnitTest \
  :gearui-kit:apiCheck :gearui-kit:lintDebug :sample:assembleDebug \
  :sample:jsApp:jsBrowserDevelopmentWebpack
CHROME_BIN='/Applications/Google Chrome.app/Contents/MacOS/Google Chrome' \
  ./gradlew :gearui-kit:jsBrowserTest
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/generate_tokens.py --check
for check in scripts/ci/check_*.sh; do bash "$check" || exit; done
node scripts/check_heroui_reference.mjs
./gradlew :gearui-kit:iosSimulatorArm64Test # currently fails at native linking
./gradlew :gearui-kit:publishToMavenLocal \
  -Dmaven.repo.local=/tmp/gearui-beta3-audit/maven \
  -PPOM_VERSION=1.0.0-beta3 -PsigningInMemoryKey=
```

Full local build/test logs, staged artifacts, independent consumer fixture and
home screenshots are under `/tmp/gearui-beta3-audit/` on the audit machine. This is
local evidence, not durable CI storage; attach final logs to the release candidate.
