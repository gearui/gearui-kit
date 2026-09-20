# 组件覆盖与融合 Spec：GearUI Kit × HeroUI v3 × HeroUI Native

状态：现行规划文档，不是发布声明。英文正本见 [COMPONENT_COVERAGE_SPEC.md](COMPONENT_COVERAGE_SPEC.md)；两份文档由同一份数据生成。

## 1. 基线

| 框架 | 版本 / 来源 | 定位 |
|---|---|---|
| HeroUI Native | v1.0.9（`b9fa541`，本仓库锁定的参考版本） | GearUI 的**视觉与交互对标**来源 |
| HeroUI v3（Web） | `@heroui/react` 3.2.6（`heroui-inc/heroui` v3 分支，2026-09-18） | 组件**覆盖面**参考，与 Native 同一设计语言 |
| HeroUI v2 | `heroui-inc/heroui` main 分支 | 仅用于标注 v3 未迁移的组件（表中写作 “v2 …”） |
| GearUI Kit | 1.0.0-beta3 | 本库 |

## 2. 数量对比

| 口径 | HeroUI v3 | HeroUI Native | GearUI Kit |
|---|---:|---:|---:|
| 组件包 / 目录数 | 85 | 43 | 70 个组件目录 + 9 个原语 + 4 个运行时（Navigator、TabHost、PageScaffold、SwipeBack） |
| 仅 sample 拼装、库内无实现 | — | — | 5（Sidebar、Breadcrumb、FAB、Message、DropdownMenu） |

包数不能直接比较：HeroUI 把子部件拆成独立包（如 `menu-item`、`list-box-section`），GearUI 把能力放在参数里。因此下面按**功能**统一成 84 行来对比：

| 统计（共 84 个功能行） | 数量 |
|---|---:|
| HeroUI v3 覆盖 | 59（另有 4 项仅在 v2） |
| HeroUI Native 覆盖 | 37 |
| HeroUI 任一端覆盖 | 63 |
| GearUI ✅ 已有 / 🟡 部分 / 🧩 仅示例 / — 无 | 72 / 4 / 2 / 6 |
| HeroUI 有的功能中，GearUI 已有 / 部分 | 54 / 3（共 63 项） |
| GearUI 独有（HeroUI 两端都没有） | 16 |

结论：GearUI 的组件数量已经超过 HeroUI Native，与 HeroUI v3 相当；移动端 P1 缺口已全部补齐（按压反馈原语、关闭按钮、链接、字段文字原语、行内提示、验证码、组合框、下拉刷新）。剩下的差距集中在**颜色**、**国际化日期**与**无障碍**，以及桌面/Web 专属能力（快捷键标签、拖拽排序等）。GearUI 独有的移动端组件（动作面板、滚轮/级联选择、底部标签栏与页面栈、轮播、滑动单元格等）是需要保留的优势。

## 3. 三框架功能对照表

图例：✅ 已有　🟡 部分　🧩 仅示例拼装（库内无组件）　— 无。优先级：P1 近期吸纳，P2 中期，P3 按需，“—” 表示无需动作。

