# GearUI Kit 架构

[English](./ARCHITECTURE.md) | [简体中文](./ARCHITECTURE.zh-Hans.md)

GearUI Kit 是构建在 Kuikly Compose 之上的 Kotlin Multiplatform UI 框架：一套
代码在 iOS、Android、Web（JS）和 HarmonyOS 上渲染为原生视图。以
`com.gearui:gearui-kit`（当前 `1.0.0-beta6`）发布，由 `privchat-ui` 等产品层
消费。

## 分层结构

```text
Application 层             View / GearApp —— 每棵页面树只有一个 App 根
Component 层               com.gearui.components.* —— 70+ 组件
Foundation 层              tokens / primitives / interaction / layout
Runtime 服务               Theme · I18n · Overlay · Navigator
─────────────────────────────────────────────────────────────
Kuikly Compose             渲染桥：每个 Compose 节点对应一个原生视图
                           （UIView / android.view / DOM / ArkUI）
```

每一层只依赖下层。组件不得自行挂载 Theme/Overlay 根；Runtime 服务不得伸手
进组件内部。

## Kuikly 集成方式

- **依赖方向。** GearUI 编译依赖 Kuikly 的已发布制品
  （`com.tencent.kuikly-open:*`，runtime、渲染器、KSP、iOS Pods 全部对齐
  2.28.0）。绝不修改或内嵌 Kuikly 源码；行为缺口要么在 GearUI 内规避，要么
  记录后反馈上游（模糊渲染的四项缺口见
  [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)）。
- **渲染模型。** Kuikly 把每个 Compose 节点映射为真实平台视图。GearUI 围绕
  三个后果做设计：`LocalIndication` 到不了视图层（按压反馈必须组件自持）；
  离屏重子树会被销毁并在主线程重建（pager 保活必须显式配置）；语义树由
  `KuiklySemantisHandler` 自动同步到原生视图。
- **事件与字段语义。** `safeAreaInsets`、尺寸变化、density 等字段保持
  Kuikly 原义。新能力必须带文档化的回退方案；绝不重新解释已有字段。

## 消费方如何引用 GearUI

| 消费方 | 机制 |
| --- | --- |
| `privchat-app` | `settings.gradle.kts` 中 `includeBuild("../gearui-kit")` + 依赖替换——kit、`privchat-ui`、`privchat-sdk-kotlin` 三个源码级组合构建 |
| 外部库 | Maven Central 制品 `com.gearui:gearui-kit:<version>`（Android AAR、JS、iOS arm64/simulatorArm64/x64 KLib） |
| HarmonyOS | `settings.ohos.gradle.kts` 并行配置；独立构建并要求显式真机证据 |

组合构建不能替代 Maven 消费验证：发布时还需用独立消费方夹具从暂存制品编译。

## 模块与目录

- `gearui-kit/` —— 库本体。源码在
  `src/commonMain/kotlin/com/gearui/`：`components/`（每组件一目录）、
  `foundation/`（tokens、primitives、interaction、layout、motion、material）、
  `theme/`、`i18n/`、`navigation/`、`runtime/`、`App.kt` / `View`。
- `sample/` —— 集成证据，不承载框架内部实现。单一 App 根、只用真实库组件、
  不允许 ComingSoon 占位。
- `tokens/` —— DTCG 2025.10 源 JSON、生成 Kotlin 与 token 契约
  （`tokens/README.md`）。
- `scripts/` —— token 生成器、图标生成器与 `scripts/ci/check_*.sh` 守卫。

## Runtime 契约

**单一 App 根。** 页面统一经 `GearApp` 进入；业务代码不得挂载第二套
Theme/Overlay 运行时。自行调用 `App(...)` 的 `View` 子类必须覆写
`autoWrapApp() = false`。

**Insets。** 宿主测量统一流经 `RuntimeEnvironment`——唯一的安全区来源。
`safeArea` 不含键盘；`keyboard` 是来自平台宿主回调的独立几何量。根容器保持
全尺寸；安全区 padding 属于内容（`PageScaffold`），绝不属于根画布。

**Overlay。** 一律经 Overlay 运行时创建/销毁，由其拥有遮罩、层级、
Back/点外/路由/超时关闭策略和"恰好一次"移除。面板局部手势只能请求宿主
关闭，不得复制全局监听。

