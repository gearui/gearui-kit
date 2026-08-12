# GearUI Sample — HarmonyOS host

DevEco Studio project that loads the sample's Kotlin/Native shared library.
Adapted from `ohosApp` in the KuiklyUI repository.

> **Status: scaffolding, never built.** Nothing here has been compiled or run.
> There is no HarmonyOS toolchain on the machine this was written on — no
> DevEco Studio, no hvigor, ohpm or hdc, no OpenHarmony SDK — so the Kotlin
> side has not been compiled either. Treat every instruction below as the
> intended shape rather than a verified procedure.

## Why ohos needs a separate build

It cannot be added as a target to the normal build. The KuiklyUI artifacts
carrying `ohosArm64` are published against **Kotlin 2.0.21-KBA-010**, a Tencent
distribution, while the main build runs stock Kotlin 2.1.21 — and
`compose:2.25.0-2.1.21` has no `ohosArm64` variant at all. KuiklyUI handles
this with parallel build files per Kotlin version, and this repository mirrors
that:

```
settings.ohos.gradle.kts        selects build.ohos.gradle.kts for every project
build.ohos.gradle.kts           root: Kotlin 2.0.21-KBA-010, AGP 7.4.2
gearui-kit/build.ohos.gradle.kts
sample/build.ohos.gradle.kts    produces libshared.so
```

The ordinary Android/iOS/Web build is untouched; ohos is driven explicitly:

```bash
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64
```

## Intended flow

1. Build the shared library with the command above.
2. Copy the resulting `libshared.so` into this project's `entry/libs/arm64-v8a/`.
3. Open `sample/ohosApp` in DevEco Studio and run on a device or emulator.

`EntryAbilityStage.ets` calls `setup("libshared.so", ...)`, so renaming the
binary in `sample/build.ohos.gradle.kts` means renaming it there too.

## What was changed from the KuiklyUI template

- `bundleName` → `com.gearui.kit.sample`, vendor and app label to match.
- Default page `router` → `MainDemo`. The template's `router` page belongs to
  KuiklyUI's own demo; the GearUI sample registers exactly one Kuikly page,
  `@Page("MainDemo")`, and navigates between its 76 component demos inside
  Compose.
- `publish.sh` removed — it publishes KuiklyUI's own ohpm packages.
  `copy_header.sh` is kept: it copies C++ headers out of
  `entry/oh_modules/@kuikly-open/render`, which this project will need too, but
  it has not been run.
- `dependencies/` is not vendored. The template ships hvigor tarballs (20MB of
  build tooling); those should come from DevEco or ohpm, or be copied from the
  KuiklyUI checkout if an offline build is needed.

## What is likely still missing

Written from reading the template, not from a successful build, so this list is
where to start rather than a complete account:

- `entry/libs/arm64-v8a/` does not exist yet; step 2 above has no destination.
- The template imports `libkuikly_entry.so` and `@kuiklybase/knoi`, which
  resolve through KuiklyUI's own ohos dependencies. Whether they come from ohpm
  or have to be copied across has not been established.
- `CrashReport` and any other files under `entry/src/main/ets/kuikly/` are
  KuiklyUI's; they may reference things that do not exist here.
- Whether the sample's Compose surface renders on ohos is entirely unverified.
  Web needed one platform-specific fix — the host's UMD wrapper — and ohos has
  had no equivalent shakedown.
