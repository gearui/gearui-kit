# Changelog

All notable changes to gearui-kit are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] — targeting 1.0.0-beta3

### Breaking changes

- Removed `ButtonType.GHOST`. It behaved identically to `ButtonType.TEXT`;
  use `ButtonType.TEXT` instead. This is a source-breaking change.

### Changed

- KuiklyUI upgraded from 2.25.0 to 2.27.0 on every channel: the Kotlin
  artifacts (`2.27.0-2.1.21`), the ohos artifacts (`2.27.0-2.0.21-ohos`),
  the web renderer, the iOS pod and the ohpm `@kuikly-open/render` package.
  The toolchain is unchanged (Kotlin 2.1.21, Compose 1.7.3, KSP 2.1.21-2.0.1),
  so this is a dependency bump, not a migration. Verified: Android, iOS
  simulator, Web bundle, ohos link, apiCheck, and privchat-ui compiling
  against the result.
- Dropped the Compose Material dependency. Loading states use the new GearUI
  `LoadingIndicator` primitive, and `Radio`/`Progress` labels use the
  foundation `Text` with typography tokens instead of Material3 widgets.
- `Cell.showArrow` renders a real `chevron_right` icon instead of an
  invisible placeholder.

### Fixed

- The ohos build gained the `currentCalendarDate()` actual it was missing
  (POSIX `localtime_r`; HarmonyOS has no Foundation). The calendar fix added
  an expect to commonMain with actuals for Android/iOS/JS only, which broke
  the parallel ohos configuration and nothing else.
- `CalendarDate.today()` returns the actual device date instead of a
  hardcoded constant, and the platform clock is pinned to the Gregorian
  calendar on Android, iOS and JS so locale calendars (e.g. Thai Buddhist)
  can no longer feed era years into the month-grid math.
- `Slider` and `RangeSlider` step snapping now origins the step grid at
  `valueRange.start` instead of zero, so ranges that do not start at zero
  snap onto the correct stops; zero-span ranges with steps no longer divide
  by zero.
- `ResponsiveGrid` computes its column count from the measured container
  width instead of a fixed three columns, and stays invisible until the
  first measurement so the initial frame never shows a wrong layout.
- `LoadingIndicator` normalises degenerate arguments: a non-positive size
  renders nothing, the stroke width is clamped into `0..size`, and a
  non-positive duration falls back to the default.

## [1.0.0-beta2] — last published release