### 基础与排版

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 按钮 | button | button | Button | ✅ 已有 | — | 已对齐按压缩放与高亮 |
| 按钮组 | button-group | — | ButtonGroup | ✅ 已有 | — | 拼接按钮共享圆角与分隔线 |
| 切换按钮 / 组 | toggle-button, toggle-button-group | — | ToggleButton, ToggleButtonGroup | ✅ 已有 | — | 单选与多选均覆盖 |
| 关闭按钮 | close-button | close-button | CloseButton | ✅ 已有 | — | 日历/通知/Snackbar/图片预览已统一 |
| 链接 / 链接按钮 | link | link-button | Link, LinkButton | ✅ 已有 | — | 含图标与分隔线色下划线 |
| 文本 / 排版 | typography | text | Text, Typography tokens | ✅ 已有 | — | 字号体系待与 HeroUI 16/14/18 对齐（待决策） |
| 快捷键标签 | kbd | — | — | — 无 | P3 | 桌面/Web 场景 |
| 分割线 | separator | separator | Divider | ✅ 已有 | — | 颜色已用 separator 角色 |
| 表面 | surface | surface | DecoratedSurface, MaterialSurface | ✅ 已有 | — | 含多层阴影与描边样式 |
| 毛玻璃 | — | glass-view | MaterialSurface, Materials | ✅ 已有 | — | 四端能力；默认关闭 |
| 主题背景 | — | theme-background | App, Theme | ✅ 已有 | — |  |
| 按压反馈原语 | (CSS states) | pressable-feedback | PressableFeedback, Modifier.pressScale, pressedSurfaceColor | ✅ 已有 | — | 已作为公开 API;高亮用真实覆盖层 |
| 滚动阴影 | scroll-shadow | scroll-shadow | ScrollShadow | ✅ 已有 | — | 按可滚动方向自动渐隐 |
| 图标 | (icons) | — | Icon, Icons (Phosphor) | ✅ 已有 | — | GearUI 独有完整图标注册表 |
| 分组标题 | header | — | SectionHeader | ✅ 已有 | — |  |

### 表单输入

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 输入框 | input | input | Input | ✅ 已有 | — | 无描边 + field 阴影；卡片内用填充变体 |
| 文本字段（标签/描述/错误组合） | textfield | text-field | Input(label, helperText, error) | ✅ 已有 | — | GearUI 为单体参数式；HeroUI 为复合组件 |
| 输入组（前后缀） | input-group | input-group | InputGroup, InputGroupAddon, InputGroupDivider | ✅ 已有 | — | 附加块与输入框共用一个外框 |
| 多行输入 | textarea | text-area | Textarea | ✅ 已有 | — |  |
| 搜索框 | search-field | search-field | SearchBar | ✅ 已有 | — | 默认填充变体，适合放在表面上 |
| 数字输入 | number-field | — | NumberField | ✅ 已有 | — | Stepper 仍作紧凑版 |
| 验证码输入 | input-otp | input-otp | InputOTP | ✅ 已有 | — | 单个隐藏原生输入 + 分格显示 |
| 字段原语（标签/描述/错误/字段组） | label, description, field-error, error-message, fieldset | label, description, field-error, control-field | FieldLabel, FieldDescription, FieldErrorText, FieldDefaults.labelGap | ✅ 已有 | — | Input / Textarea / Form 均走同一套 |
| 表单 | form | — | Form | ✅ 已有 | — |  |
| 复选框 / 组 | checkbox, checkbox-group | checkbox | Checkbox, CheckboxGroup | ✅ 已有 | — |  |
| 单选框 / 组 | radio, radio-group | radio, radio-group | RadioGroup, RadioCardGroup | ✅ 已有 | — | 含卡片式单选 |
| 开关 / 组 | switch, switch-group | switch | Switch, SwitchGroup | ✅ 已有 | — | 组内每行带副说明，整行可点 |
| 滑块 | slider | slider | Slider | ✅ 已有 | — |  |
| 下拉选择 | select | select | Select | ✅ 已有 | — |  |
| 组合框 / 自动完成 | combo-box, autocomplete | — | ComboBox | ✅ 已有 | — | 复用 Select 面板 |
| 列表框 | list-box, list-box-item, list-box-section | — | Select 面板（内部） | 🟡 部分 | P2 | 选项列表未公开为独立组件 |
| 评分 | — | — | Rate | ✅ 已有 | — | GearUI 独有 |
| 滚轮选择器 | — | — | Picker | ✅ 已有 | — | GearUI 独有，移动端常用 |
| 级联 / 树选择 / 穿梭框 | — | — | Cascader, TreeSelect, Transfer | ✅ 已有 | — | GearUI 独有 |
| 上传 | — | — | Upload | ✅ 已有 | — | 纯展示,取图与传输归宿主 |

