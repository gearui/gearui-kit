# GearUI 组件覆盖清单

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

GearUI 组件的完整清单，按分类组织，并逐条标注与 HeroUI Native 组件集的
覆盖情况。这是 kit 出厂内容的权威清单；同一份清单驱动 README 的生成索引
（`ComponentConfig.kt` → `gen_component_index.py`，CI 校验），所以两者永不
脱节。

当前规模：**70 个组件目录**、一组 foundation 原语（Text、Icon、Surface、
BasicTextField、LoadingIndicator、List、ScrollView、GearLazyColumn）与
**4 个运行时件**（Navigator、TabHost、PageScaffold、SwipeBack）。公开索引
共 **六大分类、82 个条目**。

覆盖概览：82 个条目中，**36 个 HeroUI Native 也有**、**41 个是 GearUI
独有**（HeroUI 不提供的移动优先能力）、**5 个是仅 sample** 的组合，待升级
或移除。GearUI 在组件数量上超过 HeroUI Native（43 个包），在能力覆盖上与
HeroUI v3（85 个包）持平。

图例：✅ HeroUI Native 也有 · ◆ GearUI 独有 · ○ 仅 sample。

## 1. 组件完整清单

### 基础（9）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `Button` | 触发动作 | ✅ |
| `Icon` | 图标展示（Phosphor 注册表） | ◆ |
| `Link` | 链接与 LinkButton | ✅ |
| `CloseButton` | 统一关闭按钮 | ✅ |
| `PressableFeedback` | 任意可点区域的缩放与高亮 | ✅ |
| `Text` | 文本展示 | ✅ |
| `Tag` | 标记与分类 | ✅ |
| `Badge` | 消息计数指示 | ◆ |
| `Divider` | 内容分隔 | ✅ |

### 表单（22）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `Input` | 文本输入 | ✅ |
| `Checkbox` | 多选 | ✅ |
| `Radio` | 单选 | ✅ |
| `InputOTP` | 一次性验证码输入 | ✅ |
| `ComboBox` | 可过滤的建议输入 | ◆ |
| `NumberField` | 可输入可步进的数字 | ◆ |
| `ToggleButton` | 切换与按钮组 | ◆ |
| `InputGroup` | 带附加块的字段 | ✅ |
| `Switch` | 开关 | ✅ |
| `Slider` | 取值 | ✅ |
| `Stepper` | 数字步进器 | ◆ |
| `Textarea` | 多行文本输入 | ✅ |
| `Rate` | 评分 | ◆ |
| `Select` | 下拉选择 | ✅ |
| `Picker` | 多列滚轮选择器 | ◆ |
| `DatePicker` | 日期与时间选择 | ◆ |
| `DropdownMenu` | 过滤下拉菜单 | ○ |
| `Upload` | 文件上传（展示层） | ◆ |
| `Form` | 表单容器 | ◆ |
| `Cascader` | 级联选择 | ◆ |
| `Transfer` | 数据穿梭 | ◆ |
| `TreeSelect` | 树形选择 | ◆ |

### 导航（12）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `NavBar` | 页面导航栏 | ◆ |
| `BottomNavBar` | 应用底部导航 | ◆ |
| `Tabs` | 内容切换 | ✅ |
| `NavigationMenu` | 顶部导航菜单 | ◆ |
| `Sidebar` | 侧边导航 | ○ |
| `Drawer` | 抽屉 | ◆ |
| `Steps` | 步骤指示 | ◆ |
| `Pagination` | 分页导航 | ◆ |
| `Breadcrumb` | 路径导航 | ○ |
| `Anchor` | 页面锚点导航 | ◆ |
| `Segmented` | 分段控件 | ✅ |
| `FAB` | 悬浮动作按钮 | ○ |

### 数据展示（17）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `List` | 列表展示 | ✅ |
| `Card` | 卡片容器 | ✅ |
| `Cell` | 列表单元 | ✅ |
| `CellGroup` | 分组列表行 | ✅ |
| `Table` | 数据表格 | ◆ |
| `Image` | 图片展示 | ◆ |
| `ImageViewer` | 图片预览 | ◆ |
| `Avatar` | 用户头像 | ✅ |
| `ScrollShadow` | 滚动边缘渐隐 | ✅ |
| `Collapse` | 内容折叠 / 手风琴 | ✅ |
| `Progress` | 线与环形进度 | ◆ |
| `Empty` | 空状态 | ◆ |
| `Skeleton` | 加载占位 | ✅ |
| `Timeline` | 时间轴 | ◆ |
| `Tree` | 树结构 | ◆ |
| `Calendar` | 日历展示 | ◆ |
| `Watermark` | 页面水印 | ◆ |

