# T005 — AI Provider Abstraction Evidence

Status: AUDIT_REQUIRED

## Scope

T005 verifies that the Android assistant can route model requests through an explicit provider abstraction rather than binding the assistant runtime directly to one vendor.

## Implementation observed

- `LLMProvider.kt` defines the provider abstraction.
- `GeminiClient.kt` implements a Gemini provider.
- `OpenAICompatibleClient.kt` implements an OpenAI-compatible provider.
- `ProviderRegistry.kt` provides provider registration/lookup.

## Evidence boundary

This document records implementation evidence only. It does **not** claim build, runtime, integration, or device validation.

## Required verification before T005 can be marked DONE

1. Android build completes successfully on the current `main` commit.
2. Unit/instrumentation tests covering provider selection and routing pass, if such tests are part of the acceptance criteria.
3. Provider configuration does not require hard-coding secrets in source.
4. A failed/unavailable provider is surfaced deterministically rather than silently treated as a successful model response.
5. Evidence is attached to the durable harness state before promotion to T006.

## Current conclusion

**HOLD — implementation exists, but acceptance evidence is not yet established by this artifact.**

No roadmap status or policy is changed by this file.
