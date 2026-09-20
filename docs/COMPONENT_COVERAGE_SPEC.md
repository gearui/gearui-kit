# Component Coverage and Convergence Spec: GearUI Kit × HeroUI v3 × HeroUI Native

Status: current planning document, not a release statement. Chinese companion: [COMPONENT_COVERAGE_SPEC.zh-Hans.md](COMPONENT_COVERAGE_SPEC.zh-Hans.md); both are generated from the same data.

## 1. Baseline

| Framework | Version / source | Role |
|---|---|---|
| HeroUI Native | v1.0.9 (`b9fa541`, the reference pinned in this repository) | GearUI's **visual and interaction** reference |
| HeroUI v3 (Web) | `@heroui/react` 3.2.6 (`heroui-inc/heroui` v3 branch, 2026-09-18) | **Coverage** reference, same design language as Native |
| HeroUI v2 | `heroui-inc/heroui` main branch | Only to mark components not yet in v3 (written as "v2 …") |
| GearUI Kit | 1.0.0-beta3 | This library |

## 2. Counts

| Measure | HeroUI v3 | HeroUI Native | GearUI Kit |
|---|---:|---:|---:|
| Component packages / directories | 85 | 43 | 70 component directories + 9 primitives + 4 runtime pieces (Navigator, TabHost, PageScaffold, SwipeBack) |
| Sample-only compositions, no library component | — | — | 5 (Sidebar, Breadcrumb, FAB, Message, DropdownMenu) |

Package counts are not comparable: HeroUI splits sub-parts into packages (`menu-item`, `list-box-section`) while GearUI exposes them as parameters. The comparison below therefore uses 84 capability rows:

| Summary (84 capability rows) | Count |
|---|---:|
| Covered by HeroUI v3 | 59 (plus 4 in v2 only) |
| Covered by HeroUI Native | 37 |
| Covered by either HeroUI library | 63 |
| GearUI ✅ have / 🟡 partial / 🧩 sample only / — none | 72 / 4 / 2 / 6 |
| Of HeroUI's capabilities, GearUI has / partially has | 54 / 3 (of 63) |
| GearUI only (neither HeroUI library) | 16 |

Conclusion: GearUI already exceeds HeroUI Native in component count and is on par with HeroUI v3. Every mobile P1 gap is now filled (pressable feedback, close button, link, field text primitives, inline alert, OTP, combo box, pull to refresh). What remains is **color**, **internationalized dates** and **accessibility**, plus desktop/web-only capabilities such as Kbd and drag-and-drop. GearUI's mobile-only components (action sheet, wheel/cascader pickers, bottom tab bar and page stack, swiper, swipe cell) are strengths to keep.

## 3. Three-framework capability table

Legend: ✅ have, 🟡 partial, 🧩 sample-only composition (no library component), — none. Priority: P1 near term, P2 mid term, P3 on demand, "—" no action.

### Foundations & typography

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Button | button | button | Button | ✅ Have | — | Press scale and highlight aligned |
| Button group | button-group | — | ButtonGroup | ✅ Have | — | Joined buttons sharing radius and dividers |
| Toggle button / group | toggle-button, toggle-button-group | — | ToggleButton, ToggleButtonGroup | ✅ Have | — | Single and multiple selection covered |
| Close button | close-button | close-button | CloseButton | ✅ Have | — | Calendar, Notification, Snackbar and ImageViewer unified |
| Link / link button | link | link-button | Link, LinkButton | ✅ Have | — | Icons and separator-colour underline |
| Text / typography | typography | text | Text, Typography tokens | ✅ Have | — | Type scale vs HeroUI 16/14/18 pending decision |
| Kbd | kbd | — | — | — None | P3 | Desktop/Web use |
| Separator | separator | separator | Divider | ✅ Have | — | Uses the separator role |
| Surface | surface | surface | DecoratedSurface, MaterialSurface | ✅ Have | — | Layered shadows and border styles |
| Glass / blur | — | glass-view | MaterialSurface, Materials | ✅ Have | — | Four platforms; off by default |
| Theme background | — | theme-background | App, Theme | ✅ Have | — |  |
| Pressable feedback | (CSS states) | pressable-feedback | PressableFeedback, Modifier.pressScale, pressedSurfaceColor | ✅ Have | — | Public API; highlight uses a real overlay view |
| Scroll shadow | scroll-shadow | scroll-shadow | ScrollShadow | ✅ Have | — | Fades by scroll capability |
| Icon | (icons) | — | Icon, Icons (Phosphor) | ✅ Have | — | GearUI ships a full icon registry |
| Section header | header | — | SectionHeader | ✅ Have | — |  |

