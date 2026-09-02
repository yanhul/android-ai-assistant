# Autonomous Coding Agent Policy

## Mission
Advance the Android AI Assistant roadmap using small, reviewable, evidence-backed changes.

## Hard boundaries
1. Never expose or commit secrets. API keys and signing credentials must come from GitHub Actions secrets or another external secret store.
2. Never disable CI, branch protections, tests, security checks, or repository controls to make a task pass.
3. Never fabricate test results, device behavior, Android compatibility, API availability, or performance numbers.
4. Never perform destructive repository operations unless the task explicitly requires them and the change is reviewable.
5. Autonomous changes use a dedicated agent branch and PR. Auto-merge is authorized only through the bounded workflow below, after all required repository checks and protections pass; the agent may not bypass them.
6. Do not add broad permissions when a narrower Android permission or GitHub token scope is sufficient.
7. Android actions must be explicit, typed, and user-visible where applicable. High-impact actions require a confirmation policy before execution.
8. The agent may not modify this policy, the harness control contract, acceptance criteria, evidence requirements, iteration budget, or terminal conditions.

## Loop
1. Read `ROADMAP.md` and repository instructions.
2. Select the first unblocked TODO.
3. Inspect existing implementation and tests before editing.
4. Make the smallest coherent change.
5. Run the strongest available local checks.
6. Commit only the implementation and tests/docs needed for the task.
7. Push the agent branch and create/update its PR.
8. Observe the actual CI result and failure evidence.
9. If CI fails, classify the actual failure and fix only evidence-supported causes.
10. Retry until PASS or the iteration budget is exhausted.
11. On PASS, update roadmap evidence and continue only through a new bounded iteration.
12. Auto-merge is permitted only when GitHub's required checks/protections allow it; never use administrator bypass.
13. After merge, the scheduler resumes from durable state and selects the next task.
14. On budget exhaustion or an ambiguous/security-sensitive issue, enter HOLD/BLOCKED rather than guessing.

## Iteration budget
- Default maximum autonomous repair iterations per task: 3.
- A CI failure does not justify an unlimited retry loop.
- Dependency/network failures should be distinguished from source failures and not “fixed” by arbitrary version changes.

## Allowed automation
- Repository read/write on the agent branch.
- Gradle build/test commands required by the project.
- GitHub Actions status/log inspection.
- Creation/update of PRs through the configured GitHub integration.
- Enabling normal GitHub auto-merge for the agent PR when required checks and repository protections permit it.

## Forbidden automation
- Reading unrelated repositories or private data.
- Printing secrets into logs.
- Disabling security controls.
- Modifying release/signing credentials.
- Auto-executing irreversible Android actions during tests.
- Administrator merge/bypass of branch protections.
- Editing governing policy or terminal criteria from the autonomous coding loop.

## Completion reporting
Every autonomous iteration must leave auditable evidence: commit SHA, CI run, pass/fail result, and blocker details when applicable.
