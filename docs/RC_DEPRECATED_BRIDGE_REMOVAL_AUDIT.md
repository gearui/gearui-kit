# RC Deprecated Bridge Removal Readiness Audit (RC-0)

Status: **NOT READY** — bridge removal is blocked.
Scope: read-only inventory. No bridge removed, no component code changed in this audit.
Snapshot基线: `gearui-kit` commonMain Android = **455** deprecation warnings; `sample` commonMain = **1197** deprecation warnings.
采集命令:
```
./gradlew :gearui-kit:compileDebugKotlinAndroid --rerun-tasks 2>&1 | grep "is deprecated"
./gradlew :sample:compileDebugKotlinAndroid    --rerun-tasks 2>&1 | grep "is deprecated"
```

## 0. 一句话结论

Batch 1–7 只迁移了约 18 个组件文件。**仍有 47 个 `gearui-kit` 实现文件 + 71 个 `sample` 文件在调用 deprecated bridge**。`Colors / Shapes / root Spacing / ComponentSpecs` 四个 bridge **没有一个**当前可以安全删除——任何一个删掉都会让 `:gearui-kit` 或 `:sample` 编译失败。

进入真正的 RC cleanup 之前，必须先做一轮「Batch 8+ 组件迁移」把剩余 47 个库内文件清干净，并同步迁移 sample。

## 1. 剩余 455 条按来源分组（gearui-kit 模块）

| Bridge 来源 | 定义位置 | gearui-kit | sample | 可删除? |
|---|---|---:|---:|---|
| **Colors bridge** | `theme/Colors.kt` @Deprecated getters | 276 | ~885 | ❌ 阻断 |
| **Shapes bridge** | `theme/Shapes.kt` @Deprecated getters | 60 | ~76 | ❌ 阻断 |
| **root Spacing** | `com.gearui.Spacing`（root 包，Float） | 77 | 177 | ❌ 阻断 |
| **ComponentSpecs** | `foundation/ComponentSpecs.kt`（剩 TabSpecs/DividerSpecs/AvatarSpecs） | 18 | 35 | ❌ 阻断 |
| **其他 deprecated API** | 见 §1.5 | 24 | 24 | ❌ 非本次四目标，但仍有调用点 |
| **合计** | | **455** | **1197** | |

### 1.1 Colors bridge（276）

涉及的 deprecated getter（按库内出现次数）：
`textPrimary 57 / textSecondary 52 / surfaceVariant 35 / textDisabled 26 / textAnti 17 / danger 16 / textPlaceholder 15 / stroke 14 / onPrimary 13 / disabled 7 / disabledContainer 6 / inverseSurface 4 / mask 3 / inverseOnSurface 3 / warningLight 2 / successLight 2 / dangerLight 2 / primaryLight 1 / divider 1`

覆盖 **42 个**库内文件。这是最大的阻断面。

### 1.2 Shapes bridge（60）

`small 36 / default 19 / large 5`。覆盖文件：
Cascader, Collapse, EmptyState, NavigationMenu, Notification, Pagination, picker/DatePicker, picker/Picker, Progress, Snackbar, Stepper, Tabs, Tag, Toast, TreeSelect。

### 1.3 root Spacing（77）

`object Spacing : Any`（root 包）。覆盖文件：
NavigationMenu, Notification, Pagination, Skeleton, Snackbar, Tabs, Toast, **foundation/tokens/ComponentTokens.kt**。

> 注意：`foundation/tokens/ComponentTokens.kt` 不是 dead code——`components/input/Input.kt`（`import com.gearui.foundation.tokens.*`）和 `components/tag/Tag.kt`（`import ...TagTokens`）都依赖它。它必须先自身迁移，root Spacing 才能删。

### 1.4 ComponentSpecs（18，已只剩 3 个 object）

Batch 2–6 已删 Badge/Avatar(token)/Card/Cell/Input 等 Specs，**当前只剩 3 个 deprecated object 仍被调用**：

| 残留 Specs object | 次数 | 调用点 |
|---|---:|---|
| `TabSpecs` | 8 | `primitives/Tab.kt`、`foundation/tab/TabTokens.kt` |
| `DividerSpecs` | 7 | `primitives/Divider.kt`、`foundation/layout/DividerTokens.kt` |
| `AvatarSpecs` | 3 | `primitives/Avatar.kt` |

> 发现：Batch 2 建了 `AvatarTokens`，但 `primitives/Avatar.kt` 组件本体**仍在用** `AvatarSpecs` + `colors.surfaceVariant`/`colors.textSecondary`。Avatar 的组件本体迁移当时漏了，需在 Batch 8+ 补。

### 1.5 其他 deprecated API（24，非本次四目标 bridge）

| 符号 | 次数 | 性质 |
|---|---:|---|
| `TagColorTokens(...)` deprecated 构造器 | 15 | `foundation/tokens/ComponentTokens.kt` 内部自调用（legacy 池自引用） |
| `OverlayPlacement.TopStart` | 4 | `overlay/OverlayHost.kt` 穷举 when 内部回溯兼容 |
| `OverlayPlacement.BottomStart` | 4 | 同上 |
| `TabColors(...)` deprecated 构造器 | 1 | `foundation/tab/TabColors.kt` 自身 |

这些**不属于** Colors/Shapes/Spacing/ComponentSpecs 四个目标 bridge，删除它们是独立动作；但它们说明库内还有四目标之外的 deprecation 债。

## 2. 调用点分类

