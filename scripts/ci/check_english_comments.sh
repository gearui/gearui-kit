#!/usr/bin/env bash
set -euo pipefail

# Rule: code comments are English only.
#
# Documentation is bilingual — English first, `*.zh-Hans.md` alongside — but
# comments live with the code and have one audience: whoever is reading that
# line. A mix means half the explanations are unreadable to half the readers.
#
# This started as a debt freeze: 3633 Chinese comment lines across 191 files,
# with a per-file baseline that could only shrink, because translating them in
# bulk would have wrecked the ones that matter most — why Input's border must
# not depend on focus, why the overlay host cannot carry a UMD wrapper, why a
# transient 0 from an inactive iOS Scene is filtered but any non-zero safe-area
# change is accepted immediately. Those were translated by hand, in batches,
# each one compiled.
#
# The debt is now zero, so this is a hard gate and the baseline file is gone.
#
# String literals are not checked here — user-facing Chinese copy belongs in the
# i18n packs, which check_i18n_default_text.sh already enforces. Chinese inside
# a comment that is quoting such a literal (a usage example, say) still fails:
# write the example in English.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

command -v perl >/dev/null 2>&1 || { echo "perl is required by $0"; exit 1; }

tmp_hits="$(mktemp)"
trap 'rm -f "$tmp_hits"' EXIT

cd "$ROOT_DIR"
# shellcheck disable=SC2046
perl -CSD -ne '
    print "$ARGV:$.: $_" if m{^\s*(//|\*|/\*)} && m{[\x{4e00}-\x{9fff}]};
  } continue {
    close ARGV if eof;
  ' $(git ls-files '*.kt') 2>/dev/null > "$tmp_hits" || true

if [[ -s "$tmp_hits" ]]; then
  echo "Chinese comments found:"
  cat "$tmp_hits"
  echo
  echo "Rule: code comments are English. Documentation is bilingual; comments"
  echo "      are not."
  exit 1
fi

files_scanned="$(git ls-files '*.kt' | wc -l | tr -d ' ')"
echo "English comment guard passed. files=$files_scanned chinese_comment_lines=0"
