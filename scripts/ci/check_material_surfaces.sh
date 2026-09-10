#!/usr/bin/env bash
# Every surface DESIGN_SYSTEM_SPEC §11.2 calls a material must render through
# MaterialSurface.
#
# Why this needs a guard: the material layer shipped with no consumer at all.
# MaterialSurface existed, Materials.Chrome/Sheet/Popover existed, the sample had
# a probe page — and every one of these seven components painted its own opaque
# fill. Both the KDoc on `materialPolicy` and the spec said flipping the default
# was the single change needed once upstream blur lands. It was not: flipping it
# would have changed no pixel in any app.
#
# A capability with no consumer looks finished from every angle except the one
# that matters, and nothing else in CI notices.
set -euo pipefail
cd "$(dirname "$0")/../.."

SRC=gearui-kit/src/commonMain/kotlin/com/gearui/components

# Tooltip is a typealias over Popover and has no surface of its own.
COMPONENTS="navbar/NavBar.kt bottomnavbar/BottomNavBar.kt actionsheet/ActionSheet.kt bottomsheet/BottomSheet.kt popover/Popover.kt contextmenu/ContextMenu.kt"

missing=""
for rel in $COMPONENTS; do
  f="$SRC/$rel"
  if [ ! -f "$f" ]; then
    echo "✗ missing file: $f"
    exit 1
  fi
  if ! grep -q 'MaterialSurface(' "$f"; then
    missing="$missing  $rel\n"
  fi
done

if [ -n "$missing" ]; then
  echo "✗ §11.2 surfaces not rendering through MaterialSurface:"
  printf "%b" "$missing"
  echo
  echo "  Each of these is named as a material in DESIGN_SYSTEM_SPEC §11.2. Painting"
  echo "  an opaque fill directly means materialPolicy cannot reach it, and turning"
  echo "  blur on does nothing."
  exit 1
fi

echo "✓ materials: all §11.2 surfaces render through MaterialSurface"
