# GearUI 组件封装规范

[English](./COMPONENT_SPEC.md) | [简体中文](./COMPONENT_SPEC.zh-Hans.md)

组件如何构建、命名、写文档、被守卫和被验收。视觉数值来自
[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)；token 机制来自
[DESIGN_SYSTEM.zh-Hans.md](./DESIGN_SYSTEM.zh-Hans.md)。

## 1. 结构

- 每个组件一个目录：
  `gearui-kit/src/commonMain/kotlin/com/gearui/components/<name>/`。
- 组件组合 Foundation 原语（Text、Icon、Badge、DecoratedSurface、
  PressableFeedback），不复制 badge 溢出、字体或表面实现。
- 组件专属数值放在组件自己的 `XxxTokens` 类中，由语义 token 派生。
- 组件体第一行读主题：

```kotlin
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors          // 永远是第一行
    // 只用语义 token——绝不 Color(0x...)，绝不裸 .dp 设计值
}
```

组件层禁止：硬编码颜色、圆角、海拔、边框宽度、图标尺寸、间距字面量和
静态字体排版。六个 CI 棘轮守着冻结基线；新增违规直接构建失败。

## 2. API 一致性（冻结边界）

保持参数语义一致、受控状态与回调所有权清晰。不要为了形式统一机械重排
既有 API。enabled/disabled 极性是冻结的语义边界，由
`check_state_param_naming.sh` 强制：

- **字段家族**（Input、Textarea、Select/MultiSelect、Cascader、
  日期/时间输入触发器）：`enabled: Boolean = true` 加 `error: String?`。
- **SearchBar**：`enabled`，不发明校验错误参数。
- **动作家族**（Button、Tag、SwipeCell 动作、面板条目）刻意保留
  `disabled: Boolean = false`。
- 同一组件家族绝不同时暴露 `enabled` 和 `disabled`。
- 选项模型的 `disabled`（如 `SelectOption.disabled`）描述的是选项，不是
  容器控件；它不是组件开关。
- 错误文本与展示型枚举是不同语义；仅靠参数名计数无法分类。

不要提议重命名——这是冻结边界，不是历史遗留的不一致。

## 3. 状态与交互

- 状态反馈遵循 [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) 的家族
  表；只建模适用的状态。
- 每个可点目标都有真实按压反馈（组件自持；见 VISUAL_SPEC §4 的
  `LocalIndication` 结论）。`check_press_feedback.sh` 拦截无响应的点击
  目标；`check_sample_uses_components.sh` 拦截 sample 里的裸点击修饰符。
- 允许局部控件手势；禁止复制运行时关闭/滚动/Back 所有权的私有全局监听。
- Overlay 一律走 Overlay 运行时；组件绝不自己装遮罩或 Back 处理。
- Insets 从 `RuntimeEnvironment` 读；组件绝不重新解析宿主测量，也不重复
  消费 scaffold 已消费的 inset。

## 4. 无障碍

纯图标控件携带来自 `I18n` 的 `contentDescription`；状态放
`stateDescription`；角色按 VISUAL_SPEC §7。带标签控件内的装饰图标传 null
描述。

## 5. 文档

- 代码注释只用英文（`check_english_comments.sh`）；UI 文案走类型化 i18n
  语言包（`check_i18n_default_text.sh`，零字面量基线）。
- 每个组件配指南：一句话用途、最小用法、推荐用法、参数表、边界（"不负责
  ……"）、常见问题。运行时边界问题（Overlay / safeArea / Theme）指向
  [ARCHITECTURE.zh-Hans.md](./ARCHITECTURE.zh-Hans.md)，不重复解释。
- 显式标注文档语言/状态；中文文档不得冒充已同步的英文版。

## 6. Sample 是集成证据

- Sample 保持正常的移动端页面导航，演示真实库组件，不复制视觉效果。
- 公开索引里每个条目都对应真实示例；新增覆盖不允许 ComingSoon 占位
  （`check_sample_index.sh`）。
- 单一 App 根（`check_single_app_root.sh`）；sample 页面不得挂载第二个
  App/OverlayRoot 或竞争性 Theme 运行时（`check_sample_runtime_boundary.sh`）。
- Insets、主题、i18n、Overlay 与消费方使用同一套运行时。
- 诊断夹具必须具名、可选进入，绝不是正常发布入口。

## 7. 迁移说明

公开 API 变更必须附迁移说明：源码、二进制、行为变更分开列，必要时给出
前后调用对照。不得暗示"重新编译就能修复被删符号或行为变更"。删除或重命名
公开字段由 `binary-compatibility-validator` 阻断（macOS CI 的 `apiCheck`
校验 JVM 与 KLib 基线；验证任务里绝不跑 `apiDump`）。