### Form inputs

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Input | input | input | Input | ✅ Have | — | Borderless with field shadow; filled variant on surfaces |
| Text field (label, description, error) | textfield | text-field | Input(label, helperText, error) | ✅ Have | — | GearUI is parameter-based; HeroUI is compound |
| Input group (addons) | input-group | input-group | InputGroup, InputGroupAddon, InputGroupDivider | ✅ Have | — | Addons and field share one frame |
| Textarea | textarea | text-area | Textarea | ✅ Have | — |  |
| Search field | search-field | search-field | SearchBar | ✅ Have | — | Filled variant by default for surfaces |
| Number field | number-field | — | NumberField | ✅ Have | — | Stepper remains the compact variant |
| OTP input | input-otp | input-otp | InputOTP | ✅ Have | — | One hidden native field, slots display only |
| Field primitives (label, description, error, fieldset) | label, description, field-error, error-message, fieldset | label, description, field-error, control-field | FieldLabel, FieldDescription, FieldErrorText, FieldDefaults.labelGap | ✅ Have | — | Input, Textarea and Form all use them |
| Form | form | — | Form | ✅ Have | — |  |
| Checkbox / group | checkbox, checkbox-group | checkbox | Checkbox, CheckboxGroup | ✅ Have | — |  |
| Radio / group | radio, radio-group | radio, radio-group | RadioGroup, RadioCardGroup | ✅ Have | — | Includes card radios |
| Switch / group | switch, switch-group | switch | Switch, SwitchGroup | ✅ Have | — | Group rows carry descriptions and toggle whole-row |
| Slider | slider | slider | Slider | ✅ Have | — |  |
| Select | select | select | Select | ✅ Have | — |  |
| Combo box / autocomplete | combo-box, autocomplete | — | ComboBox | ✅ Have | — | Reuses Select's panel |
| List box | list-box, list-box-item, list-box-section | — | Select 面板（内部） | 🟡 Partial | P2 | Option list not exposed on its own |
| Rate | — | — | Rate | ✅ Have | — | GearUI only |
| Wheel picker | — | — | Picker | ✅ Have | — | GearUI only; common on mobile |
| Cascader / tree select / transfer | — | — | Cascader, TreeSelect, Transfer | ✅ Have | — | GearUI only |
| Upload | — | — | Upload | ✅ Have | — | Presentation only; picking and transfer stay with the host |

### Date & time

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Calendar | calendar | — | Calendar, CalendarPopup | ✅ Have | — |  |
| Range calendar | range-calendar | — | Calendar（范围模式） | ✅ Have | — |  |
| Year picker | calendar-year-picker | — | — | — None | P2 |  |
| Date picker / range picker | date-picker, date-range-picker | — | DatePickerInput, DateRangePickerInput | ✅ Have | — | Wheel field plus calendar range field |
| Date field / time field | date-field, date-input-group, time-field | — | DateTimePickerInput, TimePickerInput | 🟡 Partial | P2 | No segmented keyboard entry or localized formats |

### Color

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Color picker family | color-picker, color-area, color-slider, color-field, color-input-group, color-swatch, color-swatch-picker | — | — | — None | P3 | Low use in mobile products |

### Navigation & pages

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Tabs | tabs | tabs | Tabs, SegmentedControl | ✅ Have | — | Spring indicator aligned |
| Breadcrumbs | breadcrumbs | — | Breadcrumb 示例 | 🧩 Sample only | P2 |  |
| Pagination | pagination | — | Pagination | ✅ Have | — |  |
| Navbar | (v2 navbar) | — | NavBar | ✅ Have | — |  |
| Bottom tab bar | — | — | BottomNavBar, TabHost | ✅ Have | — | GearUI only, with keep-alive |
| Navigation menu | — | — | NavigationMenu | ✅ Have | — |  |
| Toolbar | toolbar | — | NavBar actions | 🟡 Partial | P2 |  |
| Steps | — | — | Steps | ✅ Have | — | GearUI only |
| Anchor / back to top | — | — | Anchor, BackTop | ✅ Have | — | GearUI only |
| Sidebar | — | — | Sidebar 示例 | 🧩 Sample only | P3 |  |
| Page stack / swipe back | — | — | Navigator, SwipeBack, PageScaffold | ✅ Have | — | GearUI runtime; HeroUI relies on external routers |
| Drawer | drawer | — | Drawer | ✅ Have | — |  |

