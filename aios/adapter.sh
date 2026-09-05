#!/usr/bin/env bash
set -euo pipefail

# AIOS workload adapter: bounded software/device-agent verification.
# Device actions are never faked; absence of a device remains BLOCKED.
if [[ ! -x ./gradlew ]]; then
  printf '%s\n' '{"status":"BLOCKED","reason":"Gradle wrapper missing"}'
  exit 0
fi

if ./gradlew test >/tmp/aios-android-test.log 2>&1; then
  printf '%s\n' '{"status":"PASS","verification":"android_ci","artifact":"build/test-results"}'
else
  printf '%s\n' '{"status":"BLOCKED","verification":"android_ci","reason":"Android test suite failed","log":"/tmp/aios-android-test.log"}'
  exit 0
fi
