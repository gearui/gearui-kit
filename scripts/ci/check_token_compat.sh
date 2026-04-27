#!/usr/bin/env bash
# SPEC 4.1 token-compat-check: detect deletion / rename / semantic drift
# of GearColors / GearTypography / GearShapes fields and Themes.Light/Dark presets.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
BASELINE="$ROOT/gearui-kit/api/tokens.api"
ACTUAL="$(mktemp)"
trap 'rm -f "$ACTUAL"' EXIT

if [[ ! -f "$BASELINE" ]]; then
    echo "Missing token baseline: $BASELINE" >&2
    echo "Generate it with:" >&2
    echo "  ./scripts/ci/dump_token_snapshot.sh > gearui-kit/api/tokens.api" >&2
    exit 1
fi

python3 "$ROOT/scripts/ci/dump_token_snapshot.py" > "$ACTUAL"

if ! diff -u "$BASELINE" "$ACTUAL"; then
    cat >&2 <<'EOF'

Token schema drift detected.
SPEC 4.1: deletion / rename of semantic tokens is REJECT;
          additions and value changes must be explicit and audited.

If the change is intentional, refresh the baseline:
  ./scripts/ci/dump_token_snapshot.sh > gearui-kit/api/tokens.api

Then commit the updated 'gearui-kit/api/tokens.api' alongside the source change.
EOF
    exit 1
fi
