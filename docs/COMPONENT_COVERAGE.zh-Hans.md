# GearUI 组件覆盖清单

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

更新：2026-09-30。**85 个公开索引条目**、**76 个组件目录**、**93 条 sample 路由**。运行时诊断页与 typed-form 演示不重复计为组件；家族子入口与目录不是同一计数口径。

注册表是清单来源，README 生成结果由 CI 检查。本文记录当前能力覆盖，不等于视觉或交互验收。参考列只表示开源 HeroUI Native 有对应家族，不表示 API、像素或行为完全一致；GearUI 保留自己的移动端能力扩展。

## 1. 当前完整清单

### 基础 (10)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `Kbd` | 快捷键 | GearUI 扩展 |
| `Button` | 用于触发操作 | HeroUI Native |
| `Icon` | 图标展示 | GearUI 扩展 |
| `Link` | 文本链接与链接按钮 | GearUI 扩展 |
| `CloseButton` | 统一的关闭/移除按钮 | HeroUI Native |
| `PressableFeedback` | 任意可点区域的缩放与高亮 | HeroUI Native |
| `Text` | 文本展示 | HeroUI Native |
| `Tag` | 标记和分类 | HeroUI Native |
| `Badge` | 消息数量提示 | GearUI 扩展 |
| `Divider` | 内容分隔 | HeroUI Native |

### 表单 (24)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `ListBox` | 选项列表 | GearUI 扩展 |
| `ColorPicker` | 颜色选择 | GearUI 扩展 |
| `Input` | 文本输入 | HeroUI Native |
| `Checkbox` | 多选操作 | HeroUI Native |
| `AgreementCheckbox` | 协议与隐私同意 | HeroUI Native |
| `Radio` | 单选操作 | HeroUI Native |
| `InputOTP` | 分格验证码输入 | HeroUI Native |
| `ComboBox` | 输入筛选的下拉选择 | GearUI 扩展 |
| `NumberField` | 可输入可步进的数字字段 | GearUI 扩展 |
| `ToggleButton` | 保持按下状态的按钮与按钮组 | HeroUI Native |
| `InputGroup` | 带前后附加块的输入框 | HeroUI Native |
| `Switch` | 开关选择 | HeroUI Native |
| `Slider` | 数值选择 | HeroUI Native |
| `Stepper` | 数字增减 | GearUI 扩展 |
| `Textarea` | 多行文本输入 | HeroUI Native |
| `Rate` | 评分操作 | GearUI 扩展 |
| `Select` | 下拉选择器 | HeroUI Native |
| `Picker` | 多列选择 | GearUI 扩展 |
| `DatePicker` | 日期时间选择 | GearUI 扩展 |
| `Upload` | 文件上传 | GearUI 扩展 |
| `Form` | 表单容器 | HeroUI Native |
| `Cascader` | 级联选择器 | GearUI 扩展 |

### 导航 (9)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `Toolbar` | 工具栏 | GearUI 扩展 |
| `SubMenu` | 多级菜单 | GearUI 扩展 |
| `NavBar` | 通用页面导航栏 | GearUI 扩展 |
| `BottomNavBar` | 应用底部主导航 | GearUI 扩展 |
| `Tabs` | 内容切换 | HeroUI Native |
| `Drawer` | 侧滑抽屉 | GearUI 扩展 |
| `Steps` | 步骤指示 | GearUI 扩展 |
| `IndexBar` | 通讯录字母索引 | GearUI 扩展 |
| `Segmented` | 分段选择 | GearUI 扩展 |

### 数据展示 (20)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `Meter` | 计量值 | GearUI 扩展 |
| `User` | 用户摘要 | GearUI 扩展 |
| `Code` | 代码块 | GearUI 扩展 |
| `List` | 列表展示 | GearUI 扩展 |
| `Card` | 卡片容器 | HeroUI Native |
| `Cell` | 列表单元组件 | HeroUI Native |
| `CellGroup` | 成组的列表行 | GearUI 扩展 |
| `Table` | 数据表格 | GearUI 扩展 |
| `Image` | 图片展示 | GearUI 扩展 |
| `ImageViewer` | 图片预览查看 | GearUI 扩展 |
| `Avatar` | 用户头像 | HeroUI Native |
| `ScrollShadow` | 滚动区域边缘渐隐 | HeroUI Native |
| `Collapse` | 内容折叠 | HeroUI Native |
| `Progress` | 进度展示 | GearUI 扩展 |
| `Empty` | 空数据提示 | GearUI 扩展 |
| `Skeleton` | 加载占位 | HeroUI Native |
| `Timeline` | 时间线展示 | GearUI 扩展 |
| `Calendar` | 日历展示 | GearUI 扩展 |
| `Format` | 万亿缩写、相对时间、农历 | GearUI 扩展 |
| `Watermark` | 页面水印 | GearUI 扩展 |

### 反馈 (15)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `SwipeCell` | 滑动操作单元格 | GearUI 扩展 |
| `ActionSheet` | 底部动作面板 | GearUI 扩展 |
| `Toast` | 消息提示 | HeroUI Native |
| `Dialog` | 模态对话框 | HeroUI Native |
| `Tooltip` | 文字提示 | GearUI 扩展 |
| `ContextMenu` | 上下文菜单 | GearUI 扩展 |
| `Loading` | 加载状态 | HeroUI Native |
| `Alert` | 页面内状态提示 | HeroUI Native |
| `NoticeBar` | 滚动公告栏 | GearUI 扩展 |
| `Notification` | 全局通知 | HeroUI Native |
| `Snackbar` | 底部消息 | GearUI 扩展 |
| `Popup` | 弹出内容 | GearUI 扩展 |
| `Popover` | 气泡提示 | HeroUI Native |
| `Result` | 操作结果反馈 | GearUI 扩展 |
| `Tour` | 功能引导 | GearUI 扩展 |

### 布局 (7)

| 条目 | 用途 | 参考家族 |
| --- | --- | --- |
| `Grid` | 栅格布局 | GearUI 扩展 |
| `Swiper` | 内容轮播 | GearUI 扩展 |
| `SearchBar` | 搜索输入 | GearUI 扩展 |
| `PullRefresh` | 列表下拉刷新 | GearUI 扩展 |
| `LoadMore` | 列表分页加载 | GearUI 扩展 |
| `BottomSheet` | 底部弹出 | HeroUI Native |
| `BackTop` | 返回顶部 | GearUI 扩展 |

## 2. beta7 实施状态

稳定值 Picker、可取消 Cascader 加载、日期/时间约束、类型化异步表单、精确十进制 NumberField 与可编辑 Stepper 已接入生产调用并有测试。ListBox、Toolbar、SubMenu、Kbd、ColorPicker 家族、Meter、User、Code/Snippet 已提供公开入口和 sample 路由。IndexBar、协议行、LoadMore、语言格式化来自此前 beta7 工作。

## 3. 尚须验收的范围

功能落地不等于发布批准。全页读屏、视觉逐页判定、最大字号、RTL、iOS 真机性能、HarmonyOS 运行仍按 [QUALITY_STATUS.zh-Hans.md §7](./QUALITY_STATUS.zh-Hans.md) 执行。[BETA7_ACCEPTANCE.md](./BETA7_ACCEPTANCE.md) 记录证据和限制；已批准计划不静默删项。

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
