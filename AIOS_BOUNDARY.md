# AIOS Boundary Contract — `android-ai-assistant`

This repository is an execution substrate. Its autonomous coding agent is not the policy owner.

## Authority invariants

- **Policy owner:** `AGENT_POLICY.md`, `harness/HARNESS.md`, and `harness/controller.py`.
- **Agent authority:** bounded implementation of the selected roadmap task.
- **Protected control plane:** policy, harness, controller, workflow, and acceptance/terminal criteria are not agent-editable.
- **Durability:** `harness/state.json` is checkpointed before/after bounded execution.
- **Verification:** protected files are checked after execution; project/CI evidence is required before promotion.
- **External effect boundary:** GitHub PR creation/merge is a separate authority boundary; lack of permission must produce HOLD, not a false success.

## AIOS adoption status

| Boundary | Status |
|---|---|
| Observe → Decide → Act → Verify → Persist → Resume | IMPLEMENTED |
| Immutable agent policy/control plane | IMPLEMENTED |
| Typed tool contracts/registry | IMPLEMENTED |
| Explicit generalized permit/capability object | AIOS-CENTRAL |
| General contract/authority verifier | AIOS-CENTRAL |
| External-effect receipt/reconciliation layer | AIOS-CENTRAL — PR boundary remains fail-closed and checkpointed |
| Central workload execution | AIOS-CENTRAL — pinned to `e2dc2c32bedf7de598b66e1bfe89f300fb83ccfc` |

The Android workload does not vendor a second AIOS control plane. Its `aios/` adapter is executed by the central AIOS runner against the pinned AIOS core.

## Rule

The coding agent can request/execute bounded work, but it cannot grant itself permissions, change acceptance criteria, create a PASS from missing evidence, or redefine terminal conditions.
