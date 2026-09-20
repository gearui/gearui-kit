#!/usr/bin/env bash
# Everything a finger can press has to answer it.
#
# Why this needs a guard: Cell shipped as a bare `.clickable {}`. The row ran its
# action with nothing on screen acknowledging the touch, which reads as a dead row
# whenever the handler is not instant — and nothing in CI noticed, because the row
# worked. The same was true of tags, stepper buttons, accordion headers, pagination
# pages, anchor links, transfer rows, cascader options, tabs and the back-to-top
# button: ten components a user touches all day.
#
# A component here is expected to pair a tap target with one of the shared feedback
# helpers: rowPressFeedback, pressScale, pressedSurfaceColor, PressableFeedback, or
# Button/CloseButton, which carry their own.
set -euo pipefail
cd "$(dirname "$0")/../.."

SRC=gearui-kit/src/commonMain/kotlin/com/gearui/components
# The sample is in scope too, and used not to be. That gap let the home page — the first
# list anyone touches — ship a hand-built row with a bare clickable, no press response
# and a "›" character for a chevron, while every component page had feedback.
SAMPLE=sample/src/commonMain/kotlin/com/gearui/sample
EXCEPTIONS=scripts/ci/press_feedback_exceptions.txt

missing=""
while IFS= read -r f; do
  rel=${f#"$SRC/"}
  rel=${rel#"$SAMPLE/"}
  grep -qxF "$rel" "$EXCEPTIONS" && continue
  grep -qE '\.clickable\(|\.toggleable\(|\.selectable\(' "$f" || continue
  # collectIsPressedAsState counts: a component that reads its own pressed state is
  # driving its own visual (Switch scales, Tree and the select panel fill).
  grep -qE 'rowPressFeedback|pressScale|pressedSurfaceColor|PressableFeedback|pressableFeedback|iconPressFeedback|rememberInputFeedback|collectIsPressedAsState' "$f" && continue
  missing="$missing  $rel\n"
done < <(find "$SRC" "$SAMPLE" -name '*.kt' | sort)

if [ -n "$missing" ]; then
  echo "✗ tap targets with no press feedback:"
  printf "%b" "$missing"
  echo "  Use one of rowPressFeedback / pressScale / pressedSurfaceColor /"
  echo "  PressableFeedback, or list the file in $EXCEPTIONS with the reason."
  exit 1
fi

echo "✓ press feedback: every tap target answers the finger"
