#!/usr/bin/env bash
# Apple accessibility audit (XCUITest performAccessibilityAudit) over every sample page.
# Usage: scripts/acceptance/ios_accessibility_audit.sh <simulator-udid> [themes]
# The GearUI sample must already be installed on the simulator. Prints AUDIT lines.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
UDID="${1:?simulator UDID}"
THEMES="${2:-light,dark}"
ROUTES=$(sed -nE 's/.*ComponentInfo\("([^"]+)".*/\1/p' \
  "$ROOT/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt" | paste -sd, -)
cd "$ROOT/sample/iosApp/AccessibilityAudit"
xcodegen generate --quiet
TEST_RUNNER_GEARUI_ROUTES="$ROUTES" TEST_RUNNER_GEARUI_THEMES="$THEMES" \
  xcodebuild test -project GearUIAccessibilityAudit.xcodeproj -scheme AuditUITests \
  -destination "id=$UDID" -derivedDataPath "$ROOT/.build/DerivedData-audit" 2>&1 \
  | grep -E '^(AUDIT|AUDITPAGE)\||Test Suite|error:|\*\* TEST' || true
