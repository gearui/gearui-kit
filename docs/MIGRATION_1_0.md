# GearUI Kit 1.0 Migration Guide

公开 token API 在 1.0 冻结前完成了一次收口。本文件记录 **被删除的 pre-1.0 过时 API → 1.0 正式 API** 的映射，供下游（`privchat-ui` / `live-chat` / `lms-app` 等）迁移参考。

历史迁移过程见 [_archive/pre-beta3/TOKEN_FREEZE_DECISIONS.md](_archive/pre-beta3/TOKEN_FREEZE_DECISIONS.md)。


## beta3 候选迁移检查

此节描述当前迁移要求，不代表已发布。当前规则以 [SPEC](SPEC.md) 为准。
以下 Batch 13 等章节是历史映射，旧字段数量、默认尺寸和调色板数值不是现行规范。

- 所有消费者重新编译。`apiCheck` 校验的是当前基线，不证明与 beta2 二进制兼容。
- `ButtonType.GHOST` 改为 `TEXT`；默认 Button 仍使用 `PRIMARY`，中性按钮显式传 `DEFAULT`。
- 路由实现 `NavRoute`，用 `NavigatorController<MyRoute>` 固定公共路由类型；
  `entry.route` 直接携带数据，不再维护 `entry.key -> payload` 表。
- Navigator 使用 `initialRoute` 或 `controller` 两个独立入口，不同时传两者；
  不再传自定义 entry key，不自行实现 controller。`popTo` 传路由对象，按 routeName 匹配。
- 返回拦截接收 `PopReason`。`Pending` 的继续/取消流程以当前 NavApi 与导航契约为准，
  不照搬旧 PopRequest 示例。
- `TextStyle` 字体列表/字距、Shapes 和主题调色板等构造器新增字段：更新自定义构造调用及 API 基线。
- Input/Textarea 默认标签在上方；需要旧的横向布局时显式指定。检查窄屏、计数器、校验文案与键盘。
- Card 可使用 token 驱动的多层装饰，不再以“所有卡片必须无阴影”为规则。
- 品牌色与圆角方案独立配置；不要通过切换品牌重置整个主题。
- Kuikly runtime、平台 renderer、KSP 与 iOS Pods 保持 2.28.0 对齐。
- 弹层默认外观改为 HeroUI Native：Dialog 左对齐 + 真实按钮、菜单行按压动画、ActionSheet 单张 sheet、
  遮罩 20% 黑。`DialogAction` / `ActionSheetItem` 等 API 不变，只需重新编译并复查截图。
  `ActionSheetAlign` 默认值由 CENTER 改为 LEFT，需要居中的调用方显式传入。
- `Colors` 新增 `separator`（默认等于 `border`），直接构造 `Colors(...)` 的主题建议显式提供。
- `OverlayDefaults.transitionDurationMillis` 由 const 改为取自 token 的 val（200）；新增 `exitDurationMillis`、`anchorOffset`。
- 继承 `com.gearui.View` 又自行调用 `App(...)` 的页面必须覆写 `autoWrapApp() = false`，否则会出现两层 App（两个 Toast/ActionSheet 宿主）。

当前发布门槛与未验收范围见 [beta3 readiness](BETA3_RELEASE_READINESS.md)。

## 历史迁移映射（非当前默认值表）

## Batch 13A — `Colors` 过时 bridge 字段移除

`com.gearui.theme.Colors` 的全部 `@Deprecated` getter 已删除。`Colors` 现在只保留 24 个语义字段。下游请按下表替换字段访问。

> 说明：`Themes.Light` / `Themes.Dark` 的真实字段值未改动，本次仅删除别名 getter。

