# KuiklyUI 2.28 Dependency Audit

Audit date: 2026-09-17. This is a working-tree verification record, not release
approval. No tag or remote publication is authorized. Latest means stable
releases compatible with the supported platforms, not an unconditional version bump.

## Selected Versions

| Dependency | Selected | Scope / reason |
| --- | --- | --- |
| KuiklyUI | 2.28.0 | Kotlin, KSP, Web, CocoaPods and HarmonyOS renderer |
| Gradle | 8.14.5 | Kit/UI/app wrappers, distribution checksum and regenerated launcher |
| Kotlin / Compose / KSP | 2.1.21 / 1.7.3 / 2.1.21-2.0.1 | Kuikly's matching main-platform toolchain |
| HarmonyOS Kotlin / KSP | 2.0.21-KBA-010 / 2.0.21-1.0.27 | Separate Kuikly OHOS artifacts and compiler |
| AGP | 8.13.2 | Latest 8.x; 9.x requires a KMP Android plugin migration |
| Binary compatibility validator | 0.18.2 | Kit API checks |
| Maven publish plugin | 0.35.0 | Latest compatible line; 0.37 requires Gradle 9 and Kotlin 2.2 |
| Android compile SDK | 36 | Kit, sample, UI and SDK; minimum OS versions unchanged |
| AndroidX annotation | 1.9.1 | 1.10 Android compilation passes but common metadata needs Kotlin 2.3.20 |
| AppCompat | 1.7.1 sample; 1.8.0 app/SDK demo | 1.8 requires API 23; kit sample still supports API 21 |
| Core KTX | 1.16.0 UI; 1.18.0 app/SDK demo | UI retains API 21; 1.19 requires compile SDK 37 / AGP 9.1 |
| Camera / Media3 | 1.6.2 / 1.11.1 | Application Android integration |
| Android test junit / core / runner | 1.3.0 / 1.7.0 / 1.7.0 | Application Android tests |
| Coroutines | 1.10.2 | Consumer/SDK core, Android and test aligned; 1.11 needs Kotlin 2.2 |
| Serialization | 1.8.1 | Consumer/SDK; 1.9+ requires a newer Kotlin compiler |
| Ktor | 3.2.4 | Consumer HTTP clients; newer lines require newer Kotlin |
| Atomicfu / Okio / datetime / JNA | 0.28.0 / 3.15.0 / 0.8.0 / 5.19.1 | SDK; newer Atomicfu/Okio Native require Kotlin 2.2 |
| SDWebImage | 5.21.7 | Sample and application pods pinned |
| libpag / hypium | 4.5.2 / 1.0.28 | libpag pinned: later releases require API 15; sample retains API 12 |
| Lifecycle / RecyclerView / DynamicAnimation | 2.11.0 / 1.4.0 / 1.1.0 | SDK Android demo |
| Picasso / Glide | 2.8 / 5.0.7 | SDK Android demo, Glide compiler aligned; 5.0.9 requires API 37 |

Firebase BoM 34.19.0, Google Services 4.5.0, graphics-path 1.1.0, knoi 0.0.4
and hamock 1.0.0 were already current in the audited configurations. NDK and
HarmonyOS SDK are platform toolchains, not independently replaceable UI libraries.
The SDK's standalone Gradle wrapper is unchanged; consumer integration uses the
application's Gradle 8.14.5 composite build.

## Compatibility Evidence

