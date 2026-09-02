# Autonomous Android AI Assistant Roadmap

Status is evidence-driven. A task is DONE only when its stated acceptance criteria are satisfied and CI is green.

## Execution rules
- Work one task at a time in dependency order.
- Never claim PASS from source inspection alone when a build/test can provide evidence.
- Never invent Android API behavior, permissions, versions, measurements, or test results.
- Do not put API keys, tokens, passwords, or signing material in the repository.
- Prefer a feature branch/PR for autonomous changes. Do not auto-merge to `main` until the project explicitly enables that policy.
- On failure, inspect the actual CI log, make the smallest justified fix, and retry within the configured iteration budget.

## Tasks

| ID | Task | Status | Acceptance evidence |
|---|---|---|---|
| T001 | Android project baseline | DONE | GitHub Actions debug build passed |
| T002 | VoiceInteractionService baseline | DONE | Android project builds |
| T003 | STT/TTS voice loop | DONE | Code present; runtime device test still required |
| T004 | Explicit Android action boundary | DONE | AndroidActions + router committed; CI passed |
| T005 | AI provider abstraction | DONE | Provider abstraction + JVM provider-selection tests + Android CI success; no repository secrets |
| T006 | Tool registry and typed tool contracts | TODO | Unit tests + CI pass |
| T007 | Confirmation/safety policy | TODO | Policy tests cover destructive/ambiguous actions |
| T008 | Persistent local state | TODO | Instrumented/unit tests + CI pass |
| T009 | End-to-end assistant orchestration | TODO | Integration tests + CI pass |
| T010 | Release build pipeline | TODO | Signed/reproducible release process documented; secrets external |
| T011 | Autonomous agent loop | TODO | Agent can select next TODO, modify branch, consume CI feedback, and stop on pass/budget |
| T012 | Autonomous PR gate | TODO | Agent-created PR requires green CI before merge |

## Definition of done
A task may be marked DONE only after:
1. Implementation exists in the repository.
2. Relevant tests/build checks pass.
3. CI result is observed as `success`.
4. Any runtime-only requirement is explicitly labeled as requiring a physical Android-device test.
5. ROADMAP status and evidence are updated in the same change where practical.
