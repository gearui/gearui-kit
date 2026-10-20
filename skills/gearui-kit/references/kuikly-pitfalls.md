# KuiklyUI pitfalls

KuiklyUI runs Compose, but every Compose node becomes a **real native view** (UIView,
android.view, a DOM node, ArkUI). Code that is right in Jetpack Compose can be silently
wrong here. These are the differences that bite, each with what to do. Most are already
handled inside the kit's components — which is the strongest reason to use them.

## Drawing

| Symptom | Cause | Do |
| --- | --- | --- |
| `.clickable` row shows no press | `LocalIndication` never reaches the native view | `Button`, `Cell`, `Modifier.rowClickable`, `PressableFeedback` |
| A `drawWithContent` / `drawBehind` overlay is missing or under the children | Children are native views stacked above the parent's canvas; `drawBehind` on `Text` does not draw | An overlay is a real `Box(Modifier.matchParentSize().background(...))`; an underline is a real 1dp view |
| A second `.background` hides the first, or hides a press fill | A native view has one background colour | Pass your colour as the component's `background`/`backgroundColor` parameter (`rowClickable(background = …)`) |
| `graphicsLayer { alpha / translation }` does nothing | Its native application is disabled in current KuiklyUI (the kit uses it only where it is wired) | `Modifier.alpha` to hide; let `TabHost` / `Navigator` own visibility and motion |
| An `alpha(0f)` view still takes taps (Android) | Opacity is not hit testing | `TabHost` blocks input on hidden pages; do not hide interactive content by alpha yourself |
| A tap on blank space focuses a field on a page underneath | Native views without a touch listener let touches fall through | The `Navigator` and overlays handle it; do not stack pages yourself |
| Blur / frosted glass looks different per platform | Four renderer gaps upstream | Glass is off by default (`MaterialPolicy.Never`); do not add blur |

## Performance

| Symptom | Cause | Do |
| --- | --- | --- |
| Janky tab switches | `when (tab)` destroys and rebuilds a native view tree per switch (35–43% janky frames measured) | `TabHost` |
| A swipe-back or pager stutters as a page appears | Building a heavy subtree costs main-thread native view creation (~180–450 ms on device) | The `Navigator` keeps the page underneath composed; for pagers keep `beyondViewportPageCount = 1` (`TabPager` does) |
| Stack overflow in deep screens on Android debug builds | Kuikly composes on its own thread with a 1 MB stack | Host thread adapter with 8 MB (app-structure.md §2) |
| Animations look stepped next to scrolling on iPhone | ProMotion held at 60 Hz | `CADisableMinimumFrameDurationOnPhone = YES` in the iOS host's Info.plist |
| Slow work in a list row stalls scrolling | Composition and layout share Kuikly's single context thread | Move file and network work to `Dispatchers.Default` / IO; keep row composition cheap |

## Text and input

| Symptom | Cause | Do |
| --- | --- | --- |
| Multi-line text overlaps at large Android font sizes | Kuikly scales `lineHeight` by dp but `fontSize` by sp | Use `Text` with `Theme.typography` styles; the kit corrects it. Never hand-set `lineHeight` |
| iOS does not follow Dynamic Type | KuiklyUI turns font scaling off by default | Known limit; do not try to read the system size yourself |
| A link inside a sentence cannot be tapped | `LinkAnnotation` taps are not dispatched natively | `LinkedText` / `AgreementCheckbox` |
| No decimal keyboard | Kuikly maps only Number/Email/Password; "number" has no point or minus | `NumberField` (it picks the right keyboard and parses exactly) |
| Focus lands in the wrong field, or autofocus is lost on iOS | Native fields are recreated when the modifier chain changes; a fixed delay before focusing loses the race | `Input(autoFocus = true)` (waits for layout); keep a field's modifier chain stable — do not make its border depend on focus |
| A scroll that starts on a field focuses it | The native list scrolls, the field moves with the finger | Kit fields only focus on a real tap; do not add your own tap-to-focus |
| `Text` inside a clickable container is unreachable by VoiceOver | A node with `onClick` becomes one element; children disappear | Kit containers use gestures for focus; give icon-only controls `contentDescription` |
| Screen reader says "已选择" in an English app | Kuikly appends a hardcoded Chinese state for `selected` semantics | Use kit controls (`controlSemantics`); never set `selected` / `selectable` yourself |
| Right-to-left layout does not mirror | Kuikly does not pass layout direction through | Not supported; do not ship RTL languages |

## Threads and platform

- Composition runs on Kuikly's context thread, not the platform main thread. Calling UIKit
  or Android view APIs from a composable crashes or blanks the page; dispatch to the main
  queue/looper in your platform code.
- Safe area arrives from the host and can be 0 for a frame on iOS cold start; the runtime
  stabilises it — read it through `PageScaffold` / `LocalRuntimeEnvironment`, never raw.
- The Android keyboard height is not reported by Kuikly; the host forwards it with
  `RuntimeInsetsBridge.updateKeyboardHeight`. The kit listens to iOS keyboard notifications itself.
- Images need the host's image adapter on Android; without it `Image`/`Avatar` URLs stay blank.
- Web: no UMD wrapper in the host's webpack config, or the render bridge is lost
  (`callNative is not defined`).
