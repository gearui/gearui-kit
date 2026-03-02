#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

ohpm install --all --strict_ssl true
hvigorw clean --no-daemon
hvigorw assembleHap --mode project -p product=default -p buildMode=release --no-daemon
