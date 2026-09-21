# Surface Rendering Acceptance

## Implemented Contract

- `DecoratedSurface` renders ordered outer/inset shadow stacks, signed spread,
  offsets and blur without changing the measured content size.
- Solid, dashed, dotted, double, groove, ridge, outset, inset and custom dash
  borders share geometry across platforms. Odd dash arrays repeat; zero-length
  dash segments respect caps. Excessively dense patterns fail explicitly.
- Shadow blur is a bounded Gaussian edge-profile approximation, not exact 2D
  convolution. Coverage is quantized cumulatively for Kuikly's 8-bit alpha bridge.
- Rectangular and rounded outlines are supported; generic paths are rejected.
  Ancestors must allow shadow overflow. No frame-time/performance certification
  is implied by the screenshots.
- Font families and tracking survive DTCG-to-Kotlin lowering. The host registers
  installed custom fonts with `LocalFontRegistry`; generic fallbacks resolve per
  platform. This does not install fonts or guarantee identical glyph metrics.
- Card and MaterialSurface consume the shared renderer. Existing native-shadow
  consumers elsewhere are not automatically migrated by this addition.

## Device Evidence

The same `SurfaceLab` specimen was run on iPhone 17 Pro (iOS 26.2), a connected
Android device and the Web sample. Checked: inset holes, signed spread, border
interiors, all nine border choices, monospace, tracking and missing-family fallback.
The iOS and Web specimens also passed live light/dark and rounded/square switching.
These are focused material/font checks, not acceptance of every sample component.

Session-local screenshots:

- `/tmp/gearui-surface-ios-final.png`
- `/tmp/gearui-surface-ios-dark-square.png`
- `/tmp/gearui-surface-android-final.png`
- `/tmp/gearui-surface-android-dark-square.png`

Android also passed a separately built dark/square fixture. Web was visually
inspected at `http://127.0.0.1:8087/`, at desktop and 402x874 initial viewports.
Live browser resizing retains the host's initial layout width until reload;
responsive host resizing remains a separate issue, not a passed check.
Testing exposed and fixed
two real backend differences: difference clipping is unavailable on Web, and an
empty default font name falls back to a browser serif. Compound winding paths and
an explicit `system-ui` family now avoid both problems. Canvas callbacks are used
instead of draw-cache modifiers because the native Box/cache path did not paint.

HarmonyOS source compilation is checked separately. `hdc list targets` returns
`[Empty]`; no HarmonyOS screenshot, interaction or visual-parity pass is claimed.
Native wide-gamut output, exact blur parity and arbitrary-path rendering remain
outside this renderer's current guarantees.

## Reproduction

Local regression results: 119 Python tests, 191 Kotlin tests (zero failures),
22 shell guards, and deterministic token generation checks pass. API dumps were
refreshed for the intentional additions; downstream binaries require recompilation
as described in `MIGRATION_1_0.md`. HarmonyOS kit/sample source compilation passes.
Android sample assembly, Web webpack build, iOS simulator Xcode build, API checks
and `privchat-app` Android Kotlin compilation pass. Normal builds restore the home
entry (the fixture property is empty); the lab remains reachable from Card.
These uncommitted changes have not been validated by a remote CI run.

Open sample Card > material/font acceptance. Controls change only the specimen,
not the app-wide theme. For deterministic entry use the `surfaceAcceptance`
Gradle property: `light-rounded`, `dark-rounded`, `light-square`, `dark-square`.
Without that property the sample starts on its normal home page. For Xcode use
`ORG_GRADLE_PROJECT_surfaceAcceptance` with the same values.

The reference data format is [DTCG 2025.10 Format](https://www.designtokens.org/tr/2025.10/format/).
Standard data support and renderer-specific appearance are distinct contracts.
