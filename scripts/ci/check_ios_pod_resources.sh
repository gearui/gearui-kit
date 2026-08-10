#!/usr/bin/env bash
set -euo pipefail

# Rule: the iOS sample project must keep its "[CP] Copy Pods Resources" build
# phase.
#
# gearui_sample.podspec declares
#     spec.resources = ['build/compose/cocoapods/compose-resources']
# and the Gradle task syncSharedAssetsToPodResources fills that directory —
# but only as a finalizer of :sample:syncFramework. Run `pod install` before a
# real build has happened and the directory is empty, so CocoaPods concludes
# no pod ships resources and silently drops the copy phase from
# project.pbxproj.
#
# The failure is quiet and easy to misread. The app still builds, launches and
# renders; only the 101 icon PNGs never make it into the bundle. Icons load
# through coil3 from `assets://icons/<name>.png`, so a missing asset produces a
# correctly sized but blank box rather than an error — Select, Cascader and
# TreeSelect just lose their chevrons, SearchBar loses its magnifier, and the
# DatePicker keeps its trailing mark only because that one is a literal emoji.
#
# Correct order when regenerating pods:
#     ./gradlew :sample:syncFramework   # or any build that runs it
#     (cd sample/iosApp && pod install)

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PBXPROJ="$ROOT_DIR/sample/iosApp/GearUISample.xcodeproj/project.pbxproj"
PODSPEC="$ROOT_DIR/sample/gearui_sample.podspec"

if [[ ! -f "$PBXPROJ" ]]; then
  echo "Missing Xcode project: $PBXPROJ"
  exit 1
fi

# Only meaningful while the podspec actually declares resources.
if ! grep -q 'spec.resources' "$PODSPEC"; then
  echo "iOS pod resources guard skipped: podspec declares no resources."
  exit 0
fi

if ! grep -q 'Copy Pods Resources' "$PBXPROJ"; then
  echo "The iOS sample lost its [CP] Copy Pods Resources build phase."
  echo
  echo "This drops every asset from the app bundle, which shows up as blank"
  echo "icons rather than as a build error."
  echo
  echo "Fix: run a build that triggers :sample:syncFramework so"
  echo "     build/compose/cocoapods/compose-resources is populated, then"
  echo "     re-run 'pod install' in sample/iosApp."
  exit 1
fi

echo "iOS pod resources guard passed. copy_phase=present"
