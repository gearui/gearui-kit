# Component Acceptance Matrix

This inventory covers every current components/ directory. A source scan is not
a visual or behavioral pass. Literal dimension counts are triage data, not a
claim that every occurrence violates the token contract. Composite primitives
(Card/Link/Text), runtime navigation and platform adapters also require acceptance.

## This pass

- Static sweep: press-state ownership, overlay capture, dimension literals and
  existing contract checks across all component directories.
- Focused source review and fixes: ActionSheet, BottomSheet, ContextMenu, SwipeCell.
- Device checks and automated results are recorded separately in
  STANDARDIZATION_ACCEPTANCE.md. No row is a declaration of full HeroUI parity.

| Directory | Kotlin files | Literal dimensions | Direct overlay entry | Acceptance scope |
|---|---:|---:|---|---|
| actionsheet | 1 | 4 | yes | Press lifecycle, grid bounds, DTCG dimensions, transparent inner surface |
| anchor | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| backtop | 1 | 10 | no | Static sweep only; component-level device acceptance pending |
| bottomnavbar | 1 | 7 | no | Static sweep only; component-level device acceptance pending |
| bottomsheet | 1 | 3 | yes | Latest content/callbacks in all three entry points |
| button | 2 | 6 | no | Static sweep only; component-level device acceptance pending |
| calendar | 4 | 7 | no | Static sweep only; component-level device acceptance pending |
| cascader | 1 | 1 | yes | Static sweep only; component-level device acceptance pending |
| cell | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| cellgroup | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| checkbox | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| collapse | 2 | 0 | no | Prior partial device checks; full matrix pending |
| contextmenu | 1 | 4 | yes | Cancelled/disabled press handling; current data/theme |
| dialog | 3 | 5 | yes | Prior partial device checks; full matrix pending |
| drawer | 1 | 7 | yes | Static sweep only; component-level device acceptance pending |
| empty | 1 | 1 | no | Static sweep only; component-level device acceptance pending |
| form | 1 | 1 | no | Prior partial device checks; full matrix pending |
| grid | 2 | 5 | no | Static sweep only; component-level device acceptance pending |
| icon | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| image | 1 | 6 | no | Static sweep only; component-level device acceptance pending |
| imageviewer | 1 | 5 | no | Static sweep only; component-level device acceptance pending |
| input | 1 | 3 | no | Prior partial device checks; full matrix pending |
| loading | 1 | 9 | no | Static sweep only; component-level device acceptance pending |
| navbar | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| navigationmenu | 1 | 2 | yes | Static sweep only; component-level device acceptance pending |
| notification | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| pagination | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| picker | 2 | 19 | no | Static sweep only; component-level device acceptance pending |
| popover | 1 | 22 | yes | Static sweep only; component-level device acceptance pending |
| popup | 1 | 2 | yes | Static sweep only; component-level device acceptance pending |
| progress | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| radio | 1 | 1 | no | Static sweep only; component-level device acceptance pending |
| rate | 1 | 3 | no | Static sweep only; component-level device acceptance pending |
| result | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| scaffold | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| searchbar | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| segmented | 1 | 6 | no | Static sweep only; component-level device acceptance pending |
| select | 4 | 0 | yes | Prior partial device checks; full matrix pending |
| skeleton | 1 | 13 | no | Static sweep only; component-level device acceptance pending |
| slider | 3 | 3 | no | Static sweep only; component-level device acceptance pending |
| snackbar | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| stepper | 1 | 5 | no | Static sweep only; component-level device acceptance pending |
| steps | 1 | 5 | no | Static sweep only; component-level device acceptance pending |
| swipecell | 1 | 0 | no | Previously inert pressed state now consumes interaction events |
| swiper | 1 | 13 | no | Static sweep only; component-level device acceptance pending |
| switch | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| table | 1 | 6 | no | Static sweep only; component-level device acceptance pending |
| tabs | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| tag | 1 | 0 | no | Prior partial device checks; full matrix pending |
| textarea | 2 | 1 | no | Prior partial device checks; full matrix pending |
| timeline | 1 | 12 | no | Static sweep only; component-level device acceptance pending |
| toast | 1 | 3 | yes | Static sweep only; component-level device acceptance pending |
| tooltip | 1 | 0 | no | Static sweep only; component-level device acceptance pending |
| tour | 1 | 2 | yes | Static sweep only; component-level device acceptance pending |
| transfer | 1 | 4 | no | Static sweep only; component-level device acceptance pending |
| tree | 1 | 0 | no | Prior partial device checks; full matrix pending |
| treeselect | 1 | 2 | yes | Prior partial device checks; full matrix pending |
| watermark | 1 | 4 | no | Static sweep only; component-level device acceptance pending |

## Required per-component checks

1. Default and non-default size, long text, empty data and large data.
2. Press/release/cancel, disabled, loading, focus and errors where applicable.
3. Light/dark, independent accent, shapes and live theme changes.
4. Overlays: updated content/callbacks, outside tap, back, scroll/drag, keyboard
   and safe area. Configuration captured at presentation must be documented.
5. iOS visual/interaction checks first; Android, Web and HarmonyOS have separate
   pending device/browser acceptance. Compilation does not satisfy this row.

## Known open boundaries

- Hot rounded-to-square native border refresh remains a renderer limitation.
- Shared surface consumers render layered shadows; legacy elevation consumers and
  complete component/platform parity remain unaccepted. See SURFACE_RENDERING_ACCEPTANCE.md.
- DTCG Format/Resolver support and renderer restrictions are specified in tokens/README.md;
  this matrix does not certify general conformance.
- ActionSheet/Tour/TreeSelect are GearUI patterns; do not invent exact upstream
  HeroUI Native counterparts.
- BottomSheet outside-click policy is chosen when shown; this pass refreshes
  content and callbacks, not live replacement of overlay policy.
- Tour same-index step/callback updates and ContextMenu theme parameter behavior
  still require focused review; not claimed fixed by the source sweep.
