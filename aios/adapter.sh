#!/usr/bin/env bash
set -euo pipefail

if ! command -v gradle >/dev/null 2>&1; then
  printf '%s\n' '{"status":"BLOCKED","reason":"Gradle executable missing","artifact_refs":[],"evidence_refs":["gradle-missing"],"verification_refs":["android_ci","authority_boundary"],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}'
  exit 0
fi

if gradle --no-daemon testDebugUnitTest assembleDebug >/tmp/aios-android-test.log 2>&1; then
  test -s app/build/outputs/apk/debug/app-debug.apk
  sha256=$(sha256sum app/build/outputs/apk/debug/app-debug.apk | awk '{print $1}')
  printf '{"status":"PASS","artifact_refs":["app/build/outputs/apk/debug/app-debug.apk"],"evidence_refs":["android-build-sha256:%s"],"verification_refs":["unit_tests","assembleDebug","apk_exists","authority_boundary"],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}\n' "$sha256"
else
  printf '%s\n' '{"status":"BLOCKED","reason":"Android unit test/build failed","artifact_refs":[],"evidence_refs":["android-build-failure"],"verification_refs":["android_ci","authority_boundary"],"provenance":{"producer":"yanhul/android-ai-assistant","adapter":"android.assistant@1"}}'
fi
