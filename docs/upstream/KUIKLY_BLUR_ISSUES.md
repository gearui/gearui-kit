# KuiklyUI 高斯模糊：四个上游 issue 草稿

> Gate E2（[QUALITY_STATUS.zh-Hans.md §7 E](../QUALITY_STATUS.zh-Hans.md)）。本文件是**可直接提交**的
> issue 草稿，提交与否由维护者决定。上游仓库：`Tencent-TDS/KuiklyUI`。

## 核对基线

- GearUI 依赖：Kuikly `2.28.0`（`gradle.properties:49` `KUIKLY_VERSION=2.28.0`）。
- 所有 KuiklyUI `file:line` 引用都对 **tag `2.28.0`（`c9c1c743`，2026-09-15）** 核对过
  （`git show 2.28.0:<path>`）。本地 checkout 的工作树停在 `2.27.0-2-g8068a4f3`，但下列模糊相关文件在
  两者之间**没有差异**。
- 更早的内部记录（commit `600236b` 中的 `docs/UPSTREAM_KUIKLYUI_BLUR.md`，基于 2.27.0）有一处与
  2.28.0 源码不符，已在此更正：**鸿蒙不是 ×1，而是 ×15**（`KRBlurView.ets:71`）。
  另外新增两点：Android API < 31 路径还有一次独立饱和，Web 端 `blurRadius(0)` 关不掉模糊。
- 验证状态：Android 16 / API 36 真机实测过 GearUI 的模糊开/关（见 VISUAL_SPEC §5）。
  **Issue 2 的暗色观感与 Issue 3 的饱和在 iOS 上都只由读源码得出，没有做设备截图确认**；
  提交前建议用 GearUI sample 的 Material Probe 页（路由 `runtime-material`）各截一张图附上。

## 四项一览

| # | 标题 | 严重度（跨端 UI 库视角） | 根因位置（2.28.0） |
| --- | --- | --- | --- |
| 1 | `blurRadius` 在每个渲染器上换算不同 | 阻断 | `KRBlurView.kt:127`/`:153`（Android）、`KRBlurView.m:84`（iOS）、`KRBlurView.ets:71`（鸿蒙）、Web `KRBlurView.kt:24-35` |
| 2 | iOS/macOS 写死 Light 材质，无暗色 | 阻断 | `core-render-ios/Extension/AdvancedComps/KRBlurView.m:33`、`:82` |
| 3 | iOS 半径 ≥ 10 全部等效（饱和） | 高 | `KRBlurView.m:84` + `core/.../BlurView.kt:50` |
| 4 | Compose 层没有可用的 blur（`BlurEffect` 被静默丢弃） | 低（可绕过） | `compose/.../ui/platform/RenderNodeLayer.kt:276`、`compose/.../ui/graphics/RenderEffect.kt:65` |

1–3 是 GearUI 默认关闭毛玻璃（`RuntimeFlags.materialPolicy = MaterialPolicy.Never`）的原因；
上游修好 1–3 后 GearUI 只需把默认值改回 `Auto`。

路径缩写：
- `core/.../BlurView.kt` = `core/src/commonMain/kotlin/com/tencent/kuikly/core/views/BlurView.kt`
- Android `KRBlurView.kt` = `core-render-android/src/main/java/com/tencent/kuikly/core/render/android/expand/component/blur/KRBlurView.kt`（同目录还有 `RenderEffectBlur.kt`、`RenderScriptBlur.kt`）
- iOS `KRBlurView.m` = `core-render-ios/Extension/AdvancedComps/KRBlurView.m`
- 鸿蒙 `KRBlurView.ets` = `core-render-ohos/src/main/ets/components/KRBlurView.ets`
- Web `KRBlurView.kt` = `core-render-web/base/src/jsMain/kotlin/com/tencent/kuikly/core/render/web/expand/components/KRBlurView.kt`

## 共用最小复现

四个 issue 共用同一段 Kuikly Compose 代码：条纹背景上并排放四块不同半径的 `BlurView`。

