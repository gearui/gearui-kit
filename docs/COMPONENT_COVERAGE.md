# GearUI Component Coverage

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

The complete GearUI component inventory, organized by category, with coverage
against the HeroUI Native component set noted per entry. This is the
authoritative list of what the kit ships; the same list drives the generated
README index (`ComponentConfig.kt` → `gen_component_index.py`, CI-checked), so
the two never drift.

Scale today: **70 component directories**, a set of foundation primitives
(Text, Icon, Surface, BasicTextField, LoadingIndicator, List, ScrollView,
GearLazyColumn) and **4 runtime pieces** (Navigator, TabHost, PageScaffold,
SwipeBack). The public index lists **82 entries across six categories**.

Coverage at a glance: of the 82 entries, **36 also exist in HeroUI Native**,
**41 are GearUI-only** (mobile-first capabilities HeroUI does not ship) and
**5 are sample-only** compositions awaiting promotion or removal. GearUI
exceeds HeroUI Native (43 packages) in component count and is on par with
HeroUI v3 (85 packages) in capability coverage.

Legend: ✅ also in HeroUI Native · ◆ GearUI-only · ○ sample-only.

## 1. Full Component Inventory

### Basic (9)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `Button` | Trigger actions | ✅ |
| `Icon` | Icon display (Phosphor registry) | ◆ |
| `Link` | Link and LinkButton | ✅ |
| `CloseButton` | Unified dismiss button | ✅ |
| `PressableFeedback` | Scale and highlight for any tappable area | ✅ |
| `Text` | Text display | ✅ |
| `Tag` | Marking and classification | ✅ |
| `Badge` | Message count indicator | ◆ |
| `Divider` | Content separator | ✅ |

### Form (22)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `Input` | Text input | ✅ |
| `Checkbox` | Multiple selection | ✅ |
| `Radio` | Single selection | ✅ |
| `InputOTP` | One-time code input | ✅ |
| `ComboBox` | Filterable suggestions | ◆ |
| `NumberField` | Typed and stepped number | ◆ |
| `ToggleButton` | Toggle and button group | ◆ |
| `InputGroup` | Field with attached blocks | ✅ |
| `Switch` | Toggle switch | ✅ |
| `Slider` | Value selection | ✅ |
| `Stepper` | Number stepper | ◆ |
| `Textarea` | Multiline text input | ✅ |
| `Rate` | Rating | ◆ |
| `Select` | Dropdown selector | ✅ |
| `Picker` | Multi-column wheel picker | ◆ |
| `DatePicker` | Date and time picker | ◆ |
| `DropdownMenu` | Filter dropdown menu | ○ |
| `Upload` | File upload (presentation) | ◆ |
| `Form` | Form container | ◆ |
| `Cascader` | Cascade selector | ◆ |
| `Transfer` | Data transfer | ◆ |
| `TreeSelect` | Tree selector | ◆ |

### Navigation (12)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `NavBar` | Page navigation bar | ◆ |
| `BottomNavBar` | App bottom navigation | ◆ |
| `Tabs` | Content switching | ✅ |
| `NavigationMenu` | Top navigation menu | ◆ |
| `Sidebar` | Side navigation | ○ |
| `Drawer` | Slide drawer | ◆ |
| `Steps` | Step indicator | ◆ |
| `Pagination` | Pagination navigation | ◆ |
| `Breadcrumb` | Path navigation | ○ |
| `Anchor` | Page anchor navigation | ◆ |
| `Segmented` | Segmented control | ✅ |
| `FAB` | Floating action button | ○ |

### Data display (17)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `List` | List display | ✅ |
| `Card` | Card container | ✅ |
| `Cell` | List cell component | ✅ |
| `CellGroup` | Grouped list rows | ✅ |
| `Table` | Data table | ◆ |
| `Image` | Image display | ◆ |
| `ImageViewer` | Image preview | ◆ |
| `Avatar` | User avatar | ✅ |
| `ScrollShadow` | Fades scrollable edges | ✅ |
| `Collapse` | Content collapse / accordion | ✅ |
| `Progress` | Linear and circular progress | ◆ |
| `Empty` | Empty state | ◆ |
| `Skeleton` | Loading placeholder | ✅ |
| `Timeline` | Timeline display | ◆ |
| `Tree` | Tree structure | ◆ |
| `Calendar` | Calendar display | ◆ |
| `Watermark` | Page watermark | ◆ |

