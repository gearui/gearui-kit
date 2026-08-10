#!/usr/bin/env bash
set -euo pipefail

# Rule: icon size comes from the IconSizes scale.
#
#   IconSizes.Default   xs 12  sm 14  md 16  lg 18  xl 24   — inline with text
#   IconSizes.Display   sm 28  md 36  lg 40                 — empty states,
#                                                             result pages
#
# IconTokens has always carried the note "❌ 禁止硬编码 .size(18.dp)", and
# nothing enforced it: 34 of 37 icon sizes in the component layer were
# literals. The old scale was small/medium/large = 14/18/24, which omitted
# 16dp — the value the entire field family had settled on for its trailing
# chevrons. When a scale lacks the size people actually need, the scale is
# what gets bypassed.
#
# foundation/avatar/AvatarTokens.kt is exempt: those are avatar dimensions,
# a component's own geometry rather than icon sizes.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
SCALE_DEF="$SRC_DIR/foundation/typography/IconTokens.kt"
AVATAR_DEF="$SRC_DIR/foundation/avatar/AvatarTokens.kt"

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

grep -rEn 'size = [0-9.]+\.dp' "$SRC_DIR" --include='*.kt' \
  | grep -v "^$SCALE_DEF:" \
  | grep -v "^$AVATAR_DEF:" \
  | sed "s|$ROOT_DIR/||" \
  | sort -u > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded icon size detected outside the IconSizes scale:"
  cat "$tmp_hits"
  echo
  echo "Rule: use IconSizes.Default.{xs,sm,md,lg,xl} (12/14/16/18/24) for"
  echo "      icons that sit inline with text, or IconSizes.Display.{sm,md,lg}"
  echo "      (28/36/40) for empty-state and result illustrations."
  echo "      A size that belongs to one component (a field's trailing icon,"
  echo "      an avatar) belongs in that component's tokens, not as a literal."
  exit 1
fi

echo "Hardcoded icon size guard passed. violations=0"
