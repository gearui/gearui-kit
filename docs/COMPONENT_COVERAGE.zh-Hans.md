# GearUI 组件覆盖对比 HeroUI Native

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

对照视觉参考的组件清单。HeroUI Native **1.0.9**（锁定 commit `b9fa541`，
见 [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)）是外观与交互参考；
HeroUI v3（Web，`@heroui/react` 3.2.6）仅用于标注将来可能吸收的能力。
包数量不可直接比较——HeroUI 把子部件拆成独立包（`menu-item`、
`list-box-section`），而 GearUI 把它们暴露为参数——所以对比按能力进行。

当前规模：**70 个组件目录 + 9 个原语 + 4 个运行时件**（Navigator、
TabHost、PageScaffold、SwipeBack）。GearUI 在组件数量上超过 HeroUI
Native（43 个包），在能力覆盖上与 HeroUI v3（85 个包）持平。

## 1. HeroUI Native → GearUI

图例：✅ 已有 · 🟡 部分 · — 无。

### 基础与排版

| HeroUI Native | GearUI Kit | 状态 | 备注 |
| --- | --- | --- | --- |
| button | Button, ButtonGroup | ✅ | 按压缩放与高亮已对齐 |
| close-button | CloseButton | ✅ | Calendar、Notification、Snackbar、ImageViewer 统一使用 |
| link-button | Link, LinkButton | ✅ | 图标与分隔色下划线 |
| text | Text + Typography token | ✅ | 字号刻度决策待定（对照 HeroUI 16/14/18） |
| separator | Divider | ✅ | 使用 separator 角色 |
| surface | DecoratedSurface, MaterialSurface | ✅ | 分层阴影与边框样式 |
| glass-view | MaterialSurface, Materials | ✅ | 四平台；默认关闭（VISUAL_SPEC §5） |
| theme-background | App, Theme | ✅ | |
| pressable-feedback | PressableFeedback, Modifier.pressScale, pressedSurfaceColor | ✅ | 高亮使用真实覆盖视图 |
| scroll-shadow | ScrollShadow | ✅ | 按滚动能力渐隐 |
| —（图标） | Icon, Icons（Phosphor 注册表） | ✅ | GearUI 自带完整图标注册表；Native 没有 |
| header | SectionHeader | ✅ | |

### 表单输入

| HeroUI Native | GearUI Kit | 状态 | 备注 |
| --- | --- | --- | --- |
| input | Input | ✅ | 无描边 + field 阴影；表面上用填充变体 |
| text-field | Input(label, helperText, error) | ✅ | GearUI 是参数式；HeroUI 是组合式 |
| input-group | InputGroup, InputGroupAddon, InputGroupDivider | ✅ | 附加件与字段共享同一框架 |
| text-area | Textarea | ✅ | |
| search-field | SearchBar | ✅ | 表面上默认填充变体 |
| input-otp | InputOTP | ✅ | 单个隐藏原生输入框；槽位仅展示 |
| label / description / field-error / control-field | FieldLabel, FieldDescription, FieldErrorText, FieldDefaults | ✅ | Input、Textarea 与 Form 共享 |
| checkbox | Checkbox, CheckboxGroup | ✅ | |
| radio, radio-group | RadioGroup, RadioCardGroup | ✅ | 含卡片式单选 |
| switch | Switch, SwitchGroup | ✅ | 分组行支持整行切换 |
| slider | Slider | ✅ | |
| select | Select | ✅ | |

### 导航、浮层与反馈

| HeroUI Native | GearUI Kit | 状态 | 备注 |
| --- | --- | --- | --- |
| tabs | Tabs, SegmentedControl | ✅ | 弹簧指示器已对齐 |
| dialog | Dialog, DialogContent | ✅ | 布局与动效已对齐 |
| bottom-sheet | BottomSheet | ✅ | |
| popover | Popover | ✅ | |
| menu | ContextMenu, PopoverMenu | ✅ | 菜单行反馈已对齐 |
| sub-menu | — | — | 缺口，P2 |
| toast | Toast, Snackbar, Notification | ✅ | |
| alert | Alert | ✅ | 五种状态、动作槽、可关闭 |
| spinner | Loading | ✅ | 尺寸对齐 16/24/32 |
| skeleton, skeleton-group | Skeleton + 预设（article/card/list/grid） | ✅ | 默认微光已对齐 |
| card | Card | ✅ | 无描边、表面阴影 |
| accordion | Collapse, CollapseGroup(accordion = true) | ✅ | |
| list-group | CellGroup, Cell, ListItem | ✅ | |
| avatar | Avatar, AvatarGroup | ✅ | 重叠排列 + N 计数 |
| chip, tag-group | Tag, TagGroup | ✅ | 单选、多选、可移除 |

