#!/usr/bin/env bash
set -euo pipefail

# Rule: the pre-1.0 Float token pool stays gone.
#
# It was three files — root Radius.kt, root Typography.kt, and
# foundation/tokens/ComponentTokens.kt — carrying dimensions as raw Float on a
# radius scale (3/6/9/12) that disagreed with theme/Shapes (0/4/6/8/12). While
# it existed, this guard froze it to its last consumer. Input and Tag have
# since moved to Dp tokens under foundation/input and foundation/tag, so the
# pool is deleted and this guard flips to preventing its return.
#
# Canonical replacements:
#   com.gearui.Radius     -> com.gearui.theme.Shapes (Shape)
#                            com.gearui.foundation.layout.Radius (Dp)
#   com.gearui.Typography -> com.gearui.foundation.typography.Typography
#   ComponentTokens.*     -> per-component tokens under foundation/<component>/

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"

status=0

for f in "Radius.kt" "Typography.kt" "foundation/tokens/ComponentTokens.kt"; do
  if [[ -e "$SRC_DIR/$f" ]]; then
    echo "Legacy Float token pool reintroduced: com/gearui/$f"
    status=1
  fi
done

refs="$(grep -rEn '^import com\.gearui\.(Radius|Typography)$|com\.gearui\.foundation\.tokens\.' \
          "$SRC_DIR" --include='*.kt' | sed "s|$ROOT_DIR/||" || true)"

if [[ -n "$refs" ]]; then
  echo "Reference to the removed Float token pool:"
  echo "$refs"
  status=1
fi

if [[ $status -ne 0 ]]; then
  echo
  echo "Rule: use Theme.shapes / foundation.layout.Radius for radius,"
  echo "      foundation.typography.Typography for text styles, and"
  echo "      per-component Dp tokens under foundation/<component>/."
  exit 1
fi

echo "Legacy token pool guard passed. pool_absent=yes references=0"
