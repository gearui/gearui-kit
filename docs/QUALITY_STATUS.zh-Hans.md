# GearUI 质量状态与发布进展

[English](./QUALITY_STATUS.md) | [简体中文](./QUALITY_STATUS.zh-Hans.md)

对照 1.0.0 目标的当前位置：哪些已验证、由哪个门禁验证、哪些仍然开放。
当前版本：`1.0.0-beta8`，2026-10-05 从 tag `v1.0.0-beta8` 发布至
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

逐条的完成条件与状态以下文 §8「1.0 发布门禁」为准；本节只是概览。

- **读屏器与焦点遍历**：语义已实现并有 Android 节点树与 iOS 无障碍审计的自动证据（beta8：180/180
  页面×主题，对比度 0），但没有记录过 TalkBack / VoiceOver 的人工通过。
- **性能**：Android 2022 旗舰（小米 12 Pro，benchmark 构建）全部达标；iOS 只有模拟器 Release
  数字（冷启动中位约 1030 ms，主要耗在 Kotlin 页面创建之前）；未测中低端安卓；nightly 未实现。
- **Web**：全部示例页亮暗加载/滚动/缩放（beta8：180/180）与 320 宽窄屏（88/88）通过；键盘操作、
  焦点顺序与 Safari/WebKit 未验收。
- **HarmonyOS**：构建证据（kit 编译、sample 链接）；无运行时验收。
- **渲染器限制**：模糊因四个上游缺口默认关闭（[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) §5）；
  iOS 不跟随 Dynamic Type（Kuikly 默认关闭字号缩放）；Android 系统键盘配色只随系统深浅色
  （平台无接口）；Kuikly 文本框焦点偶发串扰登记为已知限制（kit 侧 5 处规避，见 E3）。
- **已关闭的 beta3 携带决策**：原生字号刻度（beta4 定为参考刻度默认、`Typographies.Platform`
  可选）与 iOS 密码可见切换（beta3 实现）已在 E3 关闭，不再是开放项。
- **RTL**：类型化语言包已有；Kuikly 不转发布局方向，不支持（D4）。
- 视觉：两端首屏亮暗、页尾、交互路径与窄屏已有验收记录（D2、beta8 窄屏）；完整状态覆盖
  （按下/禁用/加载/焦点/错误、长文/空/大数据、亮暗与强调色热切换）尚未逐组件完成。

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
§3 缺口和已采纳的移动业务能力，全部并入本门禁。历史竞品对照已完成决策并删除，以本表为唯一验收依据。
每一项以证据判定完成：单测、sample 路径、真机截图或日志、脚本输出，逐项写进 §2。编译通过不算任何一项的验收。
做不到的项写明缺什么（设备、上游、决策），标「外部阻断」，发布前由维护者逐条放行或推迟，不能静默略过。

状态：☐ 未开始 · ◐ 进行中 · ☑ 完成（附证据） · ⛔ 外部阻断 · ⏸ 维护者推迟

### A. 发布卫生

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| A1 | 推送主线；候选提交上远程 CI 全绿（含 iOS job） | CI run 链接与各 job 结论 | ☑ 候选代码 db346b3：CI（含 iOS job）与 Guardrails 全绿；本记录提交的 CI 见 BETA7_ACCEPTANCE |
| A2 | 下游迁移：privchat-ui、live-chat、lms-app 对候选提交编译通过；privchat-app Android/iOS 编译 | 各仓迁移提交与构建日志 | ☑ 以 db346b3 代码重编：privchat-ui、privchat-app、lms-app、live-chat Android 编译通过；iOS 此前 privchat-app/live-chat/lms-app 链接通过（本轮无下游所用签名变更）；Weey 正式包真机验证 |
| A3 | 历史竞品对照的已采纳事项归入 B/C 组，删除过时对照文档 | B/C 门禁与文档清理提交 | ☑ B/C 条目已列全；历史比较留在 git 历史，不再作为发布规范 |

