# GearUI Kit

[English](./README.md) | [简体中文](./README.zh-Hans.md)

基于 Kuikly 构建的 Kotlin Multiplatform UI 组件库。

## 发布信息

- 坐标：`com.gearui:gearui-kit:1.0.0-beta1`
- 官网：[https://gearui.com](https://gearui.com)
- License：BSD 3-Clause License

## 快速接入

### 1. 发布版依赖（推荐）

如果版本已发布到远端仓库（如 Maven Central）：

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
export ORG_GRADLE_PROJECT_signingInMemoryKey='-----BEGIN PGP PRIVATE KEY BLOCK-----...'
export ORG_GRADLE_PROJECT_signingInMemoryKeyId=<short_key_id>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<GPG_口令>
```

然后执行发布：

```bash
# 上传到 Central Portal staging deployment；在 Portal 网页手动 close + release
./gradlew :gearui-kit:publishToMavenCentral

# 或上传并自动 release
./gradlew :gearui-kit:publishAndReleaseToMavenCentral
```

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

## 开发命令

```bash
# 编译 Android 目标
./gradlew :gearui-kit:compileDebugKotlinAndroid

# 编译 iOS 目标
./gradlew :gearui-kit:compileKotlinIosSimulatorArm64

# 运行示例应用（Android）
./gradlew :sample:installDebug
```

## 许可证

BSD 3-Clause License，详见 [LICENSE](./LICENSE)。