| 旧字段（已删除） | 1.0 替换 | 备注 |
|---|---|---|
| `colors.surfaceVariant` | `colors.muted` | |
| `colors.surfaceComponent` | `colors.surface` | |
| `colors.overlay` | `colors.popover` | |
| `colors.mask` | `OverlayDefaults.scrimColor` | 遮罩是 Runtime 层 token，不在 `Colors` |
| `colors.textPrimary` | `colors.foreground` | |
| `colors.textSecondary` | `colors.mutedForeground` | |
| `colors.textPlaceholder` | `colors.mutedForeground` | |
| `colors.textDisabled` | `colors.mutedForeground` | 禁用态语义请优先用对应 `XxxTokens` |
| `colors.textAnti` | `colors.primaryForeground` | |
| `colors.textBrand` | `colors.primary` | |
| `colors.iconPrimary` | `colors.foreground` | |
| `colors.iconSecondary` | `colors.mutedForeground` | |
| `colors.onPrimary` | `colors.primaryForeground` | |
| `colors.primaryHover` | `colors.primary` | 交互态请用 `ComponentTokens`（如 `ButtonTokens.hoverBackground`） |
| `colors.primaryActive` | `colors.primary` | 交互态请用 `ComponentTokens`（如 `ButtonTokens.pressedBackground`） |
| `colors.primaryLight` | `colors.muted` | |
| `colors.primaryDisabled` | `colors.mutedForeground` | |
| `colors.stroke` | `colors.border` | 可编辑控件描边用 `colors.input` |
| `colors.divider` | `colors.border` | |
| `colors.disabled` | `colors.mutedForeground` | 优先用 `ComponentTokens.<role>.disabledForeground` |
| `colors.disabledContainer` | `colors.muted` | 优先用 `ComponentTokens.<role>.disabledBackground` |
| `colors.danger` | `colors.destructive` | |
| `colors.successLight` | `colors.success.copy(alpha = 0.12f)` | 浅底建议下沉到组件 token |
| `colors.warningLight` | `colors.warning.copy(alpha = 0.12f)` | 同上 |
| `colors.dangerLight` | `colors.destructive.copy(alpha = 0.12f)` | 同上 |
| `colors.infoLight` | `colors.info.copy(alpha = 0.12f)` | 同上 |
| `colors.inverseSurface` | `colors.foreground` | 反色面建议用组件 token（如 `ToastTokens.surface`） |
| `colors.inverseOnSurface` | `colors.background` | 同上（如 `ToastTokens.surfaceForeground`） |

## Batch 13B — `Shapes` 过时 bridge 字段移除

`com.gearui.theme.Shapes` 的全部 `@Deprecated` getter 已删除。`Shapes` 现在只保留 6 档语义 scale：`none / sm / md / lg / xl / full`。

> 说明：`ShapesDefault.Default` 的真实值未改动（none=0 / sm=4 / md=6 / lg=8 / xl=12 / full=9999dp）。注意旧 `small`/`large` 实际为 3 / 9dp，新 `sm`/`lg` 为 4 / 8dp，屏幕上视觉差异可忽略。

| 旧字段（已删除） | 1.0 替换 | 备注 |
|---|---|---|
| `shapes.small` | `shapes.sm` | 旧 3dp → 新 4dp |
| `shapes.default` | `shapes.md` | 6dp |
| `shapes.large` | `shapes.lg` | 旧 9dp → 新 8dp |
| `shapes.extraLarge` | `shapes.xl` | 12dp |
| `shapes.round` | `shapes.full` | 9999dp |
| `shapes.circle` | `CircleShape` | 直接用 `Modifier.clip(CircleShape)`，无专用 token |

## Batch 13C — root-package `Spacing` 移除

`com.gearui.Spacing`（root 包，Float 类型，`Spacing.spacer16.dp` 调用形态）整个 object 已删除。唯一公开间距来源是 `com.gearui.foundation.layout.Spacing`（Dp 类型）。

下游请将 `import com.gearui.Spacing` 改为 `import com.gearui.foundation.layout.Spacing`，并按下表替换（注意旧用法带 `.dp` 后缀，新 token 本身已是 `Dp`，无需再 `.dp`）。

