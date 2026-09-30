#!/usr/bin/env bash
# Apple accessibility audit (XCUITest performAccessibilityAudit) over every sample page.
# Usage: scripts/acceptance/ios_accessibility_audit.sh <simulator-udid> [themes] [routes]
# The GearUI sample must already be installed on the simulator. Each page is audited
# only after its NavBar title renders. The full xcodebuild log, AUDIT lines and
# run.json go to a fresh build/acceptance/ios-a11y-<time>-<sha> directory.
# Exit status: 0 no findings, 1 findings, 2 the run itself failed (build, launch,
# a page that never rendered, or fewer pages audited than expected).
set -uo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
UDID="${1:?simulator UDID}"
THEMES="${2:-light,dark}"
ONLY="${3:-}"
PAIRS=$(sed -nE 's/.*ComponentInfo\("([^"]+)", *"[^"]*", *"([^"]+)".*/\1=\2/p' \
  "$ROOT/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt")
if [ -n "$ONLY" ]; then
  PAIRS=$(echo "$PAIRS" | grep -E "^($(echo "$ONLY" | tr , '|'))=" || true)
  [ -n "$PAIRS" ] || { echo "no registered route matches: $ONLY" >&2; exit 2; }
fi
ROUTES=$(echo "$PAIRS" | paste -sd, -)
EXPECTED=$(( $(echo "$PAIRS" | wc -l) * $(echo "$THEMES" | tr , '\n' | wc -l) ))
SHA=$(git -C "$ROOT" rev-parse --short=10 HEAD)
OUT="$ROOT/build/acceptance/ios-a11y-$(date +%Y%m%d-%H%M%S)-$SHA"
mkdir -p "$OUT"
APP=$(xcrun simctl get_app_container "$UDID" com.gearui.kit.sample 2>/dev/null) \
  || { echo "sample not installed on $UDID" >&2; exit 2; }
case "$(realpath "$APP")" in */Release-*) BUILD=release ;; *) BUILD=unknown ;; esac
cat > "$OUT/run.json" <<JSON
{"sha": "$(git -C "$ROOT" rev-parse HEAD)", "device": "$UDID", "build": "$BUILD",
 "themes": "$THEMES", "pages_expected": $EXPECTED, "started": "$(date -u +%FT%TZ)"}
JSON

cd "$ROOT/sample/iosApp/AccessibilityAudit"
xcodegen generate --quiet || exit 2
TEST_RUNNER_GEARUI_ROUTES="$ROUTES" TEST_RUNNER_GEARUI_THEMES="$THEMES" \
  xcodebuild test -project GearUIAccessibilityAudit.xcodeproj -scheme AuditUITests \
  -destination "id=$UDID" -derivedDataPath "$ROOT/.build/DerivedData-audit" > "$OUT/xcodebuild.log" 2>&1
STATUS=$?
grep -E '^(AUDIT|AUDITPAGE)\|' "$OUT/xcodebuild.log" > "$OUT/audit.txt"
DONE=$(grep -cE '^AUDITPAGE\|[^|]+\|[^|]+\|[0-9]+$' "$OUT/audit.txt")
NOT_READY=$(grep -c '|NOT_READY$' "$OUT/audit.txt")
FINDINGS=$(grep -c '^AUDIT|' "$OUT/audit.txt")
cat "$OUT/audit.txt"
echo "DONE|$DONE/$EXPECTED|not_ready=$NOT_READY|findings=$FINDINGS|xcodebuild=$STATUS|$OUT"
if [ "$DONE" -ne "$EXPECTED" ] || [ "$NOT_READY" -ne 0 ]; then
  grep -E 'error:|\*\* TEST|BUILD FAILED' "$OUT/xcodebuild.log" | tail -5 >&2
  exit 2
fi
[ "$FINDINGS" -eq 0 ] && [ "$STATUS" -eq 0 ] && exit 0
# xcodebuild fails the test when findings exist; any other failure is a broken run.
[ "$FINDINGS" -gt 0 ] && exit 1
exit 2
