#!/usr/bin/env bash
set -euo pipefail

# Rule: the component index in README.md / README.zh-Hans.md is generated, not
# hand-written, and must match the sample registry.
#
# The registry (sample/.../config/ComponentConfig.kt) is what the sample's home
# page renders from, so it is the one list that is exercised on every build.
# A hand-maintained copy in the README had already decayed to "50+" by the
# first release. Generating it removes the copy; this check makes sure nobody
# edits the generated block by hand or adds a component without regenerating.
#
# To update: scripts/gen_component_index.py, then commit both READMEs.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
command -v python3 >/dev/null 2>&1 || { echo "python3 is required by $0"; exit 1; }
exec python3 "$ROOT_DIR/scripts/gen_component_index.py" --check