```kotlin
import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.extension.MakeKuiklyComposeNode
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.views.BlurView

@Composable
fun BlurRadiusProbe(dark: Boolean) {
    Box(Modifier.fillMaxSize().background(if (dark) Color.Black else Color.White)) {
        // Backdrop: 2dp stripes, so the blur strength is readable from a screenshot.
        Column(Modifier.fillMaxSize()) {
            repeat(200) { i ->
                Box(Modifier.fillMaxWidth().height(2.dp)
                    .background(if (i % 2 == 0) Color.Red else Color.Blue))
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0f, 2f, 5f, 10f, 12.5f).forEach { r ->
                Box(Modifier.weight(1f).height(160.dp)) {
                    MakeKuiklyComposeNode<BlurView>(
                        factory = { BlurView() },
                        modifier = Modifier.matchParentSize(),
                        viewInit = { getViewAttr().blurRadius(r) },
                        viewUpdate = { it.getViewAttr().blurRadius(r) },
                    )
                    Text("r=$r", color = if (dark) Color.White else Color.Black)
                }
            }
        }
    }
}
```

在 Android（API ≥ 31 与 API < 31 各一台）、iOS、鸿蒙、Web 上分别以 `dark = false / true` 截图对比。

---

## Issue 1

**English title:** `[BlurView] blurRadius is converted differently by every renderer — no value means the same on two platforms`

**中文标题：** `[BlurView] blurRadius 在各渲染器上换算方式各不相同，同一个数值在任意两端含义都不一致`

### 环境

- KuiklyUI 2.28.0（Maven 产物，源码核对 tag `2.28.0`）
- Android（API 31+ 与 API < 31）、iOS、HarmonyOS、Web（Chrome）
- 使用方式：Kuikly Compose + `MakeKuiklyComposeNode<BlurView>`（与 DSL `Blur { attr { blurRadius(..) } }` 结果相同）

### 复现

运行上面的 `BlurRadiusProbe(dark = false)`，比较同一个 `r` 在各端的模糊强度。

### 期望

`BlurAttr.blurRadius` 有一个文档化的、与平台无关的单位（建议：以 dp/vp/CSS px 为单位的高斯半径），
同一数值在各端视觉强度大致一致；`blurRadius(0)` 表示不模糊。

### 实际

core 只做了一次截断，`BlurView.kt:48-51`：

```kotlin
/// 高斯模糊半径，最大为12.5f（默认：10f）
fun blurRadius(radius: Float) {
    "blurRadius" with min(radius, 12.5f)
}
```

之后每个渲染器各自换算：

| 渲染器 | 换算 | 源码 | `r = 10` 时的实际效果 |
| --- | --- | --- | --- |
| Android API ≥ 31 | `r × 2`，作用在**按 1/20 缩小**的位图上，再放大绘制 | `KRBlurView.kt:127`（`* 2f`）、`:153`（`SizeScaler(20f)`）、`:104-110`（放大绘制）、`RenderEffectBlur.kt:54` | 约 `r × 40` **物理像素**（3x 屏约 133dp） |
| Android API < 31 | 再 `× 1.5` 并钳到 25（位图像素） | `RenderScriptBlur.kt:51-55` | `r ≥ 8.33` 即饱和（约 500 物理像素）；与 API ≥ 31 **又不一样** |
| iOS | `r / 10` 作为 `UIViewPropertyAnimator.fractionComplete` | `KRBlurView.m:84` | 固定系统 Light 材质的 **100% 强度**（不是半径） |
| macOS | 分三档映射材质 | `KRBlurView.m:93-104` | `ContentBackground` 材质 |
| HarmonyOS | `150 × r / 10` = `r × 15` → `.backdropBlur()` | `KRBlurView.ets:71`、`:29` | 150（ArkUI `backdropBlur` 单位，按文档为 px） |
| Web | `r ≤ 0 → 1`，`r > 25 → 25`，再 `× 5` → `backdrop-filter: blur(Npx)` | Web `KRBlurView.kt:24-35` | 50 CSS px |

附带问题：

1. **未设置 `blurRadius` 时的默认值也各不相同**：Android 字段初值 `5f`（位图空间，`KRBlurView.kt:45`）；
   鸿蒙 `init` 里 `setBlurRadius(10)`（`KRBlurView.ets:39`）；iOS 不创建 animator，直接满强度 Light
   （`KRBlurView.m:33`）；Web 不设置 `backdrop-filter`。core 注释写的"默认 10f"只对鸿蒙成立。
