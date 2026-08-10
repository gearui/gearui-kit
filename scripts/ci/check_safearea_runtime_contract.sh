#!/usr/bin/env bash
set -euo pipefail

# rg is required: without it these scans silently return nothing (false green)
# or blow up mid-pipeline (false red). Fail loudly instead.
command -v rg >/dev/null 2>&1 || { echo "ripgrep (rg) is required by $0"; exit 1; }

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
KIT_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin"
SAMPLE_DIR="$ROOT_DIR/sample/src/commonMain/kotlin"

echo "[safearea-contract] checking runtime-owned safe area policy..."

# Rule 1: do not reintroduce page-level/component-level useSafeArea switches.
if rg -n '\buseSafeArea\b' "$KIT_DIR/com/gearui/components" "$SAMPLE_DIR" >/tmp/safearea_use_safearea_hits.txt; then
  echo "SafeArea contract violation: 'useSafeArea' must not exist in component/sample code."
  cat /tmp/safearea_use_safearea_hits.txt
  exit 1
fi

# Rule 2: forbid direct safeAreaInsets access outside approved runtime/fallback files.
# The component entries are edge-anchored chrome that must consume an inset to
# avoid drawing under the status bar or the home indicator. Anything else reads
# insets through the runtime.
ALLOWLIST=(
  "$KIT_DIR/com/gearui/runtime/RuntimeEnvironment.kt"
  "$KIT_DIR/com/gearui/runtime/RuntimeInsetsBridge.kt"
  "$KIT_DIR/com/gearui/components/navbar/NavBar.kt"
  "$KIT_DIR/com/gearui/components/bottomnavbar/BottomNavBar.kt"
  "$KIT_DIR/com/gearui/components/drawer/Drawer.kt"
  "$KIT_DIR/com/gearui/components/actionsheet/ActionSheet.kt"
  "$KIT_DIR/com/gearui/components/bottomsheet/BottomSheet.kt"
  "$KIT_DIR/com/gearui/overlay/OverlayHost.kt"
  "$SAMPLE_DIR/com/gearui/sample/examples/runtime/InsetsDebugExample.kt"
)

tmp_hits="$(mktemp)"
tmp_allowed="$(mktemp)"
trap 'rm -f "$tmp_hits" "$tmp_allowed" /tmp/safearea_use_safearea_hits.txt' EXIT

rg -n 'safeAreaInsets' "$KIT_DIR" "$SAMPLE_DIR" \
  | sed "s|$ROOT_DIR/||" >"$tmp_hits" || true

{
  for f in "${ALLOWLIST[@]}"; do
    # Normalize to relative path used by ripgrep output.
    rel="${f#"$ROOT_DIR/"}"
    echo "^${rel}:"
  done
} >"$tmp_allowed"

if [[ -s "$tmp_hits" ]]; then
  if ! grep -Evf "$tmp_allowed" "$tmp_hits" >/tmp/safearea_violations.txt; then
    : # no violations
  else
    echo "SafeArea contract violation: direct safeAreaInsets access outside runtime allowlist."
    cat /tmp/safearea_violations.txt
    exit 1
  fi
fi

echo "[safearea-contract] passed."
