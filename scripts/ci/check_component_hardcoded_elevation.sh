#!/usr/bin/env bash
set -euo pipefail

# Rule: shadow depth comes from the four-step Elevation scale
# (none/raised/floating/modal). Two things are rejected:
#
#   1. Literal dp passed as elevation — shadow(8.dp, ...) or elevation = 4.dp.
#      Ad-hoc depths are how the scale went inconsistent in the first place
#      (Dialog sat at 6dp while a TreeSelect dropdown sat at 8dp, i.e. the
#      modal rendered below the dropdown).
#
#   2. Spacing.* passed as elevation. Spacing and Elevation are both Dp and
#      their values collide (Spacing.xs and Elevation.raised are both 4dp), so
#      this compiles and looks right — until the spacing scale is retuned and
#      silently drags every shadow with it.
#
# Baseline is 0 — hard gate. foundation/elevation/Elevation.kt is the scale
# definition and is exempt.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
SCALE_DEF="$SRC_DIR/foundation/elevation/Elevation.kt"

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

grep -rEn '(shadow\(|elevation[[:space:]]*=)' "$SRC_DIR" --include='*.kt' \
  | grep -E '[0-9]+(\.[0-9]+)?\.dp|Spacing\.' \
  | grep -v "^$SCALE_DEF:" \
  | sed "s|$ROOT_DIR/||" \
  | sort -u > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded elevation detected outside the Elevation scale:"
  cat "$tmp_hits"
  echo
  echo "Rule: use Elevation.{none,raised,floating,modal} (0/4/6/8 dp)."
  echo "      none     disabled / flat — the border-first default"
  echo "      raised   thumbs, back-to-top, popover, context menu, snackbar"
  echo "      floating dropdown panels, notification"
  echo "      modal    dialog, tour"
  echo "      Cards, cells and inputs stay flat; only overlays get elevation."
  exit 1
fi

echo "Hardcoded elevation guard passed. violations=0"
