# Standardization Acceptance — 2026-09-16

> This is batch evidence, not a release verdict. See [current beta3 readiness](BETA3_RELEASE_READINESS.md) for the latest verification and limitations.

## Scope and result

This is a verified implementation batch, not a declaration of complete HeroUI
Native parity. Existing uncommitted work was retained. No release or push was
performed. Reference: open-source HeroUI Native 1.0.9, pinned 42-file manifest.

## Implemented

- Independent brand accent API with automatic black/white contrast selection;
  existing surfaces and explicit state palettes remain intact.
- Sample light/dark, brand and shape controls; English/Simplified/Traditional
  strings; scrollable settings. Navigation and status bars use the same surface
  role, including the custom dark-purple theme.
- Current content/callback reads for Dialog, Popup, Drawer, ActionSheet and
  Popover, without dismiss/reopen on ordinary content changes.
- Consistent disabled opacity for Tag, Pagination and Radio cards. Tag click and
  close are actually disabled and work again after re-enabling.
- Real Tag sample replacing the private SimpleTag and text-only mock sizes;
  Native chip geometry and soft-color mixing come from generated token data.
- Card sample uses real primary/secondary buttons and action results; cover
  placeholder is labelled, not presented as a loaded image.
- Removed duplicate username helper from Form sample.
- BindMobilePage in privchat-app uses large field geometry for the dial-code
  block. No session, authentication, server or account behavior was changed.
- Supported DTCG composite conversion, generated material data and Collapse
  transition consumption; multi-layer shadow rendering is still a limitation.
- API/token baselines deliberately refreshed. See MIGRATION_1_0.md; recompilation
  is required for the pre-1.0 API changes. Baseline success is not ABI preservation.

## Automated verification

- 163 Android unit tests: zero failures/errors.
- 42 token compiler tests: passed (scripts/tests, not scripts).
- 22 shell guardrails: passed, with no spacing debt-baseline increase.
- Generated token outputs and 42-file reference lock: match.
- Android sample compilation, JS library compilation, API compatibility against
  the reviewed new baseline: passed. KLib API tasks compile all three iOS targets.
- iOS sample Xcode build: passed; iPhone 17 Pro, iOS 26.2.
- privchat-app Android compilation with included gearui-kit/privchat-ui: passed.
- API dump and API check must run in separate Gradle invocations: combining them
  trips the plugin's undeclared output-dependency validation.

## Device checks

- Tag: filled/soft/outline and three sizes render; clicking increments click
  count, close increments close count independently. Disabled leaves both counts
  unchanged; re-enabling allows clicking again.
- Dialog custom content: content version 0 to 1 while open; switch to dark and
  green while still open; text, surfaces, buttons and underlying page update.
  Closing works. This validates this path, not every overlay configuration.
- Brand/shape settings: green does not replace neutral page surfaces. New square
  pages render square. Rounded-to-square hot switching has a known limitation.
- Card: content, cover clipping and action layout render; confirm updates result.
- Earlier batch: Select/MultiSelect selection and dismissal, Collapse open/close,
  field focus and disabled form comparison. These are smoke checks, not recorded
  frame-by-frame parity with a running reference application.

## Open items — do not mark complete

1. **Hot shape switching:** a native border may retain its previous radius when
   changing a mounted rounded control to square with unchanged border color.
   Returning Outline.Rounded with zero corners was tested and did not resolve it;
   KNode's radius path also ignores rectangles. The ineffective implementation
   was removed. Fix the renderer/adapter; do not remount the application and lose
   user state as a workaround.
2. **Materials:** multi-layer shadows, spread and inset are preserved as token
   data but not fully rendered by the current single-shadow adapter. The thin
   border fallback is intentional and is not identical to Native.
3. **DTCG:** standard-data processing now covers all 13 types, 14 color spaces,
   group extension and Resolver composition. Kotlin rendering support is a
   separate boundary; see `docs/DTCG_ACCEPTANCE.md` and `tokens/README.md` for
   verified coverage, explicit policies and remaining adapter work.
4. **Theme geometry:** shapes/colors/typography/motion can be supplied; all
   component size tokens are not yet a runtime-injectable geometry profile.
5. **Full acceptance:** every size/variant, gesture interruption, keyboard and
   safe-area combination needs matched-reference testing. Android/Web/HarmonyOS
   visual/gesture parity has not been established by these builds.

## Where to inspect

- Sample Settings: independent brand, mode and shape controls.
- Tag: actual variant/size matrix and disable/re-enable interactions.
- Dialog → custom dialog: live content/theme updates.
- Card: real primary and secondary actions.
- Form/Input/Textarea: supporting text and disabled presentation.

## Tree selection follow-up

- Tree and TreeSelect share minimum-48 selection rows, a tokenized depth step,
  ellipsized labels, press feedback and reserved disclosure/check slots. HeroUI
  Native has no TreeSelect; this is a GearUI extension of its selection surface,
  not a claim of an upstream tree implementation.
- TreeSelect flattens visible nodes into individually keyed lazy items. Reopening
  expands selected ancestors and shows the single-selection check. Multiple
  selection remains controlled and immediate; dismissal does not emit a second
  change. Open overlays read current nodes, selection, callbacks and height.
- Disabling a selector dismisses its panel. Checkbox propagation skips disabled
  subtrees and preserves their existing selected values.
- Cascader now uses minimum-48 rows, weighted ellipsized labels, current options
  and callbacks in its open panel, and closes when disabled. Transfer defaults
  to 48-unit rows while preserving explicitly supplied heights.
- Added eight pure tree-model tests. iPhone 17 Pro smoke checks cover single
  selection/reopen and multi-selection with an indeterminate parent. This does
  not establish every deep-tree, accessibility or cross-platform behavior.
- Public function signatures are unchanged. KLib dump adds only compiler-emitted
  stability metadata for the internal visible-tree-row model in this follow-up.

## Broad component audit — 2026-09-17

Inventory: COMPONENT_ACCEPTANCE_MATRIX.md covers all 58 component directories.
This is a source sweep plus focused fixes, not 58 completed device acceptances.

- ActionSheet: replace click-latched pressed booleans with interaction sources;
  move 56/78/96 geometry to DTCG source; clamp invalid grid columns and avoid
  row-count overflow; remove opaque list/header fills over the sheet material.
- SwipeCell: its unused pressed boolean now follows real press interactions.
- ContextMenu: replace the custom pointer loop with interaction state (including
  disabled handling); open content reads current items, colors and shapes.
- BottomSheet: all three entry points read current content and dismissal
  callbacks. Outside-click policy remains fixed for a presentation.
- Automated: 176 Android tests, zero failures/errors; 42 token tests; API check,
  sample compilation, all current contract scripts and iOS build passed.
- iPhone 17 Pro: description list and grid rendered; independent cancel worked;
  disabled item did not dismiss or invoke a visible result; enabled warning item
  selected and dismissed normally. Safe-area surface remains painted.
- **Unpassed:** a synthetic drag from the first ActionSheet row to outside the
  sheet still selected that row. Could be renderer click semantics or automation
  delivery; this does NOT prove cancellation works. Physical touch or a lower-level
  gesture trace is needed before closing this acceptance item. Press-state API
  adoption alone is not a behavioral proof.
- No full animation parity, dark-mode matrix or multi-platform interaction pass
  is claimed for this batch. Remaining components stay pending in the matrix.