## 2. GearUI 独有组件（移动端强项）

两套 HeroUI 库都不提供这些；它们基于 HeroUI token 重新设计样式：

- **导航运行时**：Navigator（真实页面栈）、SwipeBack、PageScaffold、
  带 keep-alive 的 TabHost、BottomNavBar、NavigationMenu、Steps、Anchor、
  BackTop、Drawer、Pagination。HeroUI 依赖外部路由。
- **选择器**：Picker（滚轮）、Cascader、TreeSelect、Transfer、
  DatePickerInput、DateRangePickerInput、DateTimePickerInput、
  TimePickerInput、Calendar、CalendarPopup、NumberField、Rate。
- **浮层**：ActionSheet（含菜单行的单张面板）、Popup、Tooltip、Tour、
  ConfirmDialog、AlertDialog。
- **数据展示与反馈**：Table、SimpleTable、GearImage、ImageGallery、
  ImageViewer、Timeline、Tree、Grid、Swiper、Watermark、SwipeCell、
  Badge、EmptyState、Result、LinearProgress、CircularProgress、
  NoticeBar、pullRefreshItem / PullRefreshIndicator、ComboBox、Form、
  Upload（仅展示层）、ToggleButton(Group)、Stepper。

仅存在于 sample 的组合（不是库组件）：Sidebar、Breadcrumb、FAB、
Message、DropdownMenu。它们要么升级为库组件，要么从组件索引中移除——
不允许出现看起来可用但实际不可用的东西。

## 3. 缺口与优先级

全部 P1 缺口已于 2026-09-20 补齐（PressableFeedback、CloseButton、
Link/LinkButton、字段文本原语、Alert、InputOTP、ComboBox、PullRefresh、
SwitchGroup、TagGroup、ScrollShadow、NumberField、
ToggleButton/ButtonGroup、AvatarGroup）。

| 能力（来源 HeroUI v3） | 优先级 | 备注 |
| --- | --- | --- |
| 独立暴露 List box | P2 | 选项列表目前内嵌于 Select |
| 年份选择器 | P2 | |
| 日期/时间分段字段、本地化格式 | P2 | 目前仅滚轮输入 |
| Breadcrumbs | P2 | 目前仅存在于 sample |
| Toolbar | P2 | NavBar 动作区部分覆盖 |
| Sub-menu | P2 | Native 有，GearUI 没有 |
| Kbd | P3 | 桌面/Web 用途 |
| 颜色选择器家族 | P3 | 移动产品使用率低 |
| Sidebar、Meter、User、Code/Snippet | P3 | 按需 |
| 日期格式国际化 | 1.0 轨道 | 目前公历优先 |
| 无障碍追赶（React Aria 状态模型） | 1.0 轨道 | 剩余最大缺口 |
| RTL | 未验证 | 类型化语言包已有；布局方向未验收 |

## 4. 收敛规则

1. **HeroUI Native 决定外观与交互。** Native 已有的组件，样式/动效/反馈
   数值通过 DTCG token 来自其源码；不允许裸数字。
2. **仅 v3 有的组件**遵循 v3 的结构与状态模型，用 Native 的 token 体系
   渲染，保证与既有组件观感一致。
3. **API 风格**：以 GearUI 的参数式 API 为主要入口；为自定义布局补充
   可组合的字段原语，而不是强推组合式 API。
4. **保留移动端强项扩展**，基于 HeroUI token 重新设计样式。
5. **无障碍**：按钮、字段、菜单与对话框遵循 React Aria 的状态与语义
   模型，并纳入真机验收。

## 5. 维护

新增组件或状态变化时更新本文档；参考版本变化时刷新第 1–3 节。逐组件的
验收状态在 [QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md)。
