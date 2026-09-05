#!/usr/bin/env python3
"""Append-only provenance recorder for bounded coding iterations.

The policy/control plane remains authoritative outside this module. The
recorder records what happened; it cannot change acceptance, security, budget,
or terminal criteria.
"""
from __future__ import annotations
import hashlib, json
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
LOG = ROOT / "harness" / "lineage" / "records.jsonl"
REQUIRED = {"iteration", "task", "parent_artifacts", "change", "commit", "tests", "ci", "verdict", "findings", "constraints", "claims"}

def _hash(value: object) -> str:
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(",", ":")).encode()).hexdigest()

def append(record: dict) -> dict:
    missing = sorted(REQUIRED - record.keys())
    if missing:
        raise ValueError("missing lineage fields: " + ",".join(missing))
    entry = dict(record)
    entry["record_hash"] = _hash(record)
    entry["recorded_at"] = datetime.now(timezone.utc).isoformat()
    LOG.parent.mkdir(parents=True, exist_ok=True)
    with LOG.open("a", encoding="utf-8") as f:
        f.write(json.dumps(entry, sort_keys=True) + "\n")
    return entry

def read_all() -> list[dict]:
    if not LOG.exists():
        return []
    return [json.loads(x) for x in LOG.read_text(encoding="utf-8").splitlines() if x.strip()]
