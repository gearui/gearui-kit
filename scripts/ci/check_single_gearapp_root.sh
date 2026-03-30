#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SAMPLE_DIR="$ROOT_DIR/sample/src/commonMain/kotlin/com/gearui/sample"
MAIN_DEMO="$SAMPLE_DIR/MainDemo.kt"

if [[ ! -f "$MAIN_DEMO" ]]; then
  echo "Missing entry file: $MAIN_DEMO"
  exit 1
fi

echo "[single-gearapp-root] checking sample GearApp root uniqueness..."

# Rule 1: MainDemo must provide GearApp root.
if ! rg -n 'GearApp\(' "$MAIN_DEMO" >/dev/null; then
  echo "Single-root violation: MainDemo must define GearApp root entry."
  exit 1
fi

# Rule 2: sample tree may only contain GearApp(...) in MainDemo.kt.
TMP_HITS="$(mktemp)"
trap 'rm -f "$TMP_HITS"' EXIT

rg -n 'GearApp\(' "$SAMPLE_DIR" \
  | sed "s|$ROOT_DIR/||" >"$TMP_HITS" || true

if [[ ! -s "$TMP_HITS" ]]; then
  echo "Single-root violation: no GearApp usage found in sample."
  exit 1
fi

if grep -v '^sample/src/commonMain/kotlin/com/gearui/sample/MainDemo.kt:' "$TMP_HITS" >/tmp/single_gearapp_root_violations.txt; then
  echo "Single-root violation: GearApp may only be used in sample MainDemo.kt."
  cat /tmp/single_gearapp_root_violations.txt
  exit 1
fi

# Rule 3: exactly one GearApp invocation is expected in MainDemo entry.
main_count="$(grep -c '^sample/src/commonMain/kotlin/com/gearui/sample/MainDemo.kt:' "$TMP_HITS" || true)"
if [[ "$main_count" -ne 1 ]]; then
  echo "Single-root violation: expected exactly 1 GearApp(...) call in MainDemo.kt, got $main_count."
  cat "$TMP_HITS"
  exit 1
fi

echo "[single-gearapp-root] passed."
