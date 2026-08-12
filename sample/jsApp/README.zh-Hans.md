# GearUI Sample — Web (H5) 宿主

[English](./README.md) | 简体中文

通过 KuiklyUI 的 web 渲染器在浏览器里运行 GearUI sample。改编自 KuiklyUI 仓库的官方
`h5App` 模板；`src/jsMain/kotlin` 下的宿主代码刻意与模板保持接近，便于把上游修复 diff 过来。

## 运行

```bash
# 在仓库根目录
./gradlew :sample:jsApp:jsBrowserDevelopmentRun
# 然后打开 http://localhost:8081/
```

就这一条命令。sample 的 JS bundle 和图标资源由 Gradle 任务（`copySampleJsBundle`、
`copySampleAssets`）暂存到宿主资源目录，**不需要手工拷任何文件**。

dev server 用 8081 而不是 webpack 默认的 8080——后者被占用的概率高到值得避开；
用 `-PwebPort=9000` 可以改。

只要产物、不起 dev server：

```bash
./gradlew :sample:jsApp:jsBrowserDevelopmentWebpack
# 把这些放到同一目录下提供静态服务：
#   sample/jsApp/build/processedResources/js/main/       index.html、gearui_sample.js、assets/
#   sample/jsApp/build/kotlin-webpack/js/developmentExecutable/jsApp.js
```

## 各部分如何拼接

- `:sample` 编译成 JS 产物 `gearui_sample.js`。它只包含 sample 注册的那**一个** Kuikly 页面
  `@Page("MainDemo")`；76 个组件演示之间的跳转发生在 Compose 内部，不走页面路由。
- `jsApp` 是宿主，负责启动 web 渲染器并挂载 `MainDemo`。
- 图标把 `assets://icons/<name>.png` 解析成 `/assets/icons/<name>.png`，所以资源必须和
  bundle 放在一起。

## 两个会咬人的点

**宿主不能带 UMD wrapper。** `webpack.config.d/output.js` 设置 `iife` 并清空 `library`。
没有它的话，kotlin-webpack 的 UMD 尾部会把宿主的顶层导出逐个挂到 `window`，而宿主里含有
core-render-web 的 `@JsExport` 文件、会产生一个 `com` 键——于是 `window.com` 被整体替换，
抹掉业务 bundle 刚刚装上的桥接函数。症状是 `registerCallNative error` 接着
`callNative is not defined`，看起来像缺依赖，实际是打包冲突。

**Compose 需要为该目标显式开启实验开关。** `gradle.properties` 里设了
`org.jetbrains.compose.experimental.jscanvas.enabled=true`；没有它构建会直接拒绝 JS 目标。

## 现状

76 个组件演示中 **75 个渲染正常且可交互**。`Table` 在运行期失败，报 Kotlin/JS 部分链接错误，
指向的是 `TableExample.kt`——sample 自身的演示文件（883 行、46 个 render lambda，是最大的
一个），而不是 Table 组件本身。**根因尚未查实**，在向上游提 issue 之前需要最小复现。

开发版 bundle 为 23MB，production 体积尚未测量。
