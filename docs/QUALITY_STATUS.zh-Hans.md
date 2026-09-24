# GearUI 质量状态与发布进展

[English](./QUALITY_STATUS.md) | [简体中文](./QUALITY_STATUS.zh-Hans.md)

对照 1.0.0 目标的当前位置：哪些已验证、由哪个门禁验证、哪些仍然开放。
当前版本：`1.0.0-beta4`，2026-09-25 从 tag `v1.0.0-beta4` 发布至
Maven Central。

## 1. 1.0.0 目标

1. **冻结的公开 API。** Token 与组件 API 在 `binary-compatibility-validator`
   下保持稳定；破坏性变更必须附迁移说明。1.0 前的 token API 整合已完成；
   下游消费方（`privchat-ui`、`live-chat`、`lms-app`）已针对冻结形态重新
   编译。
2. **通过验收的、符合 GearUI 自己的
   [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) 的视觉一致性**（iOS 与
   Android），记录在案的偏差是唯一的差异。
3. **通过验收的无障碍**：名称/状态/角色语义用读屏器验证，而不只是
   `idb ui describe-all`。
4. **实测性能**对照预算：Android TTI ≤ 1200 ms、iOS ≤ 1000 ms、
   Web ≤ 1800 ms、滚动掉帧 < 3%、主题切换 ≤ 120 ms——并记录硬件、
   负载与方法。预算是目标，尚不是实测成绩。
5. **四平台证据**：Web 交互验收与 HarmonyOS 真机通过，各自独立于编译。
6. **按计划关闭组件缺口**：[COMPONENT_COVERAGE.zh-Hans.md](./COMPONENT_COVERAGE.zh-Hans.md)
   中的 P2 清单、日期格式国际化、React Aria 风格的无障碍状态模型。

## 2. 当前已验证

`68fc1a4`（2026-09-20，即已发布的 beta3 线）上的本地门禁：

| 门禁 | 结果 |
| --- | --- |
| Android Kotlin 测试 | 198 通过 + lint + apiCheck |
| Chrome Kotlin 测试 | 197 通过（headless） |
| iOS 模拟器 Kotlin 测试 | 197，经 `scripts/ios_native_tests.sh` 链接真实 Kuikly 宿主 |
| Token 编译器测试 | 119 通过；生成默认值与源码一致 |
| Shell 守卫 | 全部 `scripts/ci/check_*.sh` 通过 |
| Sample 构建 | Android / Web / iOS 模拟器构建、安装、启动 |
| Maven staging | 六个模块；AAR 含 97 个图标资源；独立制品消费方编译通过（Android、JS、三个 iOS target） |
| 消费方构建 | privchat-app 的 Android 与 iOS 针对 kit 编译通过 |

真机验收（2026-09-18，iPhone 17 Pro 模拟器 + Android 真机）：关键的
键盘/浮层/主题清单端到端跑完；发现并修复四个缺陷（含一个 iOS
ContextMenu 崩溃）。组件级真机通过：BottomSheet（遮罩/抓手/BACK/
边缘滑动关闭、主题切换）、Dialog（BACK/边缘滑动/点外默认不关闭
ConfirmDialog）、Drawer、ContextMenu、Input（输入、IME 动作、原生
maxLength、暗色主题、Android 字体缩放 1.3）、Popover、Select（开合）。

手势仲裁（2026-09-22，Weey 生产构建，Android 真机）：
Navigator/TabPager 侧滑返回契约——页面优先仲裁、全宽 1:1 卡片跟手、
栈底双向 overscroll 张力、拖动无反向滚动顿挫——逐帧在真机验证。

组件清单中的其余部分**仅静态扫描**：源码已审查、守卫已通过，未经真机
验收。源码扫描不是视觉或行为通过；逐组件验收对照必需检查项跟踪
（默认/长按/空态/大数据、按压/禁用/加载/焦点、亮/暗/强调色/形状实时
切换、浮层生命周期、逐平台证据）。

## 3. 开放风险与限制

