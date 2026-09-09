#!/usr/bin/env bash
set -euo pipefail

# Rule: affordances go through the Icon system, never through an emoji glyph.
#
# DatePicker and TimePicker used to render their trailing marks as the literal
# text "📅" and "🕐". It looked fine on the simulator, which is the problem:
#
#   - an emoji is a text glyph, so it ignores `tint` and cannot follow the
#     theme's foreground/muted colours;
#   - it renders from the platform font, so iOS, Android and Web each draw a
#     different picture for the same component;
#   - it sidesteps the Icon primitive that every other component is required
#     to use, so it is invisible to icon tooling and to the allowlist.
#
# It also masked a real regression: when the iOS asset copy phase went missing
# and every real icon rendered blank, the pickers still showed their emoji, so
# the family looked half-working rather than obviously broken.
#
# Baseline is 0 — hard gate.
#
# The sample is in scope too, and used not to be. That gap cost 60 sites: the
# library could not draw an emoji, while `icon: String` let a caller pass one,
# and the sample — which is the kit's own reference usage — passed emoji in
# seventeen files. GearUI was demonstrating the thing it forbids.
#
# The two scopes are checked differently on purpose. Inside the library, no
# emoji in any string literal. Inside the sample, only *icon position* is
# policed: a demo may legitimately put an emoji in content ("苹果 🍎" is data,
# not an affordance), but `icon = "📱"` is an affordance and is exactly what
# this rule is about.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"
SAMPLE_DIR="$ROOT_DIR/sample/src"

command -v perl >/dev/null 2>&1 || { echo "perl is required by $0"; exit 1; }

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

find "$SRC_DIR" -name '*.kt' -print0 \
  | xargs -0 perl -CSD -ne '
      next if m{^\s*(\*|//|/\*)};
      s{//.*$}{};
      # Pictographs, dingbats and symbol arrows inside a string literal.
      if (m{"[^"]*[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}][^"]*"}) {
        print "$ARGV:$.\n";
      }
    } continue {
      close ARGV if eof;
    ' > "$tmp_hits" || true

# Sample: icon position only.
find "$SAMPLE_DIR" -name '*.kt' -print0 \
  | xargs -0 perl -CSD -ne '
      next if m{^\s*(\*|//|/\*)};
      s{//.*$}{};
      if (m{\bicon\s*=\s*"[^"]*[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}]}) {
        print "$ARGV:$.\n";
      }
    } continue {
      close ARGV if eof;
    ' >> "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Emoji used as a UI affordance:"
  sed "s|$ROOT_DIR/||" "$tmp_hits"
  echo
  echo "Rule: use Icon(name = Icons.<name>) so the glyph follows the theme"
  echo "      tint and renders identically on every platform."
  exit 1
fi

echo "Emoji-as-icon guard passed. violations=0"