### 反馈（16）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `SwipeCell` | 可滑动单元 | ◆ |
| `ActionSheet` | 底部动作面板 | ◆ |
| `Toast` | 消息提示 | ✅ |
| `Dialog` | 模态对话框 | ✅ |
| `Tooltip` | 文字提示 | ◆ |
| `ContextMenu` | 上下文菜单 | ✅ |
| `Loading` | 加载态 / spinner | ✅ |
| `Message` | 全局消息 | ○ |
| `Alert` | 内联状态消息 | ✅ |
| `NoticeBar` | 滚动公告 | ◆ |
| `Notification` | 全局通知 | ✅ |
| `Snackbar` | 底部消息 | ✅ |
| `Popup` | 弹出内容 | ◆ |
| `Popover` | 气泡浮层 | ✅ |
| `Result` | 操作结果 | ◆ |
| `Tour` | 功能引导 | ◆ |

### 布局（6）

| 组件 | 用途 | 覆盖 |
| --- | --- | --- |
| `Grid` | 栅格布局 | ◆ |
| `Swiper` | 内容轮播 | ◆ |
| `SearchBar` | 搜索输入 | ✅ |
| `PullRefresh` | 下拉刷新列表 | ◆ |
| `BottomSheet` | 底部面板 | ✅ |
| `BackTop` | 回到顶部 | ◆ |

## 2. GearUI 独有强项

41 个 GearUI 独有条目是 kit 的移动优先深度——两套 HeroUI 库都不提供、
全部基于 GearUI token 渲染的能力：

- **导航运行时**：Navigator（真实页面栈）、SwipeBack、PageScaffold、带
  keep-alive 的 TabHost、NavBar、BottomNavBar、NavigationMenu、Steps、
  Anchor、BackTop、Drawer、Pagination。HeroUI 依赖外部路由，没有栈式导航
  运行时。
- **选择器与字段**：Picker（滚轮）、Cascader、TreeSelect、Transfer、
  DatePicker、NumberField、Rate、Stepper、ComboBox、Upload、Form。
- **浮层**：ActionSheet、Popup、Tooltip、Tour。
- **数据展示与反馈**：Table、Image、ImageViewer、Timeline、Tree、Grid、
  Swiper、Watermark、SwipeCell、Badge、Empty、Result、Progress、
  NoticeBar、PullRefresh。
- **图标集**：完整的 Phosphor 图标注册表；HeroUI Native 一个都不带。

**仅 sample 的组合（○）。** Sidebar、Breadcrumb、FAB、Message 与
DropdownMenu 目前只存在于 sample。它们要么升级为库组件，要么从索引中
移除——不允许出现看起来可用但实际不可用的东西。

## 3. 缺口与优先级

全部 P1 缺口已于 2026-09-20 补齐（PressableFeedback、CloseButton、
Link/LinkButton、字段文本原语、Alert、InputOTP、ComboBox、PullRefresh、
SwitchGroup、TagGroup、ScrollShadow、NumberField、
ToggleButton/ButtonGroup、AvatarGroup）。

| 能力 | 优先级 | 备注 |
| --- | --- | --- |
| 独立暴露 List box | P2 | 选项列表目前内嵌于 Select |
| 年份选择器 | P2 | |
| 日期/时间分段字段、本地化格式 | P2 | 目前仅滚轮输入 |
| Breadcrumbs | P2 | 目前仅存在于 sample |
| Toolbar | P2 | NavBar 动作区部分覆盖 |
| Sub-menu | P2 | 未提供嵌套菜单 |
| Kbd | P3 | 桌面/Web 用途 |
| 颜色选择器家族 | P3 | 移动产品使用率低 |
| Sidebar、Meter、User、Code/Snippet | P3 | 按需 |
| 日期格式国际化 | 1.0 轨道 | 目前公历优先 |
| 无障碍状态模型追赶 | 1.0 轨道 | 剩余最大缺口 |
| RTL | 未验证 | 类型化语言包已有；布局方向未验收 |

## 4. 收敛规则

1. **一套视觉语言。** 每个组件——无论 GearUI 独有还是共有——都通过同一套
   token 体系设计样式，保证整套观感一致
   （[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)）。
2. **API 风格**：以 GearUI 的参数式 API 为主要入口；为自定义布局补充
   可组合的字段原语，而不是强推组合式 API。
3. **保留并扩展移动优先的深度**，而不是为对齐某个参考去裁剪。
4. **无障碍**：按钮、字段、菜单与对话框遵循一致的名称/状态/角色模型，并
   纳入真机验收。

## 5. 维护

新增组件或状态变化时更新本文档，并与 README 的生成索引保持同步。逐组件的
验收状态在 [QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md)。