### 日期与时间

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 日历 | calendar | — | Calendar, CalendarPopup | ✅ 已有 | — |  |
| 范围日历 | range-calendar | — | Calendar（范围模式） | ✅ 已有 | — |  |
| 年份选择 | calendar-year-picker | — | — | — 无 | P2 |  |
| 日期选择 / 范围选择 | date-picker, date-range-picker | — | DatePickerInput, DateRangePickerInput | ✅ 已有 | — | 滚轮字段 + 日历范围字段 |
| 日期/时间字段 | date-field, date-input-group, time-field | — | DateTimePickerInput, TimePickerInput | 🟡 部分 | P2 | 缺分段键盘输入与国际化日期格式 |

### 颜色

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 取色器全家 | color-picker, color-area, color-slider, color-field, color-input-group, color-swatch, color-swatch-picker | — | — | — 无 | P3 | 移动业务使用频率低 |

### 导航与页面

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 标签页 | tabs | tabs | Tabs, SegmentedControl | ✅ 已有 | — | 指示条弹簧动画已对齐 |
| 面包屑 | breadcrumbs | — | Breadcrumb 示例 | 🧩 仅示例 | P2 |  |
| 分页 | pagination | — | Pagination | ✅ 已有 | — |  |
| 顶部导航栏 | (v2 navbar) | — | NavBar | ✅ 已有 | — |  |
| 底部标签栏 | — | — | BottomNavBar, TabHost | ✅ 已有 | — | GearUI 独有，含保活 |
| 导航菜单 | — | — | NavigationMenu | ✅ 已有 | — |  |
| 工具栏 | toolbar | — | NavBar actions | 🟡 部分 | P2 |  |
| 步骤条 | — | — | Steps | ✅ 已有 | — | GearUI 独有 |
| 锚点 / 回到顶部 | — | — | Anchor, BackTop | ✅ 已有 | — | GearUI 独有 |
| 侧边栏 | — | — | Sidebar 示例 | 🧩 仅示例 | P3 |  |
| 页面栈 / 侧滑返回 | — | — | Navigator, SwipeBack, PageScaffold | ✅ 已有 | — | GearUI 独有运行时（HeroUI 依赖外部路由库） |
| 抽屉 | drawer | — | Drawer | ✅ 已有 | — |  |

### 浮层

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 对话框 | modal | dialog | Dialog, DialogContent | ✅ 已有 | — | 已对齐布局与动效 |
| 确认对话框 | alert-dialog | — | ConfirmDialog, AlertDialog | ✅ 已有 | — |  |
| 底部面板 | — | bottom-sheet | BottomSheet | ✅ 已有 | — |  |
| 动作面板 | — | — | ActionSheet | ✅ 已有 | — | GearUI 独有（HeroUI 用 sheet+menu 组合） |
| 气泡卡片 | popover | popover | Popover | ✅ 已有 | — |  |
| 文字提示 | tooltip | — | Tooltip | ✅ 已有 | — |  |
| 菜单 / 下拉菜单 | menu, menu-item, menu-section, dropdown | menu | ContextMenu, PopoverMenu | ✅ 已有 | — | 菜单行按压已对齐 |
| 子菜单 | (menu) | sub-menu | — | — 无 | P2 |  |
| 轻提示 | toast | toast | Toast, Snackbar, Notification | ✅ 已有 | — |  |
| 弹出层 | — | — | Popup | ✅ 已有 | — |  |
| 新手引导 | — | — | Tour | ✅ 已有 | — | GearUI 独有 |