- [Kuikly 2.28 main build](https://github.com/Tencent-TDS/KuiklyUI/blob/2.28.0/build.2.1.21.gradle.kts)
  defines the matching Kotlin, Compose and KSP versions. Upgrading stock Compose
  independently does not upgrade Kuikly's renderer.
- [AGP 9 KMP migration](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html)
  requires project/plugin changes; it is not a patch upgrade.
- [Publishing plugin releases](https://github.com/vanniktech/gradle-maven-publish-plugin/releases)
  specify Gradle/Kotlin requirements. Removed `SonatypeHost` configuration is
  migrated to the current Central API with automatic release disabled.
- Android artifacts' manifests and AAR metadata were checked for minimum SDK,
  compile SDK and AGP requirements. Maven POMs were checked for Kotlin dependencies;
  successful compilation and tests remain necessary beyond metadata checks.

## Verification

- Gradle 8.14.5: kit Android 191 tests and headless Chrome 190 tests passed,
  with zero failures/errors/skips. Both test tasks were explicitly cleaned first.
- Python token suite: 119 passed. All 22 shell guards passed.
- Kit JVM/KLib API checks, Android lint (zero errors, one intentional annotation
  version-update warning), Android sample APK and Web
  host build passed. Android APK installed and launched on the connected device.
- Full iOS simulator host build/install/launch passed on iPhone 17 Pro / iOS 26.2;
  Input navigation, typing and blur were smoke-tested. This is not all-component
  visual or animation acceptance.
- iOS simulator Kotlin tests: 190 passed with zero failures/errors/skips. The
  local test harness linked the real OpenKuiklyIOSRender framework from the sample
  build and supplied its framework search path and runtime rpath. The ordinary
  standalone test task and remote CI are not thereby fixed.
- HarmonyOS Kotlin shared library and unsigned HAP rebuilt successfully with the
  new renderer and compatible libpag. No HarmonyOS device runtime verification.
- Isolated Maven staging passed for all six modules (root, Android, JS, three iOS
  targets). A separate project consuming those artifacts compiled Android, JS and
  all three iOS targets successfully. No composite substitution or remote upload.
- PrivChat application iOS simulator compilation passed, including UI and SDK
  integration. SDK Android unit tests: 32 passed, zero failures/errors/skips.
- PrivChat application Android compilation passed and all 274 application tests
  passed after cleaning the test task (zero failures/errors/skips).
- SDK Android demo APK assembled successfully with Glide 5.0.7.
- Remote CI, device interaction parity, signing and publication are not established
  by these local checks. Previous readiness results must not be treated as results
  for this upgraded dependency graph.

## Issues Found During Verification

1. Okio 3.18.2 advertises a compatible stdlib dependency but its Native KLib uses
   ABI 2.2.0 / compiler 2.2.21. Versions 3.17.0 and 3.16.4 also require 2.2;
   3.15.0 uses compiler 2.1.21 and is the selected compatible release.
2. datetime 0.8.0 removed the old `kotlinx.datetime.Clock.System` call used by the
   SDK's iOS adapter. It now uses the existing Foundation clock pattern, retaining
   epoch-millisecond semantics. Its Native artifact uses compiler 2.1.20.
3. libpag 4.5.94 failed the host's minimum-SDK check. Registry metadata confirms
   4.5.2 is the latest release supporting API 12. Its exact pin avoids silently
   selecting an incompatible 4.5.x release.
4. Repeated Maven Central TLS handshake failures occurred during consumer and
   publication dependency resolution. Missing Ktor and Glide processor artifacts
   were fetched from Google's Maven Central mirror and checked against published
   checksums before populating the local cache. The final builds used the normal
   repository configuration; no TLS verification was disabled. Failures from
   earlier attempts are not counted as passes.
5. The consumer runtime guard initially flagged existing AvatarCropPage
   `graphicsLayer` imports/usage as keep-alive mechanics. The separate application
   commit `3aa309c` narrowed the rule to hiding behavior; the runtime guard was
   rerun successfully before committing this audit. Session-boundary and icon
   contract checks also passed.
6. Isolated Maven packaging caught annotation 1.10.0's common KLib ABI 2.3.0,
   despite Android compilation passing. Direct declarations retain 1.9.1;
   refreshing API baselines would not fix this compiler incompatibility.
7. Glide 5.0.9 and its GIF modules require compile SDK 37. The SDK demo retains
   5.0.7 with its matching annotation processor; the 5.0.7 AAR metadata does not
   require the newer toolchain. No SDK requirement check was suppressed.

## Reproduction Notes

Kit checks use the checked-in Gradle 8.14.5 wrapper:

```bash
./gradlew :gearui-kit:cleanTestDebugUnitTest :gearui-kit:testDebugUnitTest \
  :gearui-kit:apiCheck :gearui-kit:lintDebug :sample:assembleDebug
CHROME_BIN=/path/to/chrome ./gradlew :gearui-kit:cleanJsBrowserTest \
  :gearui-kit:jsBrowserTest :sample:jsApp:jsBrowserDevelopmentWebpack
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/generate_tokens.py --check
```

For the native-test experiment, first build the iOS sample host. The local Gradle
init script adds these linker arguments to `linkDebugTestIosSimulatorArm64`:
`-F <DerivedData>/Build/Products/Debug-iphonesimulator/OpenKuiklyIOSRender`,
`-framework OpenKuiklyIOSRender`, and
`-rpath <DerivedData>/Build/Products/Debug-iphonesimulator/iosApp.app/Frameworks`.
Then run `:gearui-kit:iosSimulatorArm64Test` with that init script. No dummy
symbols, ignored linker failures or test exclusions were used.

Raw local logs and the temporary harnesses for this run are under
`/tmp/gearui-deps-228/`. This directory is diagnostic output, not a checked-in
release artifact. Remote CI must still verify the eventual committed candidate.
