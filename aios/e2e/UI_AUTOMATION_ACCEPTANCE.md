# Android UI automation acceptance

This document is an evidence contract, not an authorization to bypass Android security boundaries.

## Implemented source capabilities
- User-enabled AccessibilityService bridge.
- Visible-window node lookup by text.
- Back and Home global actions.
- Tap and swipe gestures.
- Deterministic voice routing for explicit `click`, `bấm`, `back`, and `home` commands.
- Automation fails closed when accessibility is unavailable.

## Still requiring physical-device evidence
- Accessibility service can be enabled by a user on a supported Android version.
- Speech recognition and TTS work on-device.
- UI node discovery works against real third-party applications.
- Gestures reach the intended target.
- End-to-end Observe -> Decide -> Act -> Verify succeeds on a real task.

## Not yet implemented
- Screenshot/vision observation.
- Model-generated structured tool calls.
- General app launching and package-aware navigation.
- Text entry through the accessibility boundary.
- Durable task state/resume for Android UI tasks.
- Autonomous multi-step task orchestration.

Do not mark full Jarvis/UI autonomy as passing from source inspection or a build alone.
