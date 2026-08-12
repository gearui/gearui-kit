# GearUI Sample — 鸿蒙宿主

[English](./README.md) | 简体中文

加载 sample 的 Kotlin/Native 共享库的 DevEco Studio 工程，改编自 KuiklyUI 仓库的 `ohosApp`。

> **状态：仅脚手架，从未构建过。** 这里的任何内容都没有被编译或运行过。编写它的机器上
> 没有鸿蒙工具链——没有 DevEco Studio，没有 hvigor / ohpm / hdc，也没有 OpenHarmony SDK——
> 因此 Kotlin 侧同样未经编译。下面所有步骤都应视为**预期形态**，而不是已验证的流程。

## 鸿蒙为什么需要独立构建

它无法作为常规构建的一个 target。带 `ohosArm64` 的 KuiklyUI 产物是基于
**Kotlin 2.0.21-KBA-010**（腾讯发行版）发布的，而主构建跑的是标准 Kotlin 2.1.21——
并且本项目使用的 `compose:2.25.0-2.1.21` **根本没有 `ohosArm64` 变体**。
KuiklyUI 自己用「按 Kotlin 版本维护并行构建文件」解决这个问题，本仓库照此办理：

```
settings.ohos.gradle.kts        把每个 project 指向 build.ohos.gradle.kts
build.ohos.gradle.kts           根：Kotlin 2.0.21-KBA-010、AGP 7.4.2
gearui-kit/build.ohos.gradle.kts
sample/build.ohos.gradle.kts    产出 libshared.so
```

常规的 Android / iOS / Web 构建完全不受影响，鸿蒙显式驱动：

```bash
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64
```

## 预期流程

1. 用上面的命令构建共享库。
2. 把产出的 `libshared.so` 拷进本工程的 `entry/libs/arm64-v8a/`。
3. 用 DevEco Studio 打开 `sample/ohosApp`，在设备或模拟器上运行。

`EntryAbilityStage.ets` 里调用的是 `setup("libshared.so", ...)`，所以在
`sample/build.ohos.gradle.kts` 里改二进制名字，也必须同步改那里。

## 相对 KuiklyUI 模板改了什么

- `bundleName` → `com.gearui.kit.sample`，vendor 与应用名同步。
- 默认页 `router` → `MainDemo`。模板的 `router` 页属于 KuiklyUI 自己的 demo；
  GearUI sample 只注册一个 Kuikly 页面 `@Page("MainDemo")`，76 个组件演示在 Compose 内部跳转。
- 删除 `publish.sh`——它发布的是 KuiklyUI 自己的 ohpm 包。保留 `copy_header.sh`：
  它从 `entry/oh_modules/@kuikly-open/render` 拷 C++ 头文件，本工程大概也需要，但尚未运行过。
- 未 vendor `dependencies/` 目录。模板里那是 20MB 的 hvigor 构建工具 tarball，应由 DevEco
  或 ohpm 提供；若需离线构建，可从 KuiklyUI 检出目录拷贝。

## 已知仍缺什么

以下来自阅读模板而非一次成功的构建，因此是**排查起点**而不是完整清单：

- `entry/libs/arm64-v8a/` 目录尚不存在，上面第 2 步没有落点。
- 模板 import 了 `libkuikly_entry.so` 与 `@kuiklybase/knoi`，它们通过 KuiklyUI 自身的
  ohos 依赖解析。究竟来自 ohpm 还是需要跨仓拷贝，尚未查清。
- `entry/src/main/ets/kuikly/` 下的 `CrashReport` 等文件属于 KuiklyUI，可能引用了这边
  不存在的东西。
- sample 的 Compose 界面能否在鸿蒙上渲染，完全未验证。Web 那次需要一处平台特定修复
  （宿主的 UMD wrapper）才跑通，鸿蒙还没有经过等价的排查。
