#!/usr/bin/env bash
set -euo pipefail

# rg is required: without it these scans silently return nothing (false green)
# or blow up mid-pipeline (false red). Fail loudly instead.
command -v rg >/dev/null 2>&1 || { echo "ripgrep (rg) is required by $0"; exit 1; }

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SAMPLE_DIR="$ROOT_DIR/sample/src/commonMain/kotlin/com/gearui/sample"
MAIN_DEMO="$SAMPLE_DIR/MainDemo.kt"

if [[ ! -f "$MAIN_DEMO" ]]; then
  echo "Missing entry file: $MAIN_DEMO"
  exit 1
fi

# Rule 1: sample entry must use App runtime wrapper.
if ! rg -n 'App\(' "$MAIN_DEMO" >/dev/null; then
  echo "Runtime boundary violation: MainDemo must use App as unified runtime entry."
  exit 1
fi

# Rule 2: sample code should not construct Theme wrapper directly.
if rg -n '\bTheme\(' "$SAMPLE_DIR" >/dev/null; then
  echo "Runtime boundary violation: direct Theme(...) usage detected in sample."
  rg -n '\bTheme\(' "$SAMPLE_DIR"
  exit 1
fi

# Rule 3: sample code should not mount overlay root directly.
if rg -n '\bGearOverlayRoot\(' "$SAMPLE_DIR" >/dev/null; then
  echo "Runtime boundary violation: direct GearOverlayRoot(...) usage detected in sample."
  rg -n '\bGearOverlayRoot\(' "$SAMPLE_DIR"
  exit 1
fi

echo "Sample runtime boundary guard passed."
