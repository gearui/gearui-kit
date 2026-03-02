#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SAMPLE_DIR="$ROOT_DIR/sample/src/commonMain/kotlin/com/gearui/sample"
MAIN_DEMO="$SAMPLE_DIR/MainDemo.kt"

if [[ ! -f "$MAIN_DEMO" ]]; then
  echo "Missing entry file: $MAIN_DEMO"
  exit 1
fi

# Rule 1: sample entry must use GearApp runtime wrapper.
if ! rg -n 'GearApp\(' "$MAIN_DEMO" >/dev/null; then
  echo "Runtime boundary violation: MainDemo must use GearApp as unified runtime entry."
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
