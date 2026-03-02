#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/entry/oh_modules/@kuikly-open/render/src/main/cpp"
DEST_DIR="$ROOT_DIR/entry/src/main/cpp/include"

if [[ ! -d "$SRC_DIR" ]]; then
  echo "render cpp headers not found: $SRC_DIR"
  echo "run 'ohpm install --all' first"
  exit 1
fi

mkdir -p "$DEST_DIR"
rsync -a --include '*/' --include '*.h' --exclude '*' "$SRC_DIR/" "$DEST_DIR/"
echo "headers copied to $DEST_DIR"
