#!/usr/bin/env bash
set -euo pipefail

# Rule: the component layer carries no bare `<n>.dp` design values. Spacing comes
# from `Spacing.*`; control geometry (sizes, widths, strokes, indicator diameters)
# comes from `ControlGeometry`, generated from tokens/controls.tokens.json where
# every value records its source. A literal `0.dp` is not a design value and is
# allowed.
#
# This used to be a debt freeze with a per-file baseline. The debt reached zero
# before 1.0.0-beta6, so it is now a hard gate and the baseline file is gone.
#
# Scope is components/ only — foundation owns raw geometry by design.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TARGET_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui/components"

hits="$(grep -rnE '(^|[^0-9.])([1-9][0-9]*(\.[0-9]+)?|0\.[0-9]+)\.dp\b' "$TARGET_DIR" --include='*.kt' | sed "s|$ROOT_DIR/||" || true)"

if [[ -n "$hits" ]]; then
  echo "Hardcoded dp values in the component layer:"
  echo "$hits"
  echo
  echo "Rule: use Spacing.* for padding, margin and gaps, and a ControlGeometry token"
  echo "      (tokens/controls.tokens.json, with its source) for control geometry."
  exit 1
fi

files_scanned="$(find "$TARGET_DIR" -name '*.kt' | wc -l | tr -d ' ')"
echo "Hardcoded dp guard passed. files=$files_scanned literals=0"