### Feedback (16)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `SwipeCell` | Swipeable cell | ◆ |
| `ActionSheet` | Bottom action sheet | ◆ |
| `Toast` | Message toast | ✅ |
| `Dialog` | Modal dialog | ✅ |
| `Tooltip` | Tooltip | ◆ |
| `ContextMenu` | Context menu | ✅ |
| `Loading` | Loading state / spinner | ✅ |
| `Message` | Global message | ○ |
| `Alert` | Inline status message | ✅ |
| `NoticeBar` | Scrolling announcement | ◆ |
| `Notification` | Global notification | ✅ |
| `Snackbar` | Bottom message | ✅ |
| `Popup` | Popup content | ◆ |
| `Popover` | Popover tooltip | ✅ |
| `Result` | Operation result | ◆ |
| `Tour` | Feature guide | ◆ |

### Layout (6)

| Component | Purpose | Coverage |
| --- | --- | --- |
| `Grid` | Grid layout | ◆ |
| `Swiper` | Content carousel | ◆ |
| `SearchBar` | Search input | ✅ |
| `PullRefresh` | Pull to refresh a list | ◆ |
| `BottomSheet` | Bottom sheet | ✅ |
| `BackTop` | Back to top | ◆ |

## 2. GearUI-Only Strengths

The 41 GearUI-only entries are the kit's mobile-first depth — capabilities
neither HeroUI library ships, all rendered on GearUI tokens:

- **Navigation runtime**: Navigator (a real page stack), SwipeBack,
  PageScaffold, TabHost with keep-alive, NavBar, BottomNavBar,
  NavigationMenu, Steps, Anchor, BackTop, Drawer, Pagination. HeroUI relies on
  external routers and has no stacked-navigation runtime.
- **Pickers and fields**: Picker (wheel), Cascader, TreeSelect, Transfer,
  DatePicker, NumberField, Rate, Stepper, ComboBox, Upload, Form.
- **Overlays**: ActionSheet, Popup, Tooltip, Tour.
- **Data display and feedback**: Table, Image, ImageViewer, Timeline, Tree,
  Grid, Swiper, Watermark, SwipeCell, Badge, Empty, Result, Progress,
  NoticeBar, PullRefresh.
- **Icon set**: a complete Phosphor icon registry; HeroUI Native ships none.

**Sample-only compositions (○).** Sidebar, Breadcrumb, FAB, Message and
DropdownMenu live only in the sample today. Each either becomes a library
component or leaves the index — nothing may look available that is not.

## 3. Gaps And Priorities

All P1 gaps were filled on 2026-09-20 (PressableFeedback, CloseButton,
Link/LinkButton, field text primitives, Alert, InputOTP, ComboBox,
PullRefresh, SwitchGroup, TagGroup, ScrollShadow, NumberField,
ToggleButton/ButtonGroup, AvatarGroup).

| Capability | Priority | Note |
| --- | --- | --- |
| List box exposed standalone | P2 | Option list currently internal to Select |
| Year picker | P2 | |
| Date/time segmented fields, localized formats | P2 | Wheel entry only today |
| Breadcrumbs | P2 | Sample-only today |
| Toolbar | P2 | NavBar actions partially cover |
| Sub-menu | P2 | Nested menu not shipped |
| Kbd | P3 | Desktop/Web use |
| Color picker family | P3 | Low use in mobile products |
| Sidebar, Meter, User, Code/Snippet | P3 | On demand |
| Internationalized date formats | 1.0 track | Gregorian-first today |
| Accessibility state model catch-up | 1.0 track | Largest remaining gap |
| RTL | unverified | Typed packs exist; layout direction not accepted |

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
