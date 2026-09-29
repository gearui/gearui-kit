# GearUI Kit 与 Ant Design Mobile RN 组件对标

日期：2026-09-29。性质：源码能力盘点与补充建议，**不是已经批准的开发计划，也不是视觉验收报告**。

## 1. 结论与边界

**不需要再换一套视觉系统，也不需要恢复一批桌面式控件。应该借鉴 Ant Design RN 对移动业务的能力拆分，补强 GearUI 已有的表单、选择器、输入和列表。**

- 继续采用 GearUI 现有视觉、按压反馈、主题和 DTCG 2025.10 Token 管线；Ant 是功能和场景参考，不是新的皮肤来源。
- “中国用户习惯”在本文指手机号分组、地址级联、订单加载、协议确认、预约日期、通讯录索引等具体任务，不表示所有中国用户偏好同一种视觉。
- 这些场景的优先级是工程建议，尚无本项目用户调研或使用频次统计支撑；不能包装成市场数据。
- 从源码已确认的差距：类型化/异步表单、稳定值联动选择、滚轮日期约束、格式化输入、精确数值、统一加载更多契约。
- 值得独立新增的候选较少：`LoadMore` 列表尾部；`IndexBar` 为 IM/城市选择的条件项。其余优先扩展现有家族或提供组合示例。
- 本次不修改组件实现、不恢复已删除 API、不升级依赖、不发布、不提交 Git。分析期间源码的变动来自同期的 e2b1e5f（删除兼容层：Select panelMode、Text fontSize/secondary、NotificationHost 等），不影响本文映射。

## 2. 样本与计数方法

| 对象 | 本次基线 | 计数口径 |
| --- | --- | --- |
| Ant Design Mobile RN | 本地 `ant-design-mobile-rn`，HEAD `ebf8a8d2`；package.json 版本 `5.4.3` | 根入口 51 个有效值导出，另有 2 个废弃空类；其中包括运行时和布局工具，不能称为 51 个独立业务控件 |
| GearUI Kit | 本地 `gearui-kit`，HEAD `e2b1e5f` | 64 个组件目录；依照索引生成器排除诊断页后，71 个公开展示条目；另列运行时与家族子入口 |

依据：

- [Ant 根导出](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/index.tsx)、[包版本](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/package.json)。
- [GearUI Sample 注册表](/Users/zoujiaqing/projects/privchat/gearui-kit/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt)、[索引过滤规则](/Users/zoujiaqing/projects/privchat/gearui-kit/scripts/gen_component_index.py)、[当前覆盖清单](/Users/zoujiaqing/projects/privchat/gearui-kit/docs/COMPONENT_COVERAGE.zh-Hans.md)。

不是比较 Ant Design Mobile Web，也不是只数文件夹。所有根导出均列入下表；重点差距进一步核查了 Props、实现和家族子入口。普通组件只做职责映射，未逐项执行状态、手势、读屏及跨平台测试。“已有对应”不等于可以替换，也不等于质量已达标。本文源码链接指向本地快照路径，分享给外部时应换成对应提交的仓库链接。

## 3. Ant Design RN 全部公开入口对照

“已有对应”仅表示职责/基础能力有落点，不表示所有参数、状态、视觉和动效等价。运行时、布局工具也列出，但不当作业务控件数量。

