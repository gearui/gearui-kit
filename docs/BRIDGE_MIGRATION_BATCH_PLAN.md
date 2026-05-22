# Bridge Migration Batch Plan (Batch 8–13)

Status: **Plan only — no code migrated, no call sites moved, no bridge removed.**
Basis: `docs/RC_DEPRECATED_BRIDGE_REMOVAL_AUDIT.md` (RC-0).
Goal: re-group the remaining **47 `gearui-kit` files + 71 `sample` files** into executable waves so every target bridge (`Colors / Shapes / root Spacing / ComponentSpecs`) can eventually reach **lib 0 + sample 0** and be removed in Batch 13.

## 0. RC-0 reference numbers

| 指标 | 值 |
|---|---:|
| `gearui-kit` commonMain Android deprecation warnings | **455** |
| `sample` commonMain deprecation warnings | **1197** |
| 库内仍调用 bridge 的实现文件 | **47** |
| sample 仍调用 bridge 的文件 | **71** |

Bridge buckets (lib / sample):
Colors **276 / ~885** · Shapes **60 / ~76** · root Spacing **77 / 177** · ComponentSpecs **18 / 35** · 其他 deprecated API **24 / 24**.

## 1. Reconciliation with proposed family list

实测库内只有以下文件存在 deprecation；据此校正提案分组：

- **不存在的组件**（提案中列出但仓库无此文件，已剔除，无 scope）：`Alert`, `TimePicker`, `ColorPicker`, `Upload`, `Breadcrumb`, `ImagePreview`。
- **TabRow**：`primitives/TabRow.kt` 存在但 **0 deprecation**，已 clean，本计划无动作（仅回归验证）。
- **提案族未覆盖、本计划补入的 straggler**：`SegmentedControl`、`Drawer`、`Tag`、`Image`/`ImageViewer`、`SwipeCell`、`Watermark`、primitives `Tab`/`Divider`/`Avatar`/`Badge`、foundation `tokens/ComponentTokens`/`tab/TabTokens`/`tab/TabColors`/`layout/DividerTokens`/`primitives/Text`/`primitives/BasicTextField`、`overlay/OverlayHost`。每个 straggler 的归属与理由见 §2 各 batch 与 §5。

> 关键发现（来自 RC-0）：`primitives/Avatar.kt` 组件本体仍用 `AvatarSpecs`+Colors bridge——Batch 2 只迁了 `AvatarTokens`，本体漏迁，本计划在 Batch 10 补。

## 2. Batch 8–13 分族表

| Batch | 族 | 库内文件数 | 库内 warning | 推进的 bucket |
|---|---|---:|---:|---|
| 8 | Feedback | 9 | 98 | Colors, Shapes, root Spacing |
| 9 | Navigation / Disclosure + Tab 族 | 13 | 120 | Colors, Shapes, root Spacing, **ComponentSpecs(TabSpecs)** |
| 10 | Complex data display + 显示 primitives + foundation 池 | 16 | 116 | Colors, Shapes, root Spacing, **ComponentSpecs(DividerSpecs/AvatarSpecs)**, 其他(TagColorTokens) |
| 11 | Complex input / selector | 8 | 113 | Colors, Shapes |
| 12 | Sample migration | (71 sample) | (1197 sample) | 全部 bucket 的 sample 侧 |
| 13 | Bridge removal | — | — | 删除四 bridge + 其他 deprecated API + BCV/baseline |

库内合计 9+13+16+8 = **46** 文件 / 98+120+116+113 = **447** warning；加 `overlay/OverlayHost`(8，归 Batch 13 其他 API 轨) = **47 文件 / 455 warning**，与 RC-0 对齐。

---

### Batch 8 — Feedback family

**Scope（9 文件）：**
`toast/Toast`(17), `snackbar/Snackbar`(15), `notification/Notification`(14), `progress/Progress`(11), `skeleton/Skeleton`(22), `result/Result`(5), `empty/EmptyState`(6), `loading/Loading`(2), `backtop/BackTop`(6)。

**Forbidden scope：** 不改 Overlay runtime；不改 Dialog/Popup/Sheet/ActionSheet/Select/Popover/ContextMenu（Batch 5 已封）；不改 navigation/输入族；不动 `OverlayHost`。Toast/Snackbar/Notification 若经由 overlay 渲染，**只迁颜色/形状/间距 token，不碰 overlay 宿主与 scrim**。

**Expected bucket impact：** Colors −（Toast/Snackbar/Notification/Progress/Result/EmptyState/Loading/BackTop 的 text*/danger/inverse* 等）；Shapes −（EmptyState/Notification/Progress/Snackbar 的 small/default）；root Spacing −（Notification/Skeleton/Snackbar/Toast）。预计库内 −98。无 ComponentSpecs 变化。

**Merge criteria：** 9 文件 deprecation 归零；§3 验证矩阵全过；无 public API 变更（如需扩展须单列说明）。

---

### Batch 9 — Navigation / Disclosure + Tab family

