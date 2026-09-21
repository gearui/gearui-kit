# GearUI Component Coverage vs HeroUI Native

[English](./COMPONENT_COVERAGE.md) | [简体中文](./COMPONENT_COVERAGE.zh-Hans.md)

The component inventory compared against the visual reference. HeroUI Native
**1.0.9** (pinned commit `b9fa541`, see
[VISUAL_SPEC.md](./VISUAL_SPEC.md)) is the appearance and interaction
reference; HeroUI v3 (Web, `@heroui/react` 3.2.6) is consulted only to mark
capabilities that may be absorbed later. Package counts are not comparable —
HeroUI splits sub-parts into packages (`menu-item`, `list-box-section`) while
GearUI exposes them as parameters — so the comparison is by capability.

Current scale: **70 component directories + 9 primitives + 4 runtime pieces**
(Navigator, TabHost, PageScaffold, SwipeBack). GearUI exceeds HeroUI Native
(43 packages) in component count and is on par with HeroUI v3 (85 packages) in
capability coverage.

## 1. HeroUI Native → GearUI

Legend: ✅ have · 🟡 partial · — none.

### Foundations & typography

| HeroUI Native | GearUI Kit | Status | Notes |
| --- | --- | --- | --- |
| button | Button, ButtonGroup | ✅ | Press scale and highlight aligned |
| close-button | CloseButton | ✅ | Unified across Calendar, Notification, Snackbar, ImageViewer |
| link-button | Link, LinkButton | ✅ | Icons and separator-colour underline |
| text | Text + Typography tokens | ✅ | Type scale decision pending against HeroUI 16/14/18 |
| separator | Divider | ✅ | Uses the separator role |
| surface | DecoratedSurface, MaterialSurface | ✅ | Layered shadows and border styles |
| glass-view | MaterialSurface, Materials | ✅ | Four platforms; off by default (VISUAL_SPEC §5) |
| theme-background | App, Theme | ✅ | |
| pressable-feedback | PressableFeedback, Modifier.pressScale, pressedSurfaceColor | ✅ | Highlight uses a real overlay view |
| scroll-shadow | ScrollShadow | ✅ | Fades by scroll capability |
| — (icons) | Icon, Icons (Phosphor registry) | ✅ | GearUI ships a full icon registry; Native has none |
| header | SectionHeader | ✅ | |

### Form inputs

| HeroUI Native | GearUI Kit | Status | Notes |
| --- | --- | --- | --- |
| input | Input | ✅ | Borderless with field shadow; filled variant on surfaces |
| text-field | Input(label, helperText, error) | ✅ | GearUI is parameter-based; HeroUI is compound |
| input-group | InputGroup, InputGroupAddon, InputGroupDivider | ✅ | Addons and field share one frame |
| text-area | Textarea | ✅ | |
| search-field | SearchBar | ✅ | Filled variant by default for surfaces |
| input-otp | InputOTP | ✅ | One hidden native field; slots display only |
| label / description / field-error / control-field | FieldLabel, FieldDescription, FieldErrorText, FieldDefaults | ✅ | Shared by Input, Textarea and Form |
| checkbox | Checkbox, CheckboxGroup | ✅ | |
| radio, radio-group | RadioGroup, RadioCardGroup | ✅ | Includes card radios |
| switch | Switch, SwitchGroup | ✅ | Group rows toggle whole-row |
| slider | Slider | ✅ | |
| select | Select | ✅ | |

### Navigation, overlays & feedback

