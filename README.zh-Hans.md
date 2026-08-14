# GearUI Kit

[English](./README.md) | [简体中文](./README.zh-Hans.md)

基于 Kuikly 构建的 Kotlin Multiplatform UI 组件库。

## 发布信息

- 坐标：`com.gearui:gearui-kit:1.0.0-beta1`
- 2026-08-15 起可从 Maven Central 获取（首个公开版本）
- 支持平台：Android、iOS（arm64 / 模拟器 arm64 / x64）、JS（浏览器）
- 官网：[https://gearui.com](https://gearui.com)
- License：BSD 3-Clause License

## 作者信息

- 作者：`zoujiaqing`
- 邮箱：`zoujiaqing@gmail.com`

## 界面截图

在 iPhone 17 Pro 模拟器上运行 sample 截取。

| 首页（中文） | 首页（英文） | 设置页（亮色） | 设置页（暗色） |
| --- | --- | --- | --- |
| <img src="docs/screenshots/home-zh.png" width="220" alt="中文组件索引" /> | <img src="docs/screenshots/home-en.png" width="220" alt="英文组件索引" /> | <img src="docs/screenshots/settings-light.png" width="220" alt="设置页亮色主题" /> | <img src="docs/screenshots/settings-dark.png" width="220" alt="设置页暗色主题" /> |

语言与主题都在设置页运行时切换，所有组件自动跟随，无需逐屏适配。

## 快速接入

### 1. 发布版依赖（推荐）

已发布到 Maven Central。只需声明根坐标一行——Gradle 会读 module metadata，
按当前编译目标自动解析到对应平台包（`-android`、`-js`、`-iosarm64` …）。
那些带后缀的坐标不要手写。

```kotlin
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.gearui:gearui-kit:1.0.0-beta1")
        }
    }
}
```

### 2. 本地联调依赖（mavenLocal）

先在 `gearui-kit` 工程发布到本地仓库：

```bash
./gradlew :gearui-kit:publishToMavenLocal
```

然后在业务工程引入：

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.gearui:gearui-kit:1.0.0-beta1")
        }
    }
}
```

### 3. 同仓库模块依赖（开发期）

```kotlin
dependencies {
    implementation(project(":gearui-kit"))
}
```

### 4. 发布到 Maven Central（Central Portal）

发布通过 `com.vanniktech.maven.publish` 接入 Sonatype Central Portal。
凭证和签名密钥通过环境变量或 Gradle property 注入：

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<Central_Portal_Token_名称>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<Central_Portal_Token_密码>
# base64 单行：gpg --export-secret-keys <指纹> | base64 | tr -d '\n'
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_GPG_私钥>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<GPG_口令>
```

**不要**设置 `signingInMemoryKeyId`，除非你要指定某个 subkey；Gradle 只在 subkey
里查找，填主密钥 id 会让所有签名任务报 "no configured signatory"。
详见 `gearui-kit/build.gradle.kts` 里的说明。

**必须在 macOS 上发布**——三个 iOS target 只有 macOS 能编，在 Linux 上它们会从
上传内容里静默消失，而不是让构建失败。

```bash
./gradlew :gearui-kit:publishToMavenCentral
```

⚠️ 插件 0.30.0 下这条命令会**报 BUILD SUCCESSFUL 但什么都没上传**：它是个
lifecycle task（日志 `Skipping task ... as it has no actions`），产物只落到
`build/publish/staging/<uuid>/`。1.0.0-beta1 是直接把该 bundle POST 到 Portal 发的：

```bash
TOKEN=$(printf '%s:%s' "$USERNAME" "$PASSWORD" | base64)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "bundle=@build/publish/staging/<uuid>.zip" \
  "https://central.sonatype.com/api/v1/publisher/upload?name=com.gearui:gearui-kit:<版本>&publishingType=USER_MANAGED"
# -> 返回 deployment id，然后轮询：
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "https://central.sonatype.com/api/v1/publisher/status?id=<deployment_id>"
```

`USER_MANAGED` 会停在 VALIDATED，最后那下 Publish 保持由人来点。
永远以 Portal 的状态为准，不要相信 Gradle 的退出码。

## 基础使用

```kotlin
@Page("MainPage")
class MainPage : View() {
    @Composable
    override fun Content() {
        GearApp(themeMode = ThemeMode.System) {
            MainPageContent()
        }
    }
}

@Composable
private fun MainPageContent() {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        Button(
            text = I18n.strings.buttonConfirm,
            theme = ButtonTheme.PRIMARY,
            onClick = {}
        )
    }
}
```

## 平台支持

| 平台 | 库 | Sample | CI |
|---|---|---|---|
| Android | ✅ | ✅ | ✅ |
| iOS | ✅ | ✅ | ✅ |
| Web (H5) | ✅ | ✅ 76 个演示页中 75 个 | ✅ |
| 鸿蒙 | ⚠️ 仅脚手架 | ⚠️ 仅脚手架 | — |

Web 走 KuiklyUI 的 web 渲染器；唯一失败的 `Table` 是 sample 自身演示文件的
Kotlin/JS 部分链接错误，不在组件本身。详见
[sample/jsApp/README.md](./sample/jsApp/README.md)。

鸿蒙目前是**未经构建的脚手架**。它无法作为常规构建的一个 target：带 `ohosArm64`
的 KuiklyUI 产物是基于 Kotlin `2.0.21-KBA-010` 发布的，因此鸿蒙使用一套并行构建配置，
通过 `-c settings.ohos.gradle.kts` 显式驱动。哪些已验证、哪些没有，见
[sample/ohosApp/README.md](./sample/ohosApp/README.md)。

## 工程说明

- 组件层位于：`gearui-kit/src/commonMain/kotlin/com/gearui/components`
- 当前组件目录规模：`50+`（以源码为准）
- Sample 工程：`sample/`

## 组件收敛策略

- 导航类只保留核心入口：`Tabs`（内容切换）。
- 手风琴模式统一并入：`Collapse.Accordion`（不再单独维护 `Accordion` 组件）。
- 不保留同义包装组件，避免重复 API 和重复示例页面。

## 文档入口

- 架构总览：[ARCHITECTURE.md](./ARCHITECTURE.md)
- 规范入口：[docs/SPEC.md](./docs/SPEC.md)
- Web 宿主：[sample/jsApp/README.md](./sample/jsApp/README.md)
- 鸿蒙宿主：[sample/ohosApp/README.md](./sample/ohosApp/README.md)

文档以英文为主，`*.zh-Hans.md` 为对应中文版。**代码注释一律英文**——
执行该约定的检查见 [docs/SPEC_CI_MAPPING.md](./docs/SPEC_CI_MAPPING.md) 第 18 条。

## 开发命令

```bash
# 分平台编译库
./gradlew :gearui-kit:compileDebugKotlinAndroid
./gradlew :gearui-kit:compileKotlinIosSimulatorArm64
./gradlew :gearui-kit:compileKotlinJs

# 运行 sample
./gradlew :sample:installDebug                    # Android
./gradlew :sample:jsApp:jsBrowserDevelopmentRun   # Web，然后打开 http://localhost:8081/

# 鸿蒙走并行构建配置（尚未构建通过 —— 见 sample/ohosApp/README.md）
./gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64

# 架构护栏 —— 共 17 条，全部可本地运行
for f in scripts/ci/check_*.sh; do "$f"; done
```

## 许可证

BSD 3-Clause License，详见 [LICENSE](./LICENSE)。
