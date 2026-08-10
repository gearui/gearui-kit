#!/usr/bin/env bash
set -euo pipefail

# Rule: the pre-1.0 Float token pool (`com.gearui.Radius`, `com.gearui.Typography`
# and the `foundation/tokens/ComponentTokens.kt` data classes built on them) is
# frozen. It is still load-bearing for Input/Tag/Surface, so it cannot simply be
# deleted — but nothing new may reference it.
#
# Canonical replacements:
#   com.gearui.Radius            -> com.gearui.theme.Shapes (Shape)
#                                   com.gearui.foundation.layout.Radius (Dp)
#   com.gearui.Typography        -> com.gearui.foundation.typography.Typography
#   ComponentTokens data classes -> per-component token objects under foundation/
#
# Untangling the pool is tracked as the Input/Field token batch; until then this
# guard stops the debt from spreading.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
ALLOWED="$SRC_DIR/foundation/tokens/ComponentTokens.kt"

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

grep -rEn '^import com\.gearui\.(Radius|Typography)$' "$SRC_DIR" --include='*.kt' \
  | grep -v "^$ALLOWED:" \
  | sed "s|$ROOT_DIR/||" \
  | sort -u > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "New reference to the frozen legacy Float token pool:"
  cat "$tmp_hits"
  echo
  echo "Rule: use Theme.shapes / foundation.layout.Radius for radius, and"
  echo "      foundation.typography.Typography for text styles."
  exit 1
fi

echo "Legacy token pool guard passed. new_references=0"