| Ant Design RN 导出（源码入口） | GearUI 对应 | 判定 | 差异与处理建议 |
| --- | --- | --- | --- |
| [Accordion](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/accordion/index.tsx) | Collapse | 已有对应 | 折叠与手风琴合并在一个家族，不另建 Accordion 同义入口。 |
| [ActionSheet](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/action-sheet/index.tsx) | ActionSheet | 已有对应 | GearUI 已有列表和网格动作；系统分享是平台服务，不能等同于动作面板。 |
| [ActivityIndicator](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/activity-indicator/index.tsx) | Loading | 已有对应 | 同时有 foundation LoadingIndicator；不另建同义组件。 |
| [Badge](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/badge/index.tsx) | Badge | 已有对应 | 消息数量和小红点场景保留；不因 HeroUI 缺失而裁剪。 |
| [Button](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/button/index.tsx) | Button | 已有对应 | 不照搬 RN 属性；继续验证禁用、加载、触控与主题。 |
| [Card](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/card/index.tsx) | Card | 已有对应 | 槽位和组合方式不同，不要求一一复制子组件。 |
| [Carousel](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/carousel/index.tsx) | Swiper | 已有对应 | 轮播属于国内首页常见能力，已有家族，不新建 Carousel。 |
| [Checkbox](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/checkbox/index.tsx) | Checkbox | 已有对应/组合 | 含 CheckboxItem、AgreeItem 的对照见后文；协议确认用 Checkbox 与 Link 组合。 |
| [Collapse](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/collapse/index.tsx) | Collapse | 已有对应 | Ant 同时导出 Accordion 与 Collapse，不代表 GearUI 也要维护两套。 |
| [DatePickerView](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/date-picker-view/index.tsx) | DatePicker | 部分覆盖 | 独立内嵌日期滚轮未公开；滚轮范围、精度、过滤还需补强。 |
| [DatePicker](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/date-picker/index.tsx) | DatePicker | 部分覆盖 | GearUI 日期/时间字段已有；业务日期约束不是仅改显示格式。 |
| [Divider](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/divider/index.tsx) | Divider | 已有对应 | 保留自己的 Token 与像素适配。 |
| [Drawer](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/drawer/index.tsx) | Drawer | 已有对应 | 返回、安全区与手势冲突需运行时验收，本次不判视觉等价。 |
| [Flex](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/flex/index.tsx) | Grid | 原语组合 | 真实对应是 Row/Column/Box/Arrangement；Grid 只补充网格场景，不是 Flex 等价物。 |
| [Form](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/form/index.ts) | Form | 部分覆盖 | 已有布局、注册、同步字符串校验；异步、类型化值、依赖字段和动态字段仍有差距。 |
| [Grid](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/grid/index.tsx) | Grid,Swiper | 部分覆盖/组合 | GearUI 已有网格与轮播；Ant 的图标文字数据驱动和分页宫格适合先补示例。 |
| [Icon](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/icon/index.tsx) | Icon | 已有对应 | 图标来源不同；保持 GearUI 的 Phosphor 体系。 |
| [InputItem](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/input-item/index.tsx) | Input,InputGroup,Form | 部分覆盖 | 行式输入可组合；phone/bankCard 格式化能力是实际差距。 |
| [Input](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/input/index.tsx) | Input,Textarea,Form | 已有对应/组合 | Ant Input.Item、Input.TextArea 不需要逐一新建 GearUI 别名。 |
| [ListView](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/list-view/index.tsx) | List,PullRefresh | 部分覆盖 | 已有懒列表和下拉刷新；缺少统一加载更多、尾部失败重试与结束态契约。 |
| [List](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/list/index.tsx) | Cell,CellGroup,List | 已有对应/组合 | Ant List/Item 偏行与分组；GearUI List 是懒滚动容器，不能按同名认定同职责。 |
| [Modal](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/modal/index.tsx) | Dialog,BottomSheet,ActionSheet | 已有对应/组合 | alert 已有，prompt 可由 Dialog+Input 组合；不是缺一个 Modal。 |
| [Marquee](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/notice-bar/index.tsx) | NoticeBar | 部分覆盖 | 滚动公告已有；独立任意内容跑马灯未单列，先不扩大 API。 |
| [NoticeBar](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/notice-bar/index.tsx) | NoticeBar | 已有对应 | 订单提醒/服务通知可用；业务消息不是全部都应滚动。 |
| [Pagination](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/pagination/index.tsx) | 无 | 不建议恢复 | GearUI 已移除页码导航；移动信息流优先加载更多，报表有确需再用应用级组合。 |
| [PickerView](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/picker-view/index.tsx) | Picker | 部分覆盖 | 滚轮核心为 internal，缺公开可内嵌的选择内容层。 |
| [Picker](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/picker/index.tsx) | Picker,Cascader | 部分覆盖 | 独立/联动滚轮已有；稳定值、异步子级状态与取消事务需要规范化。 |
| [Popover](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/popover/index.tsx) | Popover,ContextMenu | 已有对应/组合 | 锚定浮层与动作菜单分工，不照搬多层私有 Host。 |
| [Portal](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/portal/index.tsx) | 运行时 OverlayRoot/OverlayHost | 运行时覆盖 | 不是需要新增的业务控件；继续使用统一浮层宿主。 |
| [Progress](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/progress/index.tsx) | Progress | 已有对应 | 进度与加载不同，保留两者。 |
| [Provider](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/provider/index.tsx) | App/Theme/I18nRoot | 运行时覆盖 | 对齐全局配置职责，不复制 React Provider 命名。 |
| [Radio](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/radio/index.tsx) | Radio | 已有对应/组合 | Radio.Group 已有；RadioItem 用 Cell 组合，防止整行点击重复触发。 |
| [Rate](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/rate/index.tsx) | Rate | 已有对应 | 评分是本土业务常用能力，已有，不另建评分库。 |
| [Result](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/result/index.tsx) | Result,Empty | 已有对应 | 操作结果与空数据分开，避免同一个模板承载全部状态。 |
| [SearchBar](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/search-bar/index.tsx) | SearchBar | 已有对应 | 中文输入法、取消、清除和键盘行为需实机验收，不靠外观判断。 |
| [Skeleton](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/skeleton/index.tsx) | Skeleton | 已有对应 | Title/Paragraph/Provider 子入口不必同名复制。 |
| [Slider](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/slider/index.tsx) | Slider | 已有对应 | 范围与步进能力按当前 API 验证；手势质量不能由静态对照证明。 |
| [Stepper](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/stepper/index.tsx) | Stepper,NumberField | 部分覆盖 | GearUI Stepper 为整数增减，NumberField 为 Double 输入；Ant 有字符串精度、parser/formatter。 |
| [Steps](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/steps/index.tsx) | Steps | 已有对应 | 流程步骤已有；不要与数值 Stepper 混淆。 |
| [SwipeAction](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/swipe-action/index.tsx) | SwipeCell | 已有对应 | 聊天/订单侧滑动作保留；互斥展开与滚动冲突应专项验收。 |
| [Switch](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/switch/index.tsx) | Switch | 已有对应 | 另有 SwitchGroup；保持统一禁用与按压契约。 |
| [TabBar](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/tab-bar/index.tsx) | BottomNavBar | 已有对应 | 页面保活由 TabHost 提供，不把路由状态塞进底栏。 |
| [Tabs](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/tabs/index.tsx) | Tabs | 已有对应 | 另有 TabPager；切换容器和底部主导航不是同一个控件。 |
| [Tag](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/tag/index.tsx) | Tag | 已有对应 | 另有 TagGroup，筛选组合不必独立发明 Chips 家族。 |
| [Text](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/text/index.tsx) | Text | 已有对应 | Ant 直接再导出 RN Text；GearUI 有自己的字体和主题适配。 |
| [TextareaItem](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/textarea-item/index.tsx) | Textarea,Form | 已有对应/组合 | 行式多行输入可组合，不恢复旧同义包装。 |
| [Toast](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/toast/index.tsx) | Toast,Loading | 已有对应 | 短消息与阻塞加载按语义区分，避免所有反馈走同一遮罩。 |
| [Tooltip](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/tooltip/index.tsx) | Tooltip | 已有对应 | 移动端可达性需触摸/读屏验证，不依赖 hover。 |
| [View](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/view/index.tsx) | Surface/Box | 原语组合 | 通用容器由底层与 foundation 覆盖，不再封装同义 View。 |
| [WhiteSpace](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/white-space/index.tsx) | Spacing/Spacer | 原语组合 | 垂直间距由 Token 和布局原语表达，不增加组件计数。 |
| [WingBlank](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/wing-blank/index.tsx) | Spacing/padding | 原语组合 | 水平留白同理。 |

