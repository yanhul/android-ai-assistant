# M006 — Provider Verification

## Goal

Close T005 using observable build/runtime evidence without weakening the governing policy or acceptance criteria.

## Loop

`OBSERVE → DECIDE → ACT → VERIFY → PERSIST → RESUME`

## Allowed scope

- Inspect existing provider abstraction and configuration.
- Add focused tests required to establish T005 evidence.
- Fix provider-routing defects that are directly necessary for T005.
- Record evidence and update durable state only after verification.

## Forbidden

- Changing `AGENT_POLICY.md` or `harness/HARNESS.md` to make T005 pass.
- Lowering acceptance criteria.
- Marking T005 DONE without observable evidence.
- Adding real API credentials/secrets to the repository.
- Expanding into unrelated Android UI/device features.

## Terminal conditions

- `DONE`: T005 acceptance evidence is established.
- `HOLD`: implementation exists but required evidence is unavailable.
- `BLOCKED`: a required prerequisite prevents safe verification.

## Resume rule

On resume, re-observe repository state and verification evidence before taking the next action. Do not rely on stale assumptions.
