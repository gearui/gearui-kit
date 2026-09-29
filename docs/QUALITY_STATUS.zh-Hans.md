# GearUI 质量状态与发布进展

[English](./QUALITY_STATUS.md) | [简体中文](./QUALITY_STATUS.zh-Hans.md)

对照 1.0.0 目标的当前位置：哪些已验证、由哪个门禁验证、哪些仍然开放。
当前版本：`1.0.0-beta6`，2026-09-28 从 tag `v1.0.0-beta6` 发布至
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

**性能**（2026-09-28，sample 性能基准页，`scripts/perf/`）。

Android — Xiaomi 12 Pro（2201122G），Android 16，120 Hz，`benchmark` 构建类型
（release 代码、不可调试）：

| 指标 | 结果 | 预算 |
| --- | --- | --- |
| 冷启动到首页内容（应用内打点：进程启动 → 首页列表第一帧），5 次冷启动 | 中位 310 ms（306–357）：到页面 60，页面 → 内容 250 | ≤ 1200 ms |
| Activity 首帧，`am start -W -S` TotalTime，10 次冷启动 | 中位 297 ms（293–304） | — |
| 切换主题，整个 App（本页约 200 个组件）连切 20 次（状态改变 → 其后第二帧） | 中位 36.9 ms，P90 44.5，最大 51.1 | ≤ 120 ms |
| 1000 行 `List` 快速滑动 5 秒，`dumpsys gfxinfo` | 496 帧，卡顿 0.20 %，P50 8 ms，P99 11 ms | < 3 % |
| 同样的滑动，应用内帧间隔 | 589 帧，帧间隔 8.2 ms，卡顿 1.3 % | < 3 % |

同一台设备上 **debuggable** 包首帧 1496 ms、切主题 125 ms——启动慢了五倍。可调试包会关闭
ART 的优化，测的是调试器而不是 kit。请测 `benchmark` 构建。

iOS 26.2 模拟器，Release 包（回归基线——模拟器跑在 Mac 的 CPU 上）：冷启动到首页内容
中位 1030 ms——到页面 764，页面 → 内容 270；切主题中位 36.8 ms；滑动 300 帧、帧间隔
16.6 ms、卡顿 0.0 %。kit 占的那段（页面 → 内容）两端几乎相同（250–270 ms）。iOS 多出来的
部分都在页面创建之前：模拟器上加载 81 MB 静态链接二进制、宿主与 Kuikly 的启动。要在 iOS
真机上测过才能对照 1000 ms 预算下结论。

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

## 7. beta7 发布门禁（2026-09-29 立项）

beta7 不是「修几个 bug 再发」。§1 目标、§3 开放风险、[COMPONENT_COVERAGE.zh-Hans.md](./COMPONENT_COVERAGE.zh-Hans.md)
§3 缺口、[ANT_DESIGN_RN_COMPARISON.zh-Hans.md](./ANT_DESIGN_RN_COMPARISON.zh-Hans.md) §5/§9 的建议，全部并入本门禁。
每一项以证据判定完成：单测、sample 路径、真机截图或日志、脚本输出，逐项写进 §2。编译通过不算任何一项的验收。
做不到的项写明缺什么（设备、上游、决策），标「外部阻断」，发布前由维护者逐条放行或推迟，不能静默略过。

状态：☐ 未开始 · ◐ 进行中 · ☑ 完成（附证据） · ⛔ 外部阻断

### A. 发布卫生

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| A1 | 推送主线；候选提交上远程 CI 全绿（含 iOS job） | CI run 链接与各 job 结论 | ☐ |
| A2 | 下游迁移：privchat-ui、live-chat、lms-app 对候选提交编译通过；privchat-app Android/iOS 编译 | 各仓迁移提交与构建日志 | ☐ |
| A3 | Ant 对标文档入库，作为 B 组能力项来源 | 提交 | ☑ ca32b70；§9.1 实施状态随 1abe521 后续提交补充 |