2. **Web 端 `blurRadius(0)` 关不掉模糊**：`r ≤ 0` 被改成 `1f` 再 `× 5`，得到 5px 模糊（Web `KRBlurView.kt:26-27`）。
3. **Web 的 `> 25` 截断是死代码**：core 已在 12.5 截断（`BlurView.kt:50`）。
4. Android 两条路径（RenderEffect / RenderScript）强度不同，同一台 App 在 Android 12 前后观感不同。

结果：一个跨端设计系统找不到任何一个 `blurRadius` 值能在两端表达同一强度。

### 建议修复

为了不破坏现有业务，建议**新增**一个定义清晰的属性，旧 `blurRadius` 保持原行为：

```kotlin
// core/.../BlurView.kt
class BlurAttr : Attr() {
    /**
     * Backdrop Gaussian blur radius in density-independent units (dp / vp / CSS px).
     * 0 disables the blur. Renderers convert into their own space.
     */
    fun backdropBlurRadius(dp: Float) {
        "backdropBlurRadius" with max(0f, dp)
    }
}
```

各端换算：

- **Android**：`bitmapRadius = dp * density / SizeScaler.scaleFactor`（除以降采样倍数，而不是乘 2），
  放大绘制后屏幕上的半径才等于请求值；RenderScript 路径用同一公式，并在超过 25 时增大降采样倍数而不是钳值。
- **Web**：直接 `blur(${dp}px)`（CSS px 已是密度无关单位），`0` 时写 `none`。
- **鸿蒙**：`backdropBlur(vp2px(dp))`，或按 ArkUI 实际单位换算。
- **iOS**：`UIVisualEffectView` 公开 API 无法表达任意半径，见 Issue 3 的分档方案；需要在文档中注明 iOS 为分档近似。

同时修正 core 注释中"默认 10f"的描述，并让各端在未设置时采用同一个默认值。

---

## Issue 2

**English title:** `[BlurView][iOS] UIBlurEffectStyleLight is hard-coded — no dark / system-adaptive material`

**中文标题：** `[BlurView][iOS] 写死 UIBlurEffectStyleLight，暗色主题下只能得到亮色毛玻璃`

### 环境

- KuiklyUI 2.28.0；iOS（及 macOS）
- 系统外观为深色（Dark Mode），或 App 自带暗色主题

### 复现

运行 `BlurRadiusProbe(dark = true)`，iOS 系统切到深色模式。

### 期望

可以选择亮色 / 暗色 / 跟随系统的材质；至少"跟随系统"（`UIBlurEffectStyleSystem*Material`，iOS 13+ 会随
`userInterfaceStyle` 自动切换）。

### 实际

材质在两处写死为 Light，Kotlin 侧没有任何属性能改：

```objc
// KRBlurView.m:33  (initWithFrame:)
self.effect = [UIBlurEffect effectWithStyle:(UIBlurEffectStyleLight)];

// KRBlurView.m:82  (setCss_blurRadius:, 动画终点)
weakSelf.effect = [UIBlurEffect effectWithStyle:(UIBlurEffectStyleLight)];
```

macOS 同样偏向亮色：`KRBlurView.m:35` 初始 `NSVisualEffectMaterialLight`，`:94-104` 半径映射只会选
`Light` / `MediumLight` / `ContentBackground`。

另外 core 在 `BlurView.kt:35-39` 按平台写死了一层 10% 底色（iOS/macOS 黑、其他平台白），同样不随主题变化。

`UIBlurEffectStyleLight` 是不随系统外观变化的固定样式，所以深色界面上得到的是一块发白的面板。
（由源码直接得出；深色截图待附。）

### 建议修复

新增、向后兼容的属性：

```kotlin
// core/.../BlurView.kt
enum class BlurStyle { LIGHT, DARK, SYSTEM }   // SYSTEM follows the platform appearance

class BlurAttr : Attr() {
    fun blurStyle(style: BlurStyle) {
        "blurStyle" with style.ordinal
    }
}
```

| 渲染器 | 映射 |
| --- | --- |
| iOS | `UIBlurEffectStyleLight` / `Dark` / `SystemMaterial`（iOS 13+） |
| macOS | `NSVisualEffectMaterial` + `appearance` 跟随系统 |
| Android | 使用对应的亮/暗 tint 覆盖色 |
| 鸿蒙 | `.backgroundBlurStyle()` 已有亮/暗变体 |
| Web | `backdrop-filter` 搭配亮/暗 `background-color` |

