#!/usr/bin/env bash
set -euo pipefail

# Rule: code comments are English only.
#
# Documentation is bilingual — English first, `*.zh-Hans.md` alongside — but
# comments live with the code and have one audience: whoever is reading that
# line. A mix means half the explanations are unreadable to half the readers.
#
# This is a debt freeze, not a hard gate. There were 3633 Chinese comment lines
# across 191 files when the rule was written, and machine-translating them in
# bulk would destroy the ones that matter most: the comments recording why
# Input's border must not depend on focus, why the overlay host cannot carry a
# UMD wrapper, why the scrim must be pure black. Those need translating by
# someone who knows what they mean.
#
# So each file's count is frozen and may only shrink. Touch a file, translate
# its comments; the ceiling drops and cannot come back.
#
# String literals are not checked here — user-facing Chinese copy belongs in the
# i18n packs, which check_i18n_default_text.sh already enforces.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BASELINE_FILE="$ROOT_DIR/scripts/ci/chinese_comment_baseline.txt"

command -v perl >/dev/null 2>&1 || { echo "perl is required by $0"; exit 1; }

if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "Missing baseline file: $BASELINE_FILE"
  exit 1
fi

tmp_all="$(mktemp)"
tmp_new="$(mktemp)"
trap 'rm -f "$tmp_all" "$tmp_new"' EXIT

cd "$ROOT_DIR"
# shellcheck disable=SC2046
perl -CSD -ne '
    print "$ARGV\n" if m{^\s*(//|\*|/\*)} && m{[\x{4e00}-\x{9fff}]};
  } continue {
    close ARGV if eof;
  ' $(git ls-files '*.kt') 2>/dev/null \
  | sort | uniq -c | awk '{print $2 "\t" $1}' | sort > "$tmp_all" || true

awk -F'\t' '
  NR==FNR { base[$1] = $2; next }
  { if (!($1 in base) || $2 > base[$1]) print $1 "\t" $2 "\t" (($1 in base) ? base[$1] : 0) }
' <(grep -v '^#' "$BASELINE_FILE" | grep -v '^$' | sort) "$tmp_all" > "$tmp_new" || true

if [[ -s "$tmp_new" ]]; then
  echo "Chinese comments grew, or appeared in a file that had none:"
  printf 'file\tnow\tallowed\n'
  cat "$tmp_new"
  echo
  echo "Rule: code comments are English. Documentation is bilingual; comments"
  echo "      are not. Translate rather than raising the ceiling."
  exit 1
fi

total_now="$(awk -F'\t' '{s+=$2} END {print s+0}' "$tmp_all")"
total_base="$(grep -v '^#' "$BASELINE_FILE" | grep -v '^$' | awk -F'\t' '{s+=$2} END {print s+0}')"
files_now="$(wc -l < "$tmp_all" | tr -d ' ')"
echo "English comment guard passed. baseline=$total_base current=$total_now files=$files_now grown=0"