### 反馈与状态

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 行内提示 | alert | alert | Alert | ✅ 已有 | — | 五状态 + 操作 + 关闭 |
| 加载指示 | spinner | spinner | Loading | ✅ 已有 | — | 尺寸已对齐 16/24/32 |
| 进度条 / 环 | progress-bar, progress-circle | — | LinearProgress, CircularProgress | ✅ 已有 | — |  |
| 量表 | meter | — | — | — 无 | P3 | 可由进度条样式覆盖 |
| 骨架屏 / 组 | skeleton | skeleton, skeleton-group | Skeleton + 预设（文章/卡片/列表/网格） | ✅ 已有 | — | 默认 shimmer 已对齐 |
| 空状态 | empty-state | — | EmptyState | ✅ 已有 | — |  |
| 结果页 | — | — | Result | ✅ 已有 | — | GearUI 独有 |
| 徽标 | badge | — | Badge | ✅ 已有 | — |  |
| 下拉刷新 | — | — | pullRefreshItem, PullRefreshIndicator | ✅ 已有 | — | 基于平台列表手势 + GearUI 指示器 |
| 消息 / 公告栏 | — | — | NoticeBar | ✅ 已有 | — | 滚动公告,与 Alert 同一套状态色 |

### 数据展示

| 功能 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | GearUI 状态 | 优先级 | 说明 |
|---|---|---|---|---|---|---|
| 卡片 | card | card | Card | ✅ 已有 | — | 无描边 + surface 阴影 |
| 手风琴 / 披露 | accordion, disclosure, disclosure-group | accordion | Collapse, CollapseGroup(accordion = true) | ✅ 已有 | — | 单项展开由 CollapseGroup 覆盖 |
| 列表组 / 单元格 | — | list-group | CellGroup, Cell, ListItem | ✅ 已有 | — |  |
| 表格 | table | — | Table, SimpleTable | ✅ 已有 | — | 含选择与行点击 |
| 头像 / 头像组 | avatar, avatar-group | avatar | Avatar, AvatarGroup | ✅ 已有 | — | 重叠 + N 计数 |
| 标签 / 标签组 | chip, tag, tag-group | chip, tag-group | Tag, TagGroup | ✅ 已有 | — | 单选/多选/可移除 |
| 图片 / 图集 / 预览 | (v2 image) | — | GearImage, ImageGallery, ImageViewer | ✅ 已有 | — | GearUI 更完整 |
| 时间轴 / 树 / 栅格 / 轮播 / 水印 / 滑动单元格 | — | — | Timeline, Tree, Grid, Swiper, Watermark, SwipeCell | ✅ 已有 | — | GearUI 独有 |
| 用户信息块 | (v2 user) | — | Cell + Avatar | 🟡 部分 | P3 |  |
| 代码 / 片段 | (v2 code, snippet) | — | — | — 无 | P3 |  |

## 4. 能力层面对比

| 维度 | HeroUI v3（Web） | HeroUI Native | GearUI Kit | 结论 |
|---|---|---|---|---|
| 平台 | Web（React） | iOS / Android（React Native） | iOS / Android / Web / HarmonyOS（Kotlin Multiplatform + KuiklyUI） | GearUI 一份代码四端，且每个节点是原生视图 |
| 交互与无障碍基础 | React Aria Components（键盘、焦点、ARIA 完整） | 自研 primitives + RN 无障碍 | Compose 语义 + Kuikly 原生视图；屏幕阅读器尚未验收 | 无障碍是 GearUI 最大短板，应借鉴 React Aria 的状态模型 |
| 样式系统 | Tailwind v4 + tailwind-variants，BEM 类 | Uniwind（Tailwind for RN）+ 同名 BEM 类 | DTCG 2025.10 token 编译为 Kotlin 常量 | GearUI 的 token 是规范格式，可跨工具链复用 |
| 主题 | 明暗 + CSS 变量，可换主题 | 明暗 + 主题 CSS；Pro 主题含 glass | 明暗、品牌色、形状三轴独立 | — |
| 动效 | CSS 过渡 + React Aria 状态 | Reanimated：弹簧、按压缩放、菜单行 | token 化弹簧与时长；已对齐按压、弹层、指示条 | — |
| 弹层体系 | Portal + React Aria overlays | Portal + gorhom bottom sheet | OverlayHost：统一遮罩、返回键、安全区、进出场 | GearUI 有统一宿主与关闭策略 |
| 导航运行时 | 无（交给路由库） | 无（交给 react-navigation） | Navigator、TabHost 保活、边缘侧滑返回 | GearUI 独有优势 |
| 表单与校验 | Form + 字段原语 + RAC 校验 | 字段原语（label/description/error/control-field） | Input/Form 参数式内置校验文案 | 应抽出字段原语，对齐复合写法 |
| 日期国际化 | @internationalized/date，区域格式与日历系统 | 无日期组件 | 公历为主，滚轮选择 | 中长期补分段日期字段 |
| 国际化 / RTL | RTL 完整 | layout-direction provider | 类型化语言包；RTL 未验收 | — |
| 图标 | 少量内置 | 不内置 | Phosphor 全量注册表 + 资源校验 | GearUI 优势 |
| 质量门禁 | Storybook、测试 | 示例 App | 23+ 守卫、API/Token 基线、三端单测、设备验收 | GearUI 门禁更严格 |

