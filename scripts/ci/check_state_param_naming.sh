#!/usr/bin/env bash
set -euo pipefail

# Rule: state parameter naming, per GEARUI_SPEC_2026 §6.1.
#
#   Field family   enabled: Boolean = true  +  error: String? = null
#   SearchBar      enabled only — a search entry point has no value to validate
#   Action family  disabled: Boolean = false is fine (Button, Tag, SwipeCell)
#
# Only one of those is checkable without knowing a component's role: whether a
# single component exposes BOTH enabled and disabled. Input used to take
# `disabled` while every sibling took `enabled`, so a form greying out its
# fields had to flip polarity per component.
#
# A second rule — reject `errorText` / `status` as validation parameter names —
# was written and then removed. Every hit on its first run was a false
# positive: Result, Progress and Steps take a `status` display-variant enum,
# and Image's `errorText` is the caption shown when a load fails. None is field
# validation, and a parameter name alone cannot tell the difference. A guard
# whose first run is entirely false positives teaches people to ignore red,
# which costs more than the drift it would catch. Field naming is held by
# GEARUI_SPEC_2026 §6.1, the compiler and apiCheck instead.
#
# Only function parameters are checked. A data model's own flag is a different
# thing: `SelectOption(disabled = true)` marks one option unselectable while
# the Select itself stays enabled, so `val disabled: Boolean` on a data class
# is legitimate and is not matched here.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"

status=0

# 1. Mixed polarity within one component file.
mixed=""
while IFS= read -r f; do
  if grep -qE '^[[:space:]]+enabled: Boolean' "$f" && grep -qE '^[[:space:]]+disabled: Boolean' "$f"; then
    mixed="$mixed${f#"$ROOT_DIR/"}"$'\n'
  fi
done < <(find "$SRC_DIR/components" "$SRC_DIR/primitives" -name '*.kt')

if [[ -n "$mixed" ]]; then
  echo "State param violation: one component exposes both enabled and disabled."
  printf '%s' "$mixed"
  echo "Pick one polarity per component. Field-like controls use enabled;"
  echo "action-like controls may use disabled."
  status=1
fi

[[ $status -eq 0 ]] || exit 1
echo "State param naming guard passed. violations=0"