`BlurView.willInit()` 里的固定底色也建议由 `blurStyle` 派生，或允许调用方覆盖。
推荐默认 `SYSTEM`，业务无需在切换主题时重新下发属性。

---

## Issue 3

**English title:** `[BlurView][iOS] blurRadius saturates at 10: fractionComplete = radius / 10 while core allows up to 12.5`

**中文标题：** `[BlurView][iOS] blurRadius ≥ 10 渲染结果完全相同（fractionComplete = radius / 10，而 core 允许到 12.5）`

### 环境

- KuiklyUI 2.28.0；iOS 10+（`UIViewPropertyAnimator` 路径）

### 复现

运行 `BlurRadiusProbe(dark = false)`，对比 iOS 上 `r = 10` 与 `r = 12.5` 两块。

### 期望

在允许的输入范围 `[0, 12.5]` 内，模糊强度单调增加；最大值可达。

### 实际

```objc
// KRBlurView.m:81-84
animator = [[UIViewPropertyAnimator alloc] initWithDuration:1 curve:(UIViewAnimationCurveLinear) animations:^{
    weakSelf.effect = [UIBlurEffect effectWithStyle:(UIBlurEffectStyleLight)];
}];
animator.fractionComplete = css_blurRadius.floatValue / 10;
```

`fractionComplete` 的取值域是 0–1，而 core 允许 `blurRadius` 到 12.5（`BlurView.kt:50`），
即可请求 `fractionComplete = 1.25`。因此 **10 到 12.5 之间的所有值在 iOS 上等效**，
而其他端在这一段仍在变化。GearUI 的 `Materials.Popover`（10）与 `Materials.Chrome`/`Sheet`（12.5）
本应是两档强度，在 iOS 上无法区分。（由源码推出；设备截图待附。）

同一方法里另外两点：

- 每次 `setCss_blurRadius:` 都新建 animator 并长期持有（`KRBlurView.m:74-85`），
  且在 `UIApplicationDidBecomeActiveNotification`（`:48-52`）和 `didMoveToWindow`（`:54-61`）时都会重建；
  `didMoveToWindow` 在 `window == nil` 时没有停止/释放 animator。
- 暂停中的 animator 若被系统推进到结束，效果会跳到满强度。

### 建议修复

`UIVisualEffectView` 用公开 API 无法表达任意半径，推荐二选一：

1. **（推荐）映射到系统命名材质**：`UIBlurEffectStyleSystemUltraThinMaterial` / `ThinMaterial` /
   `Material` / `ThickMaterial` / `ChromeMaterial`（iOS 13+）。无需 animator，天然支持深色（同时解决 Issue 2），
   文档注明 iOS 为分档近似。可以同时提供语义化 API，如 `blurMaterial(THIN | REGULAR | THICK)`，
   在鸿蒙映射到 ArkUI `BlurStyle`、Web 映射到固定 px。
2. 保留 animator，但按上限归一：`fractionComplete = radius / 12.5`，使整个输入范围可用；
   并在 `didMoveToWindow` 且 `window == nil` 时 `stopAnimation:YES` 释放 animator。

---

## Issue 4

**English title:** `[Compose] BlurEffect / graphicsLayer(renderEffect) is silently ignored; no backdrop-blur modifier`

**中文标题：** `[Compose] BlurEffect / graphicsLayer(renderEffect) 被静默忽略，且没有 backdrop 模糊的 Modifier`

### 环境

- KuiklyUI 2.28.0 Compose（`com.tencent.kuikly.compose`）；所有平台

### 复现

```kotlin
import com.tencent.kuikly.compose.ui.graphics.BlurEffect
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer

Text(
    "blurred?",
    modifier = Modifier.graphicsLayer { renderEffect = BlurEffect(8f, 8f) },
)
```

### 期望

Android 31+ / 鸿蒙 / Web 上文字被模糊；不支持的平台（iOS）通过 `RenderEffect.isSupported()` 如实返回 `false`，
并在文档中说明。另外希望有一个 backdrop 模糊的 Modifier，而不必把 `BlurView` 作为兄弟节点挂进来。

