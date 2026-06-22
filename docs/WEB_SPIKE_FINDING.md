# Web Spike 0 — Finding

Goal（GPT 压缩版）：能不能把一个 `Text("Hello GearUI")` 的 compose 内容在浏览器里渲染出第一帧。
Timebox：半天。Mode：探索，不承诺成功。
**结论：FAIL — 上游缺口。GearUI 这边就绪，KuiklyUI 的 compose-web host 缺失。**

## 已确认（证据）

### 1. gearui-kit → JS 编译：PASS（库这半就绪）
- gearui-kit 纯 commonMain、零 expect/actual、零平台代码
- 加 `js(IR){browser()}` + `org.jetbrains.compose.experimental.jscanvas.enabled=true` → `compileKotlinJs` **BUILD SUCCESSFUL**（零代码改动；探针已还原）
- `com.tencent.kuikly-open:compose` 有已发布的 `compose-js` 变体

### 2. KuiklyUI 的 web 宿主只服务「自渲染」路径
- `h5App-js`（JS webpack 宿主 + `core-render-web`）按 `pageName` 加载**自渲染页面 bundle**（`com.tencent.kuikly.core` DSL，如 `HelloWorldPage`）
- 之前的 `sample/jsApp` 是 h5App 的完整拷贝（`KuiklyRenderView`/`pageName`）——这是自渲染宿主，与 compose 组件不兼容，运行时报 `callKotlinMethod is not a function` / `KuiklyCore-core not found`

### 3. compose 页面入口机制 = `@Page` + `ComposeContainer` + `setContent`，由 core-ksp 注册
- demo 的 compose 页面：`@Page("X") class X : ComposeContainer() { override fun willInit() { setContent { ... } } }`
- demo build 给 js 也配了 `add("kspJs", this)`

### 4. 根因：core-ksp 没有 JS EntryBuilder（compose @Page 在 js 上 fallback 到 Android）
`KuiklyCoreProcessorProvider.getEntryBuilder()` 按 source set 分发：
```kotlin
return when {
    outputSourceSet.androidJVMFamily() -> AndroidTargetEntryBuilder(...)
    outputSourceSet.iosFamily()        -> IOSTargetEntryBuilder(...)
    outputSourceSet.ohosFamily()       -> OhOsTargetEntryBuilder(...)
    else -> AndroidTargetEntryBuilder(caughtException)   // ← JS 落这里
}
```
- 实现只有 Android / iOS / OhOs，**没有 `JsTargetEntryBuilder`**
- `kspJs` 的 source set 不属于 android/ios/ohos family → 命中 `else` → 用 **AndroidTargetEntryBuilder**
- Android 入口生成器产出引用 androidMain-only 类型（`IKuiklyCoreEntry` 等）的代码 → 在 web bundle 里这些类型不存在 → 运行时 `callKotlinMethod`/`KuiklyCore-core not found`

### 5. compose jsMain 没有独立 canvas/window 宿主
- `compose/src/jsMain` 只有零碎平台桩（key codes、scheduler、GlobalSnapshotManager、WeakReference）
- 没有 `CanvasBasedWindow` / canvas 绑定 / scene→屏幕 wiring
- 整个 KuiklyUI repo 没有任何 compose-on-web 的可运行示例，git log 无相关提交
- 所以也无法绕过 @Page 直接把 ComposeScene 挂到浏览器 canvas

## 判定

| 层 | 状态 |
|---|---|
| GearUI 组件库 → JS 编译 | ✅ 就绪 |
| Kuikly compose 运行时 → JS klib | ✅ 已发布 |
| **Kuikly compose-web 渲染宿主（@Page js 入口 / ComposeContainer→canvas）** | ❌ **上游缺失** |

**第一帧画不出来不是 GearUI 的问题，是 KuiklyUI 的 compose-web runtime 尚未完成（core-ksp 缺 JsTargetEntryBuilder + compose 缺 js canvas host）。**

## 路线归档

```
Web:
  compile target:        feasible / VERIFIED
  browser runtime host:  BLOCKED upstream (KuiklyUI core-ksp JsTargetEntryBuilder 缺失)
  roadmap:               等上游能力（或我们投入给 KuiklyUI 贡献 JsEntryBuilder）
                         不在 v1.0，spike 已给出明确 blocker
```

按 spike 协议：**停在这里**，不再往 broken 路径堆代码。下一步是给 KuiklyUI 提 issue（草稿见下），而不是继续。

---

## KuiklyUI Issue 草稿

> **Title**: Compose on Web (JS): core-ksp has no JsTargetEntryBuilder; `@Page` registration falls back to AndroidTargetEntryBuilder
>
> **Environment**: `com.tencent.kuikly-open:compose:2.21.0-2.1.21`, Kotlin/JS (IR), `js(IR){browser()}`, `kspJs`
>
> **What works**: A pure-Compose component library (`androidx.compose.runtime` + `com.tencent.kuikly.compose.foundation/ui`, no `@Page`) compiles cleanly to JS via the published `compose-js` artifact (with `org.jetbrains.compose.experimental.jscanvas.enabled=true`).
>
> **What's broken**: Hosting a `@Page` + `ComposeContainer { setContent { Text("Hello") } }` on web. In `core-ksp` `KuiklyCoreProcessorProvider.getEntryBuilder()`, the platform dispatch is `androidJVMFamily → Android`, `iosFamily → iOS`, `ohosFamily → OhOs`, and **`else → AndroidTargetEntryBuilder`**. A `kspJs` source set hits the `else` branch and uses the **Android** entry builder, which generates code referencing androidMain-only types (`IKuiklyCoreEntry`, etc.). At runtime in the browser this fails with `callKotlinMethod is not a function` / `KuiklyCore-core not found`.
>
> **Also missing**: `compose/src/jsMain` has no canvas/window host (no `CanvasBasedWindow`, no scene→canvas binding), so there's no way to mount a `ComposeScene` to a browser surface bypassing `@Page` either.
>
> **Ask**: Is Compose-on-Web a supported/planned target? If yes, (1) a `JsTargetEntryBuilder` in core-ksp, and (2) a documented compose-web host entry (page-by-name via `core-render-web`, or a canvas host) would unblock it. Is there a reference compose-web demo we can follow?
