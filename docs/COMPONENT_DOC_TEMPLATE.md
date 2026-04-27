# 组件文档模板

> SPEC 6.2 Documentation & Examples 的"文档模板"。每个核心组件文档都按本结构产出。
>
> 与 `COMPONENT_TEMPLATE.md`（kotlin 代码骨架）不同：本文档定义**对外文档**结构，不是源码模板。

---

## 文件位置

`docs/components/<component-id>.md`

`<component-id>` 与 `gearui-kit/src/commonMain/kotlin/com/gearui/components/<id>/` 目录名一致（小写、单词紧贴，例如 `button` / `bottomsheet` / `searchbar`）。

## 章节结构（必填）

文档按以下顺序组织。任何章节**必须存在**，没有内容时也要保留标题并写一行说明（例：`> 暂无；详见 ...`），便于全局检索。

```markdown
# 组件名 ComponentName

> 一句话定位（What — 这是什么）。

## 概述

2-4 行描述组件解决的问题、形态特征、与同类组件的差异。

## 何时使用

- bullet 1：典型使用场景。
- bullet 2：能力上限/下限（参数空间/层级支持等）。

## 何时不要使用

- bullet：与之容易混淆但应改用 `OtherComponent` 的情形。

## 最小可用示例

最小依赖、最少参数能跑起来的代码。**只用必填参数**，不展示样式覆盖。

\`\`\`kotlin
import com.gearui.components.<id>.<Symbol>

@Composable
fun Demo() {
    <Symbol>(...)
}
\`\`\`

## 生产推荐示例

接近真实业务的用法，演示典型组合（主题、尺寸、状态、loading、disabled）。

\`\`\`kotlin
@Composable
fun ProdDemo() { ... }
\`\`\`

## 参数

按"必填 → 高频 → 低频"分组，类型与默认值要与源码完全一致。

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `xxx` | `T` | — | 必填，... |
| `yyy` | `T` | `Default` | ... |

## 枚举与常量

列出对外暴露的 enum / sealed / object，说明每个分支的语义和典型选取。

## 常见问题与边界条件

- **Q：触发条件 X 时会发生什么？**
  A：行为说明、是否有预期外副作用、规避方法。
- **边界**：不支持的输入 / 极端值 / 与 Runtime 的协议。

## 相关组件

- `OtherComponent` — 一句话说明区别与选取建议。

## 迁移与变更

- 1.0 之前的破坏性变更（如有）。如无写：`> 1.0 起首次稳定，无历史变更。`
```

## 写作要求

1. **示例可编译**：所有示例必须能在 sample 工程编译通过。优先复用 `sample/src/commonMain/kotlin/com/gearui/sample/examples/<id>/` 下已有片段。
2. **参数表与源码一致**：每次源码参数变更必须同步本文档；CI 后续会按 BCV 输出对照（暂未自动化）。
3. **不写硬编码颜色**：示例代码遵循 SPEC 4.1，不在文档示例里出现 `Color(0x...)`，统一用 `Theme.colors.*`。
4. **Runtime 边界**：涉及 Overlay / safeArea / Theme 的组件文档，"常见问题"必须显式指向 `GEARUI_SPEC_2026.md` §3.5 / §4.4 / §4.5，不要重新解释。
5. **语气**：陈述句为主，避免"建议 / 推荐 / 也许"。能力是固定的，不是建议。

## 索引

`docs/components/README.md` 维护组件文档索引（自动 / 手动皆可，1.0 暂手动）。

## 审查清单（PR 自检）

- [ ] 文件名 = 组件目录名（小写）
- [ ] 8 个章节齐全
- [ ] 最小示例只含必填参数
- [ ] 生产示例从 sample 抽取（或与 sample 一致）
- [ ] 参数表类型 / 默认值与源码一致
- [ ] 列出所有对外 enum / sealed
- [ ] 至少 2 条 Q&A 或边界
- [ ] 没有硬编码颜色 / 字号
