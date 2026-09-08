# Upstream report: KuiklyUI Gaussian blur

**Status:** open, not yet filed upstream.
**Against:** KuiklyUI `2.27.0` / `main` @ `85068438`.
**Why this exists:** GearUI ships its iOS 26 baseline with frosted glass turned
off (`RuntimeFlags.materialPolicy = MaterialPolicy.Never`). The capability is
present in KuiklyUI; what is missing is agreement between the four renderers
about what it means. This file records the findings so they can be filed
upstream, and proposes fixes small enough to send as PRs.

The GearUI side is already built and verified — `foundation/material/` and the
sample's *Material Probe* page. Turning it on is one default. See
DESIGN_SYSTEM_SPEC §11.2.

---

## Summary

KuiklyUI **does** support Gaussian blur, on all four renderers, through the
core `BlurView`. There is no missing platform. There are four separate gaps,
and they are not equally hard:

| # | Gap | Severity for a cross-platform kit | Effort |
|---|---|---|---|
| 1 | `blurRadius` is scaled differently by every renderer | **blocking** | small |
| 2 | iOS is locked to `UIBlurEffectStyleLight` — no dark material | **blocking** | small |
| 3 | iOS radius saturates: anything ≥ 10 renders identically | high | small |
| 4 | No `Modifier.blur` / backdrop-blur modifier in the compose layer | low (workaroundable) | medium / impossible on iOS |

1–3 are what keep GearUI's default off. Each is a self-contained patch.

---

## Finding 1 — `blurRadius` means four different things

`BlurAttr.blurRadius` is a single number clamped to `[0, 12.5]`:

```kotlin
// core/src/commonMain/kotlin/com/tencent/kuikly/core/views/BlurView.kt:49
fun blurRadius(radius: Float) {
    "blurRadius" with min(radius, 12.5f)
}
```

Each renderer then applies its own transform to it:

| renderer | transform | reference | effective meaning |
|---|---|---|---|
| Android | `× 2`, applied to a bitmap downscaled `20×`, then upscaled | `KRBlurView.kt:127`, `SizeScaler(20f)` at `:153`, `RenderEffectBlur.kt:54` | ≈ **`radius × 40`** in screen px |
| iOS | `÷ 10`, used as `UIViewPropertyAnimator.fractionComplete` | `KRBlurView.m:84` | a **0…1 fraction** of one fixed system effect |
| HarmonyOS | `× 1` → `.backdropBlur(radius)` | `KRBlurView.ets:29` | **vp** |
| Web | `× 5` → `backdrop-filter: blur(Npx)` | `KRBlurView.kt` (`blurRadius`) | **px** |

So `blurRadius(12.5)` asks for roughly a 500px blur on Android, a 62.5px blur
on web, a 12.5vp blur on HarmonyOS, and "the light system material at full
strength" on iOS. A design system cannot pick a number that means the same
thing on two platforms, let alone four.

Corroborating evidence that the two sides already disagree about the range:
the web renderer clamps its own input at `25`, which is dead code — core has
already clamped it at `12.5`.

### Proposed fix

Define `blurRadius` as **density-independent points of Gaussian sigma in screen
space**, document it on `BlurAttr.blurRadius`, and make each renderer convert
into its own space:

- **Android** — divide by the bitmap downscale factor rather than multiplying:
  `effectiveRadius = radius / SizeScaler.scaleFactor` (times density), so the
  post-upscale blur matches the requested screen-space radius. Note
  `RenderEffect.createBlurEffect` takes sigma, not a kernel radius.
- **Web** — drop the `× 5`, emit `blur(${radius * devicePixelRatio}px)`. CSS
  `blur()` also takes a sigma-like value, so this lines up with Android.
- **HarmonyOS** — already 1:1 in vp; keep, and document that this is the
  reference implementation.
- **iOS** — cannot express a radius with `UIVisualEffectView` at all; see
  finding 3.

Raising the `12.5` clamp is a separate question. It exists because the iOS
implementation saturates; once iOS expresses a real radius, the clamp can go.

---

## Finding 2 — iOS has no dark material

`UIBlurEffectStyleLight` is hardcoded in two places, and there is no way for
the Kotlin side to ask for anything else:

```objc
// core-render-ios/Extension/AdvancedComps/KRBlurView.m:33  (init)
self.effect = [UIBlurEffect effectWithStyle:(UIBlurEffectStyleLight)];

// core-render-ios/Extension/AdvancedComps/KRBlurView.m:82  (setCss_blurRadius)
weakSelf.effect = [UIBlurEffect effectWithStyle:(UIBlurEffectStyleLight)];
```