### Overlays

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Dialog / modal | modal | dialog | Dialog, DialogContent | ✅ Have | — | Layout and motion aligned |
| Alert dialog | alert-dialog | — | ConfirmDialog, AlertDialog | ✅ Have | — |  |
| Bottom sheet | — | bottom-sheet | BottomSheet | ✅ Have | — |  |
| Action sheet | — | — | ActionSheet | ✅ Have | — | GearUI only; HeroUI composes sheet + menu |
| Popover | popover | popover | Popover | ✅ Have | — |  |
| Tooltip | tooltip | — | Tooltip | ✅ Have | — |  |
| Menu / dropdown | menu, menu-item, menu-section, dropdown | menu | ContextMenu, PopoverMenu | ✅ Have | — | Menu row feedback aligned |
| Sub-menu | (menu) | sub-menu | — | — None | P2 |  |
| Toast | toast | toast | Toast, Snackbar, Notification | ✅ Have | — |  |
| Popup | — | — | Popup | ✅ Have | — |  |
| Tour | — | — | Tour | ✅ Have | — | GearUI only |

### Feedback & status

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Alert (inline) | alert | alert | Alert | ✅ Have | — | Five statuses, action slot and close |
| Spinner | spinner | spinner | Loading | ✅ Have | — | Sizes aligned 16/24/32 |
| Progress bar / circle | progress-bar, progress-circle | — | LinearProgress, CircularProgress | ✅ Have | — |  |
| Meter | meter | — | — | — None | P3 | Coverable with progress styles |
| Skeleton / group | skeleton | skeleton, skeleton-group | Skeleton + 预设（文章/卡片/列表/网格） | ✅ Have | — | Shimmer default aligned |
| Empty state | empty-state | — | EmptyState | ✅ Have | — |  |
| Result | — | — | Result | ✅ Have | — | GearUI only |
| Badge | badge | — | Badge | ✅ Have | — |  |
| Pull to refresh | — | — | pullRefreshItem, PullRefreshIndicator | ✅ Have | — | Platform list gesture with a GearUI indicator |
| Message / notice bar | — | — | NoticeBar | ✅ Have | — | Scrolling announcement sharing Alert's status colours |

### Data display

| Capability | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | GearUI status | Priority | Notes |
|---|---|---|---|---|---|---|
| Card | card | card | Card | ✅ Have | — | No outline, surface shadow |
| Accordion / disclosure | accordion, disclosure, disclosure-group | accordion | Collapse, CollapseGroup(accordion = true) | ✅ Have | — | Single-expand covered by CollapseGroup |
| List group / cells | — | list-group | CellGroup, Cell, ListItem | ✅ Have | — |  |
| Table | table | — | Table, SimpleTable | ✅ Have | — | Selection and row click |
| Avatar / group | avatar, avatar-group | avatar | Avatar, AvatarGroup | ✅ Have | — | Overlap with a +N counter |
| Chip / tag / tag group | chip, tag, tag-group | chip, tag-group | Tag, TagGroup | ✅ Have | — | Single, multiple and removable |
| Image / gallery / viewer | (v2 image) | — | GearImage, ImageGallery, ImageViewer | ✅ Have | — | More complete in GearUI |
| Timeline / tree / grid / swiper / watermark / swipe cell | — | — | Timeline, Tree, Grid, Swiper, Watermark, SwipeCell | ✅ Have | — | GearUI only |
| User (avatar + name) | (v2 user) | — | Cell + Avatar | 🟡 Partial | P3 |  |
| Code / snippet | (v2 code, snippet) | — | — | — None | P3 |  |

## 4. Platform-level comparison

