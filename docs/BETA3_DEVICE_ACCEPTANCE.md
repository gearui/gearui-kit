# beta3 Device Acceptance

Date: 2026-09-18. Revision: `678e348` plus this document. Platforms: iPhone 17 Pro
simulator (iOS 26.2, arm64) and a connected Android device (1440x3200). The sample
application was driven through idb / adb; every result below is from an actual
interaction, judged by the accessibility tree and a screenshot. Screenshots and the
driver scripts are under `/tmp/beta3-accept/` on the acceptance machine (local
evidence, not durable storage). Web and HarmonyOS were not exercised in this pass.

## Defects Found And Fixed In This Pass

| Defect | Platforms | Fix |
| --- | --- | --- |
| Input/Textarea `maxLength`: the native field kept rejected characters (14 shown, counter 10/10) | Android, iOS | `43329ac` applies Kuikly's native `maxLength` modifier |
| Opening a ContextMenu terminated the app: `Constraints.fixed` overflow in `DecoratedSurface` under unbounded (intrinsic) constraints | iOS crash; Android survived by chance | `678e348` unbounded axis contributes zero |
| Sample Settings page: BACK left the app on Android, edge swipe ignored on iOS | Android, iOS | `5356a7b` wraps Settings in the sample's swipe-back host |
| Standalone `iosSimulatorArm64Test` could not link the Kuikly host symbol | iOS | `fb1a710` links the real OpenKuiklyIOSRender via `-PgearuiIosTestHostDir`; `scripts/ios_native_tests.sh`; CI iOS job runs it |

## Results After The Fixes

| Check | Android | iOS |
| --- | --- | --- |
| Input typing, value and IME (Done action visible) | Pass | Pass (simulator uses the host keyboard; on-screen keyboard overlap not observed) |
| Input `maxLength` 10 with 14 typed: field and counter both stop at 10 | Pass | Pass |
| BottomSheet: scrim tap closes | Pass | Pass |
| BottomSheet: drag on grabber/header closes; drag on the item list does not | Pass | Pass |
| BottomSheet: BACK / edge swipe with sheet open closes the sheet only, page stays | Pass (BACK) | Pass (edge swipe blocked by scrim) |
| Drawer (right): scrim tap, swipe toward edge, BACK / edge swipe | Pass | Pass |
| ConfirmDialog: BACK / edge swipe does not dismiss; outside tap does not dismiss (`dismissOnOutside=false` default) | Pass | Pass |
| ContextMenu: opens anchored under the trigger, outside tap / BACK closes | Pass | Pass (was a crash) |
| Popover (arrow): opens and closes | Pass | Pass |
| Select: opens and closes | Pass | Pass |
| Settings: light/dark switch applies immediately to the open page | Pass | Pass |
| BottomSheet and Input rendered after a theme switch use the new palette | Pass | Pass |
| Settings page BACK / edge swipe returns Home | Pass (was app exit) | Pass (was ignored) |
| Large text | Android `font_scale 1.3`: text scales, no clipping on Input page | iOS Dynamic Type has no effect: Kuikly uses fixed logical sizes (renderer limit, not accepted) |

## Not Accepted Here

- Screen-reader traversal, frame performance and long/localized labels.
- Overlay content/callback updates while open (covered by unit tests only).
- Web host and HarmonyOS device interaction.
- Overlay items are absent from the iOS accessibility tree while the page content
  is present; visual checks used screenshots. Worth a follow-up for accessibility.
