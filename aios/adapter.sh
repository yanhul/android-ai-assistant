#!/usr/bin/env bash
set -euo pipefail

if [[ ! -x ./gradlew ]]; then
  printf '%s\n' '{"status":"BLOCKED","reason":"Gradle wrapper missing","artifact_refs":[],"evidence_refs":[],"verification_refs":[],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}'
  exit 0
fi

if ./gradlew test >/tmp/aios-android-test.log 2>&1; then
  printf '%s\n' '{"status":"PASS","artifact_refs":["build/test-results"],"evidence_refs":["android-ci-pass"],"verification_refs":["android_ci","authority_boundary"],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}'
else
  printf '%s\n' '{"status":"BLOCKED","artifact_refs":[],"evidence_refs":["android-ci-failure"],"verification_refs":["android_ci"],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}'
fi
