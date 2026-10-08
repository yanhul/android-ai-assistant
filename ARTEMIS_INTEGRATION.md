# ARTEMIS Execution Capability

`google/artemis` is treated as an external Android execution provider, not as the authority or policy layer of this application.

## Boundary

```text
AIOS / Assistant policy
        |
        | contract + permit + effect
        v
Android execution capability
        |
        +-- local AndroidActions
        |
        `-- ARTEMIS (external host/provider)
                |
                +-- MCP
                +-- ADB/device
                +-- UI observation/action
                `-- execution trace
```

ARTEMIS owns device interaction mechanics. This repository owns the user-facing assistant, typed tool boundary, and admission of actions. AIOS remains the authority for generalized permits, evidence requirements, and terminal criteria.

## Why this is an external provider

ARTEMIS is a host-side Python/MCP system that drives a connected Android device. It is therefore not embedded as an Android library here. The Android app must not pretend that ARTEMIS actions were executed merely because an LLM requested them.

The provider boundary should exchange only typed requests and observed receipts:

- request: natural-language task + optional device identity + execution profile
- receipt: provider status, trace/session identifier when supplied, observed result, and error details
- evidence: screenshots/logs/traces remain evidence, not authority

## Supported ARTEMIS capabilities observed in the upstream repository

- `mobile_run_task` for autonomous mobile workflows
- `mobile_manage_task` for task lifecycle/status
- `mobile_get_device_state` for device observation
- `mobile_inspect_trace` for execution evidence
- Flash and Pro execution profiles
- Dynamic-first, coordinate-fallback locating

Upstream reference: https://github.com/google/artemis

## Safety rules

1. Never grant an action permission because ARTEMIS accepts it.
2. Never convert a missing provider receipt into `PASS`.
3. `UNKNOWN`/timeout/provider failure stays non-success until independently verified.
4. High-impact Android actions require the application's confirmation policy before dispatch.
5. Provider credentials and MCP configuration stay outside the Android app source tree.
6. The ARTEMIS provider may be unavailable without making the assistant claim that an action occurred.

## Integration status

- **Capability boundary:** implemented in this repository documentation.
- **Typed provider contract:** implemented by `ArtemisExecution.kt`.
- **Direct MCP transport from the APK:** intentionally not embedded; MCP is a host-side integration boundary.
- **AIOS generalized permit/evidence layer:** remains owned by AIOS per `AIOS_BOUNDARY.md`.
