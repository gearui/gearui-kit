#!/usr/bin/env bash
# Runs the iOS simulator Kotlin tests against the real Kuikly host.
#
# The kit's native tests cannot link on their own: Kuikly's runtime expects the
# CocoaPods host (OpenKuiklyIOSRender) to provide
# _com_tencent_kuikly_IsCurrentOnContextThread. This script builds that host
# framework from the sample Pods project and passes its products directory to
# Gradle (see the test-host block in gearui-kit/build.gradle.kts). No symbol is
# stubbed and no linker error is ignored.
#
#   GEARUI_IOS_TEST_DERIVED_DATA  where xcodebuild writes (default /tmp/gearui-ios-test-host)
#   GEARUI_IOS_TEST_ARCH          simulator arch, arm64 (default) or x86_64
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DERIVED="${GEARUI_IOS_TEST_DERIVED_DATA:-/tmp/gearui-ios-test-host}"
ARCH="${GEARUI_IOS_TEST_ARCH:-arm64}"
case "$ARCH" in
  arm64) GRADLE_TASK=":gearui-kit:iosSimulatorArm64Test" ;;
  x86_64) GRADLE_TASK=":gearui-kit:iosX64Test" ;;
  *) echo "unsupported GEARUI_IOS_TEST_ARCH=$ARCH" >&2; exit 2 ;;
esac

cd "$ROOT/sample/iosApp"
if [ ! -d Pods/Pods.xcodeproj ]; then
  # The sample podspec is Gradle-generated and not tracked.
  [ -f ../gearui_sample.podspec ] || "$ROOT/gradlew" :sample:podspec
  pod install
fi

xcodebuild \
  -project Pods/Pods.xcodeproj \
  -target OpenKuiklyIOSRender \
  -configuration Debug \
  -sdk iphonesimulator \
  -derivedDataPath "$DERIVED" \
  ARCHS="$ARCH" ONLY_ACTIVE_ARCH=NO \
  build -quiet

HOST_DIR="$DERIVED/Build/Products/Debug-iphonesimulator"
test -d "$HOST_DIR/OpenKuiklyIOSRender/OpenKuiklyIOSRender.framework"

cd "$ROOT"
exec ./gradlew "$GRADLE_TASK" -PgearuiIosTestHostDir="$HOST_DIR" "$@"