macOS has the same bias — `NSVisualEffectMaterialLight` at `:35`, and the
radius→material mapping at `:90-103` picks `Light` / `MediumLight` /
`ContentBackground`.

**Consequence:** any app with a dark theme gets a *light* frosted panel on
iOS. For a kit whose visual baseline is iOS 26 this is disqualifying on its
own — it is the one platform whose look GearUI is trying to match, and it is
the one that cannot render a dark material.

### Proposed fix

Add a style attribute to `BlurAttr` and map it per platform. Small, additive,
backwards compatible:

```kotlin
// core/src/commonMain/kotlin/com/tencent/kuikly/core/views/BlurView.kt
enum class BlurStyle { LIGHT, DARK, SYSTEM }   // SYSTEM follows the trait collection

class BlurAttr : Attr() {
    fun blurStyle(style: BlurStyle) {
        "blurStyle" with style.ordinal
    }
}
```

| renderer | mapping |
|---|---|
| iOS | `UIBlurEffectStyleLight` / `Dark` / `SystemMaterial` (iOS 13+, follows `userInterfaceStyle`) |
| macOS | `NSVisualEffectMaterialLight` / `Dark` / `ContentBackground` with `appearance` following the system |
| Android | tint overlay colour — `KRBlurView` already paints one in `willInit()` |
| HarmonyOS | `.backgroundBlurStyle()` already carries light/dark variants |
| Web | pair `backdrop-filter` with a light/dark `background-color` |

`SYSTEM` should be the recommended value; it is what UIKit's own materials do
and it removes the need for the app to re-push a value on theme change.

Note `BlurView.willInit()` in core already branches on platform to pick a
tint (`0.1` black on iOS/macOS, `0.1` white elsewhere) — a fixed choice that
would be better derived from this same attribute.

---

## Finding 3 — iOS radius saturates at 10

```objc
// core-render-ios/Extension/AdvancedComps/KRBlurView.m:84
animator.fractionComplete = css_blurRadius.floatValue / 10;
```

The variable-strength blur is a paused `UIViewPropertyAnimator` held at a
fraction of the way into a `nil → UIBlurEffect` transition. `fractionComplete`
is a 0…1 progress value; UIKit clamps out-of-range input.

With `blurRadius` clamped at `12.5`, `fractionComplete` can be asked for `1.25`.
So **every radius from 10 to 12.5 renders identically on iOS.** GearUI's
`Materials.Chrome` (12.5) and `Materials.Popover` (10) are meant to be visibly
different weights and would be indistinguishable.

> Derived from reading the source; **not yet confirmed on a device.** Verifying
> it needs one iOS run of the sample's Material Probe page, where `Chrome` and
> `Popover` sit side by side.

Two further concerns in the same method, worth raising even if they are not
bugs today:

- The animator is created fresh on every radius change and retained
  indefinitely (`_animator`). A `UIViewPropertyAnimator` left paused holds its
  animation; if it is ever allowed to finish, the effect snaps to full strength.
  The existing `stopAnimation:` / `finishAnimationAtPosition:` guards handle the
  replacement path, but nothing stops it on `didMoveToWindow:nil`.
- `setCss_blurRadius:` is re-invoked from both
  `UIApplicationDidBecomeActiveNotification` and `didMoveToWindow`, so the
  animator is rebuilt on every foreground.

### Proposed fix

`UIVisualEffectView` genuinely cannot express an arbitrary radius with public
API, so the honest options are:

1. **Map the radius onto the named system materials** and document that iOS is
   stepped rather than continuous — `UIBlurEffectStyleSystemUltraThinMaterial`
   / `ThinMaterial` / `Material` / `ThickMaterial` / `ChromeMaterial` (iOS 13+).
   These are the materials iOS itself uses, they are automatically
   light/dark-aware, and they need no animator at all. This also resolves
   finding 2.
2. Keep the animator for fine control, but scale by the documented maximum
   (`fractionComplete = radius / MAX_BLUR_RADIUS`) so the top of the range is
   reachable and no input is silently clamped.

**Option 1 is the recommendation.** A cross-platform kit is better served by
five well-defined steps that match the platform's own vocabulary than by a
continuous radius that only one renderer can honour. It would also let the API
grow a semantic form — `blurMaterial(THIN | REGULAR | THICK)` — that maps
cleanly onto ArkUI's `BlurStyle` and CSS, and only needs approximating on
Android.

---

## Finding 4 — no blur in the compose layer

Two different effects are both called "blur" and only one of them is missing:

