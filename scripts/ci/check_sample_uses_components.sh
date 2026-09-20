#!/usr/bin/env bash
# The sample builds its screens out of the kit, never out of raw modifiers.
#
# Why this needs a guard: the sample is the reference usage, and it kept hand-building
# what the kit already provides. The home list — the first thing anyone touches — was a
# Row with a bare `clickable` and a "›" character for a chevron, so it answered no press
# at all while every component page did. The same pattern hid in the settings cards, the
# sidebar, the form's layout switch, the input icons and a radio row.
#
# A tap belongs to a component: Cell/ListItem for a row, Button for a button,
# SegmentedControl for a choice, PressableFeedback for anything else that has to be
# tappable. Those carry the press response; a raw modifier carries nothing, because
# Kuikly's LocalIndication pipeline does not render (see DESIGN_SYSTEM_SPEC).
set -euo pipefail
cd "$(dirname "$0")/../.."

SAMPLE=sample/src/commonMain/kotlin/com/gearui/sample

hits=$(grep -rn '\.clickable(\|\.toggleable(\|\.selectable(' --include='*.kt' "$SAMPLE" || true)

if [ -n "$hits" ]; then
  echo "✗ the sample reaches for a raw tap modifier instead of a component:"
  echo "$hits" | sed 's/^/  /'
  echo
  echo "  Use Cell / ListItem for a row, Button for a button, SegmentedControl for a"
  echo "  choice, or PressableFeedback for any other tappable area."
  exit 1
fi

echo "✓ sample: every tap goes through a component"
