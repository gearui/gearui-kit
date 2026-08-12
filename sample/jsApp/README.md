# GearUI Sample — Web (H5) host

Runs the GearUI sample in a browser through KuiklyUI's web renderer. Adapted
from the official `h5App` template in the KuiklyUI repository; the host code
under `src/jsMain/kotlin` is kept close to that template so upstream fixes can
be diffed in.

## Run

```bash
# from the repository root
./gradlew :sample:jsApp:jsBrowserDevelopmentRun
# then open http://localhost:8081/
```

That is the whole command. The dev server listens on 8081 rather than
webpack's default 8080, which is busy often enough to be worth avoiding; pass
`-PwebPort=9000` to change it. The sample's JS bundle and the icon assets are
staged into the host's resources by Gradle (`copySampleJsBundle`,
`copySampleAssets`), so there is nothing to copy by hand.

To produce a servable directory instead of running the dev server:

```bash
./gradlew :sample:jsApp:jsBrowserDevelopmentWebpack
# serve these together:
#   sample/jsApp/build/processedResources/js/main/       index.html, gearui_sample.js, assets/
#   sample/jsApp/build/kotlin-webpack/js/developmentExecutable/jsApp.js
```

## How the pieces fit

- `:sample` builds to JS as `gearui_sample.js`. It contains the one Kuikly page
  the sample registers, `@Page("MainDemo")`; navigation between the 76 component
  demos happens inside Compose, not through page routing.
- `jsApp` is the host. It boots the web renderer and attaches `MainDemo`.
- Icons resolve `assets://icons/<name>.png` to `/assets/icons/<name>.png`, which
  is why the assets have to sit next to the bundle.

## Two things that will bite

**The host must not carry a UMD wrapper.** `webpack.config.d/output.js` sets
`iife` and clears `library`. Without it, kotlin-webpack's UMD tail assigns the
host's top-level exports onto `window`, and because the host contains
`@JsExport` files from core-render-web it has a `com` key — which replaces
`window.com` wholesale and wipes the bridge the business bundle just installed.
The symptom is `registerCallNative error` followed by `callNative is not
defined`, which reads like a missing dependency rather than a bundling
collision.

**Compose needs an opt-in for this target.** `gradle.properties` sets
`org.jetbrains.compose.experimental.jscanvas.enabled=true`; without it the
build refuses the JS target outright.

## Status

75 of the 76 component demos render and are interactive. `Table` fails at
runtime with a Kotlin/JS partial-linkage error naming `TableExample.kt` — the
sample's own demo file, the largest at 883 lines with 46 render lambdas — not
the Table component itself. Root cause is not established; it needs a minimal
reproduction before anything is filed upstream.

The development bundle is 23MB. Production size has not been measured.
