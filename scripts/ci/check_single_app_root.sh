#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SAMPLE_DIR="$ROOT_DIR/sample/src/commonMain/kotlin/com/gearui/sample"
MAIN_DEMO="$SAMPLE_DIR/MainDemo.kt"

if [[ ! -f "$MAIN_DEMO" ]]; then
  echo "Missing entry file: $MAIN_DEMO"
  exit 1
fi

echo "[single-app-root] checking sample App root uniqueness..."

# Rule 1: MainDemo must provide App root.
if ! rg -n 'App\(' "$MAIN_DEMO" >/dev/null; then
  echo "Single-root violation: MainDemo must define App root entry."
  exit 1
fi

# Rule 2: sample tree may only contain App(...) in MainDemo.kt.
TMP_HITS="$(mktemp)"
trap 'rm -f "$TMP_HITS"' EXIT

rg -n 'App\(' "$SAMPLE_DIR" \
  | sed "s|$ROOT_DIR/||" >"$TMP_HITS" || true

if [[ ! -s "$TMP_HITS" ]]; then
  echo "Single-root violation: no App usage found in sample."
  exit 1
fi

if grep -v '^sample/src/commonMain/kotlin/com/gearui/sample/MainDemo.kt:' "$TMP_HITS" >/tmp/single_app_root_violations.txt; then
  echo "Single-root violation: App may only be used in sample MainDemo.kt."
  cat /tmp/single_app_root_violations.txt
  exit 1
fi

# Rule 3: exactly one App invocation is expected in MainDemo entry.
main_count="$(grep -c '^sample/src/commonMain/kotlin/com/gearui/sample/MainDemo.kt:' "$TMP_HITS" || true)"
if [[ "$main_count" -ne 1 ]]; then
  echo "Single-root violation: expected exactly 1 App(...) call in MainDemo.kt, got $main_count."
  cat "$TMP_HITS"
  exit 1
fi

echo "[single-app-root] passed."