| 旧（root，Float） | 1.0 替换（foundation.layout，Dp） |
|---|---|
| `Spacing.spacer4.dp` | `Spacing.xs` (4dp) |
| `Spacing.spacer8.dp` | `Spacing.sm` (8dp) |
| `Spacing.spacer12.dp` | `Spacing.md` (12dp) |
| `Spacing.spacer16.dp` | `Spacing.lg` (16dp) |
| `Spacing.spacer24.dp` | `Spacing.xl` (24dp) |
| `Spacing.spacer32.dp` | `Spacing.xxl` (32dp) |
| `Spacing.spacer40.dp` | `Spacing.xxxl` (40dp) |
| `Spacing.spacer48.dp` | `Spacing.huge` (48dp) |
| `Spacing.spacer64.dp` | `Spacing.massive` (64dp) |
| `Spacing.spacer96` / `Spacing.spacer160` | 无 canonical token；如需该尺寸，请在组件 token 内自行声明 |

## Batch 13D — `ComponentSpecs` 移除

`com.gearui.foundation.ComponentSpecs.kt`（早期尺寸常量池）整个文件已删除，含其中全部 9 个 `*Specs` object。组件级尺寸已在 Batch 2–11 全部迁移到对应的 `XxxTokens` / `XxxDefaults`。

| 旧 object（已删除） | 1.0 替换 |
|---|---|
| `ButtonSpecs` | `ButtonTokens`（`foundation.tokens`） |
| `CellSpecs` | `CellTokens` / `CellDefaults`（`foundation.list`） |
| `AvatarSpecs` | `AvatarTokens` / `AvatarSizeTokens`（`foundation.avatar`） |
| `BadgeSpecs` | `BadgeTokens` / `BadgeSizeTokens`（`foundation.badge`） |
| `DividerSpecs` | `DividerTokens` / `Dividers`（`foundation.layout`） |
| `TabSpecs` | `TabTokens` / `TabSizeTokens`（`foundation.tab`） |
| `CardSpecs` | `CardTokens` / `CardDefaults`（`foundation.list`） |
| `InputSpecs` | `InputTokens`（`foundation.tokens`） |
| `SectionHeaderSpecs` | `SectionTokens`（`foundation.layout`）/ 组件内默认值 |

## Batch 13E — 其余 deprecated API 移除

清理最后一批与四个 bridge 无关的 deprecated API。

### 死代码移除（无替代，本就无人使用）

| 已删除 | 说明 |
|---|---|
| `TagColorTokens`（`foundation.tokens`） | 早期 Tag 颜色常量池（硬编码 ARGB），Tag 组件已直接用 `Theme.colors` 语义色 |
| `TabColors`（`foundation.tab`） | 早期 Tab 颜色数据类，Tab 已直接用 `Theme.colors` |
| `TabColorTokens`（`foundation.tab`） | 同上，预设池 |

### OverlayPlacement 过时枚举项移除

| 旧枚举项（已删除） | 1.0 替换 |
|---|---|
| `OverlayPlacement.TopStart` | `OverlayPlacement.TopLeft` |
| `OverlayPlacement.BottomStart` | `OverlayPlacement.BottomLeft` |

> `OverlayHost` 内部对这两个别名的穷举分支已删除（它们原与 `TopLeft`/`BottomLeft` 同组左对齐），实际 placement 布局行为不变。

---

至此 GearUI Kit 已无任何 `@Deprecated` API，pre-1.0 token 迁移收口完成。

## 追加：feedback 前景 token（additive）

`Colors` 新增 3 个语义字段：`successForeground / warningForeground / infoForeground`（配合既有 `destructiveForeground`），用于彩色实底上的内容色，明暗主题各自取达标对比度的值。