| HeroUI Native | GearUI Kit | Status | Notes |
| --- | --- | --- | --- |
| tabs | Tabs, SegmentedControl | ✅ | Spring indicator aligned |
| dialog | Dialog, DialogContent | ✅ | Layout and motion aligned |
| bottom-sheet | BottomSheet | ✅ | |
| popover | Popover | ✅ | |
| menu | ContextMenu, PopoverMenu | ✅ | Menu row feedback aligned |
| sub-menu | — | — | Gap, P2 |
| toast | Toast, Snackbar, Notification | ✅ | |
| alert | Alert | ✅ | Five statuses, action slot, close |
| spinner | Loading | ✅ | Sizes aligned 16/24/32 |
| skeleton, skeleton-group | Skeleton + presets (article/card/list/grid) | ✅ | Shimmer default aligned |
| card | Card | ✅ | No outline, surface shadow |
| accordion | Collapse, CollapseGroup(accordion = true) | ✅ | |
| list-group | CellGroup, Cell, ListItem | ✅ | |
| avatar | Avatar, AvatarGroup | ✅ | Overlap with +N counter |
| chip, tag-group | Tag, TagGroup | ✅ | Single, multiple, removable |

## 2. GearUI-only components (mobile strengths)

Neither HeroUI library ships these; they are restyled on HeroUI tokens:

- **Navigation runtime**: Navigator (real page stack), SwipeBack, PageScaffold,
  TabHost with keep-alive, BottomNavBar, NavigationMenu, Steps, Anchor,
  BackTop, Drawer, Pagination. HeroUI relies on external routers.
- **Pickers**: Picker (wheel), Cascader, TreeSelect, Transfer, DatePickerInput,
  DateRangePickerInput, DateTimePickerInput, TimePickerInput, Calendar,
  CalendarPopup, NumberField, Rate.
- **Overlays**: ActionSheet (single sheet with menu rows), Popup, Tooltip, Tour,
  ConfirmDialog, AlertDialog.
- **Data display & feedback**: Table, SimpleTable, GearImage, ImageGallery,
  ImageViewer, Timeline, Tree, Grid, Swiper, Watermark, SwipeCell, Badge,
  EmptyState, Result, LinearProgress, CircularProgress, NoticeBar,
  pullRefreshItem / PullRefreshIndicator, ComboBox, Form, Upload
  (presentation-only), ToggleButton(Group), Stepper.

Sample-only compositions (not library components): Sidebar, Breadcrumb, FAB,
Message, DropdownMenu. They either become library components or leave the
component index — nothing may look available that is not.

## 3. Gaps And Priorities

All P1 gaps were filled on 2026-09-20 (PressableFeedback, CloseButton,
Link/LinkButton, field text primitives, Alert, InputOTP, ComboBox,
PullRefresh, SwitchGroup, TagGroup, ScrollShadow, NumberField,
ToggleButton/ButtonGroup, AvatarGroup).

| Capability (HeroUI v3 source) | Priority | Note |
| --- | --- | --- |
| List box exposed standalone | P2 | Option list currently internal to Select |
| Year picker | P2 | |
| Date/time segmented fields, localized formats | P2 | Wheel entry only today |
| Breadcrumbs | P2 | Sample-only today |
| Toolbar | P2 | NavBar actions partially cover |
| Sub-menu | P2 | Native has it, GearUI does not |
| Kbd | P3 | Desktop/Web use |
| Color picker family | P3 | Low use in mobile products |
| Sidebar, Meter, User, Code/Snippet | P3 | On demand |
| Internationalized date formats | 1.0 track | Gregorian-first today |
| Accessibility catch-up (React Aria state model) | 1.0 track | Largest remaining gap |
| RTL | unverified | Typed packs exist; layout direction not accepted |

## 4. Convergence Rules

1. **HeroUI Native decides appearance and interaction.** Where Native has the
   component, style/motion/feedback values come from its source through DTCG
   tokens; no bare numbers.
2. **v3-only components** follow v3's structure and state model, rendered with
   Native's token system so they match existing components.
3. **API style**: keep GearUI's parameter API as the primary entry; add
   composable field primitives for custom layouts instead of forcing a
   compound API.
4. **Keep the mobile-first extras**, restyled on HeroUI tokens.
5. **Accessibility**: follow React Aria's state and semantics model for
   buttons, fields, menus and dialogs; add it to device acceptance.

## 5. Maintenance

Update this document when a component is added or changes status, and refresh
sections 1–3 when the reference version changes. Acceptance status per
component lives in [QUALITY_STATUS.md](./QUALITY_STATUS.md).