### B. 移动业务能力补强

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| B1 | 协议同意行：Text 支持内联可点链接片段；默认不勾选的 Checkbox；点链接只打开链接、不勾选；读屏可达 | 单测 + sample 页 + 双端真机点击验证 | ☑ `LinkPiecesTest` 7 例；sample `agreement`；iOS 与 Android 真机点击：点《用户协议》只弹出打开、勾选不变，点其余文字切换勾选；iOS 读屏：勾选框读整句加状态，每个链接为独立按钮。注：Kuikly 不分发 `LinkAnnotation` 点击、不给字符位置，故 `LinkedText` 逐字排版 |
| B2 | LoadMore 列表尾：idle/loading/error/exhausted，停留底部不连发，失败可重试，文案进语言包 | 状态单测 + sample 订单列表 + 真机滚动 | ☑ `LoadMoreTest` 6 例（仅空闲且尾部可见才请求、加载中不重发、失败等用户、追加页推出尾部不连发）；sample `loadmore`；iOS 截图失败态；Android 真机点重试后第 3 页加载 |
| B3 | Input 格式化层：原始值/显示值/光标映射分离；手机号 3-4-4、银行卡四位分组、身份证末位 X | 纯函数单测 + 真机粘贴、中间插删、中文输入法 | ◐ 自动部分真机通过（逐位输入、跨分隔符删除、未满时中间插入）；发现快速连发按键丢字、满号用方向键插入偏一位；中文输入法组字/粘贴/选区替换待人工（见 BETA7_ACCEPTANCE 人工检查） |
| B4 | Calendar 周起始由语言包决定，简体默认周一、繁体按香港惯例默认周日 | 单测 + 截图 | ☑ 简体周一、繁体按香港惯例周日；FormatStringsTest 与 iOS 日历证据 |
| B5 | 数字缩写（万/亿，随语言包）与中文相对时间，放在 i18n 格式化层 | 单测（边界值、四语言） | ☑ `FormatStringsTest`（万/亿边界、向下取整、负数、K/M/B、相对时间六档含跨年与时钟超前）；语言包现为简体、繁体、英文三种，没有第四种 |
| B6 | Picker 稳定 ID 与 label 分离；Cascader 区分叶子/未加载/加载中/失败/空；地址三级联动示例 | 单测（同名节点、改父项、过期结果丢弃）+ 真机 | ☑ 稳定值 Picker 与可取消 Cascader 有单测；Android 真机：联动选到无下级的「香港特别行政区」可确认，同名节点按 value 区分 |
| B7 | DatePicker 最小/最大日期、年/月/日与时间精度、分钟步长与过滤 | 单测（闰日、月末、跨年、过滤后空列）+ 真机 | ☑ 上下界、精度、过滤、步长有边界单测；按列按需计算（默认区间几百次检查）；Android 真机：10 月 31 日滚回 9 月落到 30 日，确定写回 2026-09-30 |
| B8 | Form：类型化字段值、dirty/touched/validating、触发策略、异步校验版本保护、服务端字段错误注入 | 单测（慢请求不覆盖新值、离开组合不写回）+ sample 表单 | ☑ 类型化异步表单有单测（含同值校验合并、初始值变化保留输入）；Android 真机：taken 离焦后显示「用户名已被使用」，改值后旧结果不覆盖新值，注入服务端错误显示，提交输出类型化值 |
| B9 | NumberField 精确十进制（对称 parser/formatter、暂态输入）；Stepper 可选输入（`editable`，默认关） | 单测 + 真机 | ☑ 精确十进制有单测；Android 真机读 inputType：非负整数字段为数字键盘（0x2），小数/负数字段为文本键盘（0x1，Kuikly 无小数键盘）；Stepper 默认只读，`editable` 可开 |