**Scope（13 文件）：**
`tabs/Tabs`(15), `navigationmenu/NavigationMenu`(25), `pagination/Pagination`(16), `steps/Steps`(10), `stepper/Stepper`(16), `collapse/Collapse`(9), `anchor/Anchor`(1), `tour/Tour`(4), `segmented/SegmentedControl`(8), `primitives/Tab`(6), `foundation/tab/TabTokens`(5), `foundation/tab/TabColors`(1), `components/drawer/Drawer`(4)。
TabRow 已 clean，仅回归。

**Forbidden scope：** 不改 navigation API 大语义（路由/选中模型不动）；不改 Overlay runtime（Drawer 仅迁 token，不重写其 overlay 宿主/手势）；不顺手重构 Tab 交互。

**Expected bucket impact：** Colors −；Shapes −（Collapse/NavigationMenu/Pagination/Stepper/Tabs）；root Spacing −（NavigationMenu/Pagination/Tabs）；**ComponentSpecs：清空 `TabSpecs`**（`primitives/Tab` + `foundation/tab/TabTokens`）；`TabColors` deprecated 构造器（其他 API）一并处理。预计库内 −120。

**Merge criteria：** 13 文件归零；TabSpecs 库内引用为 0；§3 验证矩阵全过；任何 API 扩展单列。

---

### Batch 10 — Complex data display + display primitives + foundation pool

**Scope（16 文件）：**
`table/Table`(9), `tree/Tree`(6), `timeline/Timeline`(7), `watermark/Watermark`(1), `swiper/Swiper`(7), `swipecell/SwipeCell`(7), `image/Image`(12), `imageviewer/ImageViewer`(2), `tag/Tag`(13), `primitives/Avatar`(5), `primitives/Badge`(3), `primitives/Divider`(6), `foundation/layout/DividerTokens`(3), `foundation/tokens/ComponentTokens`(29), `foundation/primitives/Text`(3), `foundation/primitives/BasicTextField`(3)。

**Forbidden scope：** 保守迁移——这些组件硬编码多，**只做 token 映射，不改数据展示布局语义 / 列模型 / 虚拟化**；不重构 Badge primitive 结构（仅颜色/尺寸 token）；`ComponentTokens.kt` 是 live 池（被 Input/Tag 依赖），只迁其内部 root Spacing / `TagColorTokens` deprecated 构造器，不重排其公开 token 形状。

**Expected bucket impact：** Colors −；Shapes −（Tag）；root Spacing −（ComponentTokens）；**ComponentSpecs：清空 `DividerSpecs`（Divider+DividerTokens）与 `AvatarSpecs`（Avatar 本体补迁）**；其他 API：清 `TagColorTokens` deprecated 构造器（ComponentTokens 内）。预计库内 −116。

**Merge criteria：** 16 文件归零；DividerSpecs / AvatarSpecs 库内引用为 0；§3 验证矩阵全过；`ComponentTokens` 公开形状不变（如变更须 BCV + 说明）。

---

### Batch 11 — Complex input / selector family

**Scope（8 文件）：**
`picker/DatePicker`(44), `picker/Picker`(7), `calendar/Calendar`(12), `calendar/CalendarPopup`(3), `treeselect/TreeSelect`(20), `cascader/Cascader`(18), `transfer/Transfer`(6), `rate/Rate`(3)。
TimePicker/ColorPicker/Upload 不存在，无 scope。

**Forbidden scope：** 风险最高，最保守——不改选择器交互/弹层行为/数据回填语义；CalendarPopup 经 overlay 渲染时只迁 token，不碰 overlay 宿主；不改 Input/Form（Batch 4 已封）。

**Expected bucket impact：** Colors −（DatePicker/TreeSelect/Cascader/Calendar 等大头）；Shapes −（Cascader/DatePicker/Picker/TreeSelect）。预计库内 −113。至此**库内 455 应全部归零**（仅剩 `OverlayHost` 8 条 → Batch 13）。

**Merge criteria：** 8 文件归零；库内 deprecation 仅剩 OverlayHost 的 OverlayPlacement enum 自警告；§3 验证矩阵全过。

---

### Batch 12 — Sample migration

**Scope：** sample 71 文件 / 1197 次，按组件族镜像 Batch 8–11 顺序迁移（每个 `*Example.kt` / `pages/*` 对应其组件族）。前列：`FormExample`, `TableExample`, `SidebarComponents`, `SettingsPage`, `TextExample`, `SliderExample`, `ImageExample`, `BadgeExample`, `ProgressExample` …

**Forbidden scope：** **必须库内先归零再迁 sample**——先改 sample 会掩盖库内问题。sample 不引入新组件用法、不改 demo 业务逻辑，仅 token 映射。

**Expected bucket impact：** sample 侧 Colors/Shapes/root Spacing/ComponentSpecs 全部 −，目标 sample 1197 → 0。

**Merge criteria：** sample deprecation 归零；§3 验证矩阵全过（sample Android 实机/模拟器可跑）。

---

### Batch 13 — Bridge removal（仅当前置 bucket 全归零）

