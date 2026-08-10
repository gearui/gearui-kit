#!/usr/bin/env bash
set -euo pipefail

# Rule: spacing in the component layer comes from `Spacing.*`, not from bare
# `<n>.dp` literals.
#
# Unlike the radius and i18n guards this one is a *debt freeze*, not a hard
# gate: a large share of the current literals are legitimate geometry (icon
# sizes, stroke widths, indicator diameters) that no spacing scale should own.
# So the existing sites are frozen in a baseline and only new ones fail. The
# baseline is expected to shrink over time, never grow.
#
# Scope is deliberately components/ only — foundation/primitives owns raw
# geometry by design.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TARGET_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui/components"
BASELINE_FILE="$ROOT_DIR/scripts/ci/hardcoded_spacing_baseline.txt"

if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "Missing baseline file: $BASELINE_FILE"
  exit 1
fi

tmp_all="$(mktemp)"
tmp_new="$(mktemp)"
trap 'rm -f "$tmp_all" "$tmp_new"' EXIT

grep -rEn '[0-9]+(\.[0-9]+)?\.dp' "$TARGET_DIR" --include='*.kt' \
  | sed "s|$ROOT_DIR/||" \
  | awk -F: '{print $1 ":" $2}' \
  | sort -u > "$tmp_all" || true

comm -23 "$tmp_all" <(sort -u "$BASELINE_FILE") > "$tmp_new" || true

if [[ -s "$tmp_new" ]]; then
  echo "New hardcoded spacing literals detected in component layer:"
  cat "$tmp_new"
  echo
  echo "Rule: use Spacing.* for padding/margin/gap. Genuine geometry (icon"
  echo "      size, stroke width, indicator diameter) may stay literal, but"
  echo "      needs reviewer approval to enter the baseline."
  exit 1
fi

echo "Hardcoded spacing guard passed. baseline=$(sort -u "$BASELINE_FILE" | wc -l | tr -d ' ') current=$(wc -l < "$tmp_all" | tr -d ' ') new=0"
