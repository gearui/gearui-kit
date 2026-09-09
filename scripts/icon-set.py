"""The GearUI icon set: Phosphor names, mostly regular weight.

Read by `gen_icons.py`, which turns each entry into
`gearui-kit/src/commonMain/assets/icons/<name>.png`. The Kotlin constant name
is the Phosphor name with dashes turned into underscores, plus a `_fill`
suffix for the fill weight — Phosphor's own file naming (`star-fill.png`).

This replaced a Material-Symbols-to-Phosphor mapping. GearUI drew Phosphor
artwork under Material names, so `Icons.home` fetched `house`, `Icons.close`
fetched `x`, and the set was described in a vocabulary it did not use.

Weight is `regular` throughout except where a component distinguishes two
states with it, which is only:

  star / star-fill                    Rate's empty and filled stars
  bookmark-simple / bookmark-simple-fill   privchat-ui's Pin and Unpin
  play-fill                           a solid triangle is the play affordance

`eye` / `eye-slash` are here because a password field's show/hide toggle is a
standard affordance with no icon behind it — the sample was demonstrating it
with 👁 and 👁‍🗨.

Adding an icon means adding a line here and rerunning gen_icons.py. Adding one
*for an application* means dropping a PNG into that app's own assets/icons/,
which needs nothing from this file — see DESIGN_SYSTEM_SPEC 11.4.
"""

ICONS = [
    ('address-book', 'regular'),
    ('arrow-bend-up-left', 'regular'),
    ('arrow-bend-up-right', 'regular'),
    ('arrow-clockwise', 'regular'),
    ('arrow-down-left', 'regular'),
    ('arrow-left', 'regular'),
    ('arrow-right', 'regular'),
    ('arrow-square-out', 'regular'),
    ('arrow-up-right', 'regular'),
    ('arrows-clockwise', 'regular'),
    ('arrows-left-right', 'regular'),
    ('at', 'regular'),
    ('bell', 'regular'),
    ('bell-ringing', 'regular'),
    ('bell-slash', 'regular'),
    ('bookmark-simple', 'regular'),
    ('bookmark-simple', 'fill'),
    ('calendar-blank', 'regular'),
    ('camera', 'regular'),
    ('camera-slash', 'regular'),
    ('caret-down', 'regular'),
    ('caret-left', 'regular'),
    ('caret-right', 'regular'),
    ('caret-up', 'regular'),
    ('chat-circle', 'regular'),
    ('chats', 'regular'),
    ('check', 'regular'),
    ('check-square', 'regular'),
    ('circle', 'regular'),
    ('clock', 'regular'),
    ('clock-clockwise', 'regular'),
    ('clock-counter-clockwise', 'regular'),
    ('copy', 'regular'),
    ('dots-three', 'regular'),
    ('dots-three-circle', 'regular'),
    ('dots-three-vertical', 'regular'),
    ('download-simple', 'regular'),
    ('envelope-simple', 'regular'),
    ('eye', 'regular'),
    ('eye-slash', 'regular'),
    ('faders', 'regular'),
    ('flag', 'regular'),
    ('gear', 'regular'),
    ('gift', 'regular'),
    ('heart', 'regular'),
    ('hourglass', 'regular'),
    ('house', 'regular'),
    ('image', 'regular'),
    ('info', 'regular'),
    ('link', 'regular'),
    ('link-break', 'regular'),
    ('list', 'regular'),
    ('lock-simple', 'regular'),
    ('magnifying-glass', 'regular'),
    ('microphone', 'regular'),
    ('microphone-slash', 'regular'),
    ('minus', 'regular'),
    ('minus-square', 'regular'),
    ('paper-plane-right', 'regular'),
    ('paper-plane-tilt', 'regular'),
    ('paperclip', 'regular'),
    ('pause', 'regular'),
    ('pencil-simple', 'regular'),
    ('phone', 'regular'),
    ('phone-slash', 'regular'),
    ('play', 'fill'),
    ('plus', 'regular'),
    ('prohibit', 'regular'),
    ('qr-code', 'regular'),
    ('question', 'regular'),
    ('radio-button', 'regular'),
    ('share-network', 'regular'),
    ('shield-check', 'regular'),
    ('sign-out', 'regular'),
    ('square', 'regular'),
    ('star', 'regular'),
    ('star', 'fill'),
    ('star-half', 'regular'),
    ('thumbs-up', 'regular'),
    ('timer', 'regular'),
    ('translate', 'regular'),
    ('trash', 'regular'),
    ('upload-simple', 'regular'),
    ('user', 'regular'),
    ('user-circle', 'regular'),
    ('user-minus', 'regular'),
    ('user-plus', 'regular'),
    ('users', 'regular'),
    ('users-three', 'regular'),
    ('video-camera', 'regular'),
    ('video-camera-slash', 'regular'),
    ('wallet', 'regular'),
    ('warning', 'regular'),
    ('warning-circle', 'regular'),
    ('warning-octagon', 'regular'),
    ('x', 'regular'),
]
