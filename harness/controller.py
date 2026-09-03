#!/usr/bin/env python3
"""Durable control-plane controller for the Android AI Assistant.

The controller selects work and persists state. The coding agent is an executor
inside AGENT_POLICY.md; it may not redefine policy, gates, or terminal states.
Terminal state is accepted only with an external HMAC attestation.
"""
from __future__ import annotations

import hashlib
import hmac
import json
import os
import re
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ROADMAP = ROOT / "ROADMAP.md"
STATE = ROOT / "harness" / "state.json"
MAX_ITERATIONS = 3
TERMINAL_STATES = {"DONE", "HOLD", "BLOCKED"}


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


def terminal_attestation_valid(state: dict, roadmap_hash: str) -> bool:
    secret = os.environ.get("ANDROID_TERMINAL_AUTHORITY_SECRET")
    supplied = state.get("terminal_attestation")
    if not secret or not isinstance(supplied, str):
        return False
    reason = str(state.get("terminal_reason", ""))
    message = f"{roadmap_hash}\n{state.get('phase', '')}\n{reason}"
    expected = hmac.new(secret.encode(), message.encode(), hashlib.sha256).hexdigest()
    return hmac.compare_digest(supplied, expected)


def main() -> int:
    state = json.loads(STATE.read_text(encoding="utf-8")) if STATE.exists() else {}
    state.setdefault("goal", "Advance the Android AI Assistant roadmap")
    state.setdefault("iteration", 0)
    state.setdefault("retry_count", 0)
    state.setdefault("terminal", False)

    roadmap_hash = sha256(ROADMAP)
    checkpoint(state, phase="OBSERVE", roadmap_sha256=roadmap_hash)

    if state.get("terminal") or state.get("phase") in TERMINAL_STATES:
        if not terminal_attestation_valid(state, roadmap_hash):
            checkpoint(state, phase="HOLD", terminal=False,
                       result="HOLD: UNAUTHORIZED_TERMINAL_STATE",
                       last_error="persisted terminal state lacks valid external authority")
            print("HOLD: UNAUTHORIZED_TERMINAL_STATE")
            return 3
        print(f"TERMINAL_AUTHORITY_VERIFIED:{state.get('terminal_reason', 'unspecified')}")
        return 0

    task = state.get("task_id") or first_unblocked_todo()
    if not task:
        checkpoint(state, phase="HOLD", terminal=False,
                   result="HOLD: TERMINAL_AUTHORITY_REQUIRED",
                   terminal_reason="no unblocked TODO remains",
                   last_error="external terminal authority attestation required")
        print("HOLD: TERMINAL_AUTHORITY_REQUIRED")
        return 0

    iteration = int(state.get("iteration", 0)) + 1
    if iteration > MAX_ITERATIONS:
        checkpoint(state, phase="HOLD", terminal=False,
                   result="HOLD: ITERATION_LIMIT",
                   terminal_reason="repair budget exhausted",
                   last_error="autonomous iteration budget exhausted")
        print("HOLD: ITERATION_LIMIT")
        return 0

    checkpoint(
        state,
        phase="ACT",
        task_id=task,
        iteration=iteration,
        retry_count=int(state.get("retry_count", 0)),
        last_action=f"execute bounded agent pass for {task}",
        last_error=None,
        terminal=False,
    )
    print(f"ACT:{task}")
    print(f"ITERATION:{iteration}")
    print(f"ROADMAP_SHA256:{roadmap_hash}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
