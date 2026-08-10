#!/usr/bin/env bash
set -euo pipefail

# Rule: stroke weight comes from the BorderWidth scale
# (none/hairline/thin/thick = 0/0.5/1/2 dp).
#
# GearUI is border-first — hierarchy is carried by outlines rather than
# shadows — so stroke weight is a first-class design axis and deserves a named
# scale, the same as Shapes, Elevation and Spacing. Before it had one, 87
# literals were scattered across the tree, and the drift was already visible:
# Radio and Checkbox outlined themselves at 1.5dp while every other control
# used 1dp.
#
# Rejected:
#   - a literal dp as the first argument of .border(...)
#   - a literal dp assigned to a `thickness` / `borderWidth` property
#
# Divider *heights* are not checked here. A 1dp-tall Box is indistinguishable
# from a 1dp-tall spacer to a text scan, and guessing wrong would either nag on
# layout code or wave through real strokes. Those are covered by review.
#
# Baseline is 0 — hard gate. foundation/border/BorderWidth.kt is the scale
# definition and is exempt.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
SCALE_DEF="$SRC_DIR/foundation/border/BorderWidth.kt"

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

grep -rEn '\.border\([[:space:]]*(width[[:space:]]*=[[:space:]]*)?[0-9.]+\.dp|\b(thickness|borderWidth)[[:space:]]*=[[:space:]]*[0-9.]+\.dp' \
     "$SRC_DIR" --include='*.kt' \
  | grep -v "^$SCALE_DEF:" \
  | sed "s|$ROOT_DIR/||" \
  | sort -u > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded stroke weight detected outside the BorderWidth scale:"
  cat "$tmp_hits"
  echo
  echo "Rule: use BorderWidth.{none,hairline,thin,thick} (0/0.5/1/2 dp)."
  echo "      hairline dividers, table grid lines, card hairline outline"
  echo "      thin     the default: inputs, buttons, panels, cards"
  echo "      thick    emphasis rings: timeline nodes, step markers"
  echo "      Do not add a focus-state weight — a border that changes width on"
  echo "      focus resizes the content box and makes the layout jump."
  exit 1
fi

echo "Hardcoded border width guard passed. violations=0"
