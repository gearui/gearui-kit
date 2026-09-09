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
# emoji in any string literal. Inside the sample, two narrower rules, because a
# demo may legitimately put an emoji in *data* ("苹果 🍎" is an option label,
# not an affordance):
#
#   a. icon position — `icon = "📱"` is an affordance by definition;
#   b. a string literal that is *nothing but* one glyph — `Text(text = "✕")` is
#      a close button drawn as text, and `"✓ 引导已完成"` is a sentence.
#
# Rule (b) exists because (a) alone missed a whole class: NavBar passed
# `icon = "⋯"` eight times and four Popup demos drew their close button as
# `Text(text = "✕")`. NavBar even had an `if (icon in Icons.all) … else
# Text(icon)` fallback, so the same parameter meant two different things and a
# typo rendered the literal string into the nav bar instead of failing.
#
# The character set covers more than emoji for the same reason: `⋯` (U+22EF) and
# `⌂` (U+2302) are not pictographs and slipped through a pictograph-only range.

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
      if (m{"[^"]*[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}\x{2190}-\x{21FF}\x{2300}-\x{23FF}\x{22EE}-\x{22F1}\x{25A0}-\x{25FF}][^"]*"}) {
        print "$ARGV:$.\n";
      }
    } continue {
      close ARGV if eof;
    ' > "$tmp_hits"

# Sample: icon position, plus any literal that is nothing but a glyph.
find "$SAMPLE_DIR" -name '*.kt' -print0 \
  | xargs -0 perl -CSD -ne '
      next if m{^\s*(\*|//|/\*)};
      s{//.*$}{};
      if (m{\bicon\w*\s*=\s*"[^"]*[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}\x{2190}-\x{21FF}\x{2300}-\x{23FF}\x{22EE}-\x{22F1}\x{25A0}-\x{25FF}]}
          || m{"[\x{1F300}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}\x{2190}-\x{21FF}\x{2300}-\x{23FF}\x{22EE}-\x{22F1}\x{25A0}-\x{25FF}]\x{FE0F}?"}) {
        print "$ARGV:$.\n";
      }
    } continue {
      close ARGV if eof;
    ' >> "$tmp_hits"

if [[ -s "$tmp_hits" ]]; then
  echo "Emoji used as a UI affordance:"
  sed "s|$ROOT_DIR/||" "$tmp_hits"
  echo
  echo "Rule: use Icon(name = Icons.<name>) so the glyph follows the theme"
  echo "      tint and renders identically on every platform."
  exit 1
fi

echo "Emoji-as-icon guard passed. violations=0"