### 3.1 废弃、子入口与目录口径

| 入口 | 当前源码事实 | 处理 |
| --- | --- | --- |
| ImagePicker | 根入口是 `@deprecated export class ImagePicker {}`；目录只剩中英文文档 | 不计为可用图片选择器；GearUI Upload 并非缺同名组件 |
| SegmentedControl | 根入口是同样的废弃空类；目录只剩文档 | GearUI Segmented 有真实实现，不跟随删除或重复新建 |
| LocaleProvider / locale-provider | 有源码目录，但根入口未单独导出该名称 | 国际化能力随 Provider 对照，不另计一个根组件 |
| Checkbox.CheckboxItem / AgreeItem | 子入口；AgreeItem 当前只包装 Checkbox 并覆写红色样式 | 行式选择、协议确认按组合模式补，不复制该红色硬编码 |
| Radio.RadioItem / Group | 子入口 | Cell + RadioButton、RadioGroup 对应 |
| Input.Item / TextArea | 子入口 | FormItem + Input、Textarea 对应 |
| List.Item / Item.Brief | 子入口 | Cell / description 槽 + CellGroup 对应 |
| Form.Item / List / ErrorList / useForm / useWatch / Provider | 表单家族的功能入口，非六个独立视觉组件 | 动态字段、依赖、错误聚合列入 Form 能力差距 |
| Modal.alert / prompt / operation / useModal | 模态家族便捷入口 | Dialog/ConfirmDialog/AlertDialog、输入弹窗示例、ActionSheet 对应 |
| Skeleton.Title / Paragraph / Provider | 骨架家族组合入口 | 复用现有 Skeleton 变体/布局，不按子入口数量追平 |

## 4. GearUI 全部 71 个索引条目反向对照

本表防止仅从 Ant 清单出发，遗漏 GearUI 已有能力。样例诊断页不计入；辅助函数、重载和组组件归所属家族。