| 分类 | gearui-kit | sample | 说明 / 是否阻断 |
|---|---:|---:|---|
| **in-scope component still using bridge** | 47 文件 | — | ❌ 硬阻断。库内实现仍引用 bridge，对应 bridge 不能删。 |
| **sample / demo only** | — | 71 文件 / 1197 次 | ❌ 阻断。sample 与库同 build，不同步迁移则 `:sample` 编译爆。 |
| **docs only** | 0 | 0 | `.md` 不参与编译，不产生 warning。文档里的旧 token 名是「准确性」问题，非阻断（清理时顺带刷新）。 |
| **declaration self-warning / 内部回溯兼容** | 见 §1.5（24 次） | 24 | 半阻断。属各自 deprecated 声明的内部/自引用，随对应声明一起处理。 |
| **false positive** | 0 | 0 | 未发现误报；全部为真实引用。 |

### 2.1 in-scope 库内 47 文件（按 warning 数）

复杂数据展示族：`picker/DatePicker(44)`, `treeselect/TreeSelect(20)`, `cascader/Cascader(18)`, `table/Table(9)`, `tree/Tree(6)`, `transfer/Transfer(6)`, `timeline/Timeline(7)`, `picker/Picker(7)`
反馈/状态族：`skeleton/Skeleton(22)`, `toast/Toast(17)`, `snackbar/Snackbar(15)`, `notification/Notification(14)`, `progress/Progress(11)`, `result/Result(5)`, `empty/EmptyState(6)`, `loading/Loading(2)`
导航/披露族：`navigationmenu/NavigationMenu(25)`, `pagination/Pagination(16)`, `tabs/Tabs(15)`, `steps/Steps(10)`, `stepper/Stepper(16)`, `collapse/Collapse(9)`, `segmented/SegmentedControl(8)`, `backtop/BackTop(6)`, `anchor/Anchor(1)`, `tour/Tour(4)`, `drawer/Drawer(4)`
数据录入/展示族：`tag/Tag(13)`, `image/Image(12)`, `calendar/Calendar(12)`, `calendar/CalendarPopup(3)`, `rate/Rate(3)`, `swiper/Swiper(7)`, `swipecell/SwipeCell(7)`, `imageviewer/ImageViewer(2)`, `watermark/Watermark(1)`
primitives：`Tab(6)`, `Divider(6)`, `Avatar(5)`, `Badge(3)`
foundation：`tokens/ComponentTokens(29)`, `tab/TabTokens(5)`, `tab/TabColors(1)`, `layout/DividerTokens(3)`, `primitives/Text(3)`, `primitives/BasicTextField(3)`
overlay：`OverlayHost(8)`（§1.5 enum 回溯兼容）

### 2.2 sample 71 文件（前列）

`FormExample(51)`, `TableExample(43)`, `SidebarComponents(35)`, `SettingsPage(34)`, `TextExample(28)`, `SliderExample(28)`, `ImageExample(26)`, `BadgeExample(25)`, `ProgressExample(22)` … 共 71 文件、1197 次。

## 3. 删除顺序与就绪判定

| 阶段 | 动作 | 前置条件 | 当前就绪 |
|---|---|---|---|
| **RC-1** | remove unused **Colors** bridge | 42 库内文件 + 全部 sample 的 Colors 引用清零 | ❌ 否（276 + ~885 处在用） |
| **RC-2** | remove unused **Shapes** bridge | 15 库内文件 + sample 的 Shapes 引用清零 | ❌ 否（60 + ~76 处在用） |
| **RC-3** | remove **root Spacing** | 8 库内文件（含 ComponentTokens）+ sample 清零 | ❌ 否（77 + 177 处在用） |
| **RC-4** | remove **ComponentSpecs**（Tab/Divider/Avatar Specs） | Tab/Divider/Avatar primitive + TabTokens/DividerTokens 迁移完 | ❌ 否（18 + 35 处在用） |
| **RC-5** | final **BCV / token baseline** 刷新 + migration note | RC-1..4 全部完成 | ❌ 否（依赖前序） |

**结论：RC-1 ~ RC-5 当前全部不可执行。** 没有任何一个 bridge 处于「unused」状态。

### 3.1 进入 RC cleanup 的前置工作（建议 Batch 8+）

按 §2.1 的族分批，把 47 个库内文件迁移到语义 token（沿用 Batch 3–7 的映射规则），并**同步迁移 sample 71 文件**。完成后 `gearui-kit` 与 `sample` 的对应 bucket warning 归零，RC-1..5 才逐个解锁。

§1.5 的「其他 deprecated API」（OverlayPlacement.TopStart/BottomStart、TagColorTokens / TabColors deprecated 构造器）作为独立小项，与 Tab/Overlay 族迁移一并清理。

## 4. 明确阻断项（硬规则）

1. **任何 `.kt` 实现仍引用某 bridge → 该 bridge 不能删。** 当前 4 个目标 bridge 均被库内实现引用，全部阻断。
2. **sample 仍引用 → 也不能删，除非同步迁移。** sample 与库同一次 build；1197 处引用使所有 4 个 bridge 阻断。
3. **public API 删除必须刷新 BCV，并写 migration note。** Colors/Shapes 字段、root `Spacing`、`*Specs` 均为 public，删除即破坏性变更，需 `apiDump` 更新 `gearui-kit.api` + `gearui-kit.klib.api`，并在 CHANGELOG/迁移文档记录字段重命名映射。
4. **不在 audit 阶段改任何组件代码 / 删任何 bridge。** 本文件仅盘点。

## 5. 下一步

先执行 Batch 8+ 组件迁移波次（清空 §2.1 / §2.2），每批维持既有验证矩阵（check_token_compat / hardcoded-color / apiCheck / compileKotlinMetadata / sample Android / iOS Simulator）。待某个 bucket 在库 + sample 双双归零，再按 §3 顺序执行对应 RC-x 删除。