**应用页面模型（必须）。** 基于 GearUI 的应用只有一个 Kuikly `@Page`，在
`App(...)` 里放一个 `Navigator`，每个屏幕是一个类型化的 `NavRoute` 条目
（参数放在路由里，如 `Chat(channelId)`），用 `push` / `pop` 跳转。不允许：
用 `when(currentPage)` 自己切屏幕、每个屏幕开一个 Kuikly 页面
（`RouterModule.openPage`）、自己写滑动返回。参考实现是 privchat-app 的
`PrivChatRouteHost` / `PrivChatNavGraph`。

这样做换来的是：

- 四端一致的跟手返回：Android、Web、HarmonyOS 没有系统级的滑动返回，iOS
  原生返回只能从屏幕边缘开始；`Navigator` 在四端都是全宽 1:1 跟手、露出上一页，
  并和页面内的横向分页器仲裁。
- 一套运行时：主题、语言、Overlay、会话状态都在同一棵组合树里，切主题时所有
  页面同时变化；Overlay 归属于打开它的条目，离开页面时一起关闭。
- 跳转零启动成本：不创建新的原生控制器 / Activity，也不重新启动 Kuikly 页面，
  参数是类型化对象而不是序列化的字符串。
- 返回键统一：Android BACK 先逐页出栈，到栈底才交给宿主。

代价与边界：栈顶页和紧挨在它下面的一页保持组合（后者隐藏驻留，拖动时直接
露出，不在手势中途重建）；更深的条目离开组合，状态经 `SaveableStateHolder` 和
`RetainedEntry` 保留。iOS 的返回手感由 GearUI 定义，不是系统的
`interactivePopGestureRecognizer`。深链接用启动时 `push` 对应路由实现，栈底
仍是首页。

**导航与滑动返回。** `Navigator` 是逐条目渲染的真栈。右滑手势按仲裁规则
保证只有一个所有者消费：

1. 按下时 router 通过 `PageSwipeBackGate` 询问前台页面。分页表面（如
   `TabPager`）在当前页不是第一页时回答"我还能滑"，router 完全放手。
2. 到了页面第一页——或页面没有横向分页——router 在 Initial pass 以全宽
   认领拖拽，卡片 1:1 跟手移动。
3. 栈底时 router 完全放手，pager 保留双向过滑张力。
4. `TabPager` 两侧各保活一页（`beyondViewportPageCount = 1`）；Kuikly 默认
   为 0 时，重页面重新入画要在拖拽中途付出约 180ms 的主线程原生视图重建。
5. 栈顶下面那一页常驻组合、隐藏（Parked）。它曾在手势被识别时才从头构建，
   真机上主线程卡约 450ms，页面这段时间不跟手；驻留后手势识别到下一帧约 12ms。
   驻留页不参与读屏，前台页拦住命中测试，点空白处不会穿透到它。

系统 BACK 由运行时桥接，不使用 `androidx.activity` 的处理器。

## 硬性约束（PR 门禁）

拒绝：重复的全局运行时所有权、根画布 inset 裁剪、隐性 API 破坏、未经审查
的基线变更、被静默忽略的 token 值、新增硬编码设计值、打包资源缺失、未验证
的兼容/一致性声明、导航槽位中 icon+badge 边界被裁剪、绕开 `Navigator` 自建页面切换或滑动返回。

需要证据：额外分配、平台分支、motion/blur 成本、新主题扩展点、新资源、新
平台目标。棘轮基线只冻结已知债务；通过棘轮不等于债务消失。

完整封装规则见 [COMPONENT_SPEC.zh-Hans.md](./COMPONENT_SPEC.zh-Hans.md)。

## 文档索引

| 文档 | 职责 |
| --- | --- |
| [ARCHITECTURE.zh-Hans.md](./ARCHITECTURE.zh-Hans.md) | 分层、Kuikly 集成、消费方式、运行时契约 |
| [DESIGN_SYSTEM.zh-Hans.md](./DESIGN_SYSTEM.zh-Hans.md) | Token 管线、主题轴、颜色/几何/字体/动效规则 |
| [I18N.zh-Hans.md](./I18N.zh-Hans.md) | 分层语言运行时与强类型语言包 |
| [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) | GearUI 视觉语言、结构、反馈、材质、无障碍 |
| [COMPONENT_SPEC.zh-Hans.md](./COMPONENT_SPEC.zh-Hans.md) | 组件封装、API 一致性、CI 门禁、评审护栏 |
| [COMPONENT_COVERAGE.zh-Hans.md](./COMPONENT_COVERAGE.zh-Hans.md) | 完整组件清单、对比 HeroUI Native 覆盖、缺口与优先级 |
| [QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md) | 1.0.0 目标、已验证证据、开放风险、发布流程 |
