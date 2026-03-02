# GearUI Sample

## 多端启动命令

### Android

在 `gearui-kit` 根目录执行：

```bash
./gradlew :sample:installDebug
```

### iOS

先在 `gearui-kit/sample/iosApp` 安装 Pods：

```bash
cd sample/iosApp
pod install
```

然后回到 `gearui-kit` 根目录生成/同步 Framework：

```bash
cd ../..
./gradlew :sample:syncFramework \
  -Pkotlin.native.cocoapods.platform=iphonesimulator \
  -Pkotlin.native.cocoapods.archs=arm64 \
  -Pkotlin.native.cocoapods.configuration=Debug
```

最后用 Xcode 打开并运行：

```bash
open sample/iosApp/GearUISample.xcworkspace
```

### HarmonyOS (ohosApp)

在 `gearui-kit/sample/ohosApp` 目录执行：

```bash
cd sample/ohosApp
ohpm install --all --strict_ssl true
hvigorw assembleHap --mode project -p product=default -p buildMode=debug --no-daemon
```

如果需要 release 包：

```bash
hvigorw assembleHap --mode project -p product=default -p buildMode=release --no-daemon
```

### Web (jsApp)

在 `gearui-kit/sample/jsApp` 目录执行：

```bash
cd sample/jsApp
./gradlew jsBrowserDevelopmentRun
```

生产构建：

```bash
./gradlew jsBrowserProductionWebpack
```

产物目录：

```text
sample/jsApp/build/kotlin-webpack/js/productionExecutable
```

## 前置环境

1. Android：Android SDK + JDK 17
2. iOS：Xcode + CocoaPods
3. HarmonyOS：DevEco / OH SDK（需有 `ohpm`、`hvigorw`）
4. Web：Node.js（Gradle 会自动处理 Kotlin/JS 依赖）