| Dimension | HeroUI v3 (Web) | HeroUI Native | GearUI Kit | Takeaway |
|---|---|---|---|---|
| Platforms | Web（React） | iOS / Android（React Native） | iOS / Android / Web / HarmonyOS（Kotlin Multiplatform + KuiklyUI） | One codebase, four platforms, native views per node |
| Interaction & a11y base | React Aria Components（键盘、焦点、ARIA 完整） | 自研 primitives + RN 无障碍 | Compose 语义 + Kuikly 原生视图；屏幕阅读器尚未验收 | Accessibility is GearUI's largest gap; borrow React Aria's state model |
| Styling | Tailwind v4 + tailwind-variants，BEM 类 | Uniwind（Tailwind for RN）+ 同名 BEM 类 | DTCG 2025.10 token 编译为 Kotlin 常量 | GearUI tokens are standard DTCG, portable across tools |
| Theming | 明暗 + CSS 变量，可换主题 | 明暗 + 主题 CSS；Pro 主题含 glass | 明暗、品牌色、形状三轴独立 | — |
| Motion | CSS 过渡 + React Aria 状态 | Reanimated：弹簧、按压缩放、菜单行 | token 化弹簧与时长；已对齐按压、弹层、指示条 | — |
| Overlay system | Portal + React Aria overlays | Portal + gorhom bottom sheet | OverlayHost：统一遮罩、返回键、安全区、进出场 | GearUI has one host and one dismissal policy |
| Navigation runtime | 无（交给路由库） | 无（交给 react-navigation） | Navigator、TabHost 保活、边缘侧滑返回 | GearUI-only strength |
| Forms & validation | Form + 字段原语 + RAC 校验 | 字段原语（label/description/error/control-field） | Input/Form 参数式内置校验文案 | Extract field primitives, align with the compound pattern |
| Date i18n | @internationalized/date，区域格式与日历系统 | 无日期组件 | 公历为主，滚轮选择 | Add segmented date fields over time |
| i18n / RTL | RTL 完整 | layout-direction provider | 类型化语言包；RTL 未验收 | — |
| Icons | 少量内置 | 不内置 | Phosphor 全量注册表 + 资源校验 | GearUI strength |
| Quality gates | Storybook、测试 | 示例 App | 23+ 守卫、API/Token 基线、三端单测、设备验收 | Stricter gates in GearUI |

## 5. Absorption plan

### P1: near term (needed by mobile products, mature HeroUI design exists)


All P1 items landed on 2026-09-20 (local commits 54e6b41, ca72d4e, 4c629bb, each verified on the iPhone 17 Pro simulator): PressableFeedback, CloseButton, Link / LinkButton, field text primitives, Alert, InputOTP, ComboBox and PullRefresh, plus SwitchGroup, TagGroup, ScrollShadow, NumberField, ToggleButton / ButtonGroup and AvatarGroup.

### P2: mid term
- **List box** (v3: list-box, list-box-item, list-box-section): Option list not exposed on its own
- **Year picker** (v3: calendar-year-picker)
- **Date field / time field** (v3: date-field, date-input-group, time-field): No segmented keyboard entry or localized formats
- **Breadcrumbs** (v3: breadcrumbs)
- **Toolbar** (v3: toolbar)
- **Sub-menu** (v3: (menu); Native: sub-menu)

### P3: on demand
- **Kbd** (v3: kbd): Desktop/Web use
- **Color picker family** (v3: color-picker, color-area, color-slider, color-field, color-input-group, color-swatch, color-swatch-picker): Low use in mobile products
- **Sidebar**
- **Meter** (v3: meter): Coverable with progress styles
- **User (avatar + name)** (v3: (v2 user))
- **Code / snippet** (v3: (v2 code, snippet))

## 6. Convergence rules

1. **HeroUI Native decides appearance and interaction.** Where Native has the component, style, motion and feedback values come from `heroui-native/src/styles` and `*.animation.ts` through DTCG tokens; no bare numbers.
2. **Components only v3 has** follow v3's structure and state model, rendered with Native's token system (overlay, field, surface, radius) so they match existing components.
3. **API style:** keep GearUI's parameter API as the primary entry; add composable field primitives (Label, Description, FieldError) for custom layouts instead of forcing a compound API.
4. **Keep GearUI's mobile-first extras**, restyled on HeroUI tokens (the action sheet is already a sheet with menu rows).
5. **Sample-only compositions either become library components or leave the component index**, so nothing looks available that is not.
6. **Accessibility catch-up:** follow React Aria's state and semantics model for buttons, fields, menus and dialogs, and add it to device acceptance.

## 7. Maintenance

- Update this table and the [component acceptance matrix](COMPONENT_ACCEPTANCE_MATRIX.md) when a component is added or changes status.
- When a reference version changes, re-list the three component sets and refresh sections 1 and 2.
