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
# No component belongs on this list. NavBar, BottomNavBar, Drawer, ActionSheet
# and BottomSheet each used to read insets directly and were all allowlisted;
# they now go through runtime.rememberSafeAreaInset, so the list is back to the
# runtime itself, the overlay host, and the sample's diagnostic page. If a
# component needs adding here, that is the signal to extend the resolver rather
# than to widen the list.
ALLOWLIST=(
  "$KIT_DIR/com/gearui/runtime/RuntimeEnvironment.kt"
  "$KIT_DIR/com/gearui/runtime/RuntimeInsetsBridge.kt"
  "$KIT_DIR/com/gearui/overlay/OverlayHost.kt"
  "$SAMPLE_DIR/com/gearui/sample/examples/runtime/InsetsDebugExample.kt"
)

tmp_hits="$(mktemp)"
tmp_allowed="$(mktemp)"
trap 'rm -f "$tmp_hits" "$tmp_allowed" /tmp/safearea_use_safearea_hits.txt /tmp/safearea_top_offset_hits.txt' EXIT

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

# Rule 3: top-floating feedback overlays must resolve their public topOffset
# against runtime safeArea, and must not freeze full-screen interaction.
#
# The file list is discovered, not hand-written. A hand-written list only
# covers the components someone remembered; a third top-floating banner added
# later would silently escape it. Anything declaring a `topOffset` parameter is
# a top-floating overlay by definition.
# Portable read loop rather than `mapfile`: macOS ships bash 3.2, where
# mapfile does not exist, so that form would pass on CI and fail locally.
TOP_FLOATING_FILES=()
while IFS= read -r _f; do
  TOP_FLOATING_FILES+=("$_f")
done < <(rg -l 'topOffset[[:space:]]*:' "$KIT_DIR/com/gearui/components" | sort)

if [[ ${#TOP_FLOATING_FILES[@]} -eq 0 ]]; then
  echo "SafeArea contract: expected at least one top-floating overlay, found none."
  echo "The discovery pattern probably drifted — check for a renamed parameter."
  exit 1
fi

if rg -n 'padding\(top = topOffset\.dp\)' "${TOP_FLOATING_FILES[@]}" >/tmp/safearea_top_offset_hits.txt; then
  echo "SafeArea contract violation: top floating overlays must resolve topOffset against runtime safeArea."
  cat /tmp/safearea_top_offset_hits.txt
  exit 1
fi

for f in "${TOP_FLOATING_FILES[@]}"; do
  if ! rg -q 'passThroughOutside[[:space:]]*=[[:space:]]*true' "$f"; then
    echo "Overlay contract violation: top floating overlay must set passThroughOutside = true."
    echo "${f#"$ROOT_DIR/"}"
    exit 1
  fi
done

echo "[safearea-contract] passed."
