#!/usr/bin/env bash
# Measure the sample's performance budgets on a connected Android device.
#
#   scripts/perf/android_perf.sh [runs]        (default 10 cold starts)
#
# Needs adb and the sample installed (./gradlew :sample:installDebug). Prints JSON.
#
#   cold_start   `am start -W -S` TotalTime: process start to the first frame
#                (Android's TTID). Median of N runs, the first run discarded.
#   theme        the in-app benchmark on the Performance page, read from the screen
#                (MIUI swallows Log.*, so results are drawn, then read by uiautomator).
#   gfxinfo      `dumpsys gfxinfo` after flinging the 1000-row list for five seconds:
#                janky-frame share and frame-time percentiles from the RenderThread,
#                which is the authoritative Android number.
#   frames       the in-app frame-interval recorder over the same flings, for
#                comparison with iOS.
#
# A debuggable build is slower than a release one; record which was measured.
set -euo pipefail

PKG=com.gearui.kit.sample
ACT=com.gearui.sample.MainActivity
RUNS=${1:-10}
DUMP=/sdcard/gearui-perf-ui.xml

adb get-state >/dev/null

# ---- cold start --------------------------------------------------------------
starts=()
for i in $(seq 0 "$RUNS"); do
  adb shell am force-stop "$PKG"
  sleep 1
  t=$(adb shell am start -W -S -n "$PKG/$ACT" | tr -d '\r' | awk -F': ' '/^TotalTime/ {print $2}')
  [ "$i" -gt 0 ] && starts+=("$t")   # run 0 warms the page cache
  sleep 2
done
cold=$(printf '%s\n' "${starts[@]}" | sort -n | awk '{a[NR]=$1} END {m=(NR%2)?a[(NR+1)/2]:(a[NR/2]+a[NR/2+1])/2; printf "{\"runs\":%d,\"median_ms\":%d,\"min_ms\":%d,\"max_ms\":%d}", NR, m, a[1], a[NR]}')

# ---- helpers over the uiautomator tree ----------------------------------------
dump() { adb shell uiautomator dump "$DUMP" >/dev/null 2>&1; adb shell cat "$DUMP"; }
# centre of the first node whose text or content-desc starts with $1
center_of() {
  dump | python3 -c "
import re,sys
xml=sys.stdin.read(); key=sys.argv[1]
for m in re.finditer(r'<node [^>]*>', xml):
    n=m.group(0)
    t=(re.search(r' text=\"([^\"]*)\"', n) or [None,''])[1]
    d=(re.search(r' content-desc=\"([^\"]*)\"', n) or [None,''])[1]
    if t.startswith(key) or d.startswith(key):
        b=re.search(r'bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"', n)
        x0,y0,x1,y1=map(int,b.groups()); print((x0+x1)//2,(y0+y1)//2); break
" "$1"
}
text_of() {
  dump | python3 -c "
import re,sys
xml=sys.stdin.read(); key=sys.argv[1]
for m in re.finditer(r'(?: text| content-desc)=\"([^\"]*)\"', xml):
    if m.group(1).startswith(key): print(m.group(1)); break
" "$1"
}
wait_text() { # $1 prefix, $2 must-not-contain, $3 timeout s
  local end=$((SECONDS+${3:-60})) v
  while [ $SECONDS -lt $end ]; do
    v=$(text_of "$1" || true)
    if [ -n "$v" ] && [[ "$v" != *"$2"* ]]; then echo "$v"; return 0; fi
    sleep 1
  done
  return 1
}
tap_text() { local c; c=$(center_of "$1"); [ -n "$c" ] || { echo "not found: $1" >&2; return 1; }; adb shell input tap $c; }

# ---- open the Performance page -------------------------------------------------
adb shell am force-stop "$PKG"; adb shell am start -W -n "$PKG/$ACT" >/dev/null; sleep 3
tap_text "搜索组件名称" || tap_text "Search"
sleep 1; adb shell input text Performance; sleep 2
tap_text "Performance," || tap_text "性能基准"
sleep 3

# ---- theme --------------------------------------------------------------------
tap_text "运行主题切换测试"
theme=$(wait_text "PERF theme n=" "…" 90)

# ---- scroll ---------------------------------------------------------------------
size=$(adb shell wm size | tr -d '\r' | awk '{print $3}'); W=${size%x*}; H=${size#*x}
for _ in $(seq 1 12); do
  c=$(center_of "录制 5 秒帧间隔" || true)
  if [ -n "$c" ]; then y=${c#* }; [ "$y" -gt $((H/8)) ] && [ "$y" -lt $((H*3/5)) ] && break; fi
  adb shell input swipe $((W/2)) $((H*4/5)) $((W/2)) $((H/4)) 250; sleep 1
done
y=${c#* }; list_y=$((y + H/4))
adb shell dumpsys gfxinfo "$PKG" reset >/dev/null
adb shell input tap $c
end=$((SECONDS+5)); down=1
while [ $SECONDS -lt $end ]; do
  if [ $down = 1 ]; then adb shell input swipe $((W/2)) $((list_y+H/8)) $((W/2)) $((list_y-H/8)) 60
  else adb shell input swipe $((W/2)) $((list_y-H/8)) $((W/2)) $((list_y+H/8)) 60; fi
  down=$((1-down))
done
gfx=$(adb shell dumpsys gfxinfo "$PKG" | tr -d '\r')
frames=$(wait_text "PERF frames n=" "too few" 30 || echo "PERF frames none")

total=$(echo "$gfx" | awk -F': ' '/Total frames rendered/ {print $2; exit}')
janky=$(echo "$gfx" | awk -F': ' '/Janky frames:/ {print $2; exit}')
p50=$(echo "$gfx" | awk '/^50th percentile/ {print $3; exit}')
p90=$(echo "$gfx" | awk '/^90th percentile/ {print $3; exit}')
p95=$(echo "$gfx" | awk '/^95th percentile/ {print $3; exit}')
p99=$(echo "$gfx" | awk '/^99th percentile/ {print $3; exit}')

model=$(adb shell getprop ro.product.model | tr -d '\r')
release=$(adb shell getprop ro.build.version.release | tr -d '\r')
debuggable=$(adb shell dumpsys package "$PKG" | tr -d '\r' | grep -q 'DEBUGGABLE' && echo true || echo false)

cat <<JSON
{
  "platform": "android",
  "device": "$model",
  "android": "$release",
  "debuggable_build": $debuggable,
  "cold_start": $cold,
  "theme": "$theme",
  "gfxinfo": {"total_frames": "$total", "janky": "$janky", "p50": "$p50", "p90": "$p90", "p95": "$p95", "p99": "$p99"},
  "frames": "$frames"
}
JSON
