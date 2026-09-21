# GearUI 视觉设计规范

[English](./VISUAL_SPEC.md) | [简体中文](./VISUAL_SPEC.zh-Hans.md)

本文档拥有视觉参考及其规则：GearUI 长什么样、每个数值来自哪里、以及记录
在案的偏差。Token 机制与主题规则在
[DESIGN_SYSTEM.zh-Hans.md](./DESIGN_SYSTEM.zh-Hans.md)。

## 1. 视觉标识

GearUI 采用锁定版本的开源 **HeroUI Native 1.0.9** 的默认外观与控件反馈，
取代 git 历史中保留的 Tamagui 和纯 iOS 标识。对齐范围包括结构、密度、
字体、表面、选中指示、按压/焦点/禁用反馈与动效打断。仅颜色一致不算验收。
不要发明全局辉光、震动反馈、缩放或底部面板；没有参考对应的组件保持为显式
的 GearUI 扩展。

GearUI 保留自己的 KMP/Kuikly 运行时与 Kotlin API 约定。绝不引入付费模板；
HeroUI Pro/Web 编辑器截图只说明定制目标，不是 Native 出厂预设。吸收上游
代码时保留适用的版权声明。

## 2. 参考锁定与溯源

- 参考：HeroUI Native **1.0.9**，commit
  `b9fa5410b5fbb875475127386166f4c029e9e73f`。
- `tokens/reference/heroui-native.lock.json` 记录 42 个文件哈希。用
  `node scripts/check_heroui_reference.mjs /path/to/heroui-native` 校验本地
  checkout。该审计是可选项，不得让 CI 依赖同级仓库。哈希通过证明的是溯源，
  不是视觉一致。

| 关注点 | 参考路径 |
| --- | --- |
| 语义色板、字段、阴影值 | `src/styles/variables.css` |
| 派生颜色与圆角角色 | `src/styles/theme.css` |
| Button 几何 | `src/styles/components/button.css` |
| Button 默认变体 | `src/components/button/button.tsx` |
| 按压反馈与打断 | `src/components/pressable-feedback/pressable-feedback.animation.ts` |
| Input 表面/几何 | `src/styles/components/input.css` |
| 字段组合与状态 | `src/components/text-field/text-field.tsx` |
| Select 呈现 | `src/components/select/select.tsx` |
| 底部面板手势 | `src/helpers/internal/components/bottom-sheet-content.tsx` |

生成 Kotlin 前先解析上游单位与别名。绝不把宣传截图当作数值来源。

## 3. 组件结构

**字段。** 独立字段的标签默认在上方；显式横向表单布局仍然支持。编辑器
拥有自己的表面与焦点处理；辅助/错误与计数文本在下方。字数上限计数不是
错误。字段默认无描边、用 field 阴影——放在白色卡片/表面上时用填充变体
（`cardStyle = true`）。

**Select 与层级选择。** 字段触发器 + 分层选项表面，选中/禁用状态清晰、
指示器对齐、滚动有界、放置安全。Popover 是参考默认；sheet/dialog 是显式
呈现方式，不是所有手机上的自动替换。TreeSelect/Cascader 是 GearUI 扩展：
保留层级、展开与选择语义，而不是模仿桌面树。

**卡片与列表。** 一张卡拥有一个表面、形状、padding 和可选的 token 化
阴影/边框。CellGroup 拥有分组与分隔线；Cell 不在自己的控件外再套一张卡。
禁止嵌套装饰框。复用 Text/Icon/Badge 原语，包括其尺寸与计数溢出逻辑。

**导航栏。** NavBar 是跨平台页面组件。标题单行、操作区有界、超出省略。
顶部安全区背景跟随顶栏。Android 底部系统栏在页面之上透明、系统图标清晰
可读；不得继承顶栏配色的色条。

**对话框、面板与菜单。** 放置、遮罩、关闭与生命周期来自 Overlay 运行时。
破坏性操作与取消必须视觉可区分。长列表、键盘与安全区下内容仍可达。一张
打开状态的截图不是行为验收：要测点外、Back、拖拽取消与快速重开。

## 4. 反馈状态

只建模适用的状态；不要把每个控件都硬凑成七种状态。

| 家族 | 必须的行为 |
| --- | --- |
| Action | 默认、按压、抬起/取消、禁用；支持处加 loading |
| Field | 焦点、编辑中、禁用、支持处加只读、校验与辅助内容 |
| Selection | 选中/未选、禁用、按压/拖动/取消；支持处加半选 |
| Overlay | 进出场、关闭、打断、所有权与焦点恢复 |

禁用外观只应用一次，且必须同时抑制交互——只降透明度不等于禁用。只读保持
可读，不伪装成禁用。Hover 只存在于有指针的环境。视觉标记较小时触控目标
仍以至少 44 逻辑单位为目标；紧凑例外必须显式声明。

