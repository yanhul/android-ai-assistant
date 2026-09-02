# Durable Autonomous Harness

## Control loop

`GOAL -> OBSERVE -> DECIDE -> ACT -> VERIFY -> PERSIST -> RESUME`

The harness is the control plane. The agent is an executor inside the policy boundary.

## Invariants

- Policy, acceptance criteria, evidence requirements, permissions, iteration budget, and terminal conditions are not agent-editable.
- Every iteration persists a checkpoint before yielding.
- Every external action is followed by verification before it can be considered complete.
- A resumed run starts from persisted state; it must not assume prior in-memory context.
- CI failures are inputs to the next OBSERVE step, not reasons to guess.
- Repeated failures are bounded by the configured retry/iteration budget.
- Ambiguous, security-sensitive, or runtime-only conditions enter HOLD rather than being fabricated into PASS.
- Terminal states are explicit: `DONE`, `HOLD`, or `BLOCKED`.

## State contract

The durable state must identify at least:

- `goal`
- `phase`
- `task_id`
- `iteration`
- `retry_count`
- `last_commit`
- `last_ci_run`
- `last_ci_conclusion`
- `last_error`
- `terminal`
- `terminal_reason`
- `updated_at`

## Resume contract

A new workflow invocation must:

1. Load the persisted state.
2. Re-observe repository and CI state.
3. Verify the checkpoint is internally consistent.
4. Continue from the recorded phase or enter HOLD if recovery cannot be proven safe.

The workflow scheduler is only the wake-up mechanism; durable state remains authoritative.
