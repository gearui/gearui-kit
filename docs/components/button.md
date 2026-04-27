# Button

> 触发一次具体动作的命令组件。

## 概述

Button 用来发起一次明确、闭环的用户动作（"提交"、"删除"、"购买"），强调一次点击对应一个结果。它覆盖填充 / 描边 / 文字 / 幽灵四种形态，4 主题色 + 4 尺寸 + 5 形状的正交参数组合，并内建 loading / disabled / block / 图标位置等业务高频能力。

## 何时使用

- 表单提交、确认对话框中的主操作 / 次操作。
- 列表 / 卡片中的单步操作（购买、关注、删除）。
- 工具栏中的图标按钮（`text = ""` + `icon` + `shape = SQUARE/CIRCLE`）。

## 何时不要使用

- 选项切换（多选/单选） → 用 `Checkbox` / `RadioButton` / `Switch`。
- 跳转性质的链接（无副作用） → 用 `Anchor` 或 `type = TEXT` 的 Button 但语义上是导航的，请直接用 `Anchor`。
- 在导航栏左右两侧的图标入口 → 用 `NavBar` 的 `leading` / `trailing` 槽，不要塞独立 Button。

## 最小可用示例

```kotlin
import com.gearui.components.button.Button

@Composable
fun Demo() {
    Button(
        text = "确认",
        onClick = { /* ... */ }
    )
}
```

## 生产推荐示例

来自 `sample/.../button/ButtonExample.kt`：

```kotlin
import com.gearui.components.button.*
import com.gearui.components.toast.Toast

@Composable
fun PurchaseBar(loading: Boolean, onBuy: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            text = "加入购物车",
            onClick = { Toast.show("已加入") },
            theme = ButtonTheme.DEFAULT,
            type = ButtonType.OUTLINE,
            block = true,
            modifier = Modifier.weight(1f)
        )
        Button(
            text = "立即购买",
            onClick = onBuy,
            theme = ButtonTheme.PRIMARY,
            type = ButtonType.FILL,
            loading = loading,
            block = true,
            modifier = Modifier.weight(1f)
        )
    }
}
```

## 参数

### 必填

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `onClick` | `() -> Unit` | — | 点击回调。`disabled` 或 `loading` 时不会触发。 |

### 高频

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `text` | `String` | `""` | 按钮文字。空串 + 提供图标 → 进入"icon-only"模式。 |
| `theme` | `ButtonTheme` | `PRIMARY` | 主题色，见下方枚举。 |
| `type` | `ButtonType` | `FILL` | 视觉形态，见下方枚举。 |
| `size` | `ButtonSize` | `MEDIUM` | 高度档位：48 / 40 / 32 / 28 dp。 |
| `disabled` | `Boolean` | `false` | 不可点击且降为禁用色。 |
| `loading` | `Boolean` | `false` | 显示进度圈，禁用点击；不影响布局尺寸。 |
| `block` | `Boolean` | `false` | 通栏，使用 `fillMaxWidth()`。 |

### 低频

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | `Modifier` | 外部布局参数。Button 自身已固定 `height`，外部不要再覆写。 |
| `shape` | `ButtonShape` | `RECTANGLE` | 形状档，见下方枚举。 |
| `icon` | `String?` | `null` | 图标名（走 `FoundationIcon` 体系，preferSvg）。 |
| `iconWidget` | `(@Composable () -> Unit)?` | `null` | 自定义图标 slot。同时提供时优先于 `icon`。 |
| `iconPosition` | `ButtonIconPosition` | `LEFT` | 图标在文字左 / 右。 |
| `iconTextSpacing` | `Dp` | `8.dp` | 图标与文字间距。 |

## 枚举与常量

### `ButtonTheme`
- `PRIMARY`：品牌色（默认）
- `DANGER` / `WARNING` / `SUCCESS`：状态色，对应 `Theme.colors.danger/warning/success`
- `DEFAULT`：中性灰，用于次操作
- `LIGHT`：浅色品牌底，用于强主题页面的次按钮

### `ButtonType`
- `FILL`：实心，主操作
- `OUTLINE`：描边，次操作
- `TEXT`：纯文字，弱操作 / 表单内
- `GHOST`：透明背景 + 主题文字，深色背景上叠加用

### `ButtonSize`
| 档位 | 高度 | 横向 padding | 文字样式 |
| --- | --- | --- | --- |
| `LARGE` | 48 dp | 20 dp | `Typography.BodyLarge` |
| `MEDIUM` | 40 dp | 16 dp | `Typography.BodyMedium` |
| `SMALL` | 32 dp | 12 dp | `Typography.BodySmall` |
| `EXTRA_SMALL` | 28 dp | 8 dp | `Typography.BodySmall` |

### `ButtonShape`
- `RECTANGLE`：8 dp 圆角（默认）
- `ROUND`：高度的一半，圆角胶囊
- `SQUARE`：8 dp 圆角；与空 `text` + `icon` 组合形成正方形按钮（宽 = 高）
- `CIRCLE`：完全圆形；与空 `text` + `icon` 组合形成圆形图标按钮
- `FILLED`：胶囊（与 `ROUND` 视觉相同，语义上表示填充态）

### `ButtonIconPosition`
- `LEFT` / `RIGHT`：图标相对文字的位置。

## 常见问题与边界条件

- **Q：`disabled` 和 `loading` 同时为 true 时优先级？**
  A：两者都会阻断点击。视觉上 `loading` 优先呈现进度圈；`disabled` 影响颜色降级（`textDisabled` / `disabledContainer`）。

- **Q：icon-only 模式如何触发？**
  A：`text` 为空且 `icon` 或 `iconWidget` 非空时进入；当 `shape` 为 `SQUARE` / `CIRCLE` 时按钮宽度强制等于高度（正方形 / 圆形），其他 shape 仍按内容宽度。

- **Q：能否覆盖 Button 的高度？**
  A：不能。`size` 档位是 SPEC 6.1 API 一致性的硬约束；外部 `modifier.height(...)` 会被组件内的 `.height(height)` 覆盖。如需非标准高度，请改用 Foundation 层组件。

- **Q：能否传自定义颜色？**
  A：不能，Button 完全 token 化。要换主题色用 `theme`，要换语义角色（如品牌迁移到红色）改 `Theme.colors.primary`。SPEC 11.1 §2 禁止组件层硬编码颜色。

- **边界**：`block = true` 与 `shape = CIRCLE/SQUARE` 同时设置时，`block` 优先（按宽度铺满），`SQUARE/CIRCLE` 的 1:1 约束失效。这是已知组合，不要混用。

- **边界**：`loading = true` 时 `onClick` 完全不会触发；外层不要叠 `clickable` 试图绕过。

## 相关组件

- `Anchor` — 语义为"导航 / 链接"时用，不要用 `type = TEXT` 的 Button 替代。
- `Switch` / `Checkbox` — "状态切换"用，不要用 Button 模拟。
- `IconButton`（暂未独立提供）— 由 Button + `text = ""` + `shape = CIRCLE/SQUARE` 实现。

## 迁移与变更

> 1.0 起首次稳定，无历史变更。