- 通过 `Themes.X.colors.copy(...)` 自定义主题的下游：无需改动（copy 继承新默认值）。
- **直接 `Colors(...)` 构造**自定义主题的下游：必须补这 3 个参数（否则编译报 "No value passed"）。浅色 feedback 配近黑前景、深色 feedback 配白前景，按对比度取值。

## 追加：`foundation.layout.Radius` scale 对齐 `Shapes`

`Radius`（Dp 值）此前是旧 3/6/9/12 scale，与冻结后的 `theme.Shapes`（0/4/6/8/12）不一致，属内部双标准。现已对齐为同名同值：

| 旧字段（已删除） | 1.0 替换 | 值变化 |
|---|---|---|
| `Radius.small` | `Radius.sm` | 3dp → **4dp** |
| `Radius.default` | `Radius.md` | 6dp（不变） |
| `Radius.large` | `Radius.lg` | 9dp → **8dp** |
| `Radius.extraLarge` | `Radius.xl` | 12dp（不变） |
| `Radius.round` | `Radius.full` | 9999dp（不变） |
| （新增）| `Radius.none` | 0dp |
| `Radius.circle` | `Radius.circle` | 9999dp（保留，= full，用于圆形头像/徽章） |

库内仅 `AvatarTokens` 用到 `Radius.circle`（保留，无改动）；四个下游仓库均未使用 `Radius`。

## HeroUI Native default profile (pre-1.0)

This is an intentional visual/default-profile change, not a binary-compatible
patch. Recompile downstream applications after updating the library. No action
family `disabled` parameters are renamed.

- Regular/large fields are 48/56 logical pixels. Adjacent custom controls should
  use `FieldSizeTokens` and `FieldDefaults`, not duplicate the old heights.
- Input labels default to above the control; helper/error/counter content is
  outside the field. Explicit left labels remain available. A fixed outer height
  must budget for supporting text as well as the input area.
- `Shapes.controlLarge` separates large controls from regular fields and sheets.
  `ThemeSpec` now accepts optional button/input palettes. Defaulted constructor
  additions preserve common source calls but do not preserve old compiled ABI.
- Card defaults follow the active theme shape; an explicit radius remains a local
  override. Its thin-border material is a documented renderer fallback, not a
  multi-layer HeroUI shadow implementation.
- BottomSheet's content overload gains an optional header; AutoResizeTextarea
  gains outlined presentation; overlay transition options and swipe-dismiss
  session identity are represented in the API dump. Recompile positional and
  trailing-lambda consumers; Android consumer compilation has been checked.
- `withBrandAccent(color, foreground?)` changes primary, on-primary and focus
  roles only. It preserves surfaces, typography and shapes. The default contrast
  choice is black/white for opaque sRGB colors; an explicit foreground overrides
  it. Transparent brand colors are rejected.

```kotlin
val branded = Themes.Light.withBrandAccent(Color(0xFF12875C))
App(theme = branded, shapes = myShapes) {
    // All controls inherit the brand and shape axes independently.
}
```

`disabledAppearance` is visual only: custom controls must also disable their
click/gesture handling. Read-only is a separate state and is not automatically dimmed.

## Surface And Font Adapter

`TextStyle` appends `fontFamily: List<String> = listOf("system-ui")` and
`letterSpacing: TextUnit = 0.sp`. Existing source calls retain their defaults,
but its generated constructor/copy ABI changes: recompile downstream binaries
against the new kit. API dumps record this intentional change, not proof of
compatibility with older compiled artifacts.

Use `LocalFontRegistry` to map token family names to fonts already installed by
the host. Unknown families fall through in order, then to the platform system font.
Registration does not download or install a font. `LocalSurfaceShadowStyles`
overrides surface/field/overlay stacks independently of brand accent and shape.
Use `DecoratedSurface` with `SurfaceBorder` for non-solid border styles. See
`SURFACE_RENDERING_ACCEPTANCE.md` for rendering and platform limits.