### B. 能力补强（来源：Ant 对标 §5、§9）

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| B1 | 协议同意行：Text 支持内联可点链接片段；默认不勾选的 Checkbox；点链接只打开链接、不勾选；读屏可达 | 单测 + sample 页 + 双端真机点击验证 | ☑ `LinkPiecesTest` 7 例；sample `agreement`；iOS 与 Android 真机点击：点《用户协议》只弹出打开、勾选不变，点其余文字切换勾选；iOS 读屏：勾选框读整句加状态，每个链接为独立按钮。注：Kuikly 不分发 `LinkAnnotation` 点击、不给字符位置，故 `LinkedText` 逐字排版 |
| B2 | LoadMore 列表尾：idle/loading/error/exhausted，停留底部不连发，失败可重试，文案进语言包 | 状态单测 + sample 订单列表 + 真机滚动 | ☑ `LoadMoreTest` 6 例（仅空闲且尾部可见才请求、加载中不重发、失败等用户、追加页推出尾部不连发）；sample `loadmore`；iOS 截图失败态；Android 真机点重试后第 3 页加载 |
| B3 | Input 格式化层：原始值/显示值/光标映射分离；手机号 3-4-4、银行卡四位分组、身份证末位 X | 纯函数单测 + 真机粘贴、中间插删、中文输入法 | ◐ `InputFormatTest` 11 例（分组、+86/0086 粘贴、退格删分隔符、中间插入光标、前导零、身份证 X 与校验位）；iOS 逐字输入 11 位完整。待测：真机粘贴与中文输入法下的中间插删。已知限制：Kuikly 异步桥下脚本级连击会丢字，人手输入、粘贴、自动填充不受影响 |
| B4 | Calendar 周起始由语言包决定，简体/繁体中文默认周一 | 单测 + 截图 | ◐ `FormatStringsTest`；iOS 日历截图周一起始。与本条不一致：繁体中文按台湾惯例设为周日（`Strings.zh_Hant.format.firstDayOfWeek = 0`），待维护者决定 |
| B5 | 数字缩写（万/亿，随语言包）与中文相对时间，放在 i18n 格式化层 | 单测（边界值、四语言） | ☑ `FormatStringsTest`（万/亿边界、向下取整、负数、K/M/B、相对时间六档含跨年与时钟超前）；语言包现为简体、繁体、英文三种，没有第四种 |
| B6 | Picker 稳定 ID 与 label 分离；Cascader 区分叶子/未加载/加载中/失败/空；地址三级联动示例 | 单测（同名节点、改父项、过期结果丢弃）+ 真机 | ◐ Cascader 已完成：`CascaderLevelTest` 6 例（叶子、未加载/加载中/失败、同名节点按 value 区分、已加载层标签）；iOS 真机选到区回填「浙江省 / 杭州市 / 西湖区」、异步首次失败后重试加载城市。未做：Picker 稳定 ID 与 label 分离 |
| B7 | DatePicker 最小/最大日期、年/月/日与时间精度、分钟步长与过滤 | 单测（闰日、月末、跨年、过滤后空列）+ 真机 | ☐ |
| B8 | Form：类型化字段值、dirty/touched/validating、触发策略、异步校验版本保护、服务端字段错误注入 | 单测（慢请求不覆盖新值、离开组合不写回）+ sample 表单 | ☐ |
| B9 | NumberField 精确十进制（对称 parser/formatter、暂态输入）；Stepper `disableInput` 核实并补齐 | 单测 + 真机 | ☐ |

### C. 组件覆盖（来源：COMPONENT_COVERAGE §3）

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| C1 | P2：独立 ListBox、YearPicker、日期/时间分段字段与本地化格式、Toolbar、SubMenu、IndexBar | 每个组件：API 基线、单测、sample 页、双端真机截图 | ◐ IndexBar 与本地化格式（万亿、相对时间、农历）已完成并有 sample 页；ListBox、YearPicker、日期/时间分段字段、Toolbar、SubMenu 未做 |
| C2 | P3：Kbd、ColorPicker 家族、Meter、User、Code/Snippet | 同上 | ☐ |
| C3 | 注册表、README 索引、COMPONENT_COVERAGE、语言包、API 基线同步 | 守卫全绿 | ☐ |

### D. 运行时验收

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| D1 | 读屏：Android TalkBack 真机逐页遍历，记录朗读文本；iOS 以 XCUITest `performAccessibilityAudit` 审计全部页，关键家族 VoiceOver 真机抽检 | TalkBack 朗读日志、审计报告、问题清单与修复提交 | ☐ |
| D2 | 视觉：全部 sample 页 × 亮/暗 × Android 真机与 iOS 截图归档，对照 VISUAL_SPEC 逐页判定 | 截图目录 + 逐页结论 + 偏差登记 | ☐ |
| D3 | 大字号/适老化：Android 字号 1.3 与最大、iOS 最大动态字号，全部页截断/溢出 | 截图 + 问题清单与修复 | ☐ |
| D4 | RTL：Android 强制 RTL 布局方向逐页检查 | 截图 + 问题清单 | ☐ |
| D5 | 性能：iOS 真机（iPhone 16 Pro Max）冷启动、切主题、滚动，对照 §1 预算 | `scripts/perf` 输出 | ☐ |
| D6 | Web：浏览器自动化逐页交互与 viewport 缩放 | 脚本与结果 | ☐ |
| D7 | HarmonyOS：DevEco 模拟器运行 sample 全部页；真机验收 | 模拟器截图；真机为外部阻断 | ☐ |
| D8 | 国产 ROM 矩阵：小米已有；华为、OPPO、vivo | 各机型截图 | ⛔ 缺设备 |

### E. 已知限制处置

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| E1 | 圆角↔直角热切换原生边框不刷新：kit 侧能规避则修，否则登记并附最小复现 | 修复提交或复现工程 | ☐ |
| E2 | 模糊的四个上游缺口：整理成可直接提交的 Kuikly issue | issue 草稿；提交由维护者决定 | ☐ |
| E3 | beta3 携带决策：原生字号刻度、iOS 密码可见切换、Kuikly 文本框焦点串扰——给出决策与实现，或登记为已知限制 | 决策记录与提交 | ☐ |

### F. 发布

F1：[COMPONENT_SPEC.zh-Hans.md §8](./COMPONENT_SPEC.zh-Hans.md) 全部门禁与远程 CI 在确切候选提交上通过；六模块 staging 检查；
发布到 Central 并以 Portal 核对；tag `v1.0.0-beta7`；官网同步；下游切到 beta7。A–E 全部 ☑，或 ⛔ 项经维护者逐条放行。