### C. 组件覆盖（来源：COMPONENT_COVERAGE §3）

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| C1 | P2：独立 ListBox、YearPicker、日期/时间分段字段与本地化格式、Toolbar、SubMenu、IndexBar | 每个组件：API 基线、单测、sample 页、双端真机截图 | ☑ 全部 P2 入口已进 API、注册表、sample；Android 亮暗逐页判定并修复（见 BETA7_ACCEPTANCE D2）。维护者 2026-10-01 决定：分段日期时间（DateField/TimeField）是桌面键盘录入模式、YearPicker 与 DatePicker 年精度重复，二者在发布前删除；SubMenu（逐级进入的操作面板）与 ActionSheet、Cascader 重叠，一并删除；选型见 COMPONENT_SPEC「选择类组件选型」 |
| C2 | P3：Kbd、ColorPicker 家族、Meter、User、Code/Snippet | 同上 | ☑ 全部 P3 入口已进 API、注册表、sample；Kbd/Code 文字错位已修；ColorPicker 滑条为纯色登记为偏差 |
| C3 | 注册表、README 索引、COMPONENT_COVERAGE、语言包、API 基线同步 | 守卫全绿 | ☑ 注册表、README 索引、语言包、API 基线同步；门禁全绿 |

### D. 运行时验收

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| D1 | 读屏：Android TalkBack 真机逐页遍历，记录朗读文本；iOS 以 XCUITest `performAccessibilityAudit` 审计全部页，关键家族 VoiceOver 真机抽检 | TalkBack 朗读日志、审计报告、问题清单与修复提交 | ◐ 对比度已修（色板+组件+品牌派生，PaletteContrastTest 守护）；点击区域按规范与 iOS 27（HIG 44×44）落地，真机实测 44，iOS 审计无点击区域问题；iOS 审计/Android 节点树在候选上重跑；TalkBack/VoiceOver 需人工 |
| D2 | 视觉：全部 sample 页 × 亮/暗 × Android 真机与 iOS 截图归档，对照 VISUAL_SPEC 逐页判定 | 截图目录 + 逐页结论 + 偏差登记 | ☑ 两端首屏亮暗 + 滚动到底审查并修复；两端交互验收（表单/选择器/下拉/弹层/键盘避让）Android 41/41、iOS 36/36（含软键盘）；Web 182 页冒烟与弹层缩放全过；偏差已登记 |
| D3 | 大字号/适老化：Android 字号 1.3 与最大、iOS 最大动态字号，全部页截断/溢出 | 截图 + 问题清单与修复 | ⏸ 维护者决定推迟到下一阶段（2026-09-29） |
| D4 | RTL：Android 强制 RTL 布局方向逐页检查 | 截图 + 问题清单 | ⛔ 不支持：Kuikly 不转发系统 RTL，根部提供 RTL 布局方向也不镜像；已记为上游问题；kit 无从右到左语言包 |
| D5 | 性能：iOS 真机（iPhone 16 Pro Max）冷启动、切主题、滚动，对照 §1 预算 | `scripts/perf` 输出 | ◐ 模拟器 Release 20 次冷启动（主机负载 36–49，结果偏保守）：中位 989.5 ms（预算 1000）、p90 1082；切主题 46.1 ms；滚动 p95 16.9 ms、掉帧 0.3%；原始数据已存；真机数字待维护者 iPhone |
| D6 | Web：浏览器自动化逐页交互与 viewport 缩放 | 脚本与结果 | ☑ 186 页亮暗冒烟（加载/滚动/缩放）与弹层缩放确定性测试通过；Kuikly Web 运行时依赖 jsdelivr CDN 已记录 |
| D7 | HarmonyOS：DevEco 模拟器运行 sample 全部页；真机验收 | 模拟器截图；真机为外部阻断 | ⛔ 共享库及 unsigned HAP 构建通过；缺签名模拟器与真机运行环境 |
| D8 | 国产 ROM 矩阵：小米已有；华为、OPPO、vivo | 各机型截图 | ⛔ 缺设备 |

### E. 已知限制处置

