#!/usr/bin/env python3
"""Durable roadmap controller for the Android AI Assistant.

The controller owns lifecycle state; an agent may execute within AGENT_POLICY.md
but cannot redefine the roadmap, acceptance criteria, safety boundaries, or
terminal conditions.
"""
from __future__ import annotations

import hashlib
import json
import re
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ROADMAP = ROOT / "ROADMAP.md"
STATE = ROOT / "harness" / "state.json"
MAX_ITERATIONS = 3


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def checkpoint(state: dict, **changes) -> dict:
    state.update(changes)
    state["updated_at"] = datetime.now(timezone.utc).isoformat()
    STATE.parent.mkdir(parents=True, exist_ok=True)
    STATE.write_text(json.dumps(state, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    return state


def first_unblocked_todo() -> str | None:
    text = ROADMAP.read_text(encoding="utf-8")
    for match in re.finditer(r"\|\s*(T\d+)\s*\|.*?\|\s*TODO\s*\|", text):
        return match.group(1)
    return None


def main() -> int:
    state = json.loads(STATE.read_text(encoding="utf-8")) if STATE.exists() else {
        "phase": "OBSERVE", "iteration": 0, "retry_count": 0, "terminal": False
    }

    roadmap_hash = sha256(ROADMAP)
    checkpoint(state, phase="OBSERVE", roadmap_sha256=roadmap_hash)

    if state.get("terminal"):
        print("TERMINAL:" + str(state.get("terminal_reason", "unspecified")))
        return 0

    task = state.get("task_id") or first_unblocked_todo()
    if not task:
        checkpoint(state, phase="TERMINAL", terminal=True,
                   terminal_reason="no unblocked TODO remains", result="DONE")
        print("DONE")
        return 0

    iteration = int(state.get("iteration", 0)) + 1
    checkpoint(state, phase="DECIDE", task_id=task, iteration=iteration)

    if iteration > MAX_ITERATIONS:
        checkpoint(state, phase="HOLD", result="HOLD: ITERATION_LIMIT",
                   terminal_reason="repair budget exhausted")
        print("HOLD: ITERATION_LIMIT")
        return 0

    # The ACT boundary is deliberately explicit. The repository still requires
    # an approved coding-agent runtime before unattended edits are allowed.
    checkpoint(state, phase="ACT", result="READY_FOR_AGENT",
               last_action="select first unblocked TODO")
    checkpoint(state, phase="VERIFY", result="AWAITING_AGENT_AND_CI")
    checkpoint(state, phase="PERSIST", result="AWAITING_AGENT_AND_CI")
    checkpoint(state, phase="YIELD", current_task=task)
    print("READY_FOR_AGENT:" + task)
    print("roadmap_sha256=" + roadmap_hash)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
