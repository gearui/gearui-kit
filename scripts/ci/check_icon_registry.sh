#!/usr/bin/env bash
set -euo pipefail

# Rule: the icon constants, `Icons.all`, and the shipped PNG assets are one set.
#
# All three drifted apart at once, from a single silent failure in the asset
# generation:
#
#   - nine constants were missing from `Icons.all`, so the gallery reported
#     100 icons against the 109 that existed and those nine were unbrowsable;
#   - `block.png` shipped with no constant naming it, so nothing could ever
#     draw it;
#   - eight icons got a PNG but no SVG, which nobody noticed because
#     `Icon(preferSvg = false)` is the default and every call site takes the
#     PNG path.
#
# The SVG half is reported as a warning rather than a failure: regenerating it
# needs the Phosphor source, and the PNG path is what actually renders.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ICONS_KT="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui/components/icon/Icons.kt"
ASSETS="$ROOT_DIR/gearui-kit/src/commonMain/assets/icons"

tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT

grep -oE '^    const val [a-z_0-9]+' "$ICONS_KT" | awk '{print $3}' | sort -u > "$tmp/consts"
awk '/val all = listOf\(/,/^    \)/' "$ICONS_KT" \
  | grep -oE '^\s+[a-z_0-9]+' | tr -d ' ' | grep -v '^val$' | sort -u > "$tmp/all"
find "$ASSETS" -name '*.png' -exec basename {} .png \; | sort -u > "$tmp/png"
find "$ASSETS" -name '*.svg' -exec basename {} .svg \; | sort -u > "$tmp/svg"

fail=0
report() { # label, file_a, file_b
  local missing; missing="$(comm -23 "$2" "$3" | tr '\n' ' ')"
  if [[ -n "${missing// /}" ]]; then echo "  $1: $missing"; fail=1; fi
}

echo "Icon registry:"
report "constants with no PNG asset"      "$tmp/consts" "$tmp/png"
report "PNG assets with no constant"      "$tmp/png"    "$tmp/consts"
report "constants missing from Icons.all" "$tmp/consts" "$tmp/all"
report "Icons.all entries with no constant" "$tmp/all"  "$tmp/consts"

if [[ $fail -ne 0 ]]; then
  echo
  echo "Rule: a constant, an entry in Icons.all and a 96px PNG, or none of them."
  exit 1
fi

no_svg="$(comm -23 "$tmp/png" "$tmp/svg" | tr '\n' ' ')"
if [[ -n "${no_svg// /}" ]]; then
  echo "  warning: PNG-only, cannot render crisply above ~28dp: $no_svg"
fi

echo "Icon registry guard passed. icons=$(wc -l < "$tmp/consts" | tr -d ' ')"