**按压反馈归组件所有。** `LocalIndication` 送不到视图：Kuikly 会创建并
委托 indication 节点，但它做的一切都到不了视图层——在模拟器上用不透明
红色探针实测，内容之上绘制、之下绘制、`LayoutModifierNode` 层变换全部
零像素变化。所以每个可点的东西都是组件：行是 `Cell`，按钮是 `Button`，
选择是 `SegmentedControl` 或 `ToggleButton`；其余包 `PressableFeedback`，
自带表面的组件可用 `rowPressFeedback`（填充）或 `pressScale`。参考数值
来自 HeroUI Native：宽度补偿的 0.985 缩放 + 内容之上 10% 的
`#3f3f46` / `#d4d4d8` 高亮。

## 5. 阴影、边框与材质

`DecoratedSurface` 渲染有序 outer/inset 阴影栈、带符号 spread，以及矩形/
圆角轮廓上的复杂边框（solid、dashed、dotted、double、groove、ridge、
outset、inset、自定义虚线），不改变内容测量尺寸。模糊是有界高斯边缘近似，
不是精确二维模糊。

Glass 可选、默认关闭（`RuntimeFlags.materialPolicy = MaterialPolicy.Never`），
带不透明回退。它不是 Liquid Glass 折射，也不是平台一致性的证明。KuiklyUI
四端都通过核心 `BlurView` 支持高斯模糊，但四项缺口挡住了默认开启（结论
待提交上游）：

| # | 缺口 | 严重度 | 工作量 |
| --- | --- | --- | --- |
| 1 | `blurRadius` 在每个渲染器上的缩放不一致 | 阻断 | 小 |
| 2 | iOS 锁死 `UIBlurEffectStyleLight`——没有暗色材质 | 阻断 | 小 |
| 3 | iOS 半径饱和：≥ 10 渲染结果相同 | 高 | 小 |
| 4 | compose 层没有 `Modifier.blur` / backdrop-blur | 低（可绕过） | 中 / iOS 上不可行 |

GearUI 侧已建成并验证（`foundation/material/` 与 sample 的 Material Probe
页）；打开只差一个默认值。不要在伤害文本对比度或动效性能的地方引入模糊。

## 6. 记录在案的偏差

与锁定参考不一致的数值，附原因与实测依据。未列在这里的偏差就是 bug。

**满幅 `surface` 页面上的浮层抬升。** 参考实现靠颜色把浮层与页面分开：
它的页面坐在 `--background` 上，所以 `--overlay`（默认主题里 = `--surface`）
天然高一级，`--overlay-shadow` 只是装饰。但页面满幅铺 `surface` 时（比如
每行都是 surface 的会话列表）菜单就落在自己完全相同的颜色上。参考自己的
示例主题（上游 `docs/theming.md`）展示了正确杠杆：两种模式下 `--overlay`
都与 `--surface` 不同且更亮。

| | 参考默认 | GearUI | 原因 |
| --- | --- | --- | --- |
| 暗色 `overlay` | `oklch(0.2103 …)`，= `surface` | `oklch(0.243 …)` | 文档化杠杆。surface 卡上实测 (32,32,35) 对 (24,24,27)——原先完全相同 |
| 亮色 `overlay` | `white`，= `surface` | `white` | 不变：`surface` 已是纯白，亮色没有抬升空间 |
| 亮色 `overlay-shadow` | 2% / 1% / 3% 黑 | 6% / 3% / 10% | 亮色下唯一线索。白底边缘实测 231 级，参考值下为 247。几何（偏移、模糊）不变 |

两种模式还把参考的 `inset` 层画成真实 1dp 边框而非模糊 inset 阴影：走
阴影渲染器时它落到边缘像素只剩约五分之一的声明 alpha，白色页面上菜单
完全没有边缘；作为边框，亮色白底实测 219，暗色对 (32,32,35) 菜单实测
77，均为真机数据。

## 7. 无障碍

用户能操作的每个控件都必须能以名称和状态被访问到。

- **名称。** 文本内容即名称。纯图标控件（CloseButton、评分星、带缩略图的
  上传块）携带来自 `I18n` 的 `contentDescription`——绝不用字面量。
- **状态。** 选中与开关状态放 `stateDescription`，来自 `I18n`。不要依赖
  `Selected` 语义标志：Kuikly 桥接会为它追加硬编码中文"已选择"，既重复
  状态又无视应用语言。
- **角色。** 可点的非文本目标加 `Role.Button`，分段与 tab 单元加 `Role.Tab`。
- **装饰保持沉默。** 带标签控件内部的图标传 null 描述；空字符串不是同一
  回事——它会作为游离分隔符混进父级播报。

语义自动到达平台（`KuiklySemantisHandler`），无需宿主接线；不用读屏器也
可检查——`idb ui describe-all` 列出每个标签、角色和状态。

## 8. 平台边界

移动优先不等于无视浏览器行为。iOS、Android、Web、HarmonyOS 各有独立
证据；一个目标编译通过不是另一个目标的视觉或交互验收。字体、模糊与渲染
需要显式适配。已知渲染器限制：圆角↔直角热切换的原生边框刷新不生效（需
重载）；Web 宿主的实时 viewport-resize 行为需单独验证。
