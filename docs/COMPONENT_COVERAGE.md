# GearUI Component Coverage

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

Updated 2026-09-30. **85 public index entries**, **76 component directories**, **93 sample routes**. Runtime/diagnostic pages and the typed-form demo are not extra components. Multiple entry points in a family are not separate directories.

The registry is the inventory source; README generation is CI-checked. This document records current coverage, not visual or interaction acceptance. The reference column means an open-source HeroUI Native family exists, not API or pixel equivalence. Some GearUI families deliberately extend the reference.

## 1. Current Inventory

### Basic (10)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `Kbd` | Kbd | GearUI extension |
| `Button` | Trigger actions | HeroUI Native |
| `Icon` | Icon display | GearUI extension |
| `Link` | Link and LinkButton | GearUI extension |
| `CloseButton` | Unified dismiss button | HeroUI Native |
| `PressableFeedback` | Scale and highlight for any tappable area | HeroUI Native |
| `Text` | Text display | HeroUI Native |
| `Tag` | Marking and classification | HeroUI Native |
| `Badge` | Message count indicator | GearUI extension |
| `Divider` | Content separator | HeroUI Native |

### Form (24)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `ListBox` | ListBox | GearUI extension |
| `ColorPicker` | ColorPicker | GearUI extension |
| `Input` | Text input | HeroUI Native |
| `Checkbox` | Multiple selection | HeroUI Native |
| `AgreementCheckbox` | Terms and privacy consent | HeroUI Native |
| `Radio` | Single selection | HeroUI Native |
| `InputOTP` | One-time code input | HeroUI Native |
| `ComboBox` | Filterable suggestions | GearUI extension |
| `NumberField` | Typed and stepped number | GearUI extension |
| `ToggleButton` | Toggle and button group | HeroUI Native |
| `InputGroup` | Field with attached blocks | HeroUI Native |
| `Switch` | Toggle switch | HeroUI Native |
| `Slider` | Value selection | HeroUI Native |
| `Stepper` | Number stepper | GearUI extension |
| `Textarea` | Multiline text input | HeroUI Native |
| `Rate` | Rating | GearUI extension |
| `Select` | Dropdown selector | HeroUI Native |
| `Picker` | Multi-column picker | GearUI extension |
| `DatePicker` | Date & time picker | GearUI extension |
| `Upload` | File upload | GearUI extension |
| `Form` | Form container | HeroUI Native |
| `Cascader` | Cascade selector | GearUI extension |

### Navigation (9)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `Toolbar` | Toolbar | GearUI extension |
| `NavBar` | Page navigation bar | GearUI extension |
| `BottomNavBar` | App bottom navigation | GearUI extension |
| `Tabs` | Content switching | HeroUI Native |
| `Drawer` | Slide drawer | GearUI extension |
| `Steps` | Step indicator | GearUI extension |
| `IndexBar` | Alphabet index | GearUI extension |
| `Segmented` | Segmented control | GearUI extension |

### Data Display (20)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `Meter` | Meter | GearUI extension |
| `User` | User | GearUI extension |
| `Code` | Code | GearUI extension |
| `List` | List display | GearUI extension |
| `Card` | Card container | HeroUI Native |
| `Cell` | List cell component | HeroUI Native |
| `CellGroup` | Grouped list rows | GearUI extension |
| `Table` | Data table | GearUI extension |
| `Image` | Image display | GearUI extension |
| `ImageViewer` | Image preview | GearUI extension |
| `Avatar` | User avatar | HeroUI Native |
| `ScrollShadow` | Fades scrollable edges | HeroUI Native |
| `Collapse` | Content collapse | HeroUI Native |
| `Progress` | Progress display | GearUI extension |
| `Empty` | Empty state | GearUI extension |
| `Skeleton` | Loading placeholder | HeroUI Native |
| `Timeline` | Timeline display | GearUI extension |
| `Calendar` | Calendar display | GearUI extension |
| `Format` | Compact numbers, relative time, lunar | GearUI extension |
| `Watermark` | Page watermark | GearUI extension |

### Feedback (15)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `SwipeCell` | Swipeable cell | GearUI extension |
| `ActionSheet` | Bottom action sheet | GearUI extension |
| `Toast` | Message toast | HeroUI Native |
| `Dialog` | Modal dialog | HeroUI Native |
| `Tooltip` | Tooltip | GearUI extension |
| `ContextMenu` | Context menu | GearUI extension |
| `Loading` | Loading state | HeroUI Native |
| `Alert` | Inline status message | HeroUI Native |
| `NoticeBar` | Scrolling announcement | GearUI extension |
| `Notification` | Global notification | HeroUI Native |
| `Snackbar` | Bottom message | GearUI extension |
| `Popup` | Popup content | GearUI extension |
| `Popover` | Popover tooltip | HeroUI Native |
| `Result` | Operation result | GearUI extension |
| `Tour` | Feature guide | GearUI extension |

### Layout (7)

| Entry | Purpose | Reference |
| --- | --- | --- |
| `Grid` | Grid layout | GearUI extension |
| `Swiper` | Content carousel | GearUI extension |
| `SearchBar` | Search input | GearUI extension |
| `PullRefresh` | Pull to refresh a list | GearUI extension |
| `LoadMore` | Load the next page of a list | GearUI extension |
| `BottomSheet` | Bottom sheet | HeroUI Native |
| `BackTop` | Back to top | GearUI extension |

## 2. beta7 Implementation Status

Stable-value Picker, cancellable Cascader loading, constrained DatePicker/TimePicker, typed async forms, exact-decimal NumberField and editable Stepper are implemented with production-wired tests. ListBox, Toolbar, Kbd, ColorPicker family, Meter, User and Code/Snippet have public APIs and sample routes. IndexBar, AgreementCheckbox, LoadMore and locale formatting were introduced in the preceding beta7 work.

## 3. Acceptance Still Required

Implementation is not release approval. Full device accessibility, all-page visual review, maximum font size, RTL, real-device iOS performance and HarmonyOS runtime remain gates in [QUALITY_STATUS.md §7](./QUALITY_STATUS.md). [BETA7_ACCEPTANCE.md](./BETA7_ACCEPTANCE.md) records evidence and limitations. No feature from the approved plan is silently dropped.

## 4. Convergence Rules

1. **One visual language.** Every component, GearUI-only or shared, is styled
   through the same token system so the set stays coherent
   ([VISUAL_SPEC.md](./VISUAL_SPEC.md)).
2. **API style**: keep GearUI's parameter API as the primary entry; add
   composable field primitives for custom layouts instead of forcing a
   compound API.
3. **Mobile-first depth is kept and extended**, not trimmed to match a
   reference.
4. **Accessibility**: buttons, fields, menus and dialogs follow a consistent
   name/state/role model and are added to device acceptance.

## 5. Maintenance

Update this document when a component is added or changes status, and keep it
in step with the generated README index. Per-component acceptance status lives
in [QUALITY_STATUS.md](./QUALITY_STATUS.md).
