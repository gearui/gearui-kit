# Web Spike 0 — Finding

> **⚠️ 修订（2026-05）：本文档早先版本写「上游缺失 / compose-web host BLOCKED」是错误的、下早了结论。**
> 实测 + 官方文档复核后纠正如下。错误根源：(1) 只读了 core-ksp `getEntryBuilder()` 一段就外推；
> (2) 实测时跑的是 `:demo`（巨型 monolithic 模块），不是官方 H5 的 `:shared` + `:h5App` 路径。

Goal（GPT 压缩版）：能不能把一个 compose `Text("Hello")` 在浏览器渲染出第一帧。

## 准确结论（修订后）

1. **gearui-kit → JS 编译：PASS**（实测，见下）。
2. **官方 KuiklyUI H5 工具链真实存在且成熟**（自渲染路径）。官方 quickstart（https://kuikly.tds.qq.com/QuickStart/h5.html）：
   - 模块结构：`:shared`（页面）+ `:h5App`（host）
   - 任务：`:shared:packLocalJsBundleDebug` / `:h5App:jsBrowserRun -t` / `copyAssetsToWebpackDevServer`
   - 依赖：`core-render-web:base` + `core-render-web:h5`
   - 入口：host `delegator.init()` 按 URL `?page_name=` 加载页面
3. **Compose DSL 页面能否在 H5 渲染 = 官方文档未明确说明 = gearui-kit 的关键未知**（gearui-kit 全是 compose）。
4. **我跑 `:demo:packLocalJSBundleDebug -PpageName=X` 失败**（self-render `HelloWorldPage` 与 compose `NavigationBarDemo` 都一样炸）——但这是**用错了模块**：`:demo` 的 KSP 生成 `KuiklyCoreEntry.kt`（`package com.tencent.kuikly.core.android`，Android 入口）进 JS 源集 → 引用 android-only `IKuiklyCoreEntry`/`callKotlinMethod` → JS 编译失败。**正式 2.21.0 release 与 dev HEAD 表现一致**，所以这是 `:demo` 模块的 monolithic-entry 路径问题，**不是 H5 整体不可用**。官方 H5 走 `:shared`+`:h5App`，不是 `:demo` 这个任务。

**未结论项**：用官方 `:shared`+`:h5App` 模板放一个 compose `@Page` 跑出第一帧——尚未验证（这才是真正要做的 Web Spike）。

## 已确认（证据）

### 1. gearui-kit → JS 编译：PASS（库这半就绪）
- gearui-kit 纯 commonMain、零 expect/actual、零平台代码
- 加 `js(IR){browser()}` + `org.jetbrains.compose.experimental.jscanvas.enabled=true` → `compileKotlinJs` **BUILD SUCCESSFUL**（零代码改动；探针已还原）
- `com.tencent.kuikly-open:compose` 有已发布的 `compose-js` 变体

### 2. 实测了什么（哪些是 valid 证据，哪些是「用错模块」）
- gearui-kit `compileKotlinJs` PASS（valid）
- `:demo:packLocalJSBundleDebug -PpageName=HelloWorldPage`（自渲染）与 `-PpageName=NavigationBarDemo`（compose）**都失败**，错误相同：KSP 生成的 `demo/build/generated/ksp/js/jsMain/.../KuiklyCoreEntry.kt` 头部是 `package com.tencent.kuikly.core.android` + `import com.tencent.kuikly.core.IKuiklyCoreEntry`（Android 入口生成器产物）→ android-only 类型在 JS 不存在 → 编译失败。
- 在干净的 **2.21.0 正式 release** 上重测 HelloWorldPage：**同样失败**。
- **但这是用错了模块**：`:demo` 是含全部页面的巨型 demo，其 KSP entry 走 `else→AndroidTargetEntryBuilder`。**官方 H5 不用 `:demo`，用 `:shared`（页面）+ `:h5App`（host）+ `core-render-web:base/h5`**（见官方 quickstart）。`:shared`+`:h5App` 模板的 KSP/插件配置与 `:demo` 不同，我**尚未**用官方路径测过。

### 3. `else→AndroidTargetEntryBuilder` 这条 fallback 确实存在（但不等于「H5 不可用」）
`core-ksp` `KuiklyCoreProcessorProvider.getEntryBuilder()` 只有 android/ios/ohos 三个 builder，js 落 `else`→Android。**这解释了 `:demo` 路径为何炸**。但官方 H5 自渲染是成熟的，说明官方 `:shared`+`:h5App` 流程要么用不同的 entry 机制（core-render-web 运行时按 `page_name` 注册），要么有我没复现的配置。**不能从 `:demo` 的失败推出「H5 整体不可用」——这是我之前的错误。**

## 判定（修订）

| 层 | 状态 |
|---|---|
| GearUI 组件库 → JS 编译 | ✅ VERIFIED |
| Kuikly compose 运行时 → JS klib | ✅ 已发布 |
| KuiklyUI 官方 H5（自渲染，`:shared`+`:h5App`） | ✅ 成熟（官方文档 + 用户确认） |
| **Compose DSL 页面经官方 H5 路径渲染** | ⏳ **未验证**（官方文档未明确；这是真正要做的 spike） |

## 真正的下一步（GPT 路线，已校准为官方路径）

按官方 quickstart 搭最小验证，**不用 `:demo`**：
1. 建 `sample` 的 `:shared`（或复用 sample）放一个最小 `@Page` + `ComposeContainer { setContent { Text("Hello GearUI") } }`
2. 建/修 `:h5App`（host）按官方 `core-render-web:base/h5` + `KuiklyWebRenderViewDelegator` 接入，`?page_name=` 加载
3. `./gradlew :shared:packLocalJsBundleDebug` + `:h5App:jsBrowserRun -t`
4. 浏览器确认第一帧
5. 成功 → 接 Button/Card/Input/Navigator；失败 → 拿到官方路径下的真实错误，再判断是否 compose-on-H5 缺口、给 KuiklyUI 提精确 issue

```
Web:
  compile target:        VERIFIED
  官方 H5（自渲染）:      成熟
  compose-on-H5:         未验证 → 待按 :shared+:h5App 官方模板做 spike
  roadmap:               v1.1 spike（不在 v1.0 RC）
```
