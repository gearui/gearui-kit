#!/usr/bin/env bash
set -euo pipefail

# Rule: the agent skill's API reference (skills/gearui-kit/references/api, components.md,
# icons.txt) is generated from the kit source and must match it.
#
# Agents copy signatures from that reference into app code. A stale signature there
# becomes code that does not compile in every app an agent writes, so a public API
# change and its reference ship in the same commit.
#
# To update: scripts/gen_skill_reference.py, then commit skills/gearui-kit/references.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
command -v python3 >/dev/null 2>&1 || { echo "python3 is required by $0"; exit 1; }
exec python3 "$ROOT_DIR/scripts/gen_skill_reference.py" --check
