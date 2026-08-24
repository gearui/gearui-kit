# GearUI Sample — 鸿蒙宿主

[English](./README.md) | 简体中文

加载 sample 的 Kotlin/Native 共享库的 DevEco Studio 工程，改编自 KuiklyUI 仓库的 `ohosApp`。

> **状态：能构建，尚未跑起来。** `entry-default-unsigned.hap`（64MB）已经全链路产出
> ——Kotlin/Native → CMake NAPI 胶水 → ArkTS → HAP——里面 `libshared.so` 与
> `libkuikly_entry.so` 都在。**没做到的是装上去并启动**：那需要模拟器镜像和已签名的 HAP，
> 两者都要华为开发者账号（见[运行](#运行)）。所以 Compose 界面在鸿蒙上**从未被看见渲染过**。

## 鸿蒙为什么需要独立构建

它无法作为常规构建的一个 target。带 `ohosArm64` 的 KuiklyUI 产物是基于
**Kotlin 2.0.21-KBA-010**（腾讯发行版）发布的，而主构建跑的是标准 Kotlin 2.1.21——
并且本项目使用的 `compose:2.25.0-2.1.21` **根本没有 `ohosArm64` 变体**。
KuiklyUI 自己用「按 Kotlin 版本维护并行构建文件」解决这个问题，本仓库照此办理：

```
settings.ohos.gradle.kts        把每个 project 指向 build.ohos.gradle.kts
build.ohos.gradle.kts           根：Kotlin 2.0.21-KBA-010、AGP 7.4.2、KSP 2.0.21-1.0.27
gearui-kit/build.ohos.gradle.kts
sample/build.ohos.gradle.kts    产出 libshared.so
```

常规的 Android / iOS / Web 构建完全不受影响，鸿蒙显式驱动。

sample 源码需要的任何东西，**两个构建文件里都得有**。这正是两者漂移的方式——所以生成
`SampleBuildInfo` 的逻辑放在 `gradle/sample-build-info.gradle.kts`，两边各自 apply。

## 构建

```bash
# 1. Kotlin/Native 共享库（在仓库根目录）
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64

# 2. 把 ArkTS 宿主需要的两个产物拷过去
cp sample/build/bin/ohosArm64/sharedDebugShared/libshared.so \
   sample/ohosApp/entry/libs/arm64-v8a/
cp sample/build/bin/ohosArm64/sharedDebugShared/libshared_api.h \
   sample/ohosApp/entry/src/main/cpp/thirdparty/biz_entry/

# 3. 依赖与 HAP
cd sample/ohosApp
export DEVECO_SDK_HOME=/Applications/DevEco-Studio.app/Contents/sdk
export PATH="/Applications/DevEco-Studio.app/Contents/tools/node/bin:/Applications/DevEco-Studio.app/Contents/tools/ohpm/bin:$PATH"
ohpm install --all
/Applications/DevEco-Studio.app/Contents/tools/hvigor/bin/hvigorw \
  --mode module -p product=default -p module=entry@default assembleHap --no-daemon
```

产物：`entry/build/default/outputs/default/entry-default-unsigned.hap`。

或者直接用 DevEco Studio 打开 `sample/ohosApp`，第 3 步它全包了。

DevEco Studio 26.0 **自带 SDK**，在
`/Applications/DevEco-Studio.app/Contents/sdk`（API 26）。不需要经 SDK Manager 下载；
`~/Library/Huawei/Sdk` 下只有一个 `productConfig.json` 是正常的，不是装坏了。

## 运行

还差两样，都需要华为开发者账号：

1. **模拟器镜像。** 模拟器引擎随 DevEco 提供（`Contents/tools/emulator`），但没有系统镜像。
   在 Device Manager 里创建设备需要登录、同意模拟器协议、下载数 GB 的镜像。
2. **已签名的 HAP。** 构建产出的是**未签名**包，鸿蒙不会安装。调试签名需要一份
   provisioning profile，其 `debug-info.device-ids` 要列出目标设备的 UDID——所以必须先有
   模拟器，而 profile 由 DevEco 登录后的自动签名生成（`File → Project Structure →
   Signing Configs → Automatically generate signature`）。

两样齐了之后：

```bash
hdc list targets                      # 确认模拟器已连接
hdc install -r entry-default-signed.hap
hdc shell aa start -a EntryAbility -b com.gearui.kit.sample
```

`hdc` 在 `Contents/sdk/default/openharmony/toolchains/hdc`。

## ohpm 下载失败怎么办

症状——元数据能取，每个包的下载全挂：

```
ohpm WARN: ECONNRESET fetch package @kuikly-open/render ... failed,
errMsg: Client network socket disconnected before secure TLS connection was established
```

**这不是 registry 故障。** ohpm 把 `.har` 下载重定向到华为 CDN
（`contentcenter-drcn.dbankcdn.cn`，背后是 CloudFront），而本机代理的 fake-IP 模式把该域名
解析成 `198.18.x.x` 然后切断 TLS 握手。DNS 正确时直连本身完全没问题。

**正确的修法在代理侧**——把 `ohpm.openharmony.cn` 与 `*.dbankcdn.cn` 走直连，或关掉
fake-IP。如果改不了，用 `./fetch-deps.sh`：它通过 DoH 解析 CDN 真实地址，用
`curl --resolve` 钉住，再从下载好的文件安装。脚本结束会把 manifest 还原，保证提交进仓库的
版本仍是 registry 版本。

## 相对 KuiklyUI 模板改了什么

- `bundleName` → `com.gearui.kit.sample`，vendor 与应用名同步。
- 默认页 `router` → `MainDemo`。模板的 `router` 页属于 KuiklyUI 自己的 demo；
  GearUI sample 只注册一个 Kuikly 页面 `@Page("MainDemo")`，76 个组件演示在 Compose 内部跳转。
- `@kuikly-open/render` 改为从 **ohpm registry** 取（`^2.25.0`），不再指向此处不存在的
  `file:../../core-render-ohos` 同级 checkout。`build-profile.json5` 里的 `render` 模块一并去掉。
- **删掉了 `entry/hvigorfile.ts` 里的 CDN 下载。** 模板在 `libshared.so` 缺失时会从腾讯 CDN
  拉一个预编译的 so。那对他们的 demo 是对的，在这里很危险：下载的是**他们 demo 的二进制**，
  于是本地没构建时会静默产出一个能启动、能渲染、但根本不是本 sample 的 App。现在改为直接
  报错并给出构建命令。
- **修了同一文件里的资源路径。** 它从 `../../demo/src/commonMain/assets` 拷（KuiklyUI 的目录
  约定）。现在同时取 gearui-kit 的图标和 sample 自己的文件，与 iOS 的 pod 资源同步、Web 的
  bundle 打包保持一致。
- **删掉 `CrashReport`。** 它 import 了本项目没有的 `../../ts-api/provider`，而且那是 Bugly
  崩溃上报，sample 没有理由带。`bugly` 依赖一并移除。
- 删除 `publish.sh`——它发布的是 KuiklyUI 自己的 ohpm 包。
- `copy_header.sh` 保留但**用不上**：它按 `src/main/cpp` 找头文件，而发布包把头文件放在
  `include/Kuikly`，`find_package(render)` 自己就能定位。
- 未 vendor `dependencies/` 目录。模板里那是 20MB 的 hvigor 构建工具 tarball，应由 DevEco
  或 ohpm 提供。

## 已知缺口

- App 从未启动过，所以鸿蒙上**关于 UI 的一切都未经验证**。Web 那次需要一处平台特定修复
  （宿主的 UMD wrapper）才跑通，鸿蒙还没有经过等价的排查。
- HAP 有 64MB，因为 `libshared.so` 是带完整调试信息的 57MB debug 产物。release 构建
  （`linkSharedReleaseSharedOhosArm64`）尚未尝试。
