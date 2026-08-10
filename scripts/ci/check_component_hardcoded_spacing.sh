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

# Keyed by per-file count, not file:line. A line-number baseline goes stale
# the moment an unrelated edit shifts lines (adding one import re-reports the
# whole rest of the file as new), which trains people to regenerate the
# baseline reflexively — the exact habit a debt freeze must not create.
grep -rEo '[0-9]+(\.[0-9]+)?\.dp' "$TARGET_DIR" --include='*.kt' \
  | sed "s|$ROOT_DIR/||" \
  | cut -d: -f1 \
  | sort | uniq -c | awk '{print $2 "\t" $1}' \
  | sort > "$tmp_all" || true

# Report any file whose count exceeds its frozen count (or is absent from it).
awk -F'\t' '
  NR==FNR { base[$1] = $2; next }
  { if (!($1 in base) || $2 > base[$1]) print $1 "\t" $2 "\t" (($1 in base) ? base[$1] : 0) }
' <(grep -v '^#' "$BASELINE_FILE" | grep -v '^$' | sort) "$tmp_all" > "$tmp_new" || true

if [[ -s "$tmp_new" ]]; then
  echo "Hardcoded spacing literals grew in the component layer:"
  printf 'file\tnow\tallowed\n'
  cat "$tmp_new"
  echo
  echo "Rule: use Spacing.* for padding/margin/gap. Genuine geometry (icon"
  echo "      size, stroke width, indicator diameter) may stay literal, but"
  echo "      needs reviewer approval to raise a file's frozen count."
  exit 1
fi

total_now="$(awk -F'\t' '{s+=$2} END {print s+0}' "$tmp_all")"
total_base="$(grep -v '^#' "$BASELINE_FILE" | grep -v '^$' | awk -F'\t' '{s+=$2} END {print s+0}')"
echo "Hardcoded spacing guard passed. baseline=$total_base current=$total_now grown=0"