**前置硬条件：** 对应 bucket 必须 **lib 0 + sample 0**（见 §4）。

- **13A** remove **Colors** bridge — 删除 `theme/Colors.kt` 全部 @Deprecated getter。
- **13B** remove **Shapes** bridge — 删除 `theme/Shapes.kt` 全部 @Deprecated getter。
- **13C** remove **root Spacing** — 删除 `com.gearui.Spacing`（root 包）。
- **13D** remove **ComponentSpecs** — 删除 `foundation/ComponentSpecs.kt` 残留 object（TabSpecs/DividerSpecs/AvatarSpecs 等全部）。
- **13E** **其他 deprecated API + BCV/baseline**：删除 `OverlayPlacement.TopStart/BottomStart` deprecated 枚举项（同步改 `OverlayHost` 的 when 回溯分支）、`TabColors`/`TagColorTokens` deprecated 构造器；`apiDump` 刷新 `gearui-kit.api` + `gearui-kit.klib.api`；刷新 token snapshot baseline；写 migration notes（字段重命名映射表）写入 CHANGELOG / 迁移文档。

**Forbidden scope：** 任一 bucket 未双归零，对应 13x 不得执行。

**Merge criteria：** `gearui-kit` + `sample` deprecation = 0；BCV 基线刷新且 apiCheck 过；CHANGELOG 含完整迁移映射。

## 3. Validation matrix（每个 batch 落地前必过，固定）

1. `bash scripts/ci/check_token_compat.sh` → exit 0
2. `bash scripts/ci/check_component_hardcoded_colors.sh` → new=0
3. `./gradlew :gearui-kit:apiCheck`（无 public API 变更；有则 apiDump + 说明）
4. `./gradlew :gearui-kit:compileKotlinMetadata`
5. `./gradlew :gearui-kit:compileKotlinIosSimulatorArm64`（iOS Simulator）
6. `./gradlew :sample:compileDebugKotlinAndroid`（sample Android）
7. **deprecation count delta**：记录 batch 前后 `gearui-kit`（必要时含 `sample`）warning 数，更新 `TOKEN_FREEZE_DECISIONS.md` burn-down 表。

## 4. 每个 bridge 的解锁条件

| Bridge | 解锁条件（必须同时满足） | 依赖完成的 batch |
|---|---|---|
| **Colors** | lib Colors 引用 0 **且** sample Colors 引用 0 | Batch 8,9,10,11（lib）+ 12（sample） |
| **Shapes** | lib Shapes 引用 0 **且** sample Shapes 引用 0 | Batch 8,9,10,11（lib）+ 12（sample） |
| **root Spacing** | lib root-Spacing 引用 0 **且** sample 0 | Batch 8,9,10（lib，含 ComponentTokens）+ 12 |
| **ComponentSpecs** | lib `TabSpecs/DividerSpecs/AvatarSpecs` 引用 0 **且** sample 0 | Batch 9（TabSpecs）+ 10（Divider/Avatar）+ 12 |

> 因 sample 用到全部 4 个 bucket，**任何 bridge 在 Batch 12 完成前都无法删除**。Batch 13 必须排在 8–12 全部完成之后，且逐 bridge 复核「lib 0 + sample 0」。

## 5. Straggler 归属说明（偏离提案处）

| 文件/组件 | 归入 | 理由 |
|---|---|---|
| `SegmentedControl` | Batch 9 | 属导航/披露交互族 |
| `Drawer` | Batch 9 | 抽屉=披露式导航；但 overlay 渲染，仅迁 token 不碰宿主 |
| `Tag`, `Image`, `ImageViewer`, `SwipeCell`, `Watermark` | Batch 10 | 数据/内容展示族 |
| primitives `Avatar`, `Badge`, `Divider`, `Tab` | Batch 9(Tab) / 10(其余) | Tab 随 TabSpecs 入 9；显示 primitives 入 10 |
| foundation `ComponentTokens`, `DividerTokens`, `TabTokens`, `TabColors`, `Text`, `BasicTextField` | Batch 9(Tab*) / 10(其余) | 与各自族的 token/primitive 同批，推进对应 bucket |
| `overlay/OverlayHost` (8) | Batch 13E | 非四目标 bridge，是 OverlayPlacement 枚举自警告，随枚举废弃项一并删 |

## 6. 执行纪律

- 沿用 Batch 3–7 的映射规则（`textPrimary→foreground`、`textSecondary/textPlaceholder/textDisabled→mutedForeground`、`danger→destructive`、`surfaceVariant→muted`、shapes `small→sm/default→md/large→lg`、root `Spacing.spacerN.dp → foundation.layout.Spacing.<token>`，overlay scrim → `OverlayDefaults.scrimColor`）。
- 每族保持**一个独立 commit**，直接在 main 提交并推送。
- **能不扩 API 就不扩；** 任何 public API 扩展/变更须单列说明并刷 BCV。
- 本文件为计划；进入任一 batch 前以此为准，不在规划阶段改代码。