| 编号 | 内容 | 验收证据 | 状态 |
| --- | --- | --- | --- |
| E1 | 圆角↔直角热切换原生边框不刷新：kit 侧能规避则修，否则登记并附最小复现 | 修复提交或复现工程 | ☑ 3ef6158 的装饰层按尺寸/形状/描边重建；Android 真机与 iOS 模拟器材质页圆角↔直角热切换边框与阴影即时刷新 |
| E2 | 模糊的四个上游缺口：整理成可直接提交的 Kuikly issue | issue 草稿；提交由维护者决定 | ◐ 上游 issue 草稿（模糊 4 项 + RTL、iOS 省略号、最小触摸目标、iOS 自动更正、Web CDN）待维护者审阅后提交 |
| E3 | beta3 携带决策：原生字号刻度、iOS 密码可见切换、Kuikly 文本框焦点串扰——给出决策与实现，或登记为已知限制 | 决策记录与提交 | ☑ 原生字号刻度 beta4 已定（参考刻度默认、Platform 可选）；iOS 密码可见 beta3 已实现（单行限制已记）；焦点串扰登记为已知限制（kit 侧 5 处规避） |

### F. 发布

F1：[COMPONENT_SPEC.zh-Hans.md §8](./COMPONENT_SPEC.zh-Hans.md) 全部门禁与远程 CI 在确切候选提交上通过；六模块 staging 检查；
发布到 Central 并以 Portal 核对；tag `v1.0.0-beta7`；官网同步；下游切到 beta7。A–E 全部 ☑，或 ⛔ 项经维护者逐条放行。

2026-10-01 状态：维护者批准按现状发布 beta7——B3、D1 的人工检查与 D5 真机数字在 CHANGELOG
中列为未验证，D3/D4/D7/D8 不在本 beta 覆盖范围，E2 草稿留在本地。已从 release 提交发布到
Central 并打 tag `v1.0.0-beta7`。官网同步与下游切到 beta7 另行进行。

## 8. 1.0 发布门禁（2026-10-05 立项）

唯一的 1.0 未完成清单。每项写明完成条件；☐ 未开始，◐ 部分，☑ 完成，⊘ 限定支持（不做，但对外承诺一致）。
1.0 不再新增组件；补的是稳定性证据与对外承诺。API、视觉默认值、行为契约分开评审：
尺寸、间距等默认值变化记为「视觉」，不当作 API 破坏；字符串图标改 `IconSource`（beta8）这类才是源码兼容性变化。

### G. 契约冻结

| 编号 | 内容 | 完成条件 | 状态 |
| --- | --- | --- | --- |
| G1 | 公开 API 冻结 | 全部公开组件逐个评审（参数名、默认值、命名一致性），改动一次做完；RC1 时保存冻结基线（API dump 与 tag 绑定），RC 之后任何删除、改签名、改默认行为都要有逐条审查记录，不能靠「改代码再刷新 dump」保持绿灯；`enabled`/`disabled` 的家族边界按规范保留，不机械统一 | ☐ |
| G2 | 公开行为契约冻结 | 写明并测到：受控值与回调的时序、确认/取消、关闭原因、异步结果晚到与取消、焦点恢复、禁用与只读的区别；关键行为有回归测试（`apiCheck` 查不到这些） | ☐ |
| G3 | 主题与 Token 契约 | 关键色、亮暗、圆角、字体可独立切换，打开中的弹层同步更新；DTCG 支持范围、单位映射、错误处理写明，不支持的数据报错，不静默忽略 | ◐ 圆角↔直角热切换已修（E1）；其余未验收 |

### H. 必须修的缺陷（可达即修）

| 编号 | 内容 | 完成条件 | 状态 |
| --- | --- | --- | --- |
| H1 | 输入丢字 | 普通与格式化输入框在连发、逐字、粘贴、选区替换、中文 IME 组字下都不丢字 | ◐ 格式化连发已修（a939c9d：Android 40/40、iOS 10/10）；普通输入框连发 10/10；粘贴 6/6；选区替换与 IME 组字未测 |
| H2 | 光标错位 | 已填满的格式化号码中用方向键移到中间插入，光标与插入位置一致（beta7 记录：落在左边一位） | ☐ |
| H3 | 大字号裁字 | Android 字号 2.0、iOS 最大字号下无文字被裁 | ◐ 行高不随字号已修（36bde64）；固定高度的小号 Tag 等仍裁字 |
| H4 | 错误焦点 | 焦点不落到错误的输入框；弹层关闭后焦点回到触发处 | ◐ Kuikly 焦点串扰登记为限制（E3，kit 侧 5 处规避）；焦点恢复未验收 |
| H5 | 点击区重叠 | Android 节点树审计无相邻可点区域重叠（测量假象需逐条说明） | ◐ 可编辑 Stepper 已修（6a854ac）；SearchBar 一处待复核；ComboBox 两处为展开面板覆盖的测量假象 |

