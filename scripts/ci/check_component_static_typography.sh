#!/usr/bin/env bash
set -euo pipefail

# Rule: text styles in the library come from `Theme.typography.*`, not from the
# static `Typography.*` object.
#
# Both exist and used to carry *different* TextStyle types, so the themed scale
# was structurally unreadable and every component reached past the theme to the
# static object. Typography was therefore the one token axis a brand could not
# replace: 208 static references against 0 themed reads. The types are unified
# now, and this guard keeps them from drifting apart again.
#
# `foundation/typography/Typography.kt` defines the scale and `theme/Typography.kt`
# maps it into the themed type; both are allowed to name it.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$ROOT_DIR/gearui-kit/src/commonMain/kotlin/com/gearui"

hits="$(grep -rn --include='*.kt' -E '\bTypography\.[A-Z]' "$SRC_DIR" \
  | grep -v '/foundation/typography/Typography.kt:' \
  | grep -v '/theme/Typography.kt:' \
  | grep -vE ':[0-9]+: *(\*|//|/\*)' || true)"

if [[ -n "$hits" ]]; then
  echo "❌ static Typography.* used in the library; use Theme.typography.* instead:"
  echo "$hits"
  exit 1
fi

echo "✅ no static Typography.* outside the scale definition"