## 5. 吸纳路线

### P1：近期（移动业务刚需，HeroUI 已有成熟设计）


P1 已于 2026-09-20 全部落地（本地提交 54e6b41、ca72d4e、4c629bb，iPhone 17 Pro 模拟器逐项验收）：PressableFeedback、CloseButton、Link / LinkButton、字段文字原语、Alert、InputOTP、ComboBox、PullRefresh，另含 SwitchGroup、TagGroup、ScrollShadow、NumberField、ToggleButton / ButtonGroup、AvatarGroup。

### P2：中期
- **列表框**（v3：list-box, list-box-item, list-box-section）：选项列表未公开为独立组件
- **年份选择**（v3：calendar-year-picker）
- **日期/时间字段**（v3：date-field, date-input-group, time-field）：缺分段键盘输入与国际化日期格式
- **面包屑**（v3：breadcrumbs）
- **工具栏**（v3：toolbar）
- **子菜单**（v3：(menu)；Native：sub-menu）

### P3：按需
- **快捷键标签**（v3：kbd）：桌面/Web 场景
- **取色器全家**（v3：color-picker, color-area, color-slider, color-field, color-input-group, color-swatch, color-swatch-picker）：移动业务使用频率低
- **侧边栏**
- **量表**（v3：meter）：可由进度条样式覆盖
- **用户信息块**（v3：(v2 user)）
- **代码 / 片段**（v3：(v2 code, snippet)）

## 6. 融合原则

1. **视觉与交互以 HeroUI Native 为准。** Native 有对应组件时，样式、动效与反馈数值取自 `heroui-native/src/styles` 与 `*.animation.ts`，经 DTCG token 进入 GearUI，不写裸数值。
2. **Native 没有、v3 有的组件**，按 v3 的结构与状态模型实现，视觉沿用 Native 的 token 体系（overlay、field、surface、radius），保证与已有组件一致。
3. **API 风格**：保持 GearUI 的参数式 API 作为主入口；对表单类组件补充可组合的字段原语（Label、Description、FieldError），满足需要自定义布局的场景，不强制复合组件写法。
4. **移动端优先的 GearUI 独有组件保留**，外观按 HeroUI token 统一（如动作面板已改为 sheet + 菜单行）。
5. **sample 拼装项要么升级为库组件，要么从组件目录移除**，避免“看起来有、实际没有”。
6. **无障碍补课**：参考 React Aria 的状态与语义模型，为按钮、字段、菜单、对话框补语义与焦点顺序，并纳入设备验收。

## 7. 维护

- 组件新增或状态变化时，同步更新本表与 [组件验收矩阵](COMPONENT_ACCEPTANCE_MATRIX.md)。
- 参考版本升级时，重新拉取三方组件清单，更新第 1、2 节数字。
