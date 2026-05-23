# Dark Mode Contrast Audit

Status: **Audit only — no code changed in this document.** Only P0 items may be fixed after review.
Constraints: no token rename, no component API change, no layout refactor.
Scope: 内容色（文字/图标）与其所在背景 token 的配对，在 Light / Dark 两套主题下的对比度与一致性。

## 0. 主题色值参照

| token | Light | Dark |
|---|---|---|
| background | FFFFFF | 09090B |
| foreground | 09090B | FAFAFA |
| surface / card | FFFFFF | 111217 |
| popover | FFFFFF | 1A1C24 |
| muted | F4F4F5 | 1A1C24 |
| mutedForeground | 52525B | A1A1AA |
| **primary** | **18181B（深）** | **FAFAFA（浅）** |
| **primaryForeground** | **FFFFFF（白）** | **09090B（黑）** |
| destructive | DC2626（深红） | F87171（浅红） |
| destructiveForeground | FFFFFF | FFFFFF |
| success | 16A34A（深绿） | 22C55E（浅绿） |
| warning | F59E0B（橙） | F59E0B（橙） |
| info | 2563EB（深蓝） | 60A5FA（浅蓝） |
| border / input | E4E4E7 | 2F3340 |

## 1. 根因（系统性）

1. **`primaryForeground` 在明暗两套主题里翻转**（Light=白 / Dark=黑）。它只适合配 `primary` 背景（primary 也翻转，恒成对比）。一旦被当成"彩色填充上的通用文字色"用在 destructive/success/warning/info 上，明暗两套就会一个对一个错。这正是本会话已修的 **Badge** 与待修的 **Button/Toast/Tag** 的同款问题。
2. **缺少 feedback 前景 token**：只有 `destructiveForeground`，没有 `successForeground / warningForeground / infoForeground`。组件被迫挑 `primaryForeground` 或 `Color.White`，没有正确语义出口。
3. **明暗两套 feedback 色亮度翻转**（深红↔浅红），导致**固定白字或固定黑字都无法两套都达标**：
   - 浅色模式 feedback 偏深 → 配白字 OK（warning 橙除外）。
   - 暗色模式 feedback 偏浅 → 配白字对比不足（白字在 F87171 上仅 ~2.3），配黑字反而高对比但违反惯例。
   - 唯一稳妥解是**主题感知的 on-feedback 前景 token**（需新增 token，见 §4）。

## 2. 风险表

对比度为 WCAG 近似值（正文需 ≥4.5、大字/图标 ≥3、装饰性可放宽）。