### J. 验收

| 编号 | 内容 | 完成条件 | 状态 |
| --- | --- | --- | --- |
| J1 | 读屏人工验收 | TalkBack 与 VoiceOver 走完主要组件：朗读名称/角色/状态、阅读顺序、弹层内焦点与关闭后焦点回归；问题入 H 或 G2 | ☐ |
| J2 | 中文 IME | 搜狗与系统拼音下：组字、候选上屏、选区替换、粘贴，覆盖普通、格式化、多行输入 | ☐ |
| J3 | 真机性能 | iPhone 真机与一台中低端安卓按 §1 预算测量，记录设备、负载与方法 | ◐ Android 旗舰已达标；其余未测 |
| J4 | 生命周期与组合场景 | 输入框＋滚动列表＋弹层＋键盘＋返回手势同时使用；后台再回、旋转/窗口变化、离开页面、异步晚返回：无残留遮罩、错误焦点、重复回调、状态丢失 | ☐ |
| J5 | 完整状态覆盖 | 每个组件的按下/禁用/加载/焦点/错误、长文/空/大数据、亮暗/强调色/圆角热切换两端截图与结论；已有 D2 与窄屏证据保留 | ◐ |
| J6 | 制品消费验收 | 六模块 staging 后，用独立项目只从 Maven 制品消费（不用源码 composite）：Android Release 开 R8、iOS 真机链接、Web production 构建通过，重点核对矢量图标 | ☐（beta3 时做过一次独立编译） |
| J7 | Web 键盘与焦点 | 键盘可完成主要交互，焦点可见、顺序正确；如对外声明支持 Safari，补 WebKit 验收 | ☐ |

### K. 支持范围与对外承诺

| 编号 | 内容 | 完成条件 | 状态 |
| --- | --- | --- | --- |
| K1 | 平台与浏览器支持矩阵 | 写明最低 Android/iOS 版本、Kotlin 与 KuiklyUI 版本、支持的浏览器及各自验证等级；README、官网、发布说明一致 | ☐ |
| K2 | 限定支持项 | RTL（不支持）、HarmonyOS（构建级，运行时未验收）、iOS Dynamic Type（不跟随）三处在 README、官网、发布说明说法一致；朋友早先在鸿蒙上的测试只算历史证据，不替代最终候选验收 | ⊘ 待落文 |
| K3 | Android 最小点击区 | 维护者决定统一 44（规范兼容 iOS 设计）：记为设计选择与对 Android 48dp 规范的平台偏差，写入设计规范；以 H5 保证相邻点击区不重叠 | ⊘ 待落文 |
| K4 | 上游问题 | 模糊 4 项、行高、焦点串扰、iOS 单行省略号、RTL 等附最小复现后提交；提交不替代 kit 当前行为的验证 | ◐ 草稿在 `docs/upstream/`（本地） |

### R. RC 与 1.0 退出条件

| 编号 | 内容 | 完成条件 | 状态 |
| --- | --- | --- | --- |
| R1 | RC1 | G1–G3 冻结、H 全部 ☑、J6 通过后发 `1.0.0-rc1`，冻结基线随 tag 保存 | ☐ |
| R2 | 观察期 | RC 在 PrivChat 与 Weey 生产使用不少于 14 天，按 §5 回归清单每周回归一次；期间只修缺陷，不改 API | ☐ |
| R3 | 1.0 | 最终候选 CI 全绿；无未关闭的严重缺陷（崩溃、数据丢失、无法完成主要操作）；J1–J3 人工记录齐全；K 项文档一致 | ☐ |

执行顺序：①统一台账（本节）并定位输入丢字；②并行做 G1/G2 评审与 J1–J3 人工验收（人工问题可能要求改 API，不能等冻结后再发现）；
③完成 J6 制品消费与 J4 组合场景，发 RC1；④RC 按 R2/R3 收口后发 1.0。
