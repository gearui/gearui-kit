#!/usr/bin/env bash
# A sheet that can be dragged away must show a grabber, and a sheet that shows a
# grabber must be draggable.
#
# They are one feature. A drag with no grabber is a gesture only the authors know
# about; a grabber with no drag is a control that does nothing when pulled. Both
# are easy to end up with, because they live in different files.
#
# See DESIGN_SYSTEM_SPEC §11.3.1.
set -euo pipefail
cd "$(dirname "$0")/.."/..

SRC=gearui-kit/src/commonMain/kotlin/com/gearui/components

# Sheets only. A drawer also drags, and deliberately has no grabber: it slid in
# from an edge and the edge is the affordance, which is why neither platform puts
# a grabber on one. Widening the rule to "anything that drags" flagged Drawer the
# first time it gained the gesture — the rule was wrong, not the component.
fail=0
for f in $(grep -rl "swipeDismiss\|SheetGrabber" "$SRC" 2>/dev/null || true); do
  rel="${f#$SRC/}"
  case "$rel" in
    *sheet*) ;;
    *)
      # Not a sheet: only the reverse half applies — a grabber promises a drag.
      if grep -q "SheetGrabber(" "$f" && ! grep -q "swipeDismiss(" "$f"; then
        echo "✗ $rel shows a grabber but does not drag — pulling it does nothing"
        fail=1
      fi
      continue
      ;;
  esac

  has_drag=0; has_grabber=0
  grep -q "swipeDismiss(" "$f" && has_drag=1
  grep -q "SheetGrabber(" "$f" && has_grabber=1
  if [ "$has_drag" = 1 ] && [ "$has_grabber" = 0 ]; then
    echo "✗ $rel drags but shows no grabber — the gesture is invisible"
    fail=1
  fi
  if [ "$has_grabber" = 1 ] && [ "$has_drag" = 0 ]; then
    echo "✗ $rel shows a grabber but does not drag — pulling it does nothing"
    fail=1
  fi
done

if [ "$fail" = 1 ]; then
  echo
  echo "  A grabber and drag-to-dismiss are one feature (§11.3.1)."
  exit 1
fi

echo "✓ sheets: grabber and drag-to-dismiss ship together"
