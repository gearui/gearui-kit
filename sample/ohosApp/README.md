# GearUI Sample — HarmonyOS host

[English](./README.md) | [简体中文](./README.zh-Hans.md)

DevEco Studio project that loads the sample's Kotlin/Native shared library,
adapted from the KuiklyUI repository's `ohosApp`.

> **Status: builds, not yet run.** `entry-default-unsigned.hap` (64MB) is
> produced end to end — Kotlin/Native → CMake NAPI glue → ArkTS → HAP — and
> contains both `libshared.so` and `libkuikly_entry.so`. What has *not* happened
> is installing and launching it: that needs an emulator image and a signed HAP,
> both gated behind a Huawei developer account (see [Running](#running)). So the
> Compose UI has never been seen rendering on HarmonyOS.

## Why HarmonyOS needs a separate build

It cannot be a target of the normal build. The KuiklyUI artifacts carrying
`ohosArm64` are published against **Kotlin 2.0.21-KBA-010** (a Tencent
distribution) while the main build runs stock Kotlin 2.1.21 — and the
`compose:2.25.0-2.1.21` this project uses has **no `ohosArm64` variant at all**.
KuiklyUI solves this with parallel build files per Kotlin version; this
repository mirrors that:

```
settings.ohos.gradle.kts        points every project at build.ohos.gradle.kts
build.ohos.gradle.kts           root: Kotlin 2.0.21-KBA-010, AGP 7.4.2, KSP 2.0.21-1.0.27
gearui-kit/build.ohos.gradle.kts
sample/build.ohos.gradle.kts    produces libshared.so
```

The ordinary Android / iOS / Web builds are untouched; ohos is driven explicitly.

Anything the sample's sources need must exist in **both** build files. That is
how the two drift apart, which is why the generated `SampleBuildInfo` lives in
`gradle/sample-build-info.gradle.kts` and is applied by each.

## Building

```bash
# 1. Kotlin/Native shared library (from the repository root)
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64

# 2. Copy the two artifacts the ArkTS host expects
cp sample/build/bin/ohosArm64/sharedDebugShared/libshared.so \
   sample/ohosApp/entry/libs/arm64-v8a/
cp sample/build/bin/ohosArm64/sharedDebugShared/libshared_api.h \
   sample/ohosApp/entry/src/main/cpp/thirdparty/biz_entry/

# 3. Dependencies and HAP
cd sample/ohosApp
export DEVECO_SDK_HOME=/Applications/DevEco-Studio.app/Contents/sdk
export PATH="/Applications/DevEco-Studio.app/Contents/tools/node/bin:/Applications/DevEco-Studio.app/Contents/tools/ohpm/bin:$PATH"
ohpm install --all
/Applications/DevEco-Studio.app/Contents/tools/hvigor/bin/hvigorw \
  --mode module -p product=default -p module=entry@default assembleHap --no-daemon
```

Output: `entry/build/default/outputs/default/entry-default-unsigned.hap`.

Or just open `sample/ohosApp` in DevEco Studio, which does all of step 3 for you.

DevEco Studio 26.0 **bundles the SDK** at
`/Applications/DevEco-Studio.app/Contents/sdk` (API 26). There is nothing to
download through SDK Manager; `~/Library/Huawei/Sdk` holding only a
`productConfig.json` is normal and not a broken install.

## Running

Two things are still missing, and both need a Huawei developer account:

1. **An emulator image.** The emulator engine ships with DevEco
   (`Contents/tools/emulator`) but no system image. Creating a device in Device
   Manager requires signing in, accepting the emulator licence, and downloading
   a multi-GB image.
2. **A signed HAP.** The build produces an *unsigned* one. HarmonyOS will not
   install that. Debug signing needs a provisioning profile whose
   `debug-info.device-ids` lists the target's UDID — so the emulator has to
   exist first, and the profile comes from DevEco's automatic signing after
   login (`File → Project Structure → Signing Configs → Automatically generate
   signature`).

Once both exist:

```bash
hdc list targets                      # confirm the emulator is attached
hdc install -r entry-default-signed.hap
hdc shell aa start -a EntryAbility -b com.gearui.kit.sample
```

`hdc` is at `Contents/sdk/default/openharmony/toolchains/hdc`.

## If ohpm downloads fail

Symptom — metadata resolves, every package download dies:

```
ohpm WARN: ECONNRESET fetch package @kuikly-open/render ... failed,
errMsg: Client network socket disconnected before secure TLS connection was established
```

This is not a registry outage. ohpm redirects `.har` downloads to Huawei's CDN
(`contentcenter-drcn.dbankcdn.cn`, CloudFront-backed), and a local proxy in
fake-IP mode answers DNS for it with a `198.18.x.x` address and then kills the
TLS handshake. Direct connections are fine once DNS is.

Fix it in the proxy — route `ohpm.openharmony.cn` and `*.dbankcdn.cn` direct, or
disable fake-IP. If you cannot, `./fetch-deps.sh` resolves the CDN over DoH,
pins the address with `curl --resolve`, and installs from the downloaded files.
It restores the manifests afterwards so the committed ones keep their registry
versions.

## What was changed from the KuiklyUI template

- `bundleName` → `com.gearui.kit.sample`, vendor and app name to match.
- Default page `router` → `MainDemo`. The template's `router` page belongs to
  KuiklyUI's own demo; the GearUI sample registers one Kuikly page,
  `@Page("MainDemo")`, and navigates between its 76 component demos inside
  Compose.
- `@kuikly-open/render` now comes from the **ohpm registry** (`^2.25.0`) rather
  than a `file:../../core-render-ohos` sibling checkout that does not exist
  here. The `render` module entry in `build-profile.json5` went with it.
- **Removed the CDN download in `entry/hvigorfile.ts`.** The template fetches a
  prebuilt `libshared.so` from Tencent's CDN when the file is missing. That is
  right for their demo and dangerous here: the download is *their* demo binary,
  so a missing local build would silently produce an app that launches, renders,
  and is not this sample at all. It now fails with instructions instead.
- **Fixed the asset paths in the same file.** It copied from
  `../../demo/src/commonMain/assets` (KuiklyUI's layout). It now takes both
  gearui-kit's icons and the sample's own files, matching what the iOS pod
  resources sync and the web bundle do.
- **Deleted `CrashReport`.** It imported `../../ts-api/provider`, which is not
  part of this project, and it is Bugly crash reporting the sample has no reason
  to ship. The `bugly` dependency went with it.
- Dropped `publish.sh` — it publishes KuiklyUI's own ohpm package.
- `copy_header.sh` is kept but is **not needed** with the registry package: it
  expects headers under `src/main/cpp`, and the published package puts them in
  `include/Kuikly`, which `find_package(render)` locates on its own.
- `dependencies/` is not vendored. In the template that is a 20MB hvigor
  toolchain tarball, supplied by DevEco or ohpm.

## Known gaps

- The app has never been launched, so **nothing about the UI is verified** on
  HarmonyOS. Web needed one platform-specific fix (the host's UMD wrapper)
  before it rendered; HarmonyOS has had no equivalent shakedown.
- The HAP is 64MB because `libshared.so` is a 57MB debug binary with full debug
  info. A release build (`linkSharedReleaseSharedOhosArm64`) has not been tried.