## 8. 可执行质量门禁

Workflow 实现检查；报告记录结果。任何静态检查都不能证明完整的视觉、无障碍
或性能一致性。

### 8.1 静态门禁（`scripts/ci/`）

除 sample-index（SDK Guard 任务）外，所有 `check_*.sh` 在 Guardrails 任务
执行。

| 检查 | 强制内容 | 局限 |
| --- | --- | --- |
| `check_component_hardcoded_colors.sh` | 语义颜色、冻结遗留基线 | 不证明对比度 |
| `check_component_hardcoded_radius.sh` | 具名形状；间距不得冒充圆角 | 非实时形状渲染 |
| `check_component_hardcoded_elevation.sh` | 具名遗留海拔 | 不禁止 token 驱动的多层 Card 阴影 |
| `check_component_hardcoded_border.sh` | 具名边框宽度 | 不覆盖所有几何分隔 |
| `check_component_hardcoded_spacing.sh` | 遗留字面量不增长 | 既有棘轮债务仍在 |
| `check_component_hardcoded_icon_size.sh` | 具名图标尺寸 | Avatar 尺寸是另一角色 |
| `check_component_static_typography.sh` | 消费主题字体排版 | 不安装字体 |
| `check_legacy_token_pool.sh` | 已删 Float token 池不复活 | 非全部 token 治理 |
| `check_token_compat.sh` | 已审查 token 快照与源码一致 | 基线更新不等于旧二进制兼容 |
| `check_state_param_naming.sh` | 无 enabled/disabled 极性冲突 | 模型标志与展示枚举是两回事 |
| `check_material_surfaces.sh` | 材质共享所有权 | 非模糊/视觉测试 |
| `check_sheet_grabber.sh` | 使用共享面板抓手 | 非拖拽取消测试 |
| `check_safearea_runtime_contract.sh` | Insets 解析器与浮动栏契约 | 还需真机验证 |
| `check_sample_runtime_boundary.sh` | App 运行时入口、无竞争包装 | 源码检查非运行时证明 |
| `check_single_app_root.sh` | sample 单一 App 根 | 不扫描所有外部应用 |
| `check_i18n_default_text.sh` | 语言包外无本地化字面量 | 非翻译质量 |
| `check_english_comments.sh` | 源码注释英文 | 代码字符串另行处理 |
| `check_emoji_as_icon.sh` | 不用文本字形替代图标 | 非实际打包渲染 |
| `check_icon_registry.sh` | 常量/注册表/资源一致 | 安装后仍需检查 |
| `check_ios_pod_resources.sh` | iOS 资源拷贝构建阶段 | 不证明全新安装资源 |
| `check_readme_component_index.sh` | 生成的 README 索引与注册表一致 | 数量不是验收 |
| `check_sample_index.sh` | 路由/示例/组件覆盖 | 非逐示例行为 |

棘轮只能收紧，不能为掩盖回归而放松。确切范围与白名单在各脚本内。

### 8.2 测试与构建

- Guardrails：`python3 scripts/generate_tokens.py --check` 与
  `scripts/tests` Python 测试套件。
- Android CI：`:gearui-kit:testDebugUnitTest`、`:gearui-kit:lintDebug`。
- Web CI：`:gearui-kit:jsBrowserTest`（Chrome 缺失是环境失败，绝不静默
  跳过）。
- macOS CI：`:gearui-kit:apiCheck`；iOS 模拟器 Kotlin 测试走
  `scripts/ios_native_tests.sh`，链接真实 Kuikly 宿主。
- 构建：Android kit/sample、iOS 模拟器 sample 链接、Web 宿主。发布打包
  从确切候选提交检查全部六个 Maven 模块。

### 8.3 手工门禁

输入/键盘行为、弹层关闭与手势打断、主题热切换、大字号、真实资源加载、
读屏器与帧率需要运行时证据，统一在
[QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md) 跟踪；绝不把编译
当成交互验证。

## 9. 新组件清单

1. 目录 + tokens 类；第一行读主题；只用语义 token。
2. API 极性按 §2；受控状态与回调按家族。
3. 按压反馈与无障碍按 §3–4。
4. Sample 示例接入索引；指南按 §5 编写。
5. 注册表条目（如有图标/资源），保证 `check_icon_registry.sh` 与
   `check_readme_component_index.sh` 通过。
6. 更新 [COMPONENT_COVERAGE.zh-Hans.md](./COMPONENT_COVERAGE.zh-Hans.md) 与
   [QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md) 的验收状态。
7. 声称验收前至少在一个平台完成真机验证。
