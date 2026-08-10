#!/usr/bin/env bash
set -euo pipefail

# Rule: corner radius comes from the frozen six-step shape scale
# (none/sm/md/lg/xl/full). Components read `Theme.shapes.*`; token layers that
# need a Dp read `com.gearui.foundation.layout.Radius.*`. Literal
# `RoundedCornerShape(<n>.dp)` is only allowed in theme/Shapes.kt, which *is*
# the scale definition.
#
# Baseline is 0 — hard gate. Two scales that disagree (e.g. a 3dp "small" next
# to a 4dp `sm`) is exactly the drift this guard exists to prevent.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
SCALE_DEF="$SRC_DIR/theme/Shapes.kt"
# OverlayDefaults derives the edge-anchored sheet shape from Radius.xl.
OVERLAY_DEF="$SRC_DIR/overlay/OverlayDefaults.kt"

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

# Two failure modes: a literal dp, and a Spacing token standing in for a
# radius. The second is the sneakier one — Spacing and Radius are both Dp and
# their values line up (Spacing.md and Radius.xl are both 12dp), so
# RoundedCornerShape(Spacing.md) compiles and renders correctly today while
# tying corner radius to the spacing scale.
grep -rEn 'RoundedCornerShape\([^)]*([0-9.]+\.dp|Spacing\.)' "$SRC_DIR" --include='*.kt' \
  | grep -v "^$SCALE_DEF:" \
  | grep -v "^$OVERLAY_DEF:" \
  | sed "s|$ROOT_DIR/||" \
  | awk -F: '{print $1 ":" $2}' \
  | sort -u > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded corner radius detected outside the shape scale:"
  cat "$tmp_hits"
  echo
  echo "Rule: use Theme.shapes.{none,sm,md,lg,xl,full} (0/4/6/8/12/9999 dp),"
  echo "      or OverlayDefaults.{panelShape,modalShape,sheetShape} for overlays."
  echo "      Never pass a Spacing.* token as a corner radius."
  echo "      Off-scale values must be snapped to the nearest step, not added."
  exit 1
fi

echo "Hardcoded radius guard passed. violations=0"