### 实际

- `BlurEffect` 类存在，但 `isSupported()` 恒为 `false`（`compose/src/commonMain/kotlin/com/tencent/kuikly/compose/ui/graphics/RenderEffect.kt:59-66`）。
- `RenderNodeLayer` 保存了 `renderEffect`（`compose/src/commonMain/kotlin/com/tencent/kuikly/compose/ui/platform/RenderNodeLayer.kt:195`），
  但在 `performDrawLayer` 里只有 `// todo renderEffect`（`:276`），不下发到原生 view。
  调用方没有任何报错或日志，效果直接消失。
- Backdrop 模糊只能用 `BlurView`（`ViewConst.TYPE_BLUR_VIEW`）这个叶子节点实现，
  不能包裹子内容；Compose 里只能用 `MakeKuiklyComposeNode<BlurView>` 作为兄弟节点叠在内容下面（GearUI 目前的做法）。

### 建议修复

1. **内容模糊（`BlurEffect`）**：在 `performDrawLayer` 的 `:276` 处下发一个通用属性，各端实现：
   Android 31+ `View.setRenderEffect(RenderEffect.createBlurEffect(..))`（`RenderEffectBlur.kt:54` 已有同类用法）；
   Web `style.filter = "blur(Npx)"`（`IImageAttr.blurRadius` 的 Web 实现已有先例）；鸿蒙通用属性 `.blur()`。
   iOS 公开 API 无法模糊任意 `UIView` 自身内容，应让 `isSupported()` 在 iOS 返回 `false` 并在文档中注明，而不是静默丢弃。
2. **Backdrop 模糊**：把 backdrop blur 提升为各渲染器公共属性层的通用属性（与 `opacity`、`boxShadow` 同级），
   从而可提供 `Modifier.backdropBlur(radius)`；ArkUI 与 CSS 本身都是这样建模的。所有端都有对应原语。

本项对 GearUI 不阻断（兄弟节点方案可用），作为易用性改进提交。

---

## Related finding (not a blur issue): RTL layout is never mirrored

Found during the beta7 RTL check (gate D4), Android device, Kuikly 2.28.0:

- Android's "Force RTL layout direction" developer setting does not reach Compose: every page lays out left to right.
- Providing `LocalLayoutDirection provides LayoutDirection.Rtl` at the root (with an Arabic language tag, confirmed by the kit's strings falling back to English) also leaves every Row, NavBar and list left to right, although `Placeable.placeAutoMirrored` (compose/.../ui/layout/Placeable.kt:446-460) and `LayoutNode.layoutDirection = value[LocalLayoutDirection]` (compose/.../ui/node/LayoutNode.kt:773) exist.

Needs a minimal repro (a Row of two Text in an RTL CompositionLocalProvider) on each platform before filing; the cause is somewhere between Compose placement and the native frame.

## Related finding: iOS single-line text is cut without an ellipsis

`Text(maxLines = 1, overflow = TextOverflow.Ellipsis)` (mapped to "tail" at
compose/.../foundation/text/KuiklyTextExtension.kt:287) is cut at a character on the
iOS simulator with no "…", while Android shows the ellipsis. Seen on the NavBar long
title (sample route `navbar`, "超长标题"). Needs a minimal repro before filing.

## Related finding: minimum touch target widening never applies

Kuikly keeps Compose's hit-test widening (`NodeCoordinator.hitTest` → `distanceInMinimumTouchTarget`,
`ViewConfiguration.minimumTouchTargetSize` defaults to 48 dp, touch events arrive as
`PointerType.Touch` from `SuperTouchManager`), yet on an Android device a tap 8 dp above or below
a 28 dp clickable chip does nothing (sample route `tag`, 2026-09-30). Every small control therefore
needs its own enlarged touch node. Needs a minimal repro (a 24 dp clickable Box, tap 10 dp outside).

## Related finding: iOS text fields ignore `autoCorrectEnabled`

`KeyboardOptions(autoCorrectEnabled = false)` has no effect on iOS (`KRTextFieldView`/`KRTextAreaView`
never set `autocorrectionType`). In a ComboBox, typing "usd" and tapping "美元 USD" sometimes ends with
the field reading "USD": the keyboard commits its correction after the app set the chosen value.
Filter and search fields need autocorrection off.