- **Backdrop blur** — blur what is *behind* the view. This is what `BlurView`
  does and what a frosted material needs. Available on all four renderers.
- **Content blur** — blur the element's *own* rendering. This is Compose's
  `Modifier.blur`. Not available.

The compose layer exposes neither as a modifier. `RenderNodeLayer` carries the
graphics-layer plumbing for content blur and stops short of using it:

```kotlin
// compose/src/commonMain/kotlin/com/tencent/kuikly/compose/ui/platform/RenderNodeLayer.kt:250
// todo renderEffect
```

`performDrawLayer` around that line already applies `shadow`, `alpha`, `scale`,
`rotate`, `clipPath` and `borderRadius` onto the underlying native view, so the
hook is in the right place.

### Content blur (`Modifier.blur`) — three renderers, not four

The per-renderer primitive exists and the universal property layers are already
there to hang it on (`View.setCommonProp` in
`core-render-android/.../css/ktx/KRCSSViewExtension.kt`, the equivalents
elsewhere). There is also precedent: image content blur already ships as
`IImageAttr.blurRadius` (`IImageAttr.kt:50`), implemented on web as
`ele.style.filter = "blur()"` (`KRImageView.kt:159`).

| renderer | primitive | effort |
|---|---|---|
| Android 31+ | `View.setRenderEffect(RenderEffect.createBlurEffect(...))` | one line — already used at `RenderEffectBlur.kt:54` |
| Web | `style.filter = "blur(Npx)"` | one line |
| HarmonyOS | `.blur(radius)` universal attribute | one line |
| **iOS** | **none** | **not possible with public API** |

Blurring an arbitrary `UIView`'s own content requires `CALayer.filters` /
`CAFilter`, which is private API — SwiftUI's `.blur()` uses it internally and
third-party apps cannot. `UIVisualEffectView` only blurs the backdrop.
Snapshot-and-blur is neither live nor cheap.

So `Modifier.blur` can never be uniformly supported. If it is implemented it
should ship as an explicitly per-platform capability, with a documented no-op
on iOS. **For GearUI this is not worth pursuing** — it is not what a frosted
material needs.

### Backdrop blur as a modifier — the one worth having

A `Modifier.backdropBlur(radius)` would be implementable everywhere, since
every renderer has the primitive. The obstacle is structural rather than
platform-level: in Kuikly compose the *view type* is chosen by the node, and
`blurRadius` is handled only by `TYPE_BLUR_VIEW`. A modifier would need either

- backdrop blur promoted to a universal property in each renderer's common
  property layer (`setCommonProp` and its equivalents), the way `opacity` and
  `boxShadow` are; or
- the compose node swapping its backing view type when the modifier is present.

The first is cleaner and matches how ArkUI and CSS already model it — both
treat backdrop blur as an attribute of any element, not a special element.

**GearUI does not need this to be fixed.** `MaterialSurface` mounts the core
`BlurView` through `MakeKuiklyComposeNode` as a sibling behind the content,
which works today. Filed as an ergonomics improvement, not a blocker.

---

## What GearUI does in the meantime

`RuntimeFlags.materialPolicy` defaults to `MaterialPolicy.Never`: every
material renders as an opaque `Theme.colors.surface`. The fallback deliberately
drops the translucency along with the blur — a tint is calibrated against a
blurred backdrop, and over raw content its contrast depends on the user's data.

`MaterialPolicy.Auto` and `Always` are implemented and verified, and the sample
forces them on so the cross-renderer calibration in finding 1 stays visible.
Measured on Android 16 / API 36 (RenderEffect backend), over a backdrop that is
half solid block and half fine stripes:

- blur on: surface luminance 80.6 → 66.1 → 51.0 left to right, mid-band
  stdev 2.42 — a real Gaussian sampling the backdrop
- forced flat: 18.0 / 18.0 / 18.0, stdev 0.00 — opaque, no backdrop leakage

Turning frosted glass on once upstream lands is a single default change.

## TODO

- [ ] Confirm finding 3 on a device — one iOS run of Material Probe, comparing
      `Chrome` (12.5) against `Popover` (10).
- [ ] Confirm finding 2 visually on iOS in dark mode.
- [ ] File findings 1–3 upstream as one issue at `Tencent-TDS/KuiklyUI`.
- [ ] Offer the `blurStyle` / `blurMaterial` patch as a PR (findings 2 + 3,
      option 1) — additive and self-contained.
- [ ] Re-check on each KuiklyUI upgrade; flip
      `RuntimeFlags.materialPolicy` back to `Auto` when 1–3 are resolved.
