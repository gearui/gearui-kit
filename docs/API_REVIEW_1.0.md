# 1.0 Public API Review (G1)

The record behind gate item G1 in [QUALITY_STATUS.md](./QUALITY_STATUS.md) §8: every
public composable was checked against the naming rules below, the changes were made in one
pass, and what was kept is written down with its reason so it is not re-litigated.

Method: every public `@Composable` in `components/` (146) was extracted from source with
its parameter names, types and defaults and checked mechanically for the rules below; each
hit was then judged by hand. The extractor lives with the skill reference generator
(`scripts/gen_skill_reference.py`).

## Rules

1. A parameter is named for what it is, without an `is` prefix (`readOnly`, `enabled`,
   `scrollable`), as Compose does.
2. A choice among fixed options is an enum, never a `String`.
3. A callback pairs with the value it reports: `value` / `onValueChange`,
   `current` / `onCurrentChange`; present tense `…Change`, never `…Changed`.
4. An overlay reports leaving with `onDismiss`, whatever closed it. `onClose` is reserved for
   an inline element's close button (Alert, NoticeBar, Tag).
5. Existing parameter order is not changed for uniformity alone (COMPONENT_SPEC §2).

## Changed (2026-10-28)

| Component | Before | After | Rule |
| --- | --- | --- | --- |
| `Input` | `labelPosition: String = "top"` (`"top"`, `"left"`) | `layout: FormLayout = FormLayout.VERTICAL` | 2; one name and one enum for the label arrangement, shared with `FormItem` |
| `Input` | `isPassword` | `password` | 1 |
| `Tabs` | `isScrollable` | `scrollable` | 1 |
| `Steps` | `onChange` | `onCurrentChange` | 3 |
| `SwipeCell` | `onChange(direction, open)` | `onOpenChange(direction, open)` | 3 |
| `Swiper` | `onIndexChanged` | `onIndexChange` | 3 |
| `CalendarPopup` | `onClose` | `onDismiss` | 4 |
| `ImageViewer` | `onClose` | `onDismiss` | 4 |

**Migration.** Source-breaking renames only; no behaviour changes. Rename the argument at
each call site: `labelPosition = "top"` → `layout = FormLayout.VERTICAL`, `"left"` →
`FormLayout.HORIZONTAL` (import `com.gearui.components.form.FormLayout`); the others are
one-to-one name changes. Binary compatibility does not matter before 1.0 (no stable
release has shipped); recompiling against the new names is required. Migrated in the same
pass: the sample, privchat-app, live-chat and lms-app. Apps on the Maven `1.0.0-beta8`
artifact (itwind-app) migrate when they upgrade.

## Kept, with the reason

| Finding | Where | Why it stays |
| --- | --- | --- |
| No `modifier` parameter | `ConfirmDialog`, `AlertDialog`, `DialogContent`, `BottomSheet`, `ActionSheetContent`, `ToastHost`, `LocalToast*`, `ButtonGroupDivider`, `InputGroupDivider`, `PullRefreshIndicator` | Overlay content and hosts are placed by the overlay runtime, and the dividers by their group; a modifier would have nothing to act on |
| A content parameter before `modifier` | `Button(text, …)`, `ToggleButton(text, …)`, `InputGroupAddon(text, …)`, `SearchBarWithAction(actionText, …)`, `FullScreenLoading(text, icon, …)`, `Collapse`, `ResponsiveGrid` | Rule 5; positional calls such as `Button("Save", onClick = …)` are relied on |
| Trailing parameter is not a content lambda | `Popover` (`trigger` last), `DialogContent` (`actionLayout` last), `Watermark` (`content` is the watermark text) | `trigger` is itself the trailing lambda; the others take no slot content |
| `onFocusChanged` | `Input`, `AutoResizeTextarea` | Matches Compose's `Modifier.onFocusChanged` |
| `onChangeStart` / `onChangeEnd` | `Slider`, `RangeSlider` | They mark the ends of a drag, alongside `onValueChange` |
| `selected` vs `checked` | `RadioButton`, `ToggleButton` vs `Checkbox`, `Switch` | Compose's own split: a choice is selected, a toggle is checked |
| `selectedId` vs `selectedIndex` | `BottomNavBar`, `Tabs` vs `TabPager` | Different data: an item id versus a page index |
| `onItemClick` vs `onSelect` | `BottomSheet`, `Drawer` vs `Tabs`, `Cascader`, `ComboBox`, `IndexBar`, `BottomNavBar` | An action row is clicked; a choice is selected |
| 28 parameters | `Input` | Every field feature lives on the one field; splitting it would duplicate the family API |
| `enabled` / `disabled` | field family / action family | The frozen boundary in COMPONENT_SPEC §2 |