| # | 组件 | 背景 token | 内容 token | Light 对比 | Dark 对比 | 等级 | 建议 fix | 需 token 变更 | RC blocker |
|---|---|---|---|---|---|---|---|---|---|
| 1 | **Button** 填充 WARNING | warning(橙) | primaryForeground | **~1.75 ✗** | ~12 ✓(黑字) | **P0** | 浅色下白字不可读；用主题感知 onWarning | 是(§4) | 候选 |
| 2 | Button 填充 DESTRUCTIVE | destructive | primaryForeground | ~5.3 ✓ | ~9 ✓(黑字) | P1 | 暗色黑字违反"危险=白字"惯例 | 是/可用白 | 否 |
| 3 | Button 填充 SUCCESS | success | primaryForeground | ~3.0 △ | ~12 ✓(黑字) | P1 | 浅色白字勉强、暗色黑字不一致 | 是 | 否 |
| 4 | **Toast** SUCCESS/WARNING/ERROR | success/warning/destructive | primaryForeground | warning ✗ / 其余 △✓ | 黑字 ✓ | **P0**(warning)/P1 | 同 Button；warning 浅色不可读 | 是 | 候选 |
| 5 | **Tag** DARK 变体 + SUCCESS/WARNING/DANGER | 主题色实底 | primaryForeground（注释写"白色"实暗色为黑） | warning ✗ / 其余 △✓ | 黑字 ✓ | **P0**(warning)/P1 | 同上 | 是 | 候选 |
| 6 | ActionSheet 危险项 badge | destructive | primaryForeground | ✓ | 黑字 ✓ | P1 | 暗色黑字违反惯例 | 是/可用白 | 否 |
| 7 | Progress 条内文字(>50%) | progressColor(可为 feedback) | primaryForeground | feedback 时同上 | 同上 | P1/P2 | 条内文字本就少见 | 是 | 否 |
| 8 | Badge 彩色徽标 | destructive/success/... | **Color.White**（本会话已改） | ✓ | 白字在 F87171 ~2.3 △ | P1 | 已修黑→白；暗色浅红上白字仍偏弱，待 onFeedback | 是 | 否 |
| 9 | Toast INFO | foreground(暗色=白) | background(暗色=黑) | ✓ | ✓ | OK | 反色 chip，刻意；可读 | 否 | 否 |
| 10 | Snackbar/Notification | success/..copy(0.12) 浅底 | foreground / 主题色 | ✓ | ✓ | OK | 浅底+深字，明暗都可读 | 否 | 否 |
| 11 | overlay scrim | — | Color(0x8C000000) 纯黑55%（本会话已改） | ✓ | ✓ | OK | 已修（原近黑 09090B 在暗色不压暗） | 否 | 否 |
| 12 | disabled/placeholder | surface/muted | mutedForeground | ✓ | A1A1AA on 111217 ~4.8 ✓ | P2 | 暗色可读，层次偏弱 | 否 | 否 |
| 13 | dark popover/tooltip | popover 1A1C24 | popoverForeground FAFAFA | ✓ | ✓ | OK | 正常 | 否 | 否 |

> 已核：DatePicker「确定」按钮 = `primary` 实底 + `primaryForeground` 文字 → 配对正确（primary 与 primaryForeground 明暗同步翻转），无白底白字问题。

## 3. 分级小结

- **P0（不可读）**：#1/#4/#5 的 **warning 填充 + 白字（浅色模式，对比 ~1.75）**。（DatePicker 确定按钮已核为正确，非 P0。）
- **P1（可读但违反惯例 / 明暗不一致）**：#2/#3/#5/#6/#8 的彩色实底 + primaryForeground（暗色黑字）。
- **P2（层次偏弱，不影响可读）**：#12 disabled/muted。
- **已修（本会话）**：Badge(#8 黑→白)、scrim(#11)。

> **已实施（fix(theme): add feedback foreground tokens）**：采纳 §4 根治方案，新增 `successForeground / warningForeground / infoForeground` 主题感知 token（按 WCAG 对比度取值），并把 Button(FILL) / Toast / Tag(DARK) / Progress(条内) / Badge / ActionSheet 危险 badge 的填充态文字从误用的 `primaryForeground` 改为对应 `xxxForeground`（destructive 用既有 `destructiveForeground`）。P0（warning 填充白字不可读）已消除；P1（暗色实底黑字违反惯例）一并解决。下游主题（sample DarkPurple、lms 模板）已补值。

## 4. 建议的根治方案（已实施）

新增主题感知的 on-feedback 前景 token（**additive，非 rename**）：
```
successForeground / warningForeground / infoForeground
```
每个在 Light/Dark 分别取与该主题 feedback 实底对比达标的颜色（如 warning 橙底统一配深色文字、destructive 配白、暗色浅红可改用更深的 destructive 实底 + 白字）。组件填充态文字统一改读对应 `xxxForeground`，彻底消除 `primaryForeground` 误用。

或退一步（不加 token）：把"彩色实底文字"统一为 `destructiveForeground`(白) 用于 destructive/success/info，warning 单独深字——但 warning 暗色与 destructive 暗色仍有白字偏弱问题，不如新增 token 干净。

## 5. RC 闸门建议

- **P0 必须在 1.0 RC 前处理**（warning 填充白字不可读、潜在白底白字）。
- P1/P2 进 1.0 后的视觉 polish。
- 根治依赖 §4 的 token 新增；若该决策未就绪，P0 可先做**最小修**：warning 填充态文字改深色、DatePicker 确定按钮显式 `primaryForeground`（primary 实底正确）。
