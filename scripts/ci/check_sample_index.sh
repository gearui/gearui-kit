#!/usr/bin/env bash
set -euo pipefail

# Wrapper so this runs like every other guard. The logic used to live inline in
# ci.yml, which meant it could only ever be exercised by pushing — and since
# Actions had never run, it had never been exercised at all.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
exec python3 "$ROOT_DIR/scripts/ci/check_sample_index.py"
