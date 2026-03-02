#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TARGET_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui/components"
BASELINE_FILE="$ROOT_DIR/scripts/ci/hardcoded_color_baseline.txt"

if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "Missing baseline file: $BASELINE_FILE"
  exit 1
fi

tmp_all="$(mktemp)"
tmp_new="$(mktemp)"
trap 'rm -f "$tmp_all" "$tmp_new"' EXIT

# Capture hardcoded ARGB colors in component layer.
rg -n 'Color\(0x[0-9A-Fa-f]{8}\)' "$TARGET_DIR" \
  | sed "s|$ROOT_DIR/||" \
  | awk -F: '{print $1 ":" $2}' \
  | sort -u > "$tmp_all" || true

# Detect new violations against frozen baseline.
comm -23 "$tmp_all" <(sort -u "$BASELINE_FILE") > "$tmp_new" || true

if [[ -s "$tmp_new" ]]; then
  echo "New hardcoded color violations detected in component layer:"
  cat "$tmp_new"
  echo
  echo "Rule: components must use Theme tokens instead of hardcoded colors."
  echo "If this is intentional legacy debt, update baseline with reviewer approval."
  exit 1
fi

echo "Hardcoded color guard passed. total_current=$(wc -l < "$tmp_all") new=0"
