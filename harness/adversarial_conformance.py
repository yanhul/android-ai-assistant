#!/usr/bin/env python3
"""Runtime adversarial conformance for the Android control plane."""
from __future__ import annotations
import importlib.util
import json
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def load_controller():
    spec = importlib.util.spec_from_file_location("android_controller_under_test", ROOT / "harness" / "controller.py")
    mod = importlib.util.module_from_spec(spec); spec.loader.exec_module(mod)
    return mod


def test_terminal_tamper():
    mod = load_controller()
    with tempfile.TemporaryDirectory() as td:
        d = Path(td); state = d / "state.json"; roadmap = d / "ROADMAP.md"
        roadmap.write_text("| T1 | task | TODO |\n")
        state.write_text(json.dumps({"terminal": True, "terminal_reason": "FORGED", "phase": "DONE"}))
        mod.STATE = state; mod.ROADMAP = roadmap
        rc = mod.main()
        assert rc != 0, "FORGED terminal state was accepted"


def test_terminal_label_tamper():
    mod = load_controller()
    with tempfile.TemporaryDirectory() as td:
        d = Path(td); state = d / "state.json"; roadmap = d / "ROADMAP.md"
        roadmap.write_text("| T1 | task | TODO |\n")
        state.write_text(json.dumps({"terminal": False, "phase": "DONE", "terminal_reason": "FORGED"}))
        mod.STATE = state; mod.ROADMAP = roadmap
        rc = mod.main()
        assert rc != 0, "forged terminal phase was accepted"


if __name__ == "__main__":
    failures = []
    for test in (test_terminal_tamper, test_terminal_label_tamper):
        try: test()
        except Exception as exc: failures.append(f"{test.__name__}: {exc}")
    if failures:
        print("AIOS_ADVERSARIAL: BLOCKED")
        print("\n".join("- " + x for x in failures))
        raise SystemExit(1)
    print("AIOS_ADVERSARIAL: PASS")
