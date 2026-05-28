#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Copyright (c) 2026 gachlab
#
# E2E: kioskEntered / kioskExited round-trip for @gachlab/capacitor-kiosk.
#
# The host app registers both listeners and auto-runs enterKioskMode then
# exitKioskMode on load; the plugin emits kioskEntered (api) and kioskExited
# (reason 'api'). We grep logcat for both tagged events.
#
# `startLockTask()` requires either Device Owner OR system screen-pinning
# enabled. On a stock emulator we enable screen-pinning via `adb settings` so
# the API succeeds without DPM; user-exit detection (lifecycle, reason 'user')
# stays in the manual checklist.

set -euo pipefail

APK=$(find example-app/android/app/build/outputs/apk/debug -name "*.apk" | head -1)
PACKAGE="com.gachlab.capacitor.kiosk.example"
ACTIVITY=".MainActivity"
LOGCAT_OUT="/tmp/e2e-logcat.txt"
PASS=0

"$(dirname "$0")/wait-for-emulator.sh"

echo "→ Enabling system screen pinning so startLockTask() succeeds (no Device Owner)"
adb shell settings put global lock_to_app_enabled 1 2>/dev/null || true
adb shell settings put system lock_to_app_enabled 1 2>/dev/null || true

echo "→ Installing APK: $APK"
adb install -r --no-streaming "$APK"

echo "→ Launching app (auto-runs enter → exit)"
adb shell am start -n "${PACKAGE}/${ACTIVITY}"

# Wait until the WebView has registered the listeners so the auto-sequence's
# events are delivered to JS (the addListener call is logged by the bridge).
echo "→ Waiting for the WebView to register the kiosk listeners"
LISTENER_END=$(( $(date +%s) + 90 ))
until adb logcat -d 2>/dev/null | grep -q "addListener.*kioskEntered"; do
  if [[ $(date +%s) -ge $LISTENER_END ]]; then
    echo "✗ kioskEntered listener never registered within 90 s"
    adb logcat -d | grep -iE "Capacitor|chromium|console|error" | tail -30
    exit 1
  fi
  sleep 2
done

# The auto-sequence fires kioskExited last; seeing it means both events fired.
echo "→ Waiting for the auto-sequence to complete (kioskExited delivered)"
SEQ_END=$(( $(date +%s) + 90 ))
until adb logcat -d 2>/dev/null | grep -q "\[KIOSK-E2E\] event:kioskExited"; do
  if [[ $(date +%s) -ge $SEQ_END ]]; then
    echo "✗ kiosk auto-sequence never completed (startLockTask may have been rejected)"
    adb logcat -d | grep -iE "Capacitor|KioskMode|lock_task|SecurityException|chromium|console|error" | tail -40
    exit 1
  fi
  sleep 2
done

adb logcat -d > "$LOGCAT_OUT" 2>&1 || true

echo ""
echo "── Assertions ──────────────────────────────────────────────────────"

if grep -q '\[KIOSK-E2E\] event:kioskEntered' "$LOGCAT_OUT"; then
  echo "✓ kioskEntered delivered (API path)"
  PASS=$((PASS + 1))
else
  echo "✗ no kioskEntered event"
fi

if grep -qE '\[KIOSK-E2E\] event:kioskExited.*"reason":"api"' "$LOGCAT_OUT"; then
  echo "✓ kioskExited delivered with reason 'api'"
  PASS=$((PASS + 1))
else
  echo "✗ no kioskExited with reason 'api'"
fi

if grep -qE '\[KIOSK-E2E\] event:kiosk(Entered|Exited).*"timestamp":[0-9]{10,}' "$LOGCAT_OUT"; then
  echo "✓ events carry epoch-ms timestamps"
  PASS=$((PASS + 1))
else
  echo "✗ events missing a numeric timestamp"
fi

echo ""
if [[ "$PASS" -eq 3 ]]; then
  echo "✓ Kiosk E2E PASSED ($PASS/3)"
else
  echo "✗ Kiosk E2E FAILED ($PASS/3)"
  echo "--- KIOSK-E2E / kiosk log lines ---"
  grep -iE "KIOSK-E2E|kiosk(Entered|Exited)|lock_task" "$LOGCAT_OUT" | tail -30 || echo "(none)"
  exit 1
fi
