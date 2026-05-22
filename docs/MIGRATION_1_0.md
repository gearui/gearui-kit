# GearUI Kit 1.0 Migration Guide

公开 token API 在 1.0 冻结前完成了一次收口。本文件记录 **被删除的 pre-1.0 过时 API → 1.0 正式 API** 的映射，供下游（`privchat-ui` / `live-chat` / `lms-app` 等）迁移参考。

迁移过程见 `docs/TOKEN_FREEZE_DECISIONS.md` 的 burn-down 表（Batch 1–13）。

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
