# GearUI 设计系统

[English](./DESIGN_SYSTEM.md) | [简体中文](./DESIGN_SYSTEM.zh-Hans.md)

本文档拥有 token 管线与主题规则。GearUI 自己的视觉规则在
[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)；数值默认值以
`tokens/` 源 JSON 和生成代码为准——绝不维护第二套文字版数值表。

## 1. Token 管线

```text
DTCG 2025.10 源 JSON（tokens/）
  → scripts/generate_tokens.py
    → 生成的 Kotlin 常量
      → primitive 值 → 语义角色 → 组件角色
```

- DTCG 2025.10 是交换格式。`tokens/README.md` 拥有确切格式、解析器、颜色
  转换与渲染器限制。
- 修改源 JSON 后重新生成；不得手改生成的 Kotlin。源码、生成代码与已审查
  token 快照必须一致（`check_token_compat.sh`）。
- 未知或不支持的值必须显式报错，或走文档化的转换策略。渲染器限制不得伪装
  成"非法 token"。
- 已删除的 Float token 池不得复活（`check_legacy_token_pool.sh`）。

## 2. 独立主题轴

品牌色、形状风格、明暗模式是三根独立的轴：

- 换品牌色不得改变形状、尺寸、字体、动效或导航；换形状不得重置品牌色。
- 圆角/直角预设共享同一套组件状态机与结构。刻意的圆形、胶囊和固定指示器
  保留独立语义角色。
- 蓝色是可接受的内置品牌色，不是平台限制。
- 应用级覆盖向后代和 Overlay 传播。显式的组件级覆盖必须保留，不得在主题
  切换时被静默重新推导。
- 主题更新不得重置导航、编辑器文本/焦点，不得关闭已打开的 Overlay。
- Token 只调样式；JSON 文件替代不了布局、手势或生命周期。目前并非所有几何
  角色都可运行时注入——这是实现缺口，不是"全部可热替换"的许可。

## 3. 颜色与表面角色

使用业务中立的 `Colors` 配对：background/content、surface/content、
card/content、popover/content、brand/content 及语义状态色/content。
公开字段名以源码和已审查 API 快照为准，不另立文字版 schema。

- 页面背景、内容表面、浮动表面是三个不同角色。
- 次级文本色不是边框色。禁用边框不得因复用弱文本色而比可用边框更深。
- Error 是语义校验色，不是红色品牌色。Loading、选中、只读、禁用是不同状态。
- 产品概念（如聊天气泡）属于应用层主题扩展，不进 kit。
- 自定义 `ThemeSpec.buttonColors` / `inputColors` 是显式覆盖。从新基色派生
  时使用派生 API；不要复制内置主题却意外保留其旧组件配色。

组件层只读语义 token：禁止硬编码颜色、圆角、海拔、边框宽度、图标尺寸和
间距字面量（六个 CI 棘轮强制执行，见
[COMPONENT_SPEC.zh-Hans.md](./COMPONENT_SPEC.zh-Hans.md)）。

## 4. 几何与字体

数值默认值在 `ControlGeometry`、token JSON 和生成的 profile 中。

- 组件使用语义化的 shape/size/spacing/border 角色——绝不复制字面量，绝不
  拿 Spacing 值冒充圆角或海拔。间距描述布局；尺寸角色描述控件几何。数值
  恰好相同的无关轴不得联动。
- 组件读 `Theme.typography`。`TextStyle` 携带字体族候选、字重、字号、行高
  与字距。
- `LocalFontRegistry` 把自定义名称映射到宿主已安装的字体；它不加载资源。
  不支持的自定义字体回落到通用/系统候选。不承诺跨 OS 字体度量一致。
- 大字号与长翻译不得截断控件或遮挡操作，需单独验证。

## 5. 动效

时长来自 motion 刻度（毫秒）：`instant` 0、`fast` 100、`normal` 150、
`slow` 200、`emphasized` 250——微交互用 fast，一般状态变化用 normal，
弹层/对话框展现用 slow，底部面板落定与大场景切换用 emphasized。弹层进出场
由运行时拥有。组件不得自造一次性时长；用刻度 token，或通过组件自己的
`XxxTokens` 类暴露。弹簧参数已 token 化。减弱动效去掉的是移动，不是状态
反馈。

## 6. 材质与阴影

Card 与 MaterialSurface 共享 `DecoratedSurface`：有序 outer/inset 阴影栈、
带符号 spread、矩形/圆角轮廓上的复杂边框。模糊是有界高斯边缘近似，默认
关闭（`MaterialPolicy.Never`），等待上游达成一致——四渲染器缺口清单见
[VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md)。
`LocalSurfaceShadowStyles` 独立于品牌色与形状覆盖阴影栈。

遗留单海拔消费方在显式迁移前使用具名 Elevation 角色。"所有卡片必须扁平"
和"每个组件都有新阴影"都不是规则；每次迁移记录视觉证据。

## 7. 运行时 Insets 不是 Token

`safeArea` 与 `keyboard` 几何量是来自 `RuntimeEnvironment` 的环境测量值，
绝不是样式常量。不要把设备边框编码进 token JSON。

## 8. 治理

- 规则只有一个所有者：本文档拥有主题规则；VISUAL_SPEC 拥有视觉规则；
  `tokens/README.md` 拥有格式策略。
- 棘轮只能收紧，不能放松。零基线检查是硬门禁。
- 公开 token API 变更需要迁移说明（见
  [QUALITY_STATUS.zh-Hans.md](./QUALITY_STATUS.zh-Hans.md)）并审查 API 基线；
  删除或重命名 strings/token 字段是 breaking change，由
  `binary-compatibility-validator` 阻断。