| 分类 | GearUI 条目 | Ant 对应 | 决策 |
| --- | --- | --- | --- |
| 基础 | Button | Button | 保留并复用；具体差异见正向对照表。 |
| 基础 | Icon | Icon | 保留并复用；具体差异见正向对照表。 |
| 基础 | Link | 无独立根导出 | 协议链接、帮助入口；与 Checkbox 组合形成协议确认模式。 |
| 基础 | CloseButton | 无独立根导出 | 统一关闭动作和可访问名称，不需 Ant 同名组件背书。 |
| 基础 | PressableFeedback | 无独立根导出 | 统一点击反馈，是现有视觉基建。 |
| 基础 | Text | Text | 保留并复用；具体差异见正向对照表。 |
| 基础 | Tag | Tag | 保留并复用；具体差异见正向对照表。 |
| 基础 | Badge | Badge | 保留并复用；具体差异见正向对照表。 |
| 基础 | Divider | Divider | 保留并复用；具体差异见正向对照表。 |
| 表单 | Input | InputItem / Input | 保留并复用；具体差异见正向对照表。 |
| 表单 | Checkbox | Checkbox | 保留并复用；具体差异见正向对照表。 |
| 表单 | Radio | Radio | 保留并复用；具体差异见正向对照表。 |
| 表单 | InputOTP | 无独立根导出 | 短信验证码已有；不应再提议新建 OTP。 |
| 表单 | ComboBox | 无独立根导出 | 可输入筛选的选择器已有；远端搜索请求归业务。 |
| 表单 | NumberField | Stepper | 与 Ant Stepper 的可输入部分对照；详见精确数值差距。 |
| 表单 | ToggleButton | 无独立根导出 | 可选按钮与 ButtonGroup 已有；用于筛选，不机械复制 Segmented。 |
| 表单 | InputGroup | InputItem | 前后附加内容、区号等组合已有。 |
| 表单 | Switch | Switch | 保留并复用；具体差异见正向对照表。 |
| 表单 | Slider | Slider | 保留并复用；具体差异见正向对照表。 |
| 表单 | Stepper | Stepper | 保留并复用；具体差异见正向对照表。 |
| 表单 | Textarea | Input / TextareaItem | 保留并复用；具体差异见正向对照表。 |
| 表单 | Rate | Rate | 保留并复用；具体差异见正向对照表。 |
| 表单 | Select | 无独立根导出 | 普通选择已有；不要因参考库没同名导出而删除。 |
| 表单 | Picker | PickerView / Picker | 保留并复用；具体差异见正向对照表。 |
| 表单 | DatePicker | DatePickerView / DatePicker | 保留并复用；具体差异见正向对照表。 |
| 表单 | Upload | 无独立根导出 | 附件展示、进度、失败重试已有；选文件、权限和传输归平台/业务。 |
| 表单 | Form | Form / InputItem / Input / TextareaItem | 保留并复用；具体差异见正向对照表。 |
| 表单 | Cascader | Picker | 已有同步树级联；窄屏多列和异步子级是重点补强项。 |
| 导航 | NavBar | 无独立根导出 | 现有页面顶部导航，Ant 根入口没有同名组件。 |
| 导航 | BottomNavBar | TabBar | 保留并复用；具体差异见正向对照表。 |
| 导航 | Tabs | Tabs | 保留并复用；具体差异见正向对照表。 |
| 导航 | Drawer | Drawer | 保留并复用；具体差异见正向对照表。 |
| 导航 | Steps | Steps | 保留并复用；具体差异见正向对照表。 |
| 导航 | Segmented | 无独立根导出 | GearUI 有真实实现；Ant 根入口的 SegmentedControl 是废弃空类。 |
| 数据展示 | List | ListView / List | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Card | Card | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Cell | List | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | CellGroup | List | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Table | 无独立根导出 | 保留有真实使用场景的表格，不因为 Ant 根入口缺失就删除。 |
| 数据展示 | Image | 无独立根导出 | 基础图片已有，Ant 可用 RN 原语不等于其组件库拥有同名组件。 |
| 数据展示 | ImageViewer | 无独立根导出 | 全屏预览已有，附件/聊天常用。 |
| 数据展示 | Avatar | 无独立根导出 | 头像及 AvatarGroup 已有，通讯类产品重要。 |
| 数据展示 | ScrollShadow | 无独立根导出 | 滚动边缘提示已有。 |
| 数据展示 | Collapse | Accordion / Collapse | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Progress | Progress | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Empty | Result | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Skeleton | Skeleton | 保留并复用；具体差异见正向对照表。 |
| 数据展示 | Timeline | 无独立根导出 | 物流、办理记录等纵向过程已有。 |
| 数据展示 | Calendar | 无独立根导出 | 单选、多选、范围及 minDate/maxDate 已有；不可与滚轮 DatePicker 混为一谈。 |
| 数据展示 | Watermark | 无独立根导出 | 保留按业务启用的水印，不能视为可靠防泄漏机制。 |
| 反馈 | SwipeCell | SwipeAction | 保留并复用；具体差异见正向对照表。 |
| 反馈 | ActionSheet | ActionSheet / Modal | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Toast | Toast | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Dialog | Modal | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Tooltip | Tooltip | 保留并复用；具体差异见正向对照表。 |
| 反馈 | ContextMenu | Popover | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Loading | ActivityIndicator / Toast | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Alert | 无独立根导出 | 页面内反馈已有，不与 Dialog 混同。 |
| 反馈 | NoticeBar | Marquee / NoticeBar | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Notification | 无独立根导出 | 全局通知已有，按重要性和场景选用。 |
| 反馈 | Snackbar | 无独立根导出 | 带动作的短反馈已有，不恢复 Message 别名。 |
| 反馈 | Popup | 无独立根导出 | 通用弹出内容已有，避免新增独立浮层系统。 |
| 反馈 | Popover | Popover | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Result | Result | 保留并复用；具体差异见正向对照表。 |
| 反馈 | Tour | 无独立根导出 | 引导已有，不因对照库缺失而裁剪。 |
| 布局 | Grid | Flex / Grid | 保留并复用；具体差异见正向对照表。 |
| 布局 | Swiper | Carousel / Grid | 保留并复用；具体差异见正向对照表。 |
| 布局 | SearchBar | SearchBar | 保留并复用；具体差异见正向对照表。 |
| 布局 | PullRefresh | ListView | 保留并复用；具体差异见正向对照表。 |
| 布局 | BottomSheet | Modal | 保留并复用；具体差异见正向对照表。 |
| 布局 | BackTop | 无独立根导出 | 长内容回顶已有；是否展示由业务决定。 |

### 4.1 运行时及非索引能力

GearUI 的 App/Theme/I18nRoot、OverlayRoot/OverlayHost、Navigator、TabHost、PageScaffold、SwipeBack 不混进 71 个控件计数。Ant 的 Provider/Portal 可对照配置和浮层职责，但不是导航运行时的等价物。GearUI 的 MultiSelect、RangeSlider、SwitchGroup、TagGroup、AvatarGroup、ButtonGroup、TabPager、CalendarPopup、DateRangePickerInput 等归所属家族，本次没有把每个公开函数当作独立控件。

## 5. 优先补强的能力与充分理由

优先级：P1 为下一轮优先实施候选，P2 为明确业务需要后实施；不等同于当前发布阻断等级。以下 API 名称是设计草案，不是要求立即冻结的新公开接口。

### 5.1 P1：Form 能力补强，而不是再做一个“中国式表单”

**证据。** Ant 的 [Form/FormItem/FormList](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/form/index.ts) 和 [Form 实现](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/form/Form.tsx) 基于 rc-field-form，并暴露 watch、动态列表、错误集合等能力。[GearUI Form.kt](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/form/Form.kt) 的 `FormFieldState.value` 为 String，`FormRule.validator` 为同步 Boolean 回调；FormState 支持注册、整体校验、重置。并非“GearUI 没有校验”。

**场景与理由。** 收货地址同时含文字、选择路径和协议布尔值；实名认证含条件必填；账号名占用校验要异步。若每页自行实现，会重复出现校验旧值覆盖新值、隐藏字段仍阻断提交、重复显示错误等问题。属于通用字段协调，应在 Form 家族解决，而不是复制业务页面。

