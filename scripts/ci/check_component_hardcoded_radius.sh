#!/usr/bin/env bash
set -euo pipefail

# Rule: corner radius comes from the frozen six-step shape scale
# (none/sm/md/lg/xl/full). Components read `Theme.shapes.*`, overlays read
# `OverlayDefaults.{panelShape,modalShape,sheetShape}`, and token layers that
# need a Dp read `com.gearui.foundation.layout.Radius.*`.
#
# Two failure modes are rejected:
#
#   1. A literal dp inside RoundedCornerShape(...). Ad-hoc radii are how the
#      scale drifted (Dialog at 8 while the dropdown it covered sat at 12).
#
#   2. A Spacing.* token used as a radius. Spacing and Radius are both Dp and
#      their values line up (Spacing.md and Radius.xl are both 12dp), so this
#      compiles and renders correctly while tying corner radius to the spacing
#      scale — retuning spacing would reshape every sheet.
#
# The scan is whole-file, not line-by-line: RoundedCornerShape( often spans
# several lines, and a line-based grep silently misses those. That blind spot
# hid seven call sites, including CalendarPopup's edge-anchored sheet.
#
# ## Exemption
#
# Not every RoundedCornerShape is a surface radius. Arrow tips, half-circles
# and pill geometry are computed from a control's own dimensions, and forcing
# them onto the surface scale would be wrong. Mark those with
#
#     // shape-exempt: <reason>
#
# on the line before, or on the RoundedCornerShape line itself. The reason is
# required — an exemption without one is indistinguishable from an oversight.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
# theme/Shapes.kt defines the scale; OverlayDefaults derives the sheet shape.
SCALE_DEF="$SRC_DIR/theme/Shapes.kt"
OVERLAY_DEF="$SRC_DIR/overlay/OverlayDefaults.kt"

command -v perl >/dev/null 2>&1 || { echo "perl is required by $0"; exit 1; }

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

find "$SRC_DIR" -name '*.kt' \
     ! -path "$SCALE_DEF" ! -path "$OVERLAY_DEF" -print0 \
  | xargs -0 perl -0777 -ne '
      my @lines = split /\n/, $_, -1;
      while (m{RoundedCornerShape\s*\(([^()]*)\)}gs) {
        # Capture the offset before any other match resets @-.
        my ($args, $start) = ($1, $-[0]);
        next unless $args =~ m{[0-9]+(?:\.[0-9]+)?\.dp|Spacing\.};
        my $before = substr($_, 0, $start);
        my $line = ($before =~ tr/\n//) + 1;
        my $here = $lines[$line - 1] // "";
        my $prev = $line >= 2 ? ($lines[$line - 2] // "") : "";
        next if "$prev\n$here" =~ m{shape-exempt:\s*\S};
        print "$ARGV:$line\n";
      }
    ' > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded corner radius detected outside the shape scale:"
  sed "s|$ROOT_DIR/||" "$tmp_hits"
  echo
  echo "Rule: use Theme.shapes.{none,sm,md,lg,xl,full} (0/4/6/8/12/9999 dp),"
  echo "      or OverlayDefaults.{panelShape,modalShape,sheetShape} for overlays."
  echo "      Never pass a Spacing.* token as a corner radius."
  echo "      Off-scale values must be snapped to the nearest step, not added."
  echo "      Genuine geometry (arrow tips, half-circles) needs an explicit"
  echo "      '// shape-exempt: <reason>' on or above the line."
  exit 1
fi

echo "Hardcoded radius guard passed. violations=0"
