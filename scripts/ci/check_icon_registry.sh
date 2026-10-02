#!/usr/bin/env bash
set -euo pipefail

# Rule: GearUI's icons are generated code, and the generated code matches Phosphor.
#
# Every icon is an extension property on `Icons` in
# gearui-kit/src/commonMain/kotlin/com/gearui/components/icon/generated/, written by
# `scripts/gen_vector_icons.py` from Phosphor's flat SVGs as path data. A hand edit to a
# generated file, or a regenerated set not committed, is drift. The PNG set this guard
# used to check (constants, `Icons.all`, assets) is gone: icons are drawn vectors now.
#
# Needs the Phosphor distribution next to the repository (../phosphor-icons) or in
# PHOSPHOR_DIR; CI without it checks only that the generated files exist and are
# not empty.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
GEN="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui/components/icon/generated"
PHOSPHOR="${PHOSPHOR_DIR:-$ROOT_DIR/../phosphor-icons}"

count=$(grep -h -c "^val Icons\." "$GEN"/*.generated.kt | awk '{s+=$1} END {print s+0}')
if [[ "$count" -eq 0 ]]; then
  echo "Icon registry: no generated icons under $GEN"
  exit 1
fi

if [[ -d "$PHOSPHOR/SVGs Flat" ]]; then
  python3 "$ROOT_DIR/scripts/gen_vector_icons.py" --phosphor "$PHOSPHOR" --out-dir "$GEN" \
    --list-out "$ROOT_DIR/sample/src/commonMain/kotlin/com/gearui/sample/examples/icon/AllIcons.generated.kt" --check
else
  echo "Phosphor not found at $PHOSPHOR; checked presence only."
fi
echo "Icon registry guard passed. icons=$count"
