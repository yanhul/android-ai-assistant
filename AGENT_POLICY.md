# Autonomous Coding Agent Policy

## Mission
Advance the Android AI Assistant roadmap using small, reviewable, evidence-backed changes.

## Hard boundaries
1. Never expose or commit secrets. API keys and signing credentials must come from GitHub Actions secrets or another external secret store.
2. Never disable CI, branch protections, tests, security checks, or repository controls to make a task pass.
3. Never fabricate test results, device behavior, Android compatibility, API availability, or performance numbers.
4. Never perform destructive repository operations unless the task explicitly requires them and the change is reviewable.
5. Do not merge directly to `main` from the autonomous loop. Use a dedicated branch and PR unless an explicit repository policy later authorizes auto-merge.
6. Do not add broad permissions when a narrower Android permission or GitHub token scope is sufficient.
7. Android actions must be explicit, typed, and user-visible where applicable. High-impact actions require a confirmation policy before execution.

## Loop
1. Read `ROADMAP.md` and repository instructions.
2. Select the first unblocked TODO.
3. Inspect existing implementation and tests before editing.
4. Make the smallest coherent change.
5. Run the strongest available local checks.
6. Commit only the implementation and tests/docs needed for the task.
7. Push the agent branch and wait for CI.
8. If CI fails, read the actual failure, classify it, and fix only evidence-supported causes.
9. Retry until PASS or the iteration budget is exhausted.
10. On PASS, update roadmap evidence and continue to the next unblocked task.
11. On budget exhaustion or an ambiguous/security-sensitive issue, stop and report the blocker rather than guessing.

## Iteration budget
- Default maximum autonomous repair iterations per task: 3.
- A CI failure does not justify an unlimited retry loop.
- Dependency/network failures should be distinguished from source failures and not “fixed” by arbitrary version changes.

## Allowed automation
- Repository read/write on the agent branch.
- Gradle build/test commands required by the project.
- GitHub Actions status/log inspection.
- Creation/update of PRs through the configured GitHub integration.

## Forbidden automation
- Reading unrelated repositories or private data.
- Printing secrets into logs.
- Disabling security controls.
- Modifying release/signing credentials.
- Auto-executing irreversible Android actions during tests.

## Completion reporting
Every autonomous iteration must leave auditable evidence: commit SHA, CI run, pass/fail result, and blocker details when applicable.