**最小范围。** 类型化字段值、dirty/touched/validating、提交/失焦/变更触发策略、异步校验结果版本保护、服务端字段错误注入。明确定义 FormItem 与 Input 的错误消息只显示一次；Form 负责协同，不负责调用登录/订单接口。

**后续范围。** 依赖字段、动态字段数组、错误汇总和聚焦首个错误；优先有两种业务表单后抽象，不一次移植完整 rc-field-form。

**验收。** A 值的慢请求不得覆盖 B 值；字段离开组合后不得写回；提交校验包含未 touched 字段；日期/多选不必字符串化；布局切换不丢值；禁用与错误呈现不重复衰减/重复文案。中文组合输入期间不得按未提交拼音触发破坏性校验。

### 5.2 P1：Picker/Cascader 统一稳定值与联动状态，地址选择先做示例

**证据。** Ant [PickerViewPropsType](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/picker-view/PropsType.tsx) 分离 label/value，支持 children、cascade、loading 和选中条目回传。[GearUI Picker.Linked](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/picker/Picker.kt) 已支持联动，但入口是 `Map<String, Any>` 和文字列表；Multi 使用索引。[Cascader](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/cascader/Cascader.kt) 已有 value/label/children/disabled，但子级为空即被当作叶节点；注释声称 dynamic loading，公开 API 却没有独立的加载、失败、未知叶子状态。替换 options 可以更新数据，不等于完整异步加载契约。

**场景与理由。** 省市区、部门、商品分类都会有重复名称、父级变更、部分子项延迟加载；“同名就是同值”或“空 children 就可提交”不能可靠表达它们。窄手机上把多级列表平均分成并排窄列，也会压缩中文长地名与点击区域。

**最小范围。** 为 Picker 引入稳定 ID 与 label 分离的数据模型；独立列与联动列复用选择核心；Cascader 能区分叶子、未加载、加载中、失败、空结果。加载由应用回调负责，组件不内置网络。父级变化清除失效后代，过期结果按父路径/请求版本丢弃。

**移动呈现。** 优先在现有 BottomSheet 中承载：路径标题或分级页签 + 当前级全宽列表；小而固定的数据也可滚轮。保留大屏锚定面板能力，不强制每个选择器都变成 Sheet。确认采用临时值，取消不污染已提交值。PickerView/日期内容层若公开，应复用同一核心，不再做一套轮子。

**中国地址边界。** 示例覆盖省/市/区与两级、不等深数据、无下级项；行政区划代码和数据版本由应用维护，库不打包一个永远过时的地区库，不内置定位或地址解析服务。

**验收。** 同名节点、改父项、空列表、失败重试、快速切换父项、数据移除已选值、取消重开、触摸滚动和读屏选择。至少在小屏中文长名称上验收。

### 5.3 P1：DatePicker 的日期约束与精度，不重复新建 Calendar

**证据。** Ant [日期 Props](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/date-picker/PropsType.tsx) 有 minDate/maxDate、precision、filter、renderLabel；[列生成实现](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/date-picker/date-picker-utils.ts) 实际处理这些参数。GearUI [滚轮 DatePicker](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/picker/DatePicker.kt) 提供字符串字段和固定 `1900..2100` 年范围，日期/时间弹层私有；公开字段没有同等日期范围和过滤入口。与此同时，[Calendar](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/calendar/Calendar.kt) 和 CalendarPopup 已有 minDate/maxDate、单选/多选/范围，不能报告为整个库都没有日期约束。

**场景与理由。** 生日不能选未来，预约不能选已过去时段，账单常按年月选择。不能仅在提交时弹错误，让用户先选一个不可能合法的日期。

**最小范围。** 扩展现有日期家族的可选范围、年/月/日和时间精度、分钟步长/过滤；显示格式与内部值分离；定义空值、无合法选项、越界初值、闰年与跨月联动。日期是本地日历日期，时间戳转换和时区边界显式处理。

**不做。** 不因“中国常用”就立即上农历、节气、法定节假日数据。Ant 本次对照并未证明这些能力；若业务需要，另立数据/算法范围。

**验收。** 二月闰日、月末切月、当天过去时间、跨年上下界、过滤后空列、取消、语言切换。单测覆盖生成与归一化，真机验证滚轮选中与最终回传一致。

### 5.4 P1：Input 格式化能力与中国常用配方

**证据。** Ant [InputItem.onChange](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/input-item/index.tsx) 对 phone 去非数字并裁为 11 位，再按 3-4-4 分组；bankCard 每四位插空格。[GearUI Input](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/input/Input.kt) 有 keyboardType、密码、清除、prefix/suffix、maxLength，但没有公开格式化/反格式化和光标映射契约。`keyboardType` 只是键盘提示，不等于数字校验或格式化。

**场景与理由。** 手机号登录、联系方式和银行卡是重复出现的输入任务；由每页直接替换字符串容易破坏中间编辑、粘贴和光标位置。号码/卡号必须保留前导零，不能按数值存储。

**最小范围。** 优先建立纯函数格式化层，分离原始值、显示值、选择区映射；再核实 Kuikly 原生输入桥是否能保持 composing/selection，能力不足时明确限制，不伪造“全平台透明格式化”。手机号 +86/3-4-4 是可选地区配方，银行卡分组是显示配方，不把 11 位限制硬编码进全局 Input。

**应用层负责。** 国家区号选择、号码真实性、验证码发送和倒计时、银行卡归属、实名规则。身份证末位 X 和 18 位格式可做示例校验，但不能冒充身份核验。现有 InputOTP 已能承担验证码展示，不能再提议补一个 OTP。