- **读屏器与焦点遍历**：全局未验收。语义已实现且可检查，但没有记录过
  读屏器通过。
- **性能**：尚无实测数字；nightly 性能自动化未实现。
- **Web**：开发构建通过；实时 viewport-resize 行为与完整浏览器交互验收
  仍开放。
- **HarmonyOS**：没有可连接的真机做视觉验收；仅有构建证据。
- **渲染器限制**：圆角↔直角热切换的原生边框刷新不生效；模糊因四个上游
  缺口默认关闭（[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) §5）；
  iOS Dynamic Type 未验证。
- **从 beta3 携带的已知产品级决策**：原生字号刻度未定、iOS 密码可见
  切换、Kuikly 文本框焦点偶发串扰。
- **RTL**：类型化语言包已有；布局方向未验收。
- 完整的、对照 [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) 的像素/
  动效一致性作为整体尚未验收；记录在案的偏差是文档化的差异，其余仍需
  逐组件视觉通过。

## 4. 发布流程

候选门禁——未提交源码上的本地成功不算批准：

1. 提交完整的候选（token 源码、生成代码、测试、API 基线、迁移说明、
   资源）。绝不发布部分跟踪的树。
2. 运行 [COMPONENT_SPEC.zh-Hans.md §8](./COMPONENT_SPEC.zh-Hans.md) 的
   全部检查，并要求该确切提交上的远程 CI 绿灯。生成 API dump 不等于
   校验它。
3. 检查受支持的消费方、完整 iOS 宿主、Android/Web sample 与关键的
   键盘/浮层/主题路径；显式记录未测试的目标。
4. 在 macOS 上 stage 全部六个 Maven 模块；检查资源、元数据、KLib 与
   sources。composite 源码构建不能测试 Maven 消费。
5. 审查破坏性变更与渲染器限制，获得批准，然后设定版本/tag 并对该确切
   候选签名上传。

发布（Central Portal，经 `com.vanniktech.maven.publish`）：

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<token_name>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<token_secret>
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_gpg_private_key>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<passphrase>
./gradlew :gearui-kit:publishToMavenCentral
```

必须在 macOS 上发布——三个 iOS target 在别处构建不出来，在 Linux 上它们
会被静默地从上传中缺失。除非明确要用特定子密钥，否则不要设置
`signingInMemoryKeyId`；在那里放主密钥 id 会让所有签名任务以
"no configured signatory" 失败。以 Portal 为准而不是相信 Gradle 的退出
码：beta1 的 `publishToMavenCentral` 报了 BUILD SUCCESSFUL 却没有上传
（bundle 只能直接 POST 到 Portal API，`publishingType=USER_MANAGED`，
它会停在 VALIDATED，让最终发布保持为人工决策）。仅本地 staging：

```bash
./gradlew :gearui-kit:publishToMavenLocal \
  -Dmaven.repo.local=/tmp/gearui-staging \
  -PPOM_VERSION=<version> -PsigningInMemoryKey=
```

Beta 限制必须披露；已知的崩溃/数据丢失/输入阻塞回归不是可接受的 beta
注意事项。

## 5. 复现门禁

```bash
./gradlew :gearui-kit:cleanTestDebugUnitTest :gearui-kit:testDebugUnitTest \
  :gearui-kit:apiCheck :gearui-kit:lintDebug :sample:assembleDebug \
  :sample:jsApp:jsBrowserDevelopmentWebpack
CHROME_BIN='/Applications/Google Chrome.app/Contents/MacOS/Google Chrome' \
  ./gradlew :gearui-kit:jsBrowserTest
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/generate_tokens.py --check
for check in scripts/ci/check_*.sh; do bash "$check" || exit; done
bash scripts/ios_native_tests.sh   # needs -PgearuiIosTestHostDir host
```

## 6. 维护

门禁重跑或组件获得真机验收时更新 §2；发现新限制时把条目移入 §3，移出
§3 必须有记录在案的证据。过期的里程碑报告直接删除，不堆积——git 历史
就是档案。
