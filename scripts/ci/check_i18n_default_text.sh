#!/usr/bin/env bash
set -euo pipefail

# Rule: user-facing text must come from the i18n string packs. No CJK string
# literal may appear in library sources outside `com/gearui/i18n/`.
#
# Baseline is 0 — this guard is a hard gate, not a debt freeze. A component that
# needs new copy adds a key to the matching domain in DomainStrings.kt and fills
# all three packs (en-US / zh-Hans / zh-Hant).
#
# Implemented with perl (not rg/grep -P) so it behaves identically on macOS and
# on CI; a missing rg used to make this class of check silently red locally.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"

command -v perl >/dev/null 2>&1 || { echo "perl is required by $0"; exit 1; }

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

find "$SRC_DIR" -name '*.kt' -not -path '*/i18n/*' -print0 \
  | xargs -0 perl -CSD -ne '
      # Drop comments before scanning: full-line block/KDoc, and trailing //.
      next if m{^\s*(\*|//|/\*)};
      s{//.*$}{};
      # Any CJK ideograph inside a double-quoted literal is a violation.
      if (m{"[^"]*[\x{4e00}-\x{9fff}][^"]*"}) {
        print "$ARGV:$.\n";
      }
    } continue {
      # $. is cumulative across files; reset it so reported line numbers are
      # actually clickable.
      close ARGV if eof;
    ' > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Hardcoded CJK user-facing text detected outside the i18n packs:"
  sed "s|$ROOT_DIR/||" "$tmp_hits"
  echo
  echo "Rule: add a key to com/gearui/i18n/DomainStrings.kt and fill"
  echo "      Strings.en_US.kt / Strings.zh_Hans.kt / Strings.zh_Hant.kt."
  echo "      Read it via I18n.strings.<domain>.<key> in @Composable code, or"
  echo "      StringPacks.English.<domain>.<key> as a non-composable fallback."
  exit 1
fi

echo "i18n default-text guard passed. violations=0"