**验收。** 全量粘贴、带空格粘贴、中间插删、删除分隔符、选区替换、前导零、中文输入法、区号切换和读屏。Ant 的字符串替换实现本身不能证明这些编辑场景正确，不照抄。

### 5.5 P1：加载更多状态与列表尾部，不恢复 Pagination

**证据。** Ant [ListView](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/list-view/index.tsx) 封装 ultimate-listview，提供 onFetch、刷新和分页相关属性。[GearUI List](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/primitives/List.kt) 已经懒渲染、有分组 DSL 和 ListState；[PullRefresh](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/refresh/PullRefresh.kt) 提供 LazyListScope 的刷新项。但未发现公共的统一加载更多/失败重试/结束态入口。不能说 GearUI 缺整个 ListView。

**场景与理由。** 订单、群成员、商品和消息记录都需要连续加载。最易重复出错的是底部重复请求、刷新和分页互相覆盖、失败后无法重试，而非缺一个 spinner。

**最小范围。** 新增轻量 `LoadMore` 尾部或同等列表扩展，状态区分 idle/loading/error/exhausted，首屏 empty 与尾部 exhausted 分开。提供防重复触发的接入配方；请求游标、缓存、网络及错误恢复策略归调用方，不绑 SDK、不拷贝 RN 大型列表依赖。优先复用一个滚动宿主；List DSL 与 LazyListScope 刷新入口的组合方式要写清楚。

**验收。** 停留底部不连发，失败可点重试，刷新后旧页不写回，无更多时不触发，离开页面取消不误报，返回页面保留滚动位置。消息“向前加载”单列场景验收，不能从普通向后分页推断已支持。

### 5.6 P2：精确数值编辑；金额需求成立时优先提升

**证据。** Ant [Stepper Props](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/stepper/PropsType.tsx) 暴露 stringMode、digits、parser、formatter，[实现](/Users/zoujiaqing/projects/privchat/ant-design-mobile-rn/components/stepper/stepper.tsx) 使用 mini-decimal。GearUI [Stepper](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/stepper/Stepper.kt) 是 Int，[NumberField](/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/numberfield/NumberField.kt) 是 Double，提供 format 而不是对称 parser。

**场景与理由。** 商品数量可用整数 Stepper，退款金额和单价却要求十进制精度、舍入和输入中间态。给 Double 加一个人民币符号不等于金额正确。

**范围。** 优先给 NumberField 定义可注入解析/格式化和值策略，金额用十进制字符串或业务提供的精确数值类型，支持暂态 `-`、`0.`；不因 Ant 有 stringMode 就强制所有数字字段用字符串。另核对 Stepper 的 `disableInput`：当前中部渲染 Text，参数不能当作已实现可编辑的证明。

**验收。** 0.1 步进、较大金额、空值、粘贴、负号、小数位限制和显示回读一致；金额计算与输入展示分开。无金额业务前不增加 MoneyInput、PaymentKeyboard、支付业务流程。

## 6. 中国移动场景：哪些新增，哪些只需组合

下表是场景推导，**不是声称 Ant Design RN 全部提供这些组件**。

| 场景 | GearUI 现有基础 | 推荐落点 | 立项条件与理由 |
| --- | --- | --- | --- |
| 通讯录/城市字母索引 | List.section、ListState.scrollToItem、SearchBar | P2 新增 IndexBar 候选 + 分组列表配方 | IM 联系人列表确实需要时做；侧边索引拖动、当前位置提示、点击/读屏跳转有稳定通用交互。拼音、排序、多音字和热门城市归业务。Ant 根入口无此组件 |
| 收货地址 | Picker.Linked、Cascader、Form、Input | P1 扩展选择核心 + AddressForm 示例 | 不再新建与 Cascader 平行的地址选择引擎；外部注入版本化地区数据 |
| 用户协议确认 | Checkbox、Link、Text、Form | P1 组合示例，必要时补富文本槽 | 协议链接与勾选动作要分开，点击链接不顺带勾选；不复制 Ant AgreeItem 的红色样式，也不默认勾选 |
| 手机验证码登录 | Input、InputGroup、InputOTP、Button | P1 示例 + 输入格式化 | 请求、倒计时和风控由业务持有；不做一个包含登录 SDK 的 Login 组件 |
| 订单/商品筛选 | Tabs、TagGroup、Select、BottomSheet、DateRangePickerInput | P2 筛选面板示例 | 临时选择、重置、取消和确认可形成配方；不恢复 NavigationMenu/TreeSelect 充数 |
| 首页九宫格/分页入口 | Grid、GridItem、Swiper、Icon、Badge | P2 数据驱动组合示例 | Ant Grid 的 icon/text 与 isCarousel 结构值得借鉴，但 GearUI 已有基础，只有多处复用后再抽 ActionGrid |
| 微信式分享面板 | ActionSheet.showGrid、Icon、Text | 现有组件示例 + 平台服务接口 | 不等于真实微信/QQ分享接入；渠道图标、授权、URI 与回调由应用/平台适配负责 |
| 发布图片/附件 | Upload、ImageViewer、ActionSheet | 示例与平台桥，不新增同名 ImagePicker | GearUI 已有上传中/失败/重试 UI；系统选图、权限、压缩、上传服务不应藏进组件 |
| 预约/账单月份 | DatePicker、Calendar | P1 日期家族扩展 | 日期范围/精度有复用价值；农历和节假日不是默认前置 |
| 商品数量/金额 | Stepper、NumberField | 整数复用；金额按 5.6 扩展 | 不用 Double 默认为财务精确值，不立即引入自定义支付键盘 |

