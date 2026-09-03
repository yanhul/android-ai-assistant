#!/usr/bin/env python3
"""Static AIOS-boundary conformance checks for the Android harness.

This is intentionally a local, fail-closed check: the coding agent cannot
change the governing policy, harness, workflow, or terminal criteria without
breaking the protected-file gate. It verifies that the child harness exposes
the minimum contract vocabulary needed before later AIOS adapter work.
"""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

REQUIRED = {
    "AGENT_POLICY.md": (
        "The agent may not modify this policy",
        "Default maximum autonomous repair iterations per task: 3",
    ),
    "harness/HARNESS.md": (
        "GOAL -> OBSERVE -> DECIDE -> ACT -> VERIFY -> PERSIST -> RESUME",
        "Terminal states are explicit: `DONE`, `HOLD`, or `BLOCKED`.",
    ),
    ".github/workflows/autonomous-agent.yml": (
        "Verify protected control plane was not changed",
        "Create or update PR",
    ),
}


def main() -> int:
    failures = []
    for rel, needles in REQUIRED.items():
        text = (ROOT / rel).read_text(encoding="utf-8")
        for needle in needles:
            if needle not in text:
                failures.append(f"{rel}: missing required boundary: {needle}")

    controller = (ROOT / "harness" / "controller.py").read_text(encoding="utf-8")
    for needle in ("phase=\"OBSERVE\"", "phase=\"ACT\"", "MAX_ITERATIONS", "terminal"):
        if needle not in controller:
            failures.append(f"harness/controller.py: missing control invariant: {needle}")

    if failures:
        print("AIOS_CONFORMANCE: BLOCKED")
        for failure in failures:
            print(f"- {failure}")
        return 1
    print("AIOS_CONFORMANCE: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
