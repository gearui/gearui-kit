#!/usr/bin/env bash
# Capture first viewports only. Images are evidence to review, not automatic acceptance.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
PLATFORM="${1:?android or ios}"
DEVICE="${2:?device serial or simulator UDID}"
OUT="${3:-$ROOT/build/beta7-acceptance/$PLATFORM}"
mkdir -p "$OUT"
sed -nE 's/.*ComponentInfo\("([^"]+)".*/\1/p' \
  "$ROOT/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt" > "$OUT/routes.txt"
while IFS= read -r route; do
  for theme in light dark; do
    case "$PLATFORM" in
      android)
        adb -s "$DEVICE" shell am start -S -W \
          -n com.gearui.kit.sample/com.gearui.sample.MainActivity \
          --es route "$route" --es theme "$theme" > "$OUT/$route-$theme-launch.txt" </dev/null
        sleep 2
        adb -s "$DEVICE" exec-out screencap -p > "$OUT/$route-$theme.png" </dev/null
        ;;
      ios)
        xcrun simctl terminate "$DEVICE" com.gearui.kit.sample 2>/dev/null || true
        xcrun simctl launch "$DEVICE" com.gearui.kit.sample -route "$route" -theme "$theme" \
          > "$OUT/$route-$theme-launch.txt" </dev/null
        # A cold start on the simulator can take ~2 s to draw; a blank frame is
        # retaken rather than accepted as evidence.
        for attempt in 1 2 3; do
          sleep 3
          xcrun simctl io "$DEVICE" screenshot "$OUT/$route-$theme.png" 2>/dev/null
          python3 - "$OUT/$route-$theme.png" <<'PY' && break
import sys
from PIL import Image
im = Image.open(sys.argv[1]).convert("L")
lo, hi = im.getextrema()
sys.exit(1 if hi - lo < 16 else 0)  # a uniform frame has not drawn yet
PY
        done
        ;;
      *) echo "Unsupported platform: $PLATFORM" >&2; exit 2 ;;
    esac
    test -s "$OUT/$route-$theme.png"
    echo "$PLATFORM $route $theme captured"
  done
done < "$OUT/routes.txt"