## 7. 不建议恢复或照搬的东西

1. **Pagination**：对照库确有实现，但不能推导为我们必须恢复。普通移动列表用加载更多；存在明确报表翻页需求再评估。
2. **Tree/TreeSelect/Transfer/Anchor/NavigationMenu**：Ant RN 根入口也没有这些组件。保留移动层级选择需求，用 Cascader/Picker 表达；不把“某参考库没有”当成永远删除的唯一理由。
3. **Breadcrumb/Sidebar/FAB/Message/DropdownMenu 的旧同义或示例条目**：不为数量恢复；真正差异应以独立语义和业务复用证据证明。
4. **WhiteSpace/WingBlank/Flex/View 的重复包装**：布局原语和 Spacing 已够；新增一层名字不改善中文使用体验。
5. **PhoneInput/BankCardInput/MoneyInput/AddressPicker 各起一套控件**：先沉淀输入/选择/数值核心，再做薄配方；只有生命周期与行为确实独立时才新增公开组件。
6. **旧全局弹层实例、React Provider 形态**：GearUI 已有 Overlay/Runtime；对标职责，不移植框架特定架构。

### 7.1 参考源码也不是绝对标准

- 根入口的 ImagePicker、SegmentedControl 是废弃空类；不根据残留文档认定仍有实现。
- `InputItem` 文档提到 money，当前 Props 的 type 联合和格式化分支没有该模式；不能据文档声称已有可借用的金额键盘。
- `Checkbox.AgreeItem` 当前只是覆写红色的 Checkbox，不是独立的协议链接和触控语义实现。
- `ActionSheet.showShareActionSheetWithOptions` 当前源码调用 `navigator.share`；不能从函数名认定已完成 RN 原生系统分享，更不能认定有微信/QQ渠道能力。
- `ListView` 依赖外部 ultimate-listview，并有多处 any；借鉴状态和场景，不原样引入它的网络/分页耦合。
- 单纯增加中文文案、红色按钮或更密集布局不是“国内适配”。这些都不能替代可用的输入、选择、校验和返回行为。

## 8. 建议执行顺序与完成条件

| 阶段 | 范围 | 产物 | 完成条件 |
| --- | --- | --- | --- |
| A：协议与输入 | Form 最小状态模型、输入格式化桥接验证、协议/手机号示例 | 状态/格式化单测与两条真实 Sample 路径 | 无重复错误、异步旧结果不覆盖、中间编辑/IME 正确；底层受限则明确记录，不虚报完成 |
| B：选择与日期 | 稳定值 Picker、异步 Cascader、Sheet 内容复用、日期约束 | 地址和预约场景 | 空/失败/取消/过期请求可测；小屏长中文标签可用；无额外私有 Overlay |
| C：列表加载 | LoadMore 尾部、List/PullRefresh 组合契约 | 订单列表和向前加载消息示例 | 防重复、刷新竞争、重试、结束态和滚动位置通过 |
| D：按需扩展 | IndexBar、精确数值、九宫格/分享/附件示例 | 有业务使用方的组件或配方 | 未被业务需要的候选不扩充公开 API；明确哪些属于平台桥 |

A/B/C 可按实际业务优先级调整，不需要重建整个组件库。建议先把地址表单、手机号登录、预约日期、订单列表四页做成验收载体，而不是新增数十个零散展示页。

每一阶段共同要求：

- 源码中的设计值继续走当前 DTCG Token 机制，区分主题、品牌关键色和业务规则；不能为了对齐 Ant 样式硬编码数值。
- 保留公开 API 的 enabled/disabled 家族边界。1.0 前只做最佳方案：稳定值模型直接替换旧入口，不留兼容层或 deprecated 重载；CHANGELOG 写迁移说明，下游同批迁移。
- 复用 OverlayHost、键盘、返回、安全区及无障碍基础设施。减少动画、深浅主题、品牌色切换也在验收范围。
- 纯状态/转换逻辑单测 + Sample 装配测试 + 真机交互验证分开记录。编译通过、模拟器截图、朋友真机测试分别说明范围，不互相替代。
- Android/iOS 是逐路径运行验证；HarmonyOS/Web 按实际可用设备和渲染能力记录结果。未测试项标为未测试，不用“跨平台”一词自动填满通过表。
- 中国输入与本地化必须覆盖中文 IME、超长中文标签、字号放大、日期边界、区号和错误提示，不要求中国场景替换全局语言规则。
- 应用请求、缓存、行政区划数据、相机/相册权限、分享 SDK、登录和支付流程仍在组件库之外。

## 9. 面向中国用户市场的改进清单

第 5–6 节从 Ant 的导出出发。中国市场还有 Ant 清单覆盖不到的要求：监管合规、日历与数字习惯、国产系统。本节按“不做会怎样”排序，事实均已对 `e2b1e5f` 源码核实。

| 优先级 | 改进项 | 现状（已核实） | 理由 |
| --- | --- | --- | --- |
| P0 | **协议 / 隐私同意行**：可点《用户协议》《隐私政策》的内联链接文本 + 默认不勾选的 Checkbox | kit 的 Text 只接纯字符串，没有内联可点片段；Kuikly 2.28 有 `LinkAnnotation` / `withLink`，技术可行，待真机验证 | 《个人信息保护法》与工信部 App 专项治理要求首次启动前取得同意、不得默认勾选；华为、小米、OPPO、vivo 应用市场审核都会检查。每个国内 App 的登录页和首启弹窗都要用。点链接只打开协议、不能顺带勾选 |
| P0 | **加载更多列表尾**（即 5.5 的 LoadMore） | 无组件、无文案（语言包里没有“没有更多了”“加载失败，点击重试”） | 国内信息流、订单、群成员列表都是无限滚动加底部状态，这是国内 App 的默认形态 |
| P1 | **手机号 / 银行卡 / 身份证输入格式化**（即 5.4） | Input 无格式化与光标映射 | 国内登录几乎都是手机号 + 验证码。身份证末位 X 需要能输入（数字键盘上没有 X） |
| P1 | **日历周起始跟随语言包** | `Calendar(firstDayOfWeek = 0)` 默认周日写死，中文语言包的 weekdaysShort 也从“日”开始 | GB/T 7408 规定周一为一周之始，国内办公、排班、打卡类产品多从周一排。应由语言包（区域）给默认值，不要写死 |
| P1 | **万 / 亿数字缩写**（1.2万、3.5亿） | kit 无此能力 | 国内点赞、观看、粉丝数一律用万和亿，“12K”在中文界面是错的。live-chat 的观看人数、礼物数立刻要用。做成随语言包走的数字格式化，不做成组件 |
| P1 | **中文相对时间**（刚刚 / 3分钟前 / 昨天 12:30 / 9月28日） | kit 无，各 App 各写一份 | IM 和信息流的时间戳就是这套规则，写法和英文的“3 min ago”完全不同，放在语言包里统一 |
| P1 | **地址三级联动 + 异步子级**（即 5.2） | Cascader 把空 children 当叶子；Picker.Linked 入参是 `Map<String, Any>` | 收货地址是电商、外卖、物流的必经流程；行政区划数据由应用注入 |
| P1 | **大字体 / 适老化验收** | runtime 没有字号缩放感知（查不到 fontScale 相关代码），尚未验证系统大字体下的布局 | 工信部《互联网应用适老化及无障碍改造》对公共服务类 App 有要求；国内中老年用户开大字体比例高。先在系统最大字号下跑一遍 71 页，记录截断和溢出，再决定是否需要 kit 级缩放上限 |
| P2 | **IndexBar 字母索引**（即第 6 节） | 无 | 通讯录、城市选择；拼音与多音字排序由业务负责 |
| P2 | **鸿蒙 HarmonyOS NEXT** | sample 有 ohosApp 和 build.ohos.gradle.kts，按既定路线押后 | 国内新机逐步只跑鸿蒙原生；等 Kuikly 鸿蒙端稳定后，把 71 页纳入验收，而不是只保证能编译 |
| P2 | **国产 ROM 验收矩阵**（MIUI/HyperOS、ColorOS、OriginOS、鸿蒙） | 目前只在一台小米真机上验过 | 已踩过的坑：MIUI 吞日志、强制深色、手势条安全区。至少覆盖小米、华为、OPPO/vivo 各一台 |

**已符合国内习惯、不需要改的**：Toast 默认居中并带类型图标；Dialog 取消在左、确定在右；Badge 支持小红点和 99+；下拉刷新文案；SwipeCell（聊天列表侧滑“删除 / 标为未读”）；InputOTP 验证码；ActionSheet 网格分享面板；DatePicker 年月日顺序和“年/月/日”后缀；24 小时制。

**不做的**：农历、节气、法定节假日数据（需要时另立数据范围）；微信 / 支付宝的 SDK 接入（平台桥，不进组件库）；“红涨绿跌”（只有金融 App 需要，用主题色自己配）；Ant 风格的红色 AgreeItem。

### 9.1 实施状态（2026-09-29）

| 项 | 状态 | 落点 |
| --- | --- | --- |
| 协议 / 隐私同意行 | 已实现 | `AgreementCheckbox`、`LinkedText`（Kuikly 不分发 LinkAnnotation 点击，改为逐字排版；行首不出现「，」、行尾不留「《」） |
| 加载更多 | 已实现 | `loadMoreItem` / `LoadMoreFooter`，状态沿用 Kuikly `FooterRefresh`；下拉刷新沿用 Kuikly `pullToRefreshItem` |
| 手机号 / 银行卡 / 身份证输入 | 已实现 | `Input(format = InputFormat.ChinaMobile / BankCard / IdCard)`；Kuikly 异步桥下脚本级连击可能丢字，人手输入、粘贴、自动填充不受影响 |
| 周起始跟随语言包 | 已实现 | `Strings.format.firstDayOfWeek`，简体中文周一 |
| 万 / 亿缩写、中文相对时间 | 已实现 | `Strings.format.compactNumber` / `relativeTime` |
| 农历 | 已实现 | `Lunar`（1900–2100 离线算法、节气、传统节日、干支生肖），`Calendar(lunar = true)` |
| 地址三级联动 + 异步子级 | 已实现 | `Cascader` 改为底部面板 + 层级页签，`loadChildren` 支持加载中 / 失败重试 |
| 字母索引栏 | 已实现 | `IndexBar` |
| 大字体 / 适老化验收 | 未做 | 需在系统最大字号下逐页检查 |
| 鸿蒙 NEXT、国产 ROM 验收矩阵 | 未做 | 押后 |

## 10. 本次交付与后续维护

本次交付是完整双向组件清单、重点能力差距、场景归属与执行建议；没有实现上述候选，也未运行两个库的 UI 或测试套件。清单已按源码导出及 GearUI 注册表做数量和遗漏检查。

今后增删组件时先更新事实清单，再调整本文映射；不要让这份快照覆盖 `COMPONENT_COVERAGE`、`COMPONENT_SPEC`、`DESIGN_SYSTEM` 的权威职责。只有经确认的建议才进入正式规范和开发任务。
